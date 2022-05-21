package io.github.mikenakis.bytecode.test;

import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.printing.ByteCodePrinter;
import io.github.mikenakis.bytecode.ByteCodeClassLoader;
import io.github.mikenakis.bytecode.test.model.Class9WithCode;
import io.github.mikenakis.kit.Kit;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

/**
 * test.
 *
 * @author michael.gr
 */
public class T501_Generating
{
	public T501_Generating()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@Test public void Eval_Works() //this is unrelated to what we are doing here; keep around in case it is useful in the future.
	{
		double result = Class9WithCode.eval( "4.0*sin(1.12)/32.0+5.0" );
		assert Math.abs( result - 5.112512555 ) < 1e-7;
	}

	@Test public void Writing_And_Reading_Generated_Type_Yields_Same_Bytes()
	{
		ByteCodeType byteCodeType = Generator.generateByteCodeType();
		byte[] bytes1 = byteCodeType.write();
		ByteCodeType byteCodeType2 = ByteCodeType.read( bytes1 );
		byte[] bytes2 = byteCodeType2.write();
		assert Arrays.equals( bytes1, bytes2 );
	}

	@Test public void Writing_And_Reading_Generated_Type_Yields_Same_Text()
	{
		ByteCodeType byteCodeType = Generator.generateByteCodeType();
		String text1 = ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() );
		ByteCodeType byteCodeType2 = ByteCodeType.read( byteCodeType.write() );
		String text2 = ByteCodePrinter.printByteCodeType( byteCodeType2, Optional.empty() );
		assert text1.equals( text2 );
	}

	@Test public void Generated_Type_Works()
	{
		ByteCodeType byteCodeType = Generator.generateByteCodeType();
		Class<?> javaClass = ByteCodeClassLoader.load( getClass().getClassLoader(), byteCodeType );
		Generator.testGeneratedByteCodeType( javaClass );
	}
}
