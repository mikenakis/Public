package mikenakis.bytecode.test;

import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.bytecode.test.kit.TestKit;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T004_Printing
{
	public T004_Printing()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	private static ByteCodeType load( Path classFilePathName )
	{
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) );
		return ByteCodeReader.read( bytes );
	}

	@Test public void Printing_Is_Ok()
	{
		Path workingDirectory = Kit.path.getWorkingDirectory();
		System.out.printf( Locale.ROOT, "Current directory: %s\n", workingDirectory );
		Path outputPath = Helpers.getOutputPath( getClass() );
		Path modelPath = outputPath.resolve( "model" );
		Path printPath = workingDirectory.resolve( "prints" );
		List<Path> classFilePathNames = TestKit.collectResourcePaths( modelPath, false, ".class" );
		assert classFilePathNames.size() == 20;
		int mismatchCount = 0;
		for( Path classFilePathName : classFilePathNames )
		{
			Path printFilePathName = getPrintFilePathNameFromClassFilePathName( outputPath, classFilePathName, printPath );
			if( printAndCompareAgainstExpected( classFilePathName, printFilePathName ) )
				mismatchCount++;
		}
		assert mismatchCount == 0;
	}

	private static boolean printAndCompareAgainstExpected( Path classFilePathName, Path printFilePathName )
	{
		System.out.printf( Locale.ROOT, "Comparing class: %s <=> print: %s\n", classFilePathName, printFilePathName );
		ByteCodeType byteCodeType = load( classFilePathName );
		String actualPrint = ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() );
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

	private static Path getPrintFilePathNameFromClassFilePathName( Path basePath, Path classFilePathName, Path printPath )
	{
		String s = classFilePathName.toString();
		s = replacePrefix( s, basePath.resolve( "model" ).toString(), printPath.toString() );
		s = replaceSuffix( s, ".class", ".print" );
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
