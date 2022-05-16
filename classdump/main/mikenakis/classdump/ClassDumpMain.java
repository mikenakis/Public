package mikenakis.classdump;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.printing.ByteCodePrinter;
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
 * ClassDump Launcher.
 *
 * @author michael.gr
 */
public class ClassDumpMain
{
	public static void main( String[] commandLineArguments )
	{
		Clio clio = new Clio( "classDump" );
		Supplier<Boolean> loop = clio.addOptionalSwitchArgument( "--loop", "run in an endless loop (for profiling.)" );
		Supplier<Optional<String>> outputFileName = clio.addOptionalStringNamedArgument( "--output | --out | -o", "output file, defaults to standard output. Useful for obtaining a class dump without classDump's own logging entries." );
		Supplier<Optional<String>> sourcesDirectory = clio.addOptionalStringNamedArgument( "--sources | -s", "the root directory of sources (for locating .java files)." );
		Supplier<Path> binariesDirectory = clio.addMandatoryPathPositionalArgument( "binaries-path", "root directory of binaries (for locating .class files) or path to single .class file to dump." );
		Supplier<Optional<String>> className = clio.addOptionalStringPositionalArgument( "class-name", "fully qualified class name (or package name, or prefix thereof) of class(es) to dump. (Default is to dump everything.)" );
		Supplier<Boolean> skipOptionalAttributes = clio.addOptionalSwitchArgument( "--skip-optional-attributes", "skip optional attributes" );
		if( !clio.parse( commandLineArguments ) )
		{
			System.out.println( "example: " + clio.programName + " --sources C:\\Users\\MBV\\Personal\\IdeaProjects\\mikenakis-personal\\public\\bytecode\\bytecode-test\\test C:\\Users\\MBV\\Out\\mikenakis\\bytecode-test\\test-classes bytecode_tests.model.Class1" );
			System.exit( -1 );
		}

		Kit.tryCatch( () ->
		{
			run( loop.get(), outputFileName.get(), sourcesDirectory.get(), binariesDirectory.get(), className.get(), skipOptionalAttributes.get() );
			System.exit( 0 );
		}, throwable ->	handleThrowable( throwable, clio.programName ) );
	}

	private static void handleThrowable( Throwable throwable, String programName )
	{
		if( throwable instanceof AssertionError assertionError && assertionError.getCause() != null )
			throwable = assertionError.getCause();
		if( throwable instanceof ApplicationException applicationException )
			System.err.println( programName + ": " + applicationException.getMessage() );
		else
			Log.error( throwable );
		System.exit( 1 );
	}

	private ClassDumpMain()
	{
	}

	private static void run( boolean loop, Optional<String> outputFileName, Optional<String> sourcesDirectory, Path binariesDirectory, Optional<String> className, boolean skipOptionalAttributes )
	{
		for( ; ; )
		{
			PrintStream printStream = getPrintStream( outputFileName );
			run1( printStream, sourcesDirectory, binariesDirectory, className, skipOptionalAttributes );
			if( printStream != System.out )
				printStream.close();
			if( !loop )
				break;
		}
	}

	private static void run1( PrintStream printStream, Optional<String> sources, Path binaries, Optional<String> className, boolean skipOptionalAttributes )
	{
		Optional<Path> sourcesRootPath = skipOptionalAttributes ? Optional.empty() : sources.map( s -> Paths.get( s ).toAbsolutePath().normalize() );
		if( binaries.toFile().isFile() )
		{
			if( className.isPresent() )
				throw new ApplicationException( "binaries-path '" + binaries.toString() + "' is a file, so class-name argument should not be given." );
			classDump( binaries, sourcesRootPath, printStream, skipOptionalAttributes );
		}
		else
		{
			assert className.isPresent();
			if( !binaries.toFile().exists() )
				throw new ApplicationException( "binaries-path '" + binaries + "' does not exist." );
			if( !binaries.toFile().isDirectory() )
				throw new ApplicationException( "binaries-path '" + binaries + "' is not a directory." );
			if( sourcesRootPath.isPresent() && !sourcesRootPath.get().toFile().isDirectory() )
				throw new ApplicationException( "Sources root path should be a directory" );
			String classFileName = className.get().replace( '.', '/' ) + ".class";
			String sourceFileName = className.get().replace( '.', '/' ) + ".java";
			Path classFilePath = binaries.resolve( classFileName );
			if( !classFilePath.toFile().exists() )
				throw new ApplicationException( "Class file " + classFilePath + " does not exist." );
			if( !classFilePath.toFile().isFile() )
				throw new ApplicationException( "Class file " + classFilePath + " is not a file." );
			sourcesRootPath.map( p1 -> p1.resolve( sourceFileName ) ).ifPresent( sourceFilePath ->//
			{
				if( !sourceFilePath.toFile().exists() )
					throw new ApplicationException( "Source file '" + sourceFilePath + "' does not exist." );
				if( !sourceFilePath.toFile().isFile() )
					throw new ApplicationException( "Source file '" + sourceFilePath + "' is not a file." );
			} );
			for( ; ; )
			{
				int i = className.get().indexOf( '.' );
				if( i == -1 )
					break;
				String packagePart = className.get().substring( 0, i );
				binaries = binaries.resolve( packagePart );
				sourcesRootPath = sourcesRootPath.map( p -> p.resolve( packagePart ) );
				className = Optional.of( className.get().substring( i + 1 ) );
				assert binaries.resolve( className.get().replace( '.', '/' ) + ".class" ).toFile().isFile();
				assert sourcesRootPath.isEmpty() || sourcesRootPath.get().resolve( className.get().replace( '.', '/' ) + ".java" ).toFile().isFile();
			}
			Path finalBinariesRootPath = binaries;
			Optional<Path> finalSourcesRootPath = sourcesRootPath;
			String finalClassName = className.get();
			Helpers.forEachFile( binaries, ".class", classFilePathName -> //
			{
				Path relativePath = finalBinariesRootPath.relativize( classFilePathName );
				String relativePathName = relativePath.toString();
				assert relativePathName.endsWith( ".class" );
				//relativePathName = relativePathName.substring( 0, relativePathName.length() - ".class".length() );
				if( !relativePathName.startsWith( finalClassName ) )
					return;
				Optional<Path> sourcePath = finalSourcesRootPath.isEmpty() ? Optional.empty() : Optional.of( resolveToParentOf( finalSourcesRootPath.get(), relativePath ) );
				classDump( classFilePathName, sourcePath, printStream, skipOptionalAttributes );
			} );
		}
	}

	private static Path resolveToParentOf( Path path, Path relativePath )
	{
		Path parent = relativePath.getParent();
		if( parent == null )
			return path;
		return path.resolve( parent );
	}

	private static void classDump( Path classFilePathName, Optional<Path> sourcePath, PrintStream printStream, boolean skipOptionalAttributes )
	{
		Log.debug( "Dumping " + classFilePathName + (sourcePath.isEmpty() ? "" : " (" + sourcePath.get() + ")") );
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) );
		ByteCodeType type = ByteCodeType.read( bytes );
		String text = ByteCodePrinter.printByteCodeType( type, sourcePath, skipOptionalAttributes );
		printStream.println( text );
	}

	private static PrintStream getPrintStream( Optional<String> outputFileName )
	{
		if( outputFileName.isEmpty() )
			return System.out;
		OutputStream outputStream = Kit.unchecked( () -> Files.newOutputStream( Paths.get( outputFileName.get() ) ) );
		outputStream = new BufferedOutputStream( outputStream, 1024 * 1024 );
		return new PrintStream( outputStream, true, StandardCharsets.UTF_8 );
	}
}
