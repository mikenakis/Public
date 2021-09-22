package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.TypeAnnotation;
import mikenakis.bytecode.model.annotationvalues.AnnotationAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ArrayAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ClassAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ConstAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.EnumAnnotationValue;
import mikenakis.bytecode.model.attributes.AnnotationDefaultAttribute;
import mikenakis.bytecode.model.attributes.AnnotationsAttribute;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.ConstantValueAttribute;
import mikenakis.bytecode.model.attributes.EnclosingMethodAttribute;
import mikenakis.bytecode.model.attributes.ExceptionInfo;
import mikenakis.bytecode.model.attributes.ExceptionsAttribute;
import mikenakis.bytecode.model.attributes.InnerClass;
import mikenakis.bytecode.model.attributes.InnerClassesAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTableEntry;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableEntry;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.model.attributes.MethodParameter;
import mikenakis.bytecode.model.attributes.MethodParametersAttribute;
import mikenakis.bytecode.model.attributes.NestHostAttribute;
import mikenakis.bytecode.model.attributes.NestMembersAttribute;
import mikenakis.bytecode.model.attributes.ParameterAnnotationSet;
import mikenakis.bytecode.model.attributes.ParameterAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.SignatureAttribute;
import mikenakis.bytecode.model.attributes.SourceFileAttribute;
import mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import mikenakis.bytecode.model.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.attributes.stackmap.AppendStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.ChopStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.FullStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.SameLocals1StackItemStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.SameStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.StackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.verification.ObjectVerificationType;
import mikenakis.bytecode.model.attributes.stackmap.verification.SimpleVerificationType;
import mikenakis.bytecode.model.attributes.stackmap.verification.UninitializedVerificationType;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the constant pool of a java class file.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 */
final class ConstantPool
{
	private final List<Constant> entries;

	ConstantPool()
	{
		entries = new ArrayList<>( 1 );
		entries.add( null ); // first entry is empty. (Ancient legacy bollocks.)
	}

	private int tryGetIndex( Constant constant )
	{
		if( constant == null )
			return 0;
		for( int i = 0; i < entries.size(); i++ )
		{
			Constant existingConstant = entries.get( i );
			if( existingConstant == null )
				continue;
			if( existingConstant.equals( constant ) )
				return i;
		}
		return -1;
	}

	int getIndex( Constant constant )
	{
		int index = tryGetIndex( constant );
		assert index != -1;
		return index;
	}

	void internTerminalTypeDescriptor( TerminalTypeDescriptor terminalTypeDescriptor )
	{
		ClassConstant classConstant = ClassConstant.of( terminalTypeDescriptor.classDesc );
		internClassConstant( classConstant );
	}

	void internExtraConstant( Constant constant )
	{
		internConstant0( constant );
		switch( constant.tag )
		{
			case Constant.tagMutf8, Constant.tagInteger, Constant.tagFloat, Constant.tagLong, Constant.tagDouble -> { }
			case Constant.tagClass -> internClassConstant( constant.asClassConstant() );
			case Constant.tagString -> internStringConstant( constant.asStringConstant() );
			case Constant.tagFieldReference -> internReferenceConstant( constant.asFieldReferenceConstant() );
			case Constant.tagMethodReference -> internReferenceConstant( constant.asMethodReferenceConstant() );
			case Constant.tagInterfaceMethodReference -> internReferenceConstant( constant.asInterfaceMethodReferenceConstant() );
			case Constant.tagNameAndDescriptor -> internNameAndDescriptorConstant( constant.asNameAndDescriptorConstant() );
			case Constant.tagMethodHandle -> internMethodHandleConstant( constant.asMethodHandleConstant() );
			case Constant.tagMethodType -> internMethodTypeConstant( constant.asMethodTypeConstant() );
			case Constant.tagInvokeDynamic -> internInvokeDynamicConstant( constant.asInvokeDynamicConstant() );
			default -> throw new AssertionError( constant );
		}
	}

	private void internValueConstant( ValueConstant<?> valueConstant )
	{
		switch( valueConstant.tag )
		{
			case Constant.tagString:
				internStringConstant( valueConstant.asStringConstant() );
				break;
			case Constant.tagMutf8:
			case Constant.tagInteger:
			case Constant.tagFloat:
			case Constant.tagLong:
			case Constant.tagDouble:
				internConstant0( valueConstant );
				break;
			default:
				assert false;
				break;
		}
	}

	private void internMutf8Constant( Mutf8Constant mutf8Constant )
	{
		internConstant0( mutf8Constant );
	}

	private void internConstant0( Constant constant )
	{
		int existingIndex = tryGetIndex( constant );
		assert existingIndex != 0;
		if( existingIndex == -1 )
		{
			assert !entries.isEmpty();
			entries.add( constant );
			if( constant.tag == Constant.tagLong || constant.tag == Constant.tagDouble )
				entries.add( null ); //8-byte constants occupy two constant pool entries. (Ancient legacy bollocks.)
		}
	}

	private void internInvokeDynamicConstant( InvokeDynamicConstant invokeDynamicConstant )
	{
		internConstant0( invokeDynamicConstant );
		internNameAndDescriptorConstant( invokeDynamicConstant.nameAndDescriptorConstant() );
	}

	private void internMethodTypeConstant( MethodTypeConstant methodTypeConstant )
	{
		internConstant0( methodTypeConstant );
		internMutf8Constant( methodTypeConstant.descriptorConstant );
	}

	private void internMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		internConstant0( methodHandleConstant );
		internReferenceConstant( methodHandleConstant.referenceConstant() );
	}

	private void internNameAndDescriptorConstant( NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		internConstant0( nameAndDescriptorConstant );
		internMutf8Constant( nameAndDescriptorConstant.nameConstant );
		internMutf8Constant( nameAndDescriptorConstant.descriptorConstant );
	}

	private void internReferenceConstant( ReferenceConstant referenceConstant )
	{
		internConstant0( referenceConstant );
		internClassConstant( referenceConstant.typeConstant );
		internNameAndDescriptorConstant( referenceConstant.nameAndDescriptorConstant );
	}

	private void internStringConstant( StringConstant stringConstant )
	{
		internConstant0( stringConstant );
		internMutf8Constant( stringConstant.valueConstant() );
	}

	void internClassConstant( ClassConstant classConstant )
	{
		internConstant0( classConstant );
		internMutf8Constant( classConstant.nameConstant() );
	}

	void internField( ByteCodeField byteCodeField )
	{
		internMutf8Constant( byteCodeField.nameConstant );
		internMutf8Constant( byteCodeField.descriptorConstant );
		internAttributeSet( byteCodeField.attributeSet );
	}

	void internMethod( ByteCodeMethod byteCodeMethod )
	{
		internMutf8Constant( byteCodeMethod.nameConstant );
		internMutf8Constant( byteCodeMethod.descriptorConstant );
		internAttributeSet( byteCodeMethod.attributeSet );
	}

	void internAttributeSet( AttributeSet attributeSet )
	{
		for( Attribute attribute : attributeSet.allAttributes() )
		{
			internMutf8Constant( attribute.mutf8Name );
			internAttribute( attribute );
		}
	}

	private void internAttribute( Attribute attribute )
	{
		if( !attribute.isKnown() )
			return;
		KnownAttribute knownAttribute = attribute.asKnownAttribute();
		switch( knownAttribute.tag )
		{
			case KnownAttribute.tagAnnotationDefault -> internAnnotationDefaultAttribute( attribute.asAnnotationDefaultAttribute() );
			case KnownAttribute.tagBootstrapMethods -> internBootstrapMethodsAttribute( attribute.asBootstrapMethodsAttribute() );
			case KnownAttribute.tagCode -> internCodeAttribute( attribute.asCodeAttribute() );
			case KnownAttribute.tagConstantValue -> internConstantValueAttribute( attribute.asConstantValueAttribute() );
			case KnownAttribute.tagEnclosingMethod -> internEnclosingMethodAttribute( attribute.asEnclosingMethodAttribute() );
			case KnownAttribute.tagExceptions -> internExceptionsAttribute( attribute.asExceptionsAttribute() );
			case KnownAttribute.tagInnerClasses -> internInnerClassesAttribute( attribute.asInnerClassesAttribute() );
			case KnownAttribute.tagNestHost -> internNestHostAttribute( attribute.asNestHostAttribute() );
			case KnownAttribute.tagNestMembers -> internNestMembersAttribute( attribute.asNestMembersAttribute() );
			case KnownAttribute.tagLineNumberTable -> internLineNumberTableAttribute( attribute.asLineNumberTableAttribute() );
			case KnownAttribute.tagLocalVariableTable -> internLocalVariableTableAttribute( attribute.asLocalVariableTableAttribute() );
			case KnownAttribute.tagLocalVariableTypeTable -> internLocalVariableTypeTableAttribute( attribute.asLocalVariableTypeTableAttribute() );
			case KnownAttribute.tagMethodParameters -> internMethodParametersAttribute( attribute.asMethodParametersAttribute() );
			case KnownAttribute.tagRuntimeVisibleAnnotations -> internAnnotationsAttribute( attribute.asRuntimeVisibleAnnotationsAttribute() );
			case KnownAttribute.tagRuntimeInvisibleAnnotations -> internAnnotationsAttribute( attribute.asRuntimeInvisibleAnnotationsAttribute() );
			case KnownAttribute.tagRuntimeInvisibleParameterAnnotations -> internParameterAnnotationsAttribute( attribute.asRuntimeInvisibleParameterAnnotationsAttribute() );
			case KnownAttribute.tagRuntimeVisibleParameterAnnotations -> internParameterAnnotationsAttribute( attribute.asRuntimeVisibleParameterAnnotationsAttribute() );
			case KnownAttribute.tagRuntimeInvisibleTypeAnnotations -> internTypeAnnotationsAttribute( attribute.asRuntimeInvisibleTypeAnnotationsAttribute() );
			case KnownAttribute.tagRuntimeVisibleTypeAnnotations -> internTypeAnnotationsAttribute( attribute.asRuntimeVisibleTypeAnnotationsAttribute() );
			case KnownAttribute.tagSignature -> internSignatureAttribute( attribute.asSignatureAttribute() );
			case KnownAttribute.tagSourceFile -> internSourceFileAttribute( attribute.asSourceFileAttribute() );
			case KnownAttribute.tagStackMapTable -> internStackMapTableAttribute( attribute.asStackMapTableAttribute() );
			case KnownAttribute.tagDeprecated, KnownAttribute.tagSynthetic -> { /* nothing to do */ }
			default -> { assert false; }
		}
	}

	private void internAnnotationDefaultAttribute( AnnotationDefaultAttribute annotationDefaultAttribute )
	{
		internAnnotationValue( annotationDefaultAttribute.annotationValue() );
	}

	private void internBootstrapMethodsAttribute( BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		for( BootstrapMethod bootstrapMethod : bootstrapMethodsAttribute.bootstrapMethods )
		{
			internMethodHandleConstant( bootstrapMethod.methodHandleConstant );
			for( Constant constant : bootstrapMethod.argumentConstants )
				switch( constant.tag )
				{
					case Constant.tagClass -> internClassConstant( constant.asClassConstant() );
					case Constant.tagMethodType -> internMethodTypeConstant( constant.asMethodTypeConstant() );
					case Constant.tagString -> internStringConstant( constant.asStringConstant() );
					case Constant.tagMethodHandle -> internMethodHandleConstant( constant.asMethodHandleConstant() );
					default -> throw new AssertionError( constant );
				}
		}
	}

	private void internCodeAttribute( CodeAttribute codeAttribute )
	{
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos() )
			exceptionInfo.catchTypeConstant.ifPresent( c -> internClassConstant( c ) );

		internAttributeSet( codeAttribute.attributeSet );

		for( Instruction instruction : codeAttribute.instructions().all() )
			internInstruction( instruction );

		for( Instruction instruction : codeAttribute.instructions().all() )
			internInstruction( instruction );
	}

	private void internConstantValueAttribute( ConstantValueAttribute constantValueAttribute )
	{
		internValueConstant( constantValueAttribute.valueConstant );
	}

	private void internEnclosingMethodAttribute( EnclosingMethodAttribute enclosingMethodAttribute )
	{
		internClassConstant( enclosingMethodAttribute.classConstant() );
		enclosingMethodAttribute.methodNameAndDescriptorConstant().ifPresent( c -> internNameAndDescriptorConstant( c ) );
	}

	private void internExceptionsAttribute( ExceptionsAttribute exceptionsAttribute )
	{
		for( ClassConstant exceptionClassConstant : exceptionsAttribute.exceptionClassConstants() )
			internClassConstant( exceptionClassConstant );
	}

	private void internInnerClassesAttribute( InnerClassesAttribute innerClassesAttribute )
	{
		for( InnerClass innerClass : innerClassesAttribute.innerClasses )
		{
			internClassConstant( innerClass.innerClassConstant() );
			innerClass.outerClassConstant().ifPresent( c -> internClassConstant( c ) );
			innerClass.innerNameConstant().ifPresent( c -> internMutf8Constant( c ) );
		}
	}

	private void internNestHostAttribute( NestHostAttribute nestHostAttribute )
	{
		internClassConstant( nestHostAttribute.hostClassConstant );
	}

	private void internNestMembersAttribute( NestMembersAttribute nestMembersAttribute )
	{
		for( ClassConstant memberClassConstant : nestMembersAttribute.memberClassConstants )
			internClassConstant( memberClassConstant );
	}

	private void internLineNumberTableAttribute( LineNumberTableAttribute lineNumberTableAttribute )
	{
		// FIXME this is probably unnecessary!
		for( var lineNumber : lineNumberTableAttribute.entrys )
			internInstruction( lineNumber.instruction() );
	}

	private void internLocalVariableTableAttribute( LocalVariableTableAttribute localVariableTableAttribute )
	{
		for( LocalVariableTableEntry localVariable : localVariableTableAttribute.entrys )
		{
			internMutf8Constant( localVariable.nameConstant );
			internMutf8Constant( localVariable.descriptorConstant );
		}
	}

	private void internLocalVariableTypeTableAttribute( LocalVariableTypeTableAttribute localVariableTypeTableAttribute )
	{
		for( LocalVariableTypeTableEntry entry : localVariableTypeTableAttribute.localVariableTypes )
		{
			internMutf8Constant( entry.nameConstant );
			internMutf8Constant( entry.signatureConstant );
		}
	}

	private void internMethodParametersAttribute( MethodParametersAttribute methodParametersAttribute )
	{
		for( MethodParameter entry : methodParametersAttribute.methodParameters )
			internMutf8Constant( entry.nameConstant );
	}

	private void internAnnotationsAttribute( AnnotationsAttribute runtimeVisibleAnnotationsAttribute )
	{
		for( Annotation annotation : runtimeVisibleAnnotationsAttribute.annotations )
			internAnnotation( annotation );
	}

	private void internParameterAnnotationsAttribute( ParameterAnnotationsAttribute runtimeInvisibleParameterAnnotationsAttribute )
	{
		for( ParameterAnnotationSet entry : runtimeInvisibleParameterAnnotationsAttribute.parameterAnnotationSets() )
			for( Annotation annotation : entry.annotations )
				internAnnotation( annotation );
	}

	private void internTypeAnnotationsAttribute( TypeAnnotationsAttribute runtimeInvisibleTypeAnnotationsAttribute )
	{
		for( TypeAnnotation typeAnnotation : runtimeInvisibleTypeAnnotationsAttribute.typeAnnotations )
		{
			//internTarget( typeAnnotation.target() ); //TODO
			//typeAnnotation.typeIndex(); //TODO
			//internTypePath( typeAnnotation.typePath() ); //TODO
			internAnnotationParameters( typeAnnotation.parameters );
		}
	}

	private void internSignatureAttribute( SignatureAttribute signatureAttribute )
	{
		internMutf8Constant( signatureAttribute.signatureConstant() );
	}

	private void internSourceFileAttribute( SourceFileAttribute sourceFileAttribute )
	{
		internMutf8Constant( sourceFileAttribute.valueConstant() );
	}

	private void internStackMapTableAttribute( StackMapTableAttribute stackMapTableAttribute )
	{
		for( StackMapFrame frame : stackMapTableAttribute.frames() )
			internStackMapFrame( frame );
	}

	private void internAnnotationValue( AnnotationValue annotationValue )
	{
		switch( annotationValue.tag )
		{
			case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, //
				AnnotationValue.tagFloat, AnnotationValue.tagInteger, AnnotationValue.tagLong, AnnotationValue.tagShort, //
				AnnotationValue.tagString -> internConstAnnotationValue( annotationValue.asConstAnnotationValue() );
			case AnnotationValue.tagClass -> internClassAnnotationValue( annotationValue.asClassAnnotationValue() );
			case AnnotationValue.tagEnum -> internEnumAnnotationValue( annotationValue.asEnumAnnotationValue() );
			case AnnotationValue.tagAnnotation -> internAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue() );
			case AnnotationValue.tagArray -> internArrayAnnotationValue( annotationValue.asArrayAnnotationValue() );
			default -> throw new AssertionError( annotationValue );
		}
	}

	private void internConstAnnotationValue( ConstAnnotationValue constAnnotationValue )
	{
		internValueConstant( constAnnotationValue.valueConstant );
	}

	private void internEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue )
	{
		internMutf8Constant( enumAnnotationValue.typeNameConstant() );
		internMutf8Constant( enumAnnotationValue.valueNameConstant() );
	}

	private void internClassAnnotationValue( ClassAnnotationValue classAnnotationValue )
	{
		internMutf8Constant( classAnnotationValue.nameConstant() );
	}

	private void internAnnotationAnnotationValue( AnnotationAnnotationValue annotationAnnotationValue )
	{
		internAnnotation( annotationAnnotationValue.annotation );
	}

	private void internArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue )
	{
		for( AnnotationValue annotationValue : arrayAnnotationValue.annotationValues )
			internAnnotationValue( annotationValue );
	}

	private void internInstruction( Instruction instruction )
	{
		switch( instruction.group )
		{
			case ConstantReferencing -> internConstantReferencingInstruction( instruction.asConstantReferencingInstruction() );
			case IndirectLoadConstant -> internIndirectLoadConstantInstruction( instruction.asIndirectLoadConstantInstruction() );
			case InvokeDynamic -> internInvokeDynamicInstruction( instruction.asInvokeDynamicInstruction() );
			case InvokeInterface -> internInvokeInterfaceInstruction( instruction.asInvokeInterfaceInstruction() );
			case MultiANewArray -> internMultiANewArrayInstruction( instruction.asMultiANewArrayInstruction() );
			default -> { }
		}
	}

	private void internMultiANewArrayInstruction( MultiANewArrayInstruction multiANewArrayInstruction )
	{
		internClassConstant( multiANewArrayInstruction.classConstant );
	}

	private void internInvokeInterfaceInstruction( InvokeInterfaceInstruction invokeInterfaceInstruction )
	{
		internReferenceConstant( invokeInterfaceInstruction.interfaceMethodReferenceConstant );
	}

	private void internInvokeDynamicInstruction( InvokeDynamicInstruction invokeDynamicInstruction )
	{
		internInvokeDynamicConstant( invokeDynamicInstruction.invokeDynamicConstant );
	}

	private void internIndirectLoadConstantInstruction( IndirectLoadConstantInstruction indirectLoadConstantInstruction )
	{
		Constant constant = indirectLoadConstantInstruction.constant;
		switch( constant.tag )
		{
			case Constant.tagInteger, Constant.tagLong, Constant.tagFloat, Constant.tagDouble -> internConstant0( constant );
			case Constant.tagString -> internStringConstant( constant.asStringConstant() );
			case Constant.tagClass -> internClassConstant( constant.asClassConstant() );
			default -> throw new AssertionError( constant );
		}
	}

	private void internConstantReferencingInstruction( ConstantReferencingInstruction constantReferencingInstruction )
	{
		Constant constant = constantReferencingInstruction.constant;
		switch( constant.tag )
		{
			case Constant.tagFieldReference -> internReferenceConstant( constant.asFieldReferenceConstant() );
			case Constant.tagInterfaceMethodReference -> internReferenceConstant( constant.asInterfaceMethodReferenceConstant() );
			case Constant.tagMethodReference -> internReferenceConstant( constant.asMethodReferenceConstant() );
			case Constant.tagClass -> internClassConstant( constant.asClassConstant() );
			default -> throw new AssertionError( constant );
		}
	}

	private void internAnnotation( Annotation annotation )
	{
		internMutf8Constant( annotation.typeConstant );
		internAnnotationParameters( annotation.parameters );
	}

	private void internAnnotationParameters( Iterable<AnnotationParameter> annotationParameters )
	{
		for( AnnotationParameter annotationParameter : annotationParameters )
		{
			internMutf8Constant( annotationParameter.nameConstant );
			internAnnotationValue( annotationParameter.value );
		}
	}

	private void internStackMapFrame( StackMapFrame stackMapFrame )
	{
		if( stackMapFrame.isAppendStackMapFrame() ) //TODO use switch!
			internAppendStackMapFrame( stackMapFrame.asAppendStackMapFrame() );
		else if( stackMapFrame.isChopStackMapFrame() )
			internChopStackMapFrame( stackMapFrame.asChopStackMapFrame() );
		else if( stackMapFrame.isFullStackMapFrame() )
			internFullStackMapFrame( stackMapFrame.asFullStackMapFrame() );
		else if( stackMapFrame.isSameStackMapFrame() )
			internSameStackMapFrame( stackMapFrame.asSameStackMapFrame() );
		else if( stackMapFrame.isSameLocals1StackItemStackMapFrame() )
			internSameLocals1StackItemStackMapFrame( stackMapFrame.asSameLocals1StackItemStackMapFrame() );
		else
			assert false;
	}

	private void internAppendStackMapFrame( AppendStackMapFrame appendStackMapFrame )
	{
		for( VerificationType verificationType : appendStackMapFrame.localVerificationTypes() )
			internVerificationType( verificationType );
	}

	private void internChopStackMapFrame( ChopStackMapFrame chopStackMapFrame )
	{
		Kit.get( chopStackMapFrame ); //nothing to do.
	}

	private void internFullStackMapFrame( FullStackMapFrame fullStackMapFrame )
	{
		for( VerificationType verificationType : fullStackMapFrame.localVerificationTypes() )
			internVerificationType( verificationType );
		for( VerificationType verificationType : fullStackMapFrame.stackVerificationTypes() )
			internVerificationType( verificationType );
	}

	private void internSameStackMapFrame( SameStackMapFrame sameStackMapFrame )
	{
		Kit.get( sameStackMapFrame ); //nothing to do.
	}

	private void internSameLocals1StackItemStackMapFrame( SameLocals1StackItemStackMapFrame sameLocals1StackItemStackMapFrame )
	{
		internVerificationType( sameLocals1StackItemStackMapFrame.stackVerificationType() );
	}

	private void internVerificationType( VerificationType verificationType )
	{
		switch( verificationType.tag )
		{
			case VerificationType.tagTop, VerificationType.tagInteger, VerificationType.tagFloat, VerificationType.tagDouble, VerificationType.tagLong, //
				VerificationType.tagNull, VerificationType.tagUninitializedThis -> internSimpleVerificationType( verificationType.asSimpleVerificationType() );
			case VerificationType.tagObject -> internObjectVerificationType( verificationType.asObjectVerificationType() );
			case VerificationType.tagUninitialized -> internUninitializedVerificationType( verificationType.asUninitializedVerificationType() );
			default -> throw new AssertionError( verificationType );
		}
	}

	private static void internUninitializedVerificationType( UninitializedVerificationType uninitializedVerificationType )
	{
		Kit.get( uninitializedVerificationType ); //nothing to do
	}

	private static void internSimpleVerificationType( SimpleVerificationType simpleVerificationType )
	{
		Kit.get( simpleVerificationType ); //nothing to do
	}

	private void internObjectVerificationType( ObjectVerificationType objectVerificationType )
	{
		internClassConstant( objectVerificationType.classConstant() );
	}

	int size()
	{
		return entries.size();
	}

	Iterable<Constant> constants()
	{
		return Kit.iterable.filtered( entries, c -> c != null );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " entries";
	}
}
