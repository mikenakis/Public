package mikenakis.testana.discovery.maven;

import mikenakis.kit.Kit;
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
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transport.file.FileTransporterFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

final class MavenHelper
{
	private final ModelResolver mavenModelResolver;
	private final ModelBuilder mavenModelBuilder = new DefaultModelBuilderFactory().newInstance();

	MavenHelper( Path localRepositoryPath )
	{
		mavenModelResolver = createMavenModelResolver( localRepositoryPath );
	}

	private static ModelResolver createMavenModelResolver( Path localRepositoryPath )
	{
		ServiceLocator serviceLocator = newMavenServiceLocator();
		RepositorySystem system = serviceLocator.getService( RepositorySystem.class );
		RepositorySystemSession session = newMavenRepositorySystemSession( system, localRepositoryPath );
		RemoteRepositoryManager remoteRepositoryManager = serviceLocator.getService( RemoteRepositoryManager.class );
		List<RemoteRepository> repos = List.of( new RemoteRepository.Builder( "central", "default", "https://repo.maven.apache.org/maven2/" ).build() );
		RequestTrace requestTrace = new RequestTrace( null );
		DefaultRepositorySystem repositorySystem = new DefaultRepositorySystem();
		repositorySystem.initService( serviceLocator );
		return new ProjectModelResolver( session, requestTrace, repositorySystem, remoteRepositoryManager, repos, ProjectBuildingRequest.RepositoryMerging.POM_DOMINANT, null );
	}

	private static RepositorySystemSession newMavenRepositorySystemSession( RepositorySystem system, Path localRepositoryPath )
	{
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		LocalRepository localRepo = new LocalRepository( localRepositoryPath.toFile() );
		session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );
		return session;
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
		reportMavenModelProblems( modelBuildingResult.getProblems() );
		return modelBuildingResult.getEffectiveModel();
	}

	private static Properties getUserProperties()
	{
		Properties properties = new Properties();
		Kit.map.add( properties, "env.OUT", System.getenv( "OUT" ) );
		return properties;
	}

	private static void reportMavenModelProblems( Iterable<ModelProblem> mavenModelProblems )
	{
		for( var mavenModelProblem : mavenModelProblems )
			reportMavenModelProblem( mavenModelProblem );
	}

	private static void reportMavenModelProblem( ModelProblem mavenModelProblem )
	{
		Log.Level level = switch( mavenModelProblem.getSeverity() )
			{
				case FATAL, ERROR -> Log.Level.ERROR;
				case WARNING -> Log.Level.WARN;
			};
		Log.message( level, mavenModelProblem.getMessage() );
	}

	private static ServiceLocator newMavenServiceLocator()
	{
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.setErrorHandler( new DefaultServiceLocator.ErrorHandler()
		{
			@Override public void serviceCreationFailed( Class<?> type, Class<?> impl, Throwable exception )
			{
				sneakyThrow( exception );
			}
		} );
		locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
		locator.addService( TransporterFactory.class, FileTransporterFactory.class );
		locator.addService( ProjectBuildingHelper.class, DefaultProjectBuildingHelper.class );
		locator.addService( ProjectBuilder.class, DefaultProjectBuilder.class );
		return locator;
	}

	@SuppressWarnings( "unchecked" ) private static <T extends Exception> RuntimeException sneakyThrow( Throwable t ) throws T
	{
		throw (T)t;
	}
}
