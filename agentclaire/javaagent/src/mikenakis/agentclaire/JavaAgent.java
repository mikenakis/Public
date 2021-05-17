package mikenakis.agentclaire;

import mikenakis.bytecode.ByteCodeType;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.services.bytecode.ByteCodeService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * The Java Agent.
 * <p>
 * To make use of this Java Agent, launch your project with the following VM option:
 * <p>
 * {@code -javaagent:${OUT}/agentclaire-phat.jar=${OUT}/rumination-phat.jar}
 * <p>
 * (Assuming that this has been compiled into {@code agentclaire-phat.jar}, that you want to use an interceptor called {@code rumination-phat.jar}, and that {@code ${OUT}} points to the output path.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class JavaAgent implements ClassFileTransformer
{
	public static void premain( String args, Instrumentation instrumentation )
	{
		List<String> arguments = Kit.string.splitAtCharacter( args, ';' );
		JavaAgent javaAgent = new JavaAgent( instrumentation, arguments );
		if( Kit.get( false ) )
			javaAgent.retransformLoadedClasses();
	}

	private static String getAttributeValue( Attributes attributes, String name ) throws Exception
	{
		String result = attributes.getValue( name );
		if( result == null )
			throw new Exception( "entry not found: " + name );
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final AgentClaire agentClaire = new AgentClaire()
	{
		@Override public void registerInterceptor( boolean register, Interceptor interceptor )
		{
			Kit.collection.addOrRemove( interceptors, register, interceptor );
		}
	};

	private final Instrumentation instrumentation;
	private final Collection<Interceptor> interceptors = new HashSet<>();

	private JavaAgent( Instrumentation instrumentation, Iterable<String> arguments )
	{
		assert instrumentation.isRedefineClassesSupported();
		assert instrumentation.isRetransformClassesSupported();
		this.instrumentation = instrumentation;
		instrumentation.addTransformer( this, true );
		loadInterceptors( arguments );
	}

	private void loadInterceptors( Iterable<String> jarFileNames )
	{
		ClassLoader parentClassLoader = JavaAgent.class.getClassLoader();
		assert parentClassLoader != null;
		for( var jarFileName : jarFileNames )
		{
			Log.debug( "Opening jar: " + jarFileName );
			Optional<String> className = getJarFileManifestEntry( jarFileName, "AgentClaire-Interceptor-Class" );
			if( className.isEmpty() )
				continue;
			Optional<Constructor<Object>> constructor = tryGetConstructorFromJarFile( jarFileName, className.get(), AgentClaire.class );
			if( constructor.isEmpty() )
				continue;
			createObject( constructor.get(), agentClaire );
		}
	}

	void retransformLoadedClasses()
	{
		Log.debug( "Retransforming loaded classes..." );
		Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
		for( Class<?> loadedClass : loadedClasses )
		{
			if( !instrumentation.isModifiableClass( loadedClass ) )
				continue;
			Kit.unchecked( () -> instrumentation.retransformClasses( loadedClass ) );
		}
		Log.debug( "done retransforming loaded classes." );
	}

	@Override public byte[] transform( ClassLoader classLoader, String jvmInternalClassName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, //
		byte[] bytes )
	{
		try
		{
			String className = jvmInternalClassName.replace( '/', '.' );
			ByteCodeType byteCodeType = ByteCodeService.instance.tryGetOrCreateByteCodeType( Optional.ofNullable( classLoader ), className, //
				Optional.of( bytes ) ).orElseThrow();
			if( classLoader != null )
			{
				boolean modified = transform( classLoader, byteCodeType );
				if( modified )
					return byteCodeType.getBuffer().getBytes();
			}
		}
		catch( Throwable throwable )
		{
			Log.debug( "Exception " + throwable.getClass().getName() + ": " + throwable.getMessage() );
		}
		return bytes;
	}

	private boolean transform( ClassLoader classLoader, ByteCodeType byteCodeType )
	{
		boolean modified = false;
		for( Interceptor interceptor : interceptors )
			modified |= interceptor.intercept( byteCodeType, classLoader );
		return modified;
	}

	private Optional<String> getJarFileManifestEntry( String jarFileName, String entryName )
	{
		try
		{
			JarFile jarFile = new JarFile( jarFileName );
			Manifest manifest = jarFile.getManifest();
			return Optional.of( getAttributeValue( manifest.getMainAttributes(), entryName ) );
		}
		catch( Exception e )
		{
			Log.error( "Could not open '" + jarFileName + "': " + e.getClass().getName() + ": " + e.getMessage() );
			return Optional.empty();
		}
	}

	private <T> Optional<Constructor<T>> tryGetConstructorFromJarFile( String jarFileName, String className, Class<?>... constructorArgumentTypes )
	{
		ClassLoader parentClassLoader = getClass().getClassLoader();
		Path path = Paths.get( jarFileName ).toAbsolutePath();
		URL[] urls = { Kit.unchecked( () -> path.toUri().toURL() ) };
		@SuppressWarnings( "ClassLoaderInstantiation" ) URLClassLoader jarClassLoader = new URLClassLoader( jarFileName, urls, parentClassLoader );
		Class<?> jvmClass;
		try
		{
			jvmClass = Class.forName( className, true, jarClassLoader );
		}
		catch( ClassNotFoundException e )
		{
			Log.error( "Could not find '" + className + "' in '" + jarFileName + "'" );
			return Optional.empty();
		}

		@SuppressWarnings( "unchecked" )
		Class<T> interceptorClass = (Class<T>)jvmClass;
		Log.debug( "Found interceptor: " + interceptorClass.getName() );
		try
		{
			return Optional.of( interceptorClass.getDeclaredConstructor( constructorArgumentTypes ) );
		}
		catch( NoSuchMethodException e )
		{
			Log.error( "Class '" + className + "' in '" + jarFileName + "' does not have an appropriate constructor." );
			return Optional.empty();
		}
	}

	private static <T> Optional<T> createObject( Constructor<T> constructor, Object... arguments )
	{
		try
		{
			return Optional.of( constructor.newInstance( arguments ) );
		}
		catch( InvocationTargetException e )
		{
			Class<T> declaringClass = constructor.getDeclaringClass();
			String className = declaringClass.getName();
			String jarFileName = declaringClass.getClassLoader().getName();
			Log.error( getMessage( "Constructor of class '" + className + "' (" + jarFileName + ") threw an exception: ", e ) );
			return Optional.empty();
		}
		catch( InstantiationException | IllegalAccessException e )
		{
			Class<T> declaringClass = constructor.getDeclaringClass();
			String className = declaringClass.getName();
			String jarFileName = declaringClass.getClassLoader().getName();
			Log.error( "Class '" + className + "' (" + jarFileName + ") could not be instantiated: " + e );
			return Optional.empty();
		}
	}

	private static String getMessage( String message, Throwable throwable )
	{
		StringWriter stringWriter = new StringWriter();
		stringWriter.append( message );
//        stringWriter.append( "\n" );
//        stringWriter.append( throwable.getClass().getName() );
//        stringWriter.append( ": " );
//        stringWriter.append( throwable.getMessage() );
//        stringWriter.append( "\n" );
		PrintWriter printWriter = new PrintWriter( stringWriter );
		throwable.printStackTrace( printWriter );
		return stringWriter.toString();
	}
}
