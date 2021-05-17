package mikenakis.classdump;

import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.dumping.Style;
import mikenakis.bytecode.dumping.printers.ByteCodePrinter;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.classdump.kit.Helpers;
import mikenakis.clio.Clio;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Testana Launcher.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ClassDumpMain
{
	public static void main( String[] commandLineArguments )
	{
		Clio clio = new Clio( "classDump" );
		Supplier<Style> styleOption = clio.addEnumParameterOption( "--style", "display-style", Style.class, Style.RAW_ONLY, "the style of the output." );
		Supplier<Boolean> loopOption = clio.addSwitchOption( "--loop", "run in an endless loop (for profiling.)" );
		Supplier<String> outputOption = clio.addStringParameterOption( "--output | --out | -o", "output-file", "", "output file, defaults to standard output." );
		Supplier<String> sourcesRootDirectoryOption = clio.addStringParameterOption( "--sources | -s", "sources-root-directory", "", "the root directory of sources (for locating .java files)." );
		Supplier<Path> binariesRootPathArgument = clio.addPathPositionalOption( "binaries-root-directory", "the root directory of binaries (for locating .class files) or path to single .class file to dump." );
		Supplier<String> classArgument = clio.addStringPositionalOption( "class-name", "", "fully qualified class name (or package name, or prefix thereof) of class(es) to dump. (Default is to dump everything under the binaries root directory.)" );
		if( !clio.parse( commandLineArguments ) )
			System.exit( -1 );

		ClassDumpMain classDumpMain = new ClassDumpMain();
		try
		{
			int result = classDumpMain.run( styleOption.get(), loopOption.get(), outputOption.get(),
				sourcesRootDirectoryOption.get(), binariesRootPathArgument.get(),
				classArgument.get() );
			System.exit( result );
		}
		catch( Throwable throwable )
		{
			System.err.println( clio.programName + ": " + throwable.getMessage() );
		}
	}

	public ClassDumpMain()
	{
	}

	private int run( Style style, boolean loop, String output, String sourcesRootDirectory, Path binariesRootPath, String className )
	{
		for( ; ; )
		{
			PrintStream printStream = getPrintStream( output );

			Optional<Path> sourcesRootPath = sourcesRootDirectory.isEmpty() ? Optional.empty() : Optional.of( Paths.get( sourcesRootDirectory ).toAbsolutePath().normalize() );
			if( binariesRootPath.toFile().isFile() )
			{
				if( !className.isEmpty() )
				{
					System.out.println( "target is a file, so class argument should be omitted." );
					return -1;
				}
				classDump( binariesRootPath, sourcesRootPath, style, printStream );
			}
			else
			{
				assert binariesRootPath.toFile().isDirectory();
				assert sourcesRootPath.isEmpty() || sourcesRootPath.get().toFile().isDirectory();
				assert binariesRootPath.resolve( className.replace( '.', '/' ) + ".class" ).toFile().isFile();
				assert sourcesRootPath.isEmpty() || sourcesRootPath.get().resolve( className.replace( '.', '/' ) + ".java" ).toFile().isFile();
				for( ; ; )
				{
					int i = className.indexOf( '.' );
					if( i == -1 )
						break;
					String packagePart = className.substring( 0, i );
					binariesRootPath = binariesRootPath.resolve( packagePart );
					sourcesRootPath = sourcesRootPath.isEmpty() ? Optional.empty() : Optional.of( sourcesRootPath.get().resolve( packagePart ) );
					className = className.substring( i + 1 );
					assert binariesRootPath.resolve( className.replace( '.', '/' ) + ".class" ).toFile().isFile();
					assert sourcesRootPath.isEmpty() || sourcesRootPath.get().resolve( className.replace( '.', '/' ) + ".java" ).toFile().isFile();
				}
				Path finalBinariesRootPath = binariesRootPath;
				Optional<Path> finalSourcesRootPath = sourcesRootPath;
				String finalClassName = className;
				Helpers.forEachFile( binariesRootPath, ".class", classFilePathName ->
				{
					Path relativePath = finalBinariesRootPath.relativize( classFilePathName );
					String relativePathName = relativePath.toString();
					assert relativePathName.endsWith( ".class" );
					//relativePathName = relativePathName.substring( 0, relativePathName.length() - ".class".length() );
					if( !relativePathName.startsWith( finalClassName ) )
						return;
					Optional<Path> sourcePath = finalSourcesRootPath.isEmpty() ? Optional.empty() : Optional.of( resolveToParentOf( finalSourcesRootPath.get(), relativePath ) );
					classDump( classFilePathName, sourcePath, style, printStream );
				} );
			}

			if( printStream != System.out )
				printStream.close();

			if( !loop )
				break;
		}

		return 0;
	}

	private static Path resolveToParentOf( Path path, Path relativePath )
	{
		Path parent = relativePath.getParent();
		if( parent == null )
			return path;
		return path.resolve( parent );
	}

	private void classDump( Path classFilePathName, Optional<Path> sourcePath, Style style, PrintStream printStream )
	{
		Log.debug( "Dumping " + classFilePathName + (sourcePath.isEmpty() ? "" : " (" + sourcePath + ")") );
		ByteCodeType type = ByteCodeType.create( classFilePathName, sourcePath );
		ByteCodePrinter byteCodePrinter = new ByteCodePrinter( type );
		String text = byteCodePrinter.toLongString( style );
		printStream.println( text );
		verify( type );
	}

	private static void verify( ByteCodeType type )
	{
		ByteCodePrinter byteCodePrinter = new ByteCodePrinter( type );
		String s1 = byteCodePrinter.toLongString( Style.GILDED_ONLY );
		String s2 = byteCodePrinter.toLongString( Style.GILDED_ONLY );
		assert s2.equals( s1 );
		//noinspection unused
		String sBefore = byteCodePrinter.toLongString( Style.MIXED );
		type.rebuild();
		//noinspection unused
		String sAfter = byteCodePrinter.toLongString( Style.MIXED );
		String s3 = byteCodePrinter.toLongString( Style.GILDED_ONLY );
		assert s3.equals( s1 );
		Buffer buffer2 = type.getBuffer();
		ByteCodeType type2 = ByteCodeType.create( buffer2, type.classFilePathName, type.sourcePath );
		ByteCodePrinter byteCodePrinter2 = new ByteCodePrinter( type2 );
		String s4 = byteCodePrinter2.toLongString( Style.GILDED_ONLY );
		assert s4.equals( s1 );
	}

	private static PrintStream getPrintStream( String output )
	{
		if( output.isEmpty() )
			return System.out;
		OutputStream outputStream = Kit.unchecked( () -> Files.newOutputStream( Paths.get( output ) ) );
		outputStream = new BufferedOutputStream( outputStream, 1024 * 1024 );
		return new PrintStream( outputStream, true, StandardCharsets.UTF_8 );
	}
}
