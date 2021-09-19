package mikenakis.bytecode.test;

import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.bytecode.test.kit.TestKit;
import mikenakis.bytecode.test.model.Class1;
import mikenakis.bytecode.test.model.Enum1;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.bytecode.writing.ByteCodeWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T201_Writing
{
	public T201_Writing()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@Test public void Class_Checks_Out()
	{
		Path path = Helpers.getPathToClassFile( Class1.class );
		test( path );
	}

	@Test public void Enum_Checks_Out()
	{
		Path path = Helpers.getPathToClassFile( Enum1.class );
		test( path );
	}

	@Test public void Multiple_Classes_Checks_Out()
	{
		Path rootPath = Helpers.getPathToResource( getClass(), getClass().getSimpleName() + ".class" ).getParent().resolve( "model" );
		List<Path> classFilePathNames = TestKit.collectResourcePaths( rootPath, false, ".class" );
		for( Path classFilePathName : classFilePathNames )
			test( classFilePathName );
	}

	private static void test( Path classFilePathName )
	{
		//		Log.debug( "Testing " + classFilePathName );
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) );
		ByteCodeType type = ByteCodeReader.read( bytes );
		String string1 = ByteCodePrinter.printByteCodeType( type, Optional.empty() );

		byte[] bytes2 = ByteCodeWriter.write( type );
		Path classFilePathName2 = getGeneratedClassFilePath( classFilePathName );
		Kit.unchecked( () -> Files.createDirectories( classFilePathName2.getParent() ) );
		Kit.unchecked( () -> Files.write( classFilePathName2, bytes2 ) );
		Log.debug( "saved " + classFilePathName2 );
		ByteCodeType type2 = ByteCodeReader.read( bytes2 );
		String string2 = ByteCodePrinter.printByteCodeType( type2, Optional.empty() );
		assert string1.equals( string2 );
	}

	private static Path getGeneratedClassFilePath( Path path )
	{
		String child = path.getFileName().toString();
		return path.getParent().resolve( "r" ).resolve( child );
	}
}
