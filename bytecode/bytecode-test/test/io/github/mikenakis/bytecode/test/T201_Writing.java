package io.github.mikenakis.bytecode.test;

import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.printing.ByteCodePrinter;
import io.github.mikenakis.bytecode.test.model.Class1WithFields;
import io.github.mikenakis.bytecode.test.model.Enum1;
import io.github.mikenakis.bytecode.test.model.Model;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.logging.Log;
import io.github.mikenakis.testkit.TestKit;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

/**
 * test.
 *
 * @author michael.gr
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
		Path path = TestKit.getPathToClassFile( Class1WithFields.class );
		test( path );
	}

	@Test public void Enum_Checks_Out()
	{
		Path path = TestKit.getPathToClassFile( Enum1.class );
		test( path );
	}

	@Test public void Multiple_Classes_Checks_Out()
	{
		for( Path classFilePathName : Model.getClassFilePathNames() )
			test( classFilePathName );
	}

	private static final boolean save = Kit.get( false ); //saving is only useful when troubleshooting.

	private static void test( Path classFilePathName )
	{
		Log.debug( "Testing " + classFilePathName );
		byte[] bytes1 = Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) );
		ByteCodeType type1 = ByteCodeType.read( bytes1 );
		String string1 = ByteCodePrinter.printByteCodeType( type1, Optional.empty() );
		if( save )
			savePrint( replaceExtension( classFilePathName.getParent().resolve( "p1" ).resolve( classFilePathName.getFileName() ), ".print" ), string1 );

		byte[] bytes2 = type1.write();
		if( save )
			saveBytes( classFilePathName.getParent().resolve( "b" ).resolve( classFilePathName.getFileName().toString() ), bytes2 );
		ByteCodeType type2 = ByteCodeType.read( bytes2 );
		String string2 = ByteCodePrinter.printByteCodeType( type2, Optional.empty() );
		if( save )
			savePrint( replaceExtension( classFilePathName.getParent().resolve( "p2" ).resolve( classFilePathName.getFileName() ), ".print" ), string2 );
		assert string1.equals( string2 );
	}

	private static void saveBytes( Path path, byte[] bytes )
	{
		Kit.unchecked( () -> Files.createDirectories( path.getParent() ) );
		Kit.unchecked( () -> Files.write( path, bytes ) );
		Log.debug( "saved bytes: " + path );
	}

	private static void savePrint( Path path, String print )
	{
		Kit.unchecked( () -> Files.createDirectories( path.getParent() ) );
		Kit.unchecked( () -> Files.writeString( path, print, StandardCharsets.UTF_8, StandardOpenOption.CREATE ) );
		Log.debug( "saved print: " + path );
	}

	private static String getBaseName( Path path )
	{
		String pathName = path.toString();
		int index = pathName.lastIndexOf( '.' );
		return index == -1 ? pathName : pathName.substring( 0, index );
	}

	private static Path replaceExtension( Path path, String extension )
	{
		return path.getParent().resolve( getBaseName( path ) + extension );
	}
}
