package mikenakis.testkit;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public final class TestKit
{
	private TestKit() { }

	public static <T extends Throwable> T expect( Class<T> expectedThrowableClass, Procedure0 procedure )
	{
		assert expectedThrowableClass != null;
		assert procedure != null;
		Throwable caughtThrowable = invokeAndCatch( procedure );
		assert caughtThrowable != null : new ExceptionExpectedException( expectedThrowableClass );
		caughtThrowable = unwrap( caughtThrowable );
		assert caughtThrowable.getClass() == expectedThrowableClass : new ExceptionDiffersFromExpectedException( expectedThrowableClass, caughtThrowable );
		return expectedThrowableClass.cast( caughtThrowable );
	}

	private static Throwable unwrap( Throwable throwable )
	{
		for( ; ; )
		{
			if( throwable instanceof AssertionError && throwable.getCause() != null )
			{
				throwable = throwable.getCause();
				continue;
			}
			break;
		}
		return throwable;
	}

	private static Throwable invokeAndCatch( Procedure0 procedure )
	{
		assert !Kit.expectingException;
		Kit.expectingException = true;
		try
		{
			procedure.invoke();
			return null;
		}
		catch( Throwable throwable )
		{
			return throwable;
		}
		finally
		{
			assert Kit.expectingException;
			Kit.expectingException = false;
		}
	}

	public static PrintStream nullPrintStream()
	{
		return new PrintStream( OutputStream.nullOutputStream() );
	}

	public static String withCapturedOutputStream( Procedure0 procedure )
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream( byteArrayOutputStream );
		PrintStream oldSystemOut = System.out;
		System.setOut( printStream );
		Kit.tryFinally( procedure, () -> System.setOut( oldSystemOut ) );
		return byteArrayOutputStream.toString( StandardCharsets.UTF_8 );
	}

	public static List<Path> collectResourcePaths( Path rootPath, boolean recursive, String suffix )
	{
		List<Path> result = new ArrayList<>();
		Kit.unchecked( () -> Files.walkFileTree( rootPath, new SimpleFileVisitor<>()
		{
			@Override public FileVisitResult visitFile( Path filePath, BasicFileAttributes attrs )
			{
				if( filePath.toString().endsWith( suffix ) )
					result.add( filePath );
				return FileVisitResult.CONTINUE;
			}

			@Override public FileVisitResult preVisitDirectory( Path directoryPath, BasicFileAttributes attrs )
			{
				return directoryPath.equals( rootPath ) || recursive ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
			}
		} ) );
		return result;
	}

	private static Path getPathToResource( Class<?> javaClass, String resourceName )
	{
		URL url = javaClass.getResource( resourceName );
		assert url != null : resourceName;
		return Kit.classLoading.getPathFromUrl( url );
	}

	public static Path getPathToClassFile( Class<?> javaClass )
	{
		int packageNameLength = javaClass.getPackageName().length();
		String name = javaClass.getName().substring( packageNameLength + 1 ) + ".class";
		return getPathToResource( javaClass, name );
	}

	public static String readFile( Path path )
	{
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( path ) );
		return new String( bytes );
	}

	public static boolean filesAreEqual( Path path1, Path path2 )
	{
		String content1 = readFile( path1 );
		String content2 = readFile( path2 );
		return content1.equals( content2 );
	}

	public static String getMethodName()
	{
		return getMethodName( 1 );
	}

	public static String getMethodName( int framesToSkip )
	{
		StackWalker.StackFrame stackFrame = Kit.getStackFrame( framesToSkip + 1 );
		return stackFrame.getMethodName();
	}
}
