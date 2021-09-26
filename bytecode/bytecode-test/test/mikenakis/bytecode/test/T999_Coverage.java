package mikenakis.bytecode.test;

import mikenakis.bytecode.exceptions.InvalidAnnotationValueTagException;
import mikenakis.bytecode.exceptions.InvalidConstAnnotationValueTagException;
import mikenakis.bytecode.exceptions.InvalidConstantTagException;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.annotationvalues.ArrayAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ClassAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ConstAnnotationValue;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.ExceptionsAttribute;
import mikenakis.bytecode.model.attributes.InnerClassesAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTableEntry;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.model.attributes.MethodParametersAttribute;
import mikenakis.bytecode.model.attributes.NestMembersAttribute;
import mikenakis.bytecode.model.attributes.ParameterAnnotationSet;
import mikenakis.bytecode.model.attributes.RuntimeInvisibleAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeInvisibleParameterAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeInvisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeVisibleAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeVisibleParameterAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeVisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.SignatureAttribute;
import mikenakis.bytecode.model.attributes.SourceFileAttribute;
import mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import mikenakis.bytecode.model.attributes.SyntheticAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.InstructionList;
import mikenakis.bytecode.model.attributes.stackmap.verification.SimpleVerificationType;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.kit.Kit;
import org.junit.Test;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * The purpose of this test class is to cover miscellaneous methods that are currently unused
 * but are expected to be used in the future.
 * This is necessary while working towards 100% code coverage because it allows us to see at a glance
 * whether we have covered everything that was meant to be covered at a particular stage of development
 * without being confused by apparent incomplete coverage which is only due to methods that were not
 * meant to be covered during this stage.
 * <p>
 * Ideally, this test class would be removed when the module reaches maturity.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T999_Coverage
{
	public T999_Coverage()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@Test public void Cover_Misc_Methods()
	{
		ClassConstant classConstant = ClassConstant.of( java.util.HashMap.class.getName() );
		Mutf8Constant nameConstant = Mutf8Constant.of( "name" );
		Mutf8Constant descriptorConstant = Mutf8Constant.of( "descriptor" );
		NameAndDescriptorConstant nameAndDescriptorConstant = NameAndDescriptorConstant.of( nameConstant, descriptorConstant );
		FieldReferenceConstant fieldReferenceConstant = FieldReferenceConstant.of( classConstant, nameAndDescriptorConstant );
		MethodHandleConstant methodHandleConstant = new MethodHandleConstant( MethodHandleConstant.ReferenceKind.GetField );
		methodHandleConstant.setReferenceConstant( fieldReferenceConstant );
		List<Constant> argumentConstants = new ArrayList<>();
		argumentConstants.add( StringConstant.of( "bootstrapMethodArgument" ) );
		BootstrapMethod bootstrapMethod = BootstrapMethod.of( methodHandleConstant, argumentConstants );
		//		var x = bootstrapMethod.constantDescriptor();

		BootstrapMethodsAttribute bootstrapMethodsAttribute = BootstrapMethodsAttribute.of();
		bootstrapMethodsAttribute.getIndexOfBootstrapMethod( bootstrapMethod );
		bootstrapMethodsAttribute.bootstrapMethods.add( bootstrapMethod );
		bootstrapMethodsAttribute.getIndexOfBootstrapMethod( bootstrapMethod );

		CodeAttribute codeAttribute = CodeAttribute.of( 0, 0 );
		Instruction instruction1 = codeAttribute.addILoad( 0 );
		Instruction instruction2 = codeAttribute.addLLoad( 0 );
		codeAttribute.addFLoad( 0 );
		codeAttribute.addDLoad( 0 );
		codeAttribute.addALoad( 0 );
		codeAttribute.addIStore( 0 );
		codeAttribute.addLStore( 0 );
		codeAttribute.addFStore( 0 );
		codeAttribute.addDStore( 0 );
		codeAttribute.addAStore( 0 );
		codeAttribute.addLdc( false );
		codeAttribute.addLdc( 1 );
		codeAttribute.addLdc( 2L );
		codeAttribute.addLdc( 3f );
		codeAttribute.addLdc( 4.0 );
		codeAttribute.addLdc( "" );
		codeAttribute.addIfEQ();
		codeAttribute.addIfNE();
		codeAttribute.addIfLT();
		codeAttribute.addIfGE();
		codeAttribute.addIfGT();
		codeAttribute.addIfLE();
		codeAttribute.addIfICmpEQ();
		codeAttribute.addIfICmpNE();
		codeAttribute.addIfICmpLT();
		codeAttribute.addIfICmpGE();
		codeAttribute.addIfICmpGT();
		codeAttribute.addIfICmpLE();
		codeAttribute.addIfNull();
		codeAttribute.addIfNonNull();
		codeAttribute.addIfACmpEQ();
		codeAttribute.addIfACmpNE();
		codeAttribute.addGoto();
		codeAttribute.addJsr();
		codeAttribute.addIInc( 0, 0 );
		InterfaceMethodReferenceConstant interfaceMethodReferenceConstant = InterfaceMethodReferenceConstant.of( classConstant, nameAndDescriptorConstant );
		codeAttribute.addInvokeInterface( interfaceMethodReferenceConstant, 0 );
		InvokeDynamicConstant invokeDynamicConstant = InvokeDynamicConstant.of( bootstrapMethod, nameAndDescriptorConstant );
		codeAttribute.addInvokeDynamic( invokeDynamicConstant );
		codeAttribute.addMultiANewArray( classConstant, 0 );
		codeAttribute.addTableSwitch( 0 );
		codeAttribute.addLookupSwitch();
		codeAttribute.addNewArrayOfBoolean();
		codeAttribute.addNewArrayOfChar();
		codeAttribute.addNewArrayOfFloat();
		codeAttribute.addNewArrayOfDouble();
		codeAttribute.addNewArrayOfByte();
		codeAttribute.addNewArrayOfShort();
		codeAttribute.addNewArrayOfInt();
		codeAttribute.addNewArrayOfLong();
		codeAttribute.addRet( 0 );
		codeAttribute.addGetStatic( fieldReferenceConstant );
		codeAttribute.addPutStatic( fieldReferenceConstant );
		codeAttribute.addGetField( fieldReferenceConstant );
		codeAttribute.addPutField( fieldReferenceConstant );
		MethodReferenceConstant methodReferenceConstant = PlainMethodReferenceConstant.of( classConstant, nameAndDescriptorConstant );
		codeAttribute.addInvokeVirtual( methodReferenceConstant );
		codeAttribute.addInvokeSpecial( methodReferenceConstant );
		codeAttribute.addInvokeStatic( methodReferenceConstant );
		codeAttribute.addNew( classConstant );
		codeAttribute.addANewArray( classConstant );
		codeAttribute.addCheckCast( classConstant );
		codeAttribute.addInstanceOf( classConstant );
		codeAttribute.addNop();
		codeAttribute.addAConstNull();
		codeAttribute.addIALoad();
		codeAttribute.addLALoad();
		codeAttribute.addFALoad();
		codeAttribute.addDALoad();
		codeAttribute.addAALoad();
		codeAttribute.addBALoad();
		codeAttribute.addCALoad();
		codeAttribute.addSALoad();
		codeAttribute.addIAStore();
		codeAttribute.addLAStore();
		codeAttribute.addFAStore();
		codeAttribute.addDAStore();
		codeAttribute.addAAStore();
		codeAttribute.addBAStore();
		codeAttribute.addCAStore();
		codeAttribute.addSAStore();
		codeAttribute.addPop();
		codeAttribute.addPop2();
		codeAttribute.addDup();
		codeAttribute.addDupX1();
		codeAttribute.addDupX2();
		codeAttribute.addDup2();
		codeAttribute.addDup2X1();
		codeAttribute.addDup2X2();
		codeAttribute.addSwap();
		codeAttribute.addIAdd();
		codeAttribute.addLAdd();
		codeAttribute.addFAdd();
		codeAttribute.addDAdd();
		codeAttribute.addISub();
		codeAttribute.addLSub();
		codeAttribute.addFSub();
		codeAttribute.addDSub();
		codeAttribute.addIMul();
		codeAttribute.addLMul();
		codeAttribute.addFMul();
		codeAttribute.addDMul();
		codeAttribute.addIDiv();
		codeAttribute.addLDiv();
		codeAttribute.addFDiv();
		codeAttribute.addDDiv();
		codeAttribute.addIRem();
		codeAttribute.addLRem();
		codeAttribute.addFRem();
		codeAttribute.addDRem();
		codeAttribute.addINeg();
		codeAttribute.addLNeg();
		codeAttribute.addFNeg();
		codeAttribute.addDNeg();
		codeAttribute.addIShl();
		codeAttribute.addLShl();
		codeAttribute.addIShr();
		codeAttribute.addLShr();
		codeAttribute.addIUShr();
		codeAttribute.addLUShr();
		codeAttribute.addIAnd();
		codeAttribute.addLAnd();
		codeAttribute.addIOr();
		codeAttribute.addLOr();
		codeAttribute.addIXor();
		codeAttribute.addLXor();
		codeAttribute.addI2L();
		codeAttribute.addI2F();
		codeAttribute.addI2D();
		codeAttribute.addL2I();
		codeAttribute.addL2F();
		codeAttribute.addL2D();
		codeAttribute.addF2I();
		codeAttribute.addF2L();
		codeAttribute.addF2D();
		codeAttribute.addD2I();
		codeAttribute.addD2L();
		codeAttribute.addD2F();
		codeAttribute.addI2B();
		codeAttribute.addI2C();
		codeAttribute.addI2S();
		codeAttribute.addLCmp();
		codeAttribute.addFCmpL();
		codeAttribute.addFCmpG();
		codeAttribute.addDCmpL();
		codeAttribute.addDCmpG();
		codeAttribute.addIReturn();
		codeAttribute.addLReturn();
		codeAttribute.addFReturn();
		codeAttribute.addDReturn();
		codeAttribute.addAReturn();
		codeAttribute.addReturn();
		codeAttribute.addArrayLength();
		codeAttribute.addAThrow();
		codeAttribute.addMonitorEnter();
		codeAttribute.addMonitorExit();

		AttributeSet attributeSet = AttributeSet.of();
		attributeSet.addAttribute( codeAttribute );
		attributeSet.addOrReplaceAttribute( codeAttribute );
		attributeSet.replaceAttribute( codeAttribute );
		attributeSet.removeAttribute( codeAttribute );

		ByteCodeField.of( ByteCodeField.modifierFlagsEnum.of(), "testField", ClassDesc.ofDescriptor( "Ljava/lang/Object;" ) );
		ByteCodeType byteCodeType = ByteCodeType.of( ByteCodeType.modifierFlagsEnum.of(), "test.testClass", Optional.empty() ); //FIXME things fail if a package name is not specified.
		byteCodeType.createOrGetBootstrapMethodsAttribute();

		//		InvalidAnnotationValueTagException invalidAnnotationValueTagException = Kit.testing.expectException( //
		//			InvalidAnnotationValueTagException.class, () -> AnnotationValue.doSwitch( '_', new AnnotationValue.Switcher<>()
		//		{
		//			@Override public Object caseAnnotation() { return null; }
		//			@Override public Object caseArray() { return null; }
		//			@Override public Object caseBoolean() { return null; }
		//			@Override public Object caseByte() { return null; }
		//			@Override public Object caseCharacter() { return null; }
		//			@Override public Object caseClass() { return null; }
		//			@Override public Object caseDouble() { return null; }
		//			@Override public Object caseEnum() { return null; }
		//			@Override public Object caseFloat() { return null; }
		//			@Override public Object caseInteger() { return null; }
		//			@Override public Object caseLong() { return null; }
		//			@Override public Object caseShort() { return null; }
		//			@Override public Object caseString() { return null; }
		//		} ) );
		//		assert invalidAnnotationValueTagException.tag == '_';

		for( int constantTag : List.of( Constant.tag_Mutf8, Constant.tag_Integer, Constant.tag_Float, Constant.tag_Long, Constant.tag_Double, //
			Constant.tag_Class, Constant.tag_String, Constant.tag_FieldReference, Constant.tag_MethodReference, Constant.tag_InterfaceMethodReference, //
			Constant.tag_NameAndDescriptor, Constant.tag_MethodHandle, Constant.tag_MethodType, Constant.tag_InvokeDynamic ) )
			Kit.get( Constant.tagName( constantTag ) );
		InvalidConstantTagException invalidConstantTagException = Kit.testing.expectException( //
			InvalidConstantTagException.class, () -> Kit.get( Constant.tagName( 999 ) ) );
		assert invalidConstantTagException.constantTag == 999;

		InvalidAnnotationValueTagException invalidAnnotationValueTagException = Kit.testing.expectException( //
			InvalidAnnotationValueTagException.class, () -> Kit.get( AnnotationValue.tagName( '_' ) ) );
		assert invalidAnnotationValueTagException.annotationValueTag == '_';

		ArrayAnnotationValue.of();
		ClassAnnotationValue.of( "test" );
		ConstAnnotationValue.of( true );

		InvalidConstAnnotationValueTagException invalidConstElementValueTagException = Kit.testing.expectException( //
			InvalidConstAnnotationValueTagException.class, () -> ConstAnnotationValue.of( 'e', LongConstant.of( 1L ) ) );
		assert invalidConstElementValueTagException.annotationValueTag == 'e';

		ExceptionsAttribute.of();
		InnerClassesAttribute.of();
		LineNumberTableAttribute.of();
		LocalVariableTableEntry.of( instruction1, Optional.of( instruction2 ), "name", "", 0 );
		LocalVariableTableAttribute.of();
		LocalVariableTypeTableAttribute.of();
		MethodParametersAttribute.of();
		NestMembersAttribute.of();
		ParameterAnnotationSet.of();
		RuntimeInvisibleAnnotationsAttribute.of();
		RuntimeInvisibleParameterAnnotationsAttribute.of();
		RuntimeInvisibleTypeAnnotationsAttribute.of();
		RuntimeVisibleAnnotationsAttribute.of();
		RuntimeVisibleParameterAnnotationsAttribute.of();
		RuntimeVisibleTypeAnnotationsAttribute.of();
		SignatureAttribute.of( "" );
		SourceFileAttribute.of( "" );
		StackMapTableAttribute stackMapTableAttribute = StackMapTableAttribute.of();
		stackMapTableAttribute.addSameFrame( instruction1 );
		VerificationType stackVerificationType = new SimpleVerificationType( VerificationType.tag_Double );
		stackMapTableAttribute.addSameLocals1StackItemFrame( instruction1, stackVerificationType );
		stackMapTableAttribute.addChopFrame( instruction1, 1 );
		stackMapTableAttribute.addAppendFrame( instruction1, List.of( stackVerificationType ) );
		stackMapTableAttribute.addFullFrame( instruction1, List.of( stackVerificationType ), List.of( stackVerificationType ) );
		KnownAttribute syntheticAttribute = SyntheticAttribute.of();
		syntheticAttribute.asSyntheticAttribute();

		InstructionList instructionList = InstructionList.of();
		instructionList.add( instruction1 );
		instructionList.insert( 0, instruction2 );

		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierFlagsEnum.of(), Mutf8Constant.of( "testMethod" ), Mutf8Constant.of( "()V" ), AttributeSet.of() );
		byteCodeType.methods.add( byteCodeMethod );
		ByteCodeType descendantByteCodeType = ByteCodeType.of( ByteCodeType.modifierFlagsEnum.of(), "test.test2", Optional.of( "test.testClass" ) );
		Function<String,ByteCodeType> byteCodeTypeResolver = s ->
		{
			if( s.equals( "test.testClass" ) )
				return byteCodeType;
			assert false;
			return null;
		};
		descendantByteCodeType.getMethodByNameAndDescriptor( "testMethod", MethodTypeDesc.ofDescriptor( "()V" ), byteCodeTypeResolver );
		descendantByteCodeType.getMethodByNameAndDescriptor( "nonExistentTestMethod", MethodTypeDesc.ofDescriptor( "()V" ), byteCodeTypeResolver );
	}
}