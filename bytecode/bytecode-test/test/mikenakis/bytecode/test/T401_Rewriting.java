package mikenakis.bytecode.test;

import mikenakis.bytecode.ByteCodeClassLoader;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.bytecode.test.kit.TestKit;
import mikenakis.bytecode.test.model.Class0HelloWorld;
import mikenakis.bytecode.writing.ByteCodeWriter;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T401_Rewriting
{
	public T401_Rewriting()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@Test public void Reading_And_Loading_Compiler_Generated_Class_Works()
	{
		Path classFilePathName = TestKit.getPathToClassFile( Class0HelloWorld.class );
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) );
		ByteCodeType byteCodeType = ByteCodeReader.read( bytes );
		ByteCodeClassLoader byteCodeClassLoader = new ByteCodeClassLoader();
		Class<?> javaClass = byteCodeClassLoader.load( byteCodeType );
		testHelloWorldJavaClass( javaClass );
	}

	@Test public void Reading_Writing_And_Loading_Compiler_Generated_Class_Works()
	{
		Path classFilePathName = TestKit.getPathToClassFile( Class0HelloWorld.class );

		ByteCodeType byteCodeType1 = ByteCodeReader.read( Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) ) );
		byte[] bytes = ByteCodeWriter.write( byteCodeType1 );
		ByteCodeType byteCodeType2 = ByteCodeReader.read( bytes );

		String text1 = ByteCodePrinter.printByteCodeType( byteCodeType1, Optional.empty() );
		String text2 = ByteCodePrinter.printByteCodeType( byteCodeType2, Optional.empty() );
		assert text1.equals( text2 );

		ByteCodeClassLoader byteCodeClassLoader = new ByteCodeClassLoader();
		Class<?> javaClass = byteCodeClassLoader.load( byteCodeType2 );
		testHelloWorldJavaClass( javaClass );
	}

	private static void testHelloWorldJavaClass( Class<?> javaClass )
	{
		Method mainMethod = Kit.unchecked( () -> javaClass.getDeclaredMethod( "main", String[].class ) );
		//Object instance = Kit.newInstance( javaClass );
		String output = Kit.testing.withCapturedOutputStream( //
			() -> Kit.unchecked( () -> mainMethod.invoke( null/*instance*/, (Object)(new String[0]) ) ) );
		assert output.equals( "Hello, world!\n" );
	}
}
