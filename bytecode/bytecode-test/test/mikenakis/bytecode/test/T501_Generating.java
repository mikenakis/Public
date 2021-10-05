package mikenakis.bytecode.test;

import mikenakis.bytecode.ByteCodeClassLoader;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.bytecode.test.model.Class9WithCode;
import mikenakis.bytecode.writing.ByteCodeWriter;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T501_Generating
{
	private final ByteCodeClassLoader byteCodeClassLoader;

	public T501_Generating()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
		ClassLoader classLoader = getClass().getClassLoader();
		//Kit.classLoading.troubleshoot( classLoader ).forEach( s -> Log.debug( s.toString() ) );
		byteCodeClassLoader = new ByteCodeClassLoader( classLoader );
	}

	@Test public void Eval_Works() //this is unrelated to what we are doing here; keep around in case it is useful in the future.
	{
		double result = Class9WithCode.eval( "4.0*sin(1.12)/32.0+5.0" );
		assert Math.abs( result - 5.112512555 ) < 1e-7;
	}

	@Test public void Writing_And_Reading_Generated_Type_Yields_Same_Bytes()
	{
		ByteCodeType byteCodeType = Generator.generateByteCodeType();
		byte[] bytes1 = ByteCodeWriter.write( byteCodeType );
		ByteCodeType byteCodeType2 = ByteCodeReader.read( bytes1 );
		byte[] bytes2 = ByteCodeWriter.write( byteCodeType2 );
		assert Arrays.equals( bytes1, bytes2 );
	}

	@Test public void Writing_And_Reading_Generated_Type_Yields_Same_Text()
	{
		ByteCodeType byteCodeType = Generator.generateByteCodeType();
		String text1 = ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() );
		ByteCodeType byteCodeType2 = ByteCodeReader.read( ByteCodeWriter.write( byteCodeType ) );
		String text2 = ByteCodePrinter.printByteCodeType( byteCodeType2, Optional.empty() );
		assert text1.equals( text2 );
	}

	@Test public void Generated_Type_Works()
	{
		ByteCodeType byteCodeType = Generator.generateByteCodeType();
		Class<?> javaClass = byteCodeClassLoader.load( byteCodeType );
		Generator.testGeneratedByteCodeType( javaClass );
	}
}
