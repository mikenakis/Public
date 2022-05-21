package io.github.mikenakis.testkit;

import io.github.mikenakis.kit.Kit;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestInfo
{
	public static TestInfo of( Class<?> testClass, String testSourceRoot )
	{
		return of( testClass, testSourceRoot, 1 );
	}

	public static TestInfo of( Class<?> testClass, String testSourceRoot, int framesToSkip )
	{
		StackWalker.StackFrame stackFrame = Kit.getStackFrame( framesToSkip + 1 );
		String testMethodName = stackFrame.getMethodName();
		return new TestInfo( testSourceRoot, testClass, testMethodName );
	}

	private final String testSourceRoot;
	private final Class<?> testClass;
	private final String testMethodName;

	private TestInfo( String testSourceRoot, Class<?> testClass, String testMethodName )
	{
		this.testSourceRoot = testSourceRoot;
		this.testClass = testClass;
		this.testMethodName = testMethodName;
	}

	public Path getTestOutputDataPath()
	{
		String testClassName = getClassName( testSourceRoot, testClass );
		return getTestPath( Kit.path.getWorkingDirectory().toString(), "TestOutputData", testClassName + "." + testMethodName );
	}

	public Path getTestTempPath()
	{
		String testClassName = testClass.getName();
		return getTestPath( System.getProperty( "java.io.tmpdir" ), "TestTemp", testClassName + "." + testMethodName );
	}

	private static String getClassName( String modulePrefix, Class<?> testClass )
	{
		String testClassName = testClass.getName();
		assert testClassName.startsWith( modulePrefix );
		return testClassName.substring( modulePrefix.length() + 1 );
	}

	private static Path getTestPath( String... suffixes )
	{
		return getTestPath( List.of( suffixes ) );
	}

	private static Path getTestPath( List<String> suffixes )
	{
		var builder = new StringBuilder();
		for( int i = 0; i < suffixes.size() - 1; i++ )
		{
			if( !builder.isEmpty() && builder.charAt( builder.length() - 1 ) != File.separatorChar )
				builder.append( File.separatorChar );
			builder.append( suffixes.get( i ) );
		}
		String pathName = builder.toString();
		if( !suffixes.isEmpty() )
			suffixes = List.of( suffixes.get( suffixes.size() - 1 ) );
		return Paths.get( pathName, suffixes.toArray( String[]::new ) );
	}
}
