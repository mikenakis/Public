package mikenakis.testana.discovery.maven;

import io.github.mikenakis.debug.Debug;
import mikenakis.kit.logging.Log;
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
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
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
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.transport.wagon.WagonTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

final class MavenHelper
{
	private static final List<RemoteRepository> remoteRepositories = List.of( new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build() );

	final Path localRepositoryPath;
	private final ServiceLocator serviceLocator = newServiceLocator();
	private final RepositorySystem repositorySystem = serviceLocator.getService( RepositorySystem.class );
	private final RepositorySystemSession repositorySystemSession;
	private final ModelResolver mavenModelResolver;
	private final ModelBuilder mavenModelBuilder = new DefaultModelBuilderFactory().newInstance();

	MavenHelper()
	{
		localRepositoryPath = getLocalRepositoryPath();
		repositorySystemSession = newMavenRepositorySystemSession( repositorySystem, localRepositoryPath );
		mavenModelResolver = createMavenModelResolver( serviceLocator, repositorySystemSession );
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

	private static ModelResolver createMavenModelResolver( ServiceLocator serviceLocator, RepositorySystemSession repositorySystemSession )
	{
		RemoteRepositoryManager remoteRepositoryManager = serviceLocator.getService( RemoteRepositoryManager.class );
		RequestTrace requestTrace = new RequestTrace( null );
		DefaultRepositorySystem repositorySystem = new DefaultRepositorySystem();
		repositorySystem.initService( serviceLocator );
		return new ProjectModelResolver( repositorySystemSession, requestTrace, repositorySystem, remoteRepositoryManager, remoteRepositories, ProjectBuildingRequest.RepositoryMerging.POM_DOMINANT, null );
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
		locator.addService( TransporterFactory.class, FileTransporterFactory.class );
		locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
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
