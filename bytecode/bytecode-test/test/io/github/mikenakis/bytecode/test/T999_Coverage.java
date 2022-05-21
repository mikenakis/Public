package io.github.mikenakis.bytecode.test;

import io.github.mikenakis.bytecode.exceptions.InvalidAnnotationValueTagException;
import io.github.mikenakis.bytecode.exceptions.InvalidConstAnnotationValueTagException;
import io.github.mikenakis.bytecode.exceptions.InvalidConstantTagException;
import io.github.mikenakis.bytecode.model.AnnotationValue;
import io.github.mikenakis.bytecode.model.AttributeSet;
import io.github.mikenakis.bytecode.model.ByteCodeField;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.bytecode.model.annotationvalues.ArrayAnnotationValue;
import io.github.mikenakis.bytecode.model.annotationvalues.ConstAnnotationValue;
import io.github.mikenakis.bytecode.model.attributes.BootstrapMethod;
import io.github.mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import io.github.mikenakis.bytecode.model.attributes.CodeAttribute;
import io.github.mikenakis.bytecode.model.attributes.ExceptionsAttribute;
import io.github.mikenakis.bytecode.model.attributes.InnerClassesAttribute;
import io.github.mikenakis.bytecode.model.attributes.KnownAttribute;
import io.github.mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.LocalVariableTableEntry;
import io.github.mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.MethodParametersAttribute;
import io.github.mikenakis.bytecode.model.attributes.NestMembersAttribute;
import io.github.mikenakis.bytecode.model.attributes.ParameterAnnotationSet;
import io.github.mikenakis.bytecode.model.attributes.RuntimeInvisibleAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeInvisibleParameterAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeInvisibleTypeAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeVisibleAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeVisibleParameterAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeVisibleTypeAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.SignatureAttribute;
import io.github.mikenakis.bytecode.model.attributes.SourceFileAttribute;
import io.github.mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.SyntheticAttribute;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.Type;
import io.github.mikenakis.bytecode.model.attributes.stackmap.verification.SimpleVerificationType;
import io.github.mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import io.github.mikenakis.bytecode.model.constants.ClassConstant;
import io.github.mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import io.github.mikenakis.bytecode.model.constants.MethodHandleConstant;
import io.github.mikenakis.bytecode.model.constants.MethodReferenceConstant;
import io.github.mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import io.github.mikenakis.bytecode.model.constants.ReferenceKind;
import io.github.mikenakis.bytecode.model.constants.value.LongValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.StringValueConstant;
import io.github.mikenakis.bytecode.model.descriptors.FieldPrototype;
import io.github.mikenakis.bytecode.model.descriptors.FieldReference;
import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.bytecode.model.descriptors.MethodReference;
import io.github.mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import io.github.mikenakis.java_type_model.MethodDescriptor;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.testkit.TestKit;
import org.junit.Test;

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
 * @author michael.gr
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
		TypeDescriptor typeDescriptor = TypeDescriptor.of( String.class );
		FieldReference fieldReference = FieldReference.of( typeDescriptor, FieldPrototype.of( "foo", String.class ) );
		MethodReference methodReference = MethodReference.of( MethodReferenceKind.Plain, typeDescriptor, MethodPrototype.of( "foo", MethodDescriptor.of( typeDescriptor ) ) );
		ClassConstant classConstant = ClassConstant.of( typeDescriptor );
		NameAndDescriptorConstant nameAndDescriptorConstant = NameAndDescriptorConstant.of( methodReference.methodPrototype );
		MethodReferenceConstant methodReferenceConstant = MethodReferenceConstant.of( methodReference );
		MethodHandleConstant methodHandleConstant = new MethodHandleConstant( ReferenceKind.GetField );
		methodHandleConstant.setReferenceConstant( methodReferenceConstant );
		List<Constant> argumentConstants = new ArrayList<>();
		argumentConstants.add( StringValueConstant.of( "bootstrapMethodArgument" ) );
		BootstrapMethod bootstrapMethod = BootstrapMethod.of( methodHandleConstant, argumentConstants );
		//		var x = bootstrapMethod.constantDescriptor();

		BootstrapMethodsAttribute bootstrapMethodsAttribute = BootstrapMethodsAttribute.of();
		bootstrapMethodsAttribute.getIndexOfBootstrapMethod( bootstrapMethod );
		bootstrapMethodsAttribute.bootstrapMethods.add( bootstrapMethod );
		bootstrapMethodsAttribute.getIndexOfBootstrapMethod( bootstrapMethod );

		ByteCodeType byteCodeType = ByteCodeType.of( ByteCodeType.modifierEnum.of(), TerminalTypeDescriptor.of( "test.testClass" ), Optional.empty(), List.of() ); //FIXME things fail if a package name is not specified.
		byteCodeType.createOrGetBootstrapMethodsAttribute();

		CodeAttribute codeAttribute = CodeAttribute.of( 0, 0 );
		Instruction instruction1 = codeAttribute.xLOAD( Type.Int, 0 );
		Instruction instruction2 = codeAttribute.xLOAD( Type.Long, 1 );
		codeAttribute.xSTORE( Type.Int, 0 );
		codeAttribute.xSTORE( Type.Long, 1 );
		codeAttribute.ILOAD( 0 );
		codeAttribute.LLOAD( 0 );
		codeAttribute.FLOAD( 0 );
		codeAttribute.DLOAD( 0 );
		codeAttribute.ALOAD( 0 );
		codeAttribute.ISTORE( 0 );
		codeAttribute.ISTORE( 1 );
		codeAttribute.LSTORE( 0 );
		codeAttribute.LSTORE( 1 );
		codeAttribute.FSTORE( 0 );
		codeAttribute.FSTORE( 1 );
		codeAttribute.DSTORE( 0 );
		codeAttribute.ASTORE( 0 );
		codeAttribute.LDC( false );
		codeAttribute.LDC( 1 );
		codeAttribute.LDC( 2L );
		codeAttribute.LDC( 3f );
		codeAttribute.LDC( 4.0 );
		codeAttribute.LDC( "" );
		codeAttribute.IFEQ();
		codeAttribute.IFNE();
		codeAttribute.IFLT();
		codeAttribute.IFGE();
		codeAttribute.IFGT();
		codeAttribute.IFLE();
		codeAttribute.IFICMPEQ();
		codeAttribute.IFICMPNE();
		codeAttribute.IFICMPLT();
		codeAttribute.IFICMPGE();
		codeAttribute.IFICMPGT();
		codeAttribute.IFICMPLE();
		codeAttribute.IFNULL();
		codeAttribute.IFNONNULL();
		codeAttribute.IFACMPEQ();
		codeAttribute.IFACMPNE();
		codeAttribute.GOTO();
		codeAttribute.JSR();
		codeAttribute.IINC( 0, 0 );
		codeAttribute.IINC( 1, 1 );
		MethodReference interfaceMethodReference = MethodReference.of( MethodReferenceKind.Interface, typeDescriptor, methodReference.methodPrototype );
		codeAttribute.INVOKEINTERFACE( interfaceMethodReference, 0 );
		InvokeDynamicConstant invokeDynamicConstant = InvokeDynamicConstant.of( bootstrapMethod, nameAndDescriptorConstant );
		codeAttribute.INVOKEDYNAMIC( invokeDynamicConstant );
		codeAttribute.MULTIANEWARRAY( TerminalTypeDescriptor.of( String.class ), 0 );
		codeAttribute.MULTIANEWARRAY( TerminalTypeDescriptor.of( String.class ), 1 );
		codeAttribute.TABLESWITCH( 0 );
		codeAttribute.LOOKUPSWITCH();
		codeAttribute.NEWARRAY( Type.Boolean );
		codeAttribute.NEWARRAY( Type.Char );
		codeAttribute.RET( 0 );
		codeAttribute.GETSTATIC( fieldReference );
		codeAttribute.PUTSTATIC( fieldReference );
		codeAttribute.GETFIELD( fieldReference );
		codeAttribute.PUTFIELD( fieldReference );
		codeAttribute.INVOKEVIRTUAL( methodReference );
		codeAttribute.INVOKESPECIAL( methodReference );
		codeAttribute.INVOKESTATIC( methodReference );
		codeAttribute.NEW( typeDescriptor );
		codeAttribute.ANEWARRAY( typeDescriptor );
		codeAttribute.CHECKCAST( typeDescriptor );
		codeAttribute.INSTANCEOF( typeDescriptor );
		codeAttribute.NOP();
		codeAttribute.ACONST_NULL();
		codeAttribute.xALOAD( Type.Int );
		codeAttribute.xALOAD( Type.Double );
		codeAttribute.IALOAD();
		codeAttribute.LALOAD();
		codeAttribute.FALOAD();
		codeAttribute.DALOAD();
		codeAttribute.AALOAD();
		codeAttribute.BALOAD();
		codeAttribute.CALOAD();
		codeAttribute.SALOAD();
		codeAttribute.xASTORE( Type.Int );
		codeAttribute.xASTORE( Type.Double );
		codeAttribute.IASTORE();
		codeAttribute.LASTORE();
		codeAttribute.FASTORE();
		codeAttribute.DASTORE();
		codeAttribute.AASTORE();
		codeAttribute.BASTORE();
		codeAttribute.CASTORE();
		codeAttribute.SASTORE();
		codeAttribute.POP();
		codeAttribute.POP2();
		codeAttribute.DUP();
		codeAttribute.DUPX1();
		codeAttribute.DUPX2();
		codeAttribute.DUP2();
		codeAttribute.DUP2X1();
		codeAttribute.DUP2X2();
		codeAttribute.SWAP();
		codeAttribute.IADD();
		codeAttribute.LADD();
		codeAttribute.FADD();
		codeAttribute.DADD();
		codeAttribute.xADD( Type.Int );
		codeAttribute.xADD( Type.Double );
		codeAttribute.ISUB();
		codeAttribute.LSUB();
		codeAttribute.FSUB();
		codeAttribute.DSUB();
		codeAttribute.xSUB( Type.Int );
		codeAttribute.xSUB( Type.Double );
		codeAttribute.IMUL();
		codeAttribute.LMUL();
		codeAttribute.FMUL();
		codeAttribute.DMUL();
		codeAttribute.xMUL( Type.Int );
		codeAttribute.xMUL( Type.Double );
		codeAttribute.IDIV();
		codeAttribute.LDIV();
		codeAttribute.FDIV();
		codeAttribute.DDIV();
		codeAttribute.xDIV( Type.Int );
		codeAttribute.xDIV( Type.Double );
		codeAttribute.IREM();
		codeAttribute.LREM();
		codeAttribute.FREM();
		codeAttribute.DREM();
		codeAttribute.xREM( Type.Int );
		codeAttribute.xREM( Type.Double );
		codeAttribute.INEG();
		codeAttribute.LNEG();
		codeAttribute.FNEG();
		codeAttribute.DNEG();
		codeAttribute.xNEG( Type.Int );
		codeAttribute.xNEG( Type.Double );
		codeAttribute.ISHL();
		codeAttribute.LSHL();
		codeAttribute.ISHR();
		codeAttribute.LSHR();
		codeAttribute.IUSHR();
		codeAttribute.LUSHR();
		codeAttribute.IAND();
		codeAttribute.LAND();
		codeAttribute.IOR();
		codeAttribute.LOR();
		codeAttribute.IXOR();
		codeAttribute.LXOR();
		codeAttribute.I2L();
		codeAttribute.I2F();
		codeAttribute.I2D();
		codeAttribute.L2I();
		codeAttribute.L2F();
		codeAttribute.L2D();
		codeAttribute.F2I();
		codeAttribute.F2L();
		codeAttribute.F2D();
		codeAttribute.D2I();
		codeAttribute.D2L();
		codeAttribute.D2F();
		codeAttribute.I2B();
		codeAttribute.I2C();
		codeAttribute.I2S();
		codeAttribute.LCMP();
		codeAttribute.FCMPL();
		codeAttribute.FCMPG();
		codeAttribute.DCMPL();
		codeAttribute.DCMPG();
		codeAttribute.IRETURN();
		codeAttribute.LRETURN();
		codeAttribute.FRETURN();
		codeAttribute.DRETURN();
		codeAttribute.ARETURN();
		codeAttribute.xRETURN( Type.Address );
		codeAttribute.xRETURN( Type.Float );
		codeAttribute.RETURN();
		codeAttribute.ARRAYLENGTH();
		codeAttribute.ATHROW();
		codeAttribute.MONITORENTER();
		codeAttribute.MONITOREXIT();

		AttributeSet attributeSet = AttributeSet.of();
		attributeSet.addAttribute( codeAttribute );
		attributeSet.addOrReplaceAttribute( codeAttribute );
		attributeSet.replaceAttribute( codeAttribute );
		attributeSet.removeAttribute( codeAttribute );

		ByteCodeField.of( ByteCodeField.modifierEnum.of(), fieldReference.fieldPrototype );

		//		InvalidAnnotationValueTagException invalidAnnotationValueTagException = TestKit.expectException( //
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
			Constant.tag_Class, Constant.tag_String, Constant.tag_FieldReference, Constant.tag_PlainMethodReference, Constant.tag_InterfaceMethodReference, //
			Constant.tag_NameAndDescriptor, Constant.tag_MethodHandle, Constant.tag_MethodType, Constant.tag_InvokeDynamic ) )
			Kit.get( Constant.tagName( constantTag ) );
		InvalidConstantTagException invalidConstantTagException = TestKit.expect( //
			InvalidConstantTagException.class, () -> Kit.get( Constant.tagName( 999 ) ) );
		assert invalidConstantTagException.constantTag == 999;

		InvalidAnnotationValueTagException invalidAnnotationValueTagException = TestKit.expect( //
			InvalidAnnotationValueTagException.class, () -> Kit.get( AnnotationValue.tagName( '_' ) ) );
		assert invalidAnnotationValueTagException.annotationValueTag == '_';

		ArrayAnnotationValue.of();
		ConstAnnotationValue.of( true );

		InvalidConstAnnotationValueTagException invalidConstElementValueTagException = TestKit.expect( //
			InvalidConstAnnotationValueTagException.class, () -> ConstAnnotationValue.of( 'e', LongValueConstant.of( 1L ) ) );
		assert invalidConstElementValueTagException.annotationValueTag == 'e';

		ExceptionsAttribute.of();
		InnerClassesAttribute.of();
		LineNumberTableAttribute.of();
		FieldPrototype variablePrototype = FieldPrototype.of( "test", String.class );
		LocalVariableTableEntry.of( instruction1, Optional.of( instruction2 ), variablePrototype, 0 );
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

		//InstructionList instructionList = InstructionList.of();
		//instructionList.add( instruction1 );
		//instructionList.insert( 0, instruction2 );

		ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( ByteCodeMethod.modifierEnum.of(), Mutf8ValueConstant.of( "testMethod" ), Mutf8ValueConstant.of( "()V" ), AttributeSet.of() );
		byteCodeType.methods.add( byteCodeMethod );
		ByteCodeType descendantByteCodeType = ByteCodeType.of( ByteCodeType.modifierEnum.of(), TerminalTypeDescriptor.of( "test.test2" ), Optional.of( TerminalTypeDescriptor.of( "test.testClass" ) ), List.of() );
		Function<String,ByteCodeType> byteCodeTypeResolver = s ->
		{
			if( s.equals( "test.testClass" ) )
				return byteCodeType;
			assert false;
			return null;
		};
		descendantByteCodeType.getMethod( MethodPrototype.of( "testMethod", MethodDescriptor.of( void.class ) ), byteCodeTypeResolver );
		descendantByteCodeType.getMethod( MethodPrototype.of( "nonExistentTestMethod", MethodDescriptor.of( void.class ) ), byteCodeTypeResolver );
	}
}
