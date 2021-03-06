package io.github.mikenakis.bytecode.test;

import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.printing.ByteCodePrinter;
import io.github.mikenakis.bytecode.test.model.Model;
import io.github.mikenakis.debug.Debug;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * test.
 *
 * @author michael.gr
 */
public class T301_Printing
{
	public T301_Printing()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	private static ByteCodeType load( Path classFilePathName )
	{
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) );
		return ByteCodeType.read( bytes );
	}

	@Test public void Printing_Checks_Out()
	{
		Path workingDirectory = Kit.path.getWorkingDirectory();
		Log.debug( "Current directory: " + workingDirectory );
		Path printsPath = workingDirectory.resolve( "prints" );
		Path sourcesPath = workingDirectory.resolve( "test" ).resolve( getClass().getPackageName().replace( '.', '/' ) ).resolve( "model" );
		List<Path> classFilePathNames = Model.getClassFilePathNames();
		int mismatchCount = 0;
		for( Path classFilePathName : classFilePathNames )
		{
			Path printFilePathName = getPrintFilePathNameFromClassFilePathName( classFilePathName, printsPath, ".print" );
			if( printAndCompareAgainstExpected( classFilePathName, printFilePathName, sourcesPath ) )
			{
				Log.debug( "mismatch: " + printFilePathName );
				mismatchCount++;
			}
		}

		// a "mismatch" means that the test detected some difference between the output of the printer and one or more existing .print files.
		// the .print file has already been overwritten with the new output of the printer, so next time the test runs no mismatch will be detected.
		// The file comparison feature of the source control system can be used to see what has changed.
		// We use an assertion to signal that a mismatch has been detected because we have no other means of alerting the programmer
		// from within a test. (Nobody looks at the log. Testana even hides the log.)
		if( mismatchCount != 0 ) //this is not an error! It just means that the output of the printer has changed since the last run of the test.
		{
			Log.debug( "files changed in " +  printsPath );
			Debug.breakPoint();
		}
	}

	private static boolean printAndCompareAgainstExpected( Path classFilePathName, Path printFilePathName, Path sourcesPath )
	{
		Log.debug( "Comparing class: " + classFilePathName + "  <=>  print: " + printFilePathName );
		ByteCodeType byteCodeType = load( classFilePathName );
		String actualPrint = ByteCodePrinter.printByteCodeType( byteCodeType, Optional.of( sourcesPath ) );
		Kit.unchecked( () -> Files.createDirectories( printFilePathName.getParent() ) );
		boolean mismatch;
		boolean needSave;
		if( !Files.exists( printFilePathName ) )
		{
			needSave = true;
			mismatch = false;
		}
		else
		{
			String expectedPrint = Kit.unchecked( () -> Files.readString( printFilePathName ) );
			mismatch = !actualPrint.equals( expectedPrint );
			needSave = mismatch;
		}
		if( needSave )
			Kit.unchecked( () -> Files.writeString( printFilePathName, actualPrint ) );
		return mismatch;
	}

	private static Path getPrintFilePathNameFromClassFilePathName( Path classFilePathName, Path printsPath, String extension )
	{
		String s = classFilePathName.toString();
		s = replacePrefix( s, classFilePathName.getParent().toString(), printsPath.toString() );
		s = replaceSuffix( s, ".class", extension );
		return Path.of( s );
	}

	private static String replacePrefix( String s, String prefix, String replacement )
	{
		assert s.startsWith( prefix );
		return replacement + s.substring( prefix.length() );
	}

	private static String replaceSuffix( String s, String suffix, String replacement )
	{
		assert s.endsWith( suffix );
		return s.substring( 0, s.length() - suffix.length() ) + replacement;
	}
}
