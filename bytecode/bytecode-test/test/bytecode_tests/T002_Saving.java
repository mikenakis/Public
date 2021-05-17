package bytecode_tests;

import bytecode_tests.kit.TestKit;
import bytecode_tests.model.Class1;
import bytecode_tests.model.Enum1;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.dumping.Style;
import mikenakis.bytecode.dumping.printers.ByteCodePrinter;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T002_Saving
{
	public T002_Saving()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@Test
	public void Class_Checks_Out()
	{
		Path path = Helpers.getPathToClassFile( Class1.class );
		test( path );
	}

	@Test
	public void Enum_Checks_Out()
	{
		Path path = Helpers.getPathToClassFile( Enum1.class );
		test( path );
	}

	@Test
	public void Multiple_Classes_Check_Out()
	{
		Path rootPath = Helpers.getPathToResource( getClass(), getClass().getSimpleName() + ".class" ).getParent().resolve( "model" );
		List<Path> classFilePathNames = TestKit.collectResourcePaths( rootPath, ".class" );
		for( Path classFilePathName : classFilePathNames )
			test( classFilePathName );
	}

	private static void test( Path classFilePathName )
	{
//		Log.debug( "Testing " + classFilePathName );
		Buffer buffer1 = Buffer.create( Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) ) );
		verify( buffer1, classFilePathName );
	}

	private static void verify( Buffer buffer, Path classFilePathName )
	{
		ByteCodeType type0 = ByteCodeType.create( buffer, classFilePathName );
		ByteCodePrinter byteCodePrinter0 = new ByteCodePrinter( type0 );
		@SuppressWarnings( "unused" )
		String sBefore0 = byteCodePrinter0.toLongString( Style.MIXED );
		@SuppressWarnings( "unused" )
		String sAfter0 = byteCodePrinter0.toLongString( Style.MIXED );

		ByteCodeType type = ByteCodeType.create( buffer, classFilePathName );
		ByteCodePrinter byteCodePrinter = new ByteCodePrinter( type );
		String s1 = byteCodePrinter.toLongString( Style.GILDED_ONLY );
		String s2 = byteCodePrinter.toLongString( Style.GILDED_ONLY );
		assert s1.equals( s2 );
		String sBefore1 = byteCodePrinter.toLongString( Style.MIXED );
		String sBefore2 = byteCodePrinter.toLongString( Style.MIXED );
		assert sBefore1.equals( sBefore2 );
		type.rebuild();
		String sAfter1 = byteCodePrinter.toLongString( Style.MIXED );
		String sAfter2 = byteCodePrinter.toLongString( Style.MIXED );
		assert sAfter1.equals( sAfter2 );
		String s3 = byteCodePrinter.toLongString( Style.GILDED_ONLY );
		String s4 = byteCodePrinter.toLongString( Style.GILDED_ONLY );
		assert s3.equals( s4 );
		assert s1.equals( s3 );
		Buffer buffer2 = type.getBuffer();
		ByteCodeType type2 = ByteCodeType.create( buffer2, classFilePathName );
		ByteCodePrinter byteCodePrinter2 = new ByteCodePrinter( type2 );
		String s5 = byteCodePrinter2.toLongString( Style.GILDED_ONLY );
		assert s1.equals( s5 );
	}
}
