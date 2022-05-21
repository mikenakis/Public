package io.github.mikenakis.bytecode.test;

import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.printing.ByteCodePrinter;
import io.github.mikenakis.bytecode.ByteCodeClassLoader;
import io.github.mikenakis.bytecode.test.model.Class0HelloWorld;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.testkit.TestKit;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * test.
 *
 * @author michael.gr
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
		ByteCodeType byteCodeType = ByteCodeType.read( bytes );
		Class<?> javaClass = ByteCodeClassLoader.load( getClass().getClassLoader(), byteCodeType );
		testHelloWorldJavaClass( javaClass );
	}

	@Test public void Reading_Writing_And_Loading_Compiler_Generated_Class_Works()
	{
		Path classFilePathName = TestKit.getPathToClassFile( Class0HelloWorld.class );

		ByteCodeType byteCodeType1 = ByteCodeType.read( Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) ) );
		byte[] bytes = byteCodeType1.write();
		ByteCodeType byteCodeType2 = ByteCodeType.read( bytes );

		String text1 = ByteCodePrinter.printByteCodeType( byteCodeType1, Optional.empty() );
		String text2 = ByteCodePrinter.printByteCodeType( byteCodeType2, Optional.empty() );
		assert text1.equals( text2 );

		Class<?> javaClass = ByteCodeClassLoader.load( getClass().getClassLoader(), byteCodeType2 );
		testHelloWorldJavaClass( javaClass );
	}

	private static void testHelloWorldJavaClass( Class<?> javaClass )
	{
		Method mainMethod = Kit.unchecked( () -> javaClass.getDeclaredMethod( "main", String[].class ) );
		String output = TestKit.withCapturedOutputStream( //
			() -> Kit.unchecked( () -> mainMethod.invoke( null, (Object)(new String[0]) ) ) );
		assert output.equals( "Hello, world!\n" );
	}
}
