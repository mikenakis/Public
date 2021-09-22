package mikenakis.bytecode.test;

import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.bytecode.test.model.Class9WithCode;
import mikenakis.kit.Kit;
import mikenakis.kit.collections.FlagEnumSet;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T401_Generating
{
	public T401_Generating()
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
		FlagEnumSet<ByteCodeType.Modifier> classModifiers = ByteCodeType.modifierFlagsEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Super, ByteCodeType.Modifier.Abstract );
		ByteCodeType byteCodeType = ByteCodeType.of( classModifiers, "HelloWorld", Optional.empty() );


		FlagEnumSet<ByteCodeMethod.Modifier> initMethodModifiers = ByteCodeMethod.modifierFlagsEnum.of( ByteCodeMethod.Modifier.Public );
		Mutf8Constant evalMethodNameConstant = Mutf8Constant.of( "<init>" );
		Mutf8Constant evalMethodDescriptorConstant = Mutf8Constant.of( "(Ljava/lang/String;)D" );
		ByteCodeMethod initMethod = ByteCodeMethod.of( initMethodModifiers, //
			evalMethodNameConstant, evalMethodDescriptorConstant, AttributeSet.of() );
		byteCodeType.methods.add( initMethod );

		CodeAttribute codeAttribute = CodeAttribute.of( 0, 0 );
		codeAttribute.setMaxStack( 2 );
		codeAttribute.setMaxLocals( 2 );
		//evalMethod.attributeSet.addAttribute( codeAttribute );

		codeAttribute.addNew( ClassConstant.of( byteCodeType.thisClassDescriptor.classDesc ) );
		codeAttribute.addDup();
		codeAttribute.addALoad( 0 );

		Mutf8Constant initMethodNameConstant = Mutf8Constant.of( "<init>" );
		Mutf8Constant initMethodTypeConstant = Mutf8Constant.of( "(Ljava/lang/String;)V" );
		NameAndDescriptorConstant initMethodNameAndDescriptorConstant = NameAndDescriptorConstant.of( initMethodNameConstant, initMethodTypeConstant );
		PlainMethodReferenceConstant initMethodReferenceConstant = PlainMethodReferenceConstant.of( ClassConstant.of( byteCodeType.thisClassDescriptor.classDesc ), initMethodNameAndDescriptorConstant );
		codeAttribute.addInvokeSpecial( initMethodReferenceConstant );
		codeAttribute.addAStore( 1 );
		codeAttribute.addALoad( 1 );

		Mutf8Constant parseMethodNameConstant = Mutf8Constant.of( "parse" );
		Mutf8Constant parseMethodTypeConstant = Mutf8Constant.of( "()D" );
		NameAndDescriptorConstant parseMethodNameAndDescriptorConstant = NameAndDescriptorConstant.of( parseMethodNameConstant, parseMethodTypeConstant );
		PlainMethodReferenceConstant parseMethodReferenceConstant = PlainMethodReferenceConstant.of( ClassConstant.of( byteCodeType.thisClassDescriptor.classDesc ), parseMethodNameAndDescriptorConstant );
		codeAttribute.addInvokeVirtual( parseMethodReferenceConstant );
	}
}
