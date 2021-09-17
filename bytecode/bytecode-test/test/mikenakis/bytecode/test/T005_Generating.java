package mikenakis.bytecode.test;

import mikenakis.bytecode.test.model.Class9WithCode;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.NameAndTypeConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T005_Generating
{
	public T005_Generating()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	private static ByteCodeType load( Path classFilePathName )
	{
		byte[] bytes = Kit.unchecked( () -> Files.readAllBytes( classFilePathName ) );
		return ByteCodeReader.read( bytes );
	}

	@Test public void Test1()
	{
		double result = Class9WithCode.eval( "4.0*sin(1.12)/32.0+5.0" );
		assert Math.abs( result - 5.112512555 ) < 1e-7;
	}

	@Test public void Test2()
	{
		Utf8Constant thisClassName = Utf8Constant.of( "Evaluator" );
		ClassConstant thisClassConstant = ClassConstant.of( thisClassName );
		ByteCodeType byteCodeType = ByteCodeType.of( ByteCodeType.modifierFlagsEnum.of( ByteCodeType.Modifier.Public ), thisClassConstant, Optional.empty() );
		Utf8Constant evalMethodNameConstant = Utf8Constant.of( "eval" );
		Utf8Constant evalMethodDescriptorConstant = Utf8Constant.of( "(Ljava/lang/String;)D" );
		ByteCodeMethod evalMethod = ByteCodeMethod.of( ByteCodeMethod.modifierFlagsEnum.of( ByteCodeMethod.Modifier.Public, ByteCodeMethod.Modifier.Static ), //
			evalMethodNameConstant, evalMethodDescriptorConstant, AttributeSet.of() );
		byteCodeType.addMethod( evalMethod );
		CodeAttribute codeAttribute = CodeAttribute.of();
		codeAttribute.setMaxStack( 2 );
		codeAttribute.setMaxLocals( 2 );
		evalMethod.attributeSet.addAttribute( codeAttribute );

		codeAttribute.addNew( thisClassConstant );
		codeAttribute.addDup();
		codeAttribute.addALoad( 0 );

		Utf8Constant initMethodNameConstant = Utf8Constant.of( "<init>" );
		Utf8Constant initMethodTypeConstant = Utf8Constant.of( "(Ljava/lang/String;)V" );
		NameAndTypeConstant initMethodNameAndTypeConstant = NameAndTypeConstant.of( initMethodNameConstant, initMethodTypeConstant );
		PlainMethodReferenceConstant initMethodReferenceConstant = PlainMethodReferenceConstant.of( thisClassConstant, initMethodNameAndTypeConstant );
		codeAttribute.addInvokeSpecial( initMethodReferenceConstant );
		codeAttribute.addAStore( 1 );
		codeAttribute.addALoad( 1 );

		Utf8Constant parseMethodNameConstant = Utf8Constant.of( "parse" );
		Utf8Constant parseMethodTypeConstant = Utf8Constant.of( "()D" );
		NameAndTypeConstant parseMethodNameAndTypeConstant = NameAndTypeConstant.of( parseMethodNameConstant, parseMethodTypeConstant );
		PlainMethodReferenceConstant parseMethodReferenceConstant = PlainMethodReferenceConstant.of( thisClassConstant, parseMethodNameAndTypeConstant );
		codeAttribute.addInvokeVirtual( parseMethodReferenceConstant );
	}
}
