package io.github.mikenakis.testana.discovery.maven;

import io.github.mikenakis.debug.Debug;
import io.github.mikenakis.kit.logging.Log;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.project.DefaultProjectBuilder;
import org.apache.maven.project.DefaultProjectBuildingHelper;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingHelper;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectModelResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transport.wagon.WagonTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * TODO: remove maven central remote repository
 *       - currently, the maven central repository needs to be included in the list of remote repositories, otherwise Testana fails to run, and I do not know
 *         why. According to my understanding, all artifacts should have already been copied to the local repository, so no external lookups should be
 *         necessary, and the fact that external lookups are being made unnecessarily slows down the tests. Need to figure out what is going on and fix this.
 */
final class MavenHelper
{
	final Path localRepositoryPath;
	private final ServiceLocator serviceLocator = newServiceLocator();
	private final RepositorySystem repositorySystem = serviceLocator.getService( RepositorySystem.class );
	private final RepositorySystemSession repositorySystemSession;
	private final ModelResolver mavenModelResolver;
	private final ModelBuilder mavenModelBuilder = new DefaultModelBuilderFactory().newInstance();
	private final List<RemoteRepository> remoteRepositories;

	MavenHelper()
	{
		localRepositoryPath = getLocalRepositoryPath();
		repositorySystemSession = newMavenRepositorySystemSession( repositorySystem, localRepositoryPath );
		remoteRepositories = getRemoteRepositories();
		mavenModelResolver = createMavenModelResolver( serviceLocator, repositorySystemSession, remoteRepositories );
	}

	private static List<RemoteRepository> getRemoteRepositories()
	{
		Collection<RemoteRepository> repositories = new HashSet<>();
		//repositories.add( new RemoteRepository.Builder("local", "default", "file:C:/Users/MBV/.m2/repository").build() );
		repositories.add( new RemoteRepository.Builder( "central", "default", "https://repo1.maven.org/maven2/" ).build() );
		//repositories.add( new RemoteRepository.Builder( "repsy-mikenakis-public", "default", "https://repo.repsy.io/mvn/mikenakis/mikenakis-public" ).build() );
		Settings settings = newSettings();
		for( Profile profile : settings.getProfiles() )
			if( isProfileActive( settings, profile ) )
				for( Repository repository : profile.getRepositories() )
					repositories.add( toRemoteRepository( settings, repository ) );
		return repositories.stream().toList();
	}

	private static boolean isProfileActive( Settings settings, Profile profile )
	{
		return settings.getActiveProfiles().contains( profile.getId() ) || (profile.getActivation() != null && profile.getActivation().isActiveByDefault());
	}

	private static RemoteRepository toRemoteRepository( Settings settings, Repository repository )
	{
		RemoteRepository.Builder remoteBuilder = toRemoteRepositoryBuilder( settings, repository.getId(), repository.getLayout(), repository.getUrl() );
		setPolicy( remoteBuilder, repository.getSnapshots(), true );
		setPolicy( remoteBuilder, repository.getReleases(), false );
		return remoteBuilder.build();
	}

	private static RemoteRepository.Builder toRemoteRepositoryBuilder( Settings settings, String id, String layout, String url )
	{
		Proxy activeProxy = settings.getActiveProxy();
		RemoteRepository.Builder remoteBuilder = new RemoteRepository.Builder( id, layout, url );
		Server server = settings.getServer( id );
		if( server != null )
			remoteBuilder.setAuthentication( new AuthenticationBuilder().addUsername( server.getUsername() ).addPassword( server.getPassword() ).build() );
		if( activeProxy != null )
			if( null == activeProxy.getNonProxyHosts() )
				remoteBuilder.setProxy( getActiveAetherProxyFromSettings( settings ) );
			else if( !repositoryUrlMatchNonProxyHosts( settings.getActiveProxy().getNonProxyHosts(), remoteBuilder.build().getUrl() ) )
				remoteBuilder.setProxy( getActiveAetherProxyFromSettings( settings ) );
		return remoteBuilder;
	}

	private static org.eclipse.aether.repository.Proxy getActiveAetherProxyFromSettings( Settings settings )
	{
		return new org.eclipse.aether.repository.Proxy( settings.getActiveProxy().getProtocol(), settings.getActiveProxy().getHost(), settings.getActiveProxy().getPort(), new AuthenticationBuilder().addUsername( settings.getActiveProxy().getUsername() ).addPassword( settings.getActiveProxy().getPassword() ).build() );
	}

	private static boolean repositoryUrlMatchNonProxyHosts( String nonProxyHosts, String artifactURL )
	{
		Optional<String> host = getHost( artifactURL );
		if( host.isEmpty() )
			return false;
		String nonProxyHostsRegexp = nonProxyHosts.replace( "*", ".*" ); // Replace * with .* so that nonProxyHosts complies with pattern matching syntax
		Pattern p = Pattern.compile( nonProxyHostsRegexp );
		return p.matcher( host.get() ).find();
	}

	private static Optional<String> getHost( String artifactURL )
	{
		try
		{
			return Optional.of( new URL( artifactURL ).getHost() );
		}
		catch( MalformedURLException e )
		{
			Log.warning( "Failed to parse proxy URL '" + artifactURL + "', cause: " + e.getMessage() );
			return Optional.empty();
		}
	}

	private static void setPolicy( RemoteRepository.Builder builder, RepositoryPolicy policy, boolean snapshot )
	{
		if( policy != null )
		{
			org.eclipse.aether.repository.RepositoryPolicy repoPolicy = new org.eclipse.aether.repository.RepositoryPolicy( policy.isEnabled(), policy.getUpdatePolicy() != null ? policy.getUpdatePolicy() : org.eclipse.aether.repository.RepositoryPolicy.UPDATE_POLICY_NEVER, policy.getChecksumPolicy() != null ? policy.getChecksumPolicy() : org.eclipse.aether.repository.RepositoryPolicy.CHECKSUM_POLICY_WARN );
			if( snapshot )
				builder.setSnapshotPolicy( repoPolicy );
			else
				builder.setReleasePolicy( repoPolicy );
		}
	}

	private static Settings newSettings()
	{
		SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();
		SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

		String mavenHome = System.getenv( "M2_HOME" );
		if( mavenHome != null )
		{
			File globalSettingsFile = new File( mavenHome + "/conf/settings.xml" );
			if( globalSettingsFile.exists() )
				request.setGlobalSettingsFile( globalSettingsFile );
		}

		request.setSystemProperties( System.getProperties() );

		Settings settings;
		try
		{
			settings = settingsBuilder.build( request ).getEffectiveSettings();
		}
		catch( SettingsBuildingException e )
		{
			throw new RuntimeException( e );
		}

		if( settings.getLocalRepository() == null )
		{
			String userHome = System.getProperty( "user.home" );
			if( userHome != null )
				settings.setLocalRepository( userHome + "/.m2/repository" );
			else
				throw new RuntimeException( "Cannot find local maven repository" );
		}

		return settings;
	}

	Model loadMavenModel( File pomFile )
	{
		ModelBuildingRequest modelBuildingRequest = new DefaultModelBuildingRequest();
		modelBuildingRequest.setPomFile( pomFile );
		modelBuildingRequest.setModelResolver( mavenModelResolver );
		modelBuildingRequest.setProcessPlugins( false );
		modelBuildingRequest.setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL );
		modelBuildingRequest.setUserProperties( getUserProperties() );
		ModelBuildingResult modelBuildingResult;
		try
		{
			modelBuildingResult = mavenModelBuilder.build( modelBuildingRequest );
		}
		catch( ModelBuildingException e )
		{
			modelBuildingResult = e.getResult();
		}
		boolean errors = reportMavenModelProblems( modelBuildingResult.getProblems() );
		assert !errors;
		Model model = modelBuildingResult.getEffectiveModel();
		/**
		 * PEARL: `ModelBuildingResult.getEffectiveModel()` may return null, even though its documentation explicitly states that it never returns null!
		 * The documentation of `ModelBuildingResult.getEffectiveModel()` makes the following statement:
		 *   "Returns: The assembled model, never null."
		 * Now, the documentation of the vast majority of methods out there (e.g. Object.getClass(), Thread.currentThread(), etc.) never says that a method
		 * does not return null; it is taken for granted. In general, null is mentioned only when it may be returned, not when it may not be returned.
		 * So, it is kind of strange that in the case of `ModelBuildingResult.getEffectiveModel()` they explicitly state that it never returns null, right?
		 * It sounds kind of suspicious, doesn't it? It is almost as if they are insecure about it, right? Well, indeed, IT HAS BEEN OBSERVED TO RETURN NULL.
		 * Specifically, it will return null if something is wrong with the pom file preventing the model from being assembled, for example referring to a
		 * non-existent parent pom.
		 */
		assert model != null;
		return model;
	}

	Collection<File> getAllExternalDependencies( String groupId, String artifactId, String version )
	{
		//code adapted from https://stackoverflow.com/a/40820480/773113
		DefaultArtifact artifact = new DefaultArtifact( groupId, artifactId, "jar", version );
		Dependency dependency = new Dependency( artifact, JavaScopes.COMPILE );
		CollectRequest collectRequest = new CollectRequest( dependency, remoteRepositories );
		DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, null );
		DependencyResult dependencyResult;
		try
		{
			dependencyResult = repositorySystem.resolveDependencies( repositorySystemSession, dependencyRequest );
		}
		catch( DependencyResolutionException e )
		{
			Log.error( e );
			Debug.breakPoint();
			return List.of();
		}
		Collection<File> externalDependencies = new LinkedHashSet<>();
		for( ArtifactResult artifactResult : dependencyResult.getArtifactResults() )
			getExternalDependenciesRecursively( artifactResult.getRequest().getDependencyNode(), externalDependencies );
		return externalDependencies;
	}

	private static void getExternalDependenciesRecursively( DependencyNode dependencyNode, Collection<File> mutableFiles )
	{
		File file = dependencyNode.getArtifact().getFile();
		if( file == null )
			Log.warning( "Artifact " + dependencyNode.getArtifact() + " has no file!" );
		else
			mutableFiles.add( dependencyNode.getArtifact().getFile() );
		for( DependencyNode child : dependencyNode.getChildren() )
			getExternalDependenciesRecursively( child, mutableFiles );
	}

	private static ModelResolver createMavenModelResolver( ServiceLocator serviceLocator, RepositorySystemSession repositorySystemSession, List<RemoteRepository> remoteRepositories )
	{
		RemoteRepositoryManager remoteRepositoryManager = serviceLocator.getService( RemoteRepositoryManager.class ); //new DefaultRemoteRepositoryManager();
		DefaultRepositorySystem repositorySystem = new DefaultRepositorySystem();
		repositorySystem.initService( serviceLocator );
		return new ProjectModelResolver( repositorySystemSession, null/* new RequestTrace( null ) */, repositorySystem, remoteRepositoryManager, remoteRepositories, ProjectBuildingRequest.RepositoryMerging.POM_DOMINANT, null );
	}

	private static RepositorySystemSession newMavenRepositorySystemSession( RepositorySystem system, Path localRepositoryPath )
	{
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		LocalRepository localRepo = new LocalRepository( localRepositoryPath.toFile() );
		session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );
		return session;
	}

	private static Properties getUserProperties()
	{
		return new Properties();
	}

	private static boolean reportMavenModelProblems( Iterable<ModelProblem> mavenModelProblems )
	{
		boolean error = false;
		for( var mavenModelProblem : mavenModelProblems )
			error |= reportMavenModelProblem( mavenModelProblem );
		return error;
	}

	private static boolean reportMavenModelProblem( ModelProblem mavenModelProblem )
	{
		Log.Level level = switch( mavenModelProblem.getSeverity() )
			{
				case FATAL, ERROR -> Log.Level.ERROR;
				case WARNING -> Log.Level.WARN;
			};
		Log.message( level, mavenModelProblem.getMessage() );
		return level == Log.Level.ERROR;
	}

	private static ServiceLocator newServiceLocator()
	{
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.setErrorHandler( new DefaultServiceLocator.ErrorHandler()
		{
			@Override public void serviceCreationFailed( Class<?> type, Class<?> impl, Throwable exception )
			{
				throw sneakyThrow( exception );
			}
		} );
		locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
		locator.addService( TransporterFactory.class, WagonTransporterFactory.class );
		locator.addService( ProjectBuildingHelper.class, DefaultProjectBuildingHelper.class );
		locator.addService( ProjectBuilder.class, DefaultProjectBuilder.class );
		return locator;
	}

	@SuppressWarnings( "unchecked" ) private static <T extends Exception> RuntimeException sneakyThrow( Throwable t ) throws T
	{
		throw (T)t;
	}

	private static Path getLocalRepositoryPath()
	{
		Path m2Path = Paths.get( System.getProperty( "user.home" ), ".m2" );
		// XXX See MVN-TODO-1
		//		Path mavenSettingsPath = m2Path.resolve( "settings.xml" );
		//      extract <localRepository>...</localRepository> from settings.xml
		return m2Path.resolve( "repository" );
	}
}
