package mikenakis.bytecode.writing;

import mikenakis.bytecode.exceptions.InvalidKnownAttributeTagException;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
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
import mikenakis.bytecode.model.attributes.DeprecatedAttribute;
import mikenakis.bytecode.model.attributes.EnclosingMethodAttribute;
import mikenakis.bytecode.model.attributes.ExceptionInfo;
import mikenakis.bytecode.model.attributes.ExceptionsAttribute;
import mikenakis.bytecode.model.attributes.InnerClass;
import mikenakis.bytecode.model.attributes.InnerClassesAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableEntry;
import mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTableEntry;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableEntry;
import mikenakis.bytecode.model.attributes.MethodParameter;
import mikenakis.bytecode.model.attributes.MethodParametersAttribute;
import mikenakis.bytecode.model.attributes.NestHostAttribute;
import mikenakis.bytecode.model.attributes.NestMembersAttribute;
import mikenakis.bytecode.model.attributes.ParameterAnnotationSet;
import mikenakis.bytecode.model.attributes.ParameterAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.SignatureAttribute;
import mikenakis.bytecode.model.attributes.SourceFileAttribute;
import mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import mikenakis.bytecode.model.attributes.SyntheticAttribute;
import mikenakis.bytecode.model.attributes.UnknownAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ClassConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.FieldConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchEntry;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MethodConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
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
import mikenakis.bytecode.model.attributes.target.CatchTarget;
import mikenakis.bytecode.model.attributes.target.EmptyTarget;
import mikenakis.bytecode.model.attributes.target.FormalParameterTarget;
import mikenakis.bytecode.model.attributes.target.LocalVariableTarget;
import mikenakis.bytecode.model.attributes.target.LocalVariableTargetEntry;
import mikenakis.bytecode.model.attributes.target.OffsetTarget;
import mikenakis.bytecode.model.attributes.target.SupertypeTarget;
import mikenakis.bytecode.model.attributes.target.Target;
import mikenakis.bytecode.model.attributes.target.ThrowsTarget;
import mikenakis.bytecode.model.attributes.target.TypeArgumentTarget;
import mikenakis.bytecode.model.attributes.target.TypeParameterBoundTarget;
import mikenakis.bytecode.model.attributes.target.TypeParameterTarget;
import mikenakis.bytecode.model.attributes.target.TypePathEntry;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ByteCodeWriter
{
	public static byte[] write( ByteCodeType byteCodeType )
	{
		BufferWriter bufferWriter = new BufferWriter();
		bufferWriter.writeInt( ByteCodeType.MAGIC );
		bufferWriter.writeUnsignedShort( byteCodeType.version.minor() );
		bufferWriter.writeUnsignedShort( byteCodeType.version.major() );

		ConstantPool constantPool = new ConstantPool();
		constantPool.internClassConstant( byteCodeType.classConstant() );
		byteCodeType.superClassConstant().ifPresent( c -> constantPool.internClassConstant( c ) );
		for( ClassConstant classConstant : byteCodeType.interfaceClassConstants() )
			constantPool.internClassConstant( classConstant );
		for( ByteCodeField field : byteCodeType.fields )
			constantPool.internField( field );

		BootstrapPool bootstrapPool = new BootstrapPool();
		for( ByteCodeMethod method : byteCodeType.methods )
		{
			constantPool.internMethod( method );
			method.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_Code ) //
				.map( attribute -> attribute.asCodeAttribute() ) //
				.ifPresent( codeAttribute -> //
				{
					for( Instruction instruction : codeAttribute.instructions.all() )
						if( instruction.groupTag == Instruction.groupTag_InvokeDynamic )
							bootstrapPool.intern( instruction.asInvokeDynamicInstruction().invokeDynamicConstant.getBootstrapMethod() );
				} );
		}
		if( !bootstrapPool.bootstrapMethods().isEmpty() )
		{
			BootstrapMethodsAttribute bootstrapMethodsAttribute = BootstrapMethodsAttribute.of();
			bootstrapMethodsAttribute.bootstrapMethods.addAll( bootstrapPool.bootstrapMethods() ); // TODO perhaps replace the bootstrapPool with the bootstrapMethodsAttribute
			byteCodeType.attributeSet.addOrReplaceAttribute( bootstrapMethodsAttribute );
		}

		constantPool.internAttributeSet( byteCodeType.attributeSet );

		for( Constant extraConstant : byteCodeType.extraClassConstants )
			constantPool.internExtraConstant( extraConstant );

		//TODO: optimize the constant pool by moving the constants most frequently used by the IndirectLoadConstantInstruction to the first 256 entries!

		bufferWriter.writeUnsignedShort( constantPool.size() );
		for( Constant constant : constantPool.constants() )
			writeConstant( constantPool, bootstrapPool, bufferWriter, constant );
		bufferWriter.writeUnsignedShort( byteCodeType.modifiers.getBits() );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( byteCodeType.classConstant() ) );
		bufferWriter.writeUnsignedShort( byteCodeType.superClassConstant().map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
		List<ClassConstant> interfaceClassConstants = byteCodeType.interfaceClassConstants();
		bufferWriter.writeUnsignedShort( interfaceClassConstants.size() );
		for( ClassConstant interfaceClassConstant : interfaceClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( interfaceClassConstant ) );
		bufferWriter.writeUnsignedShort( byteCodeType.fields.size() );
		for( ByteCodeField field : byteCodeType.fields )
		{
			bufferWriter.writeUnsignedShort( field.modifiers.getBits() );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( field.memberNameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( field.fieldDescriptorStringConstant ) );
			writeAttributeSet( bufferWriter, constantPool, field.attributeSet, Optional.empty() );
		}
		bufferWriter.writeUnsignedShort( byteCodeType.methods.size() );
		for( ByteCodeMethod method : byteCodeType.methods )
		{
			bufferWriter.writeUnsignedShort( method.modifiers.getBits() );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( method.memberNameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( method.methodDescriptorStringConstant ) );
			writeAttributeSet( bufferWriter, constantPool, method.attributeSet, Optional.empty() );
		}
		writeAttributeSet( bufferWriter, constantPool, byteCodeType.attributeSet, Optional.empty() );
		return bufferWriter.toBytes();
	}

	private static void writeConstant( ConstantPool constantPool, BootstrapPool bootstrapPool, BufferWriter bufferWriter, Constant constant )
	{
		bufferWriter.writeUnsignedByte( constant.tag );
		switch( constant.tag )
		{
			case Constant.tag_Mutf8 -> writeMutf8Constant( bufferWriter, constant.asMutf8Constant() );
			case Constant.tag_Integer -> writeIntegerConstant( bufferWriter, constant.asIntegerConstant() );
			case Constant.tag_Float -> writeFloatConstant( bufferWriter, constant.asFloatConstant() );
			case Constant.tag_Long -> writeLongConstant( bufferWriter, constant.asLongConstant() );
			case Constant.tag_Double -> writeDoubleConstant( bufferWriter, constant.asDoubleConstant() );
			case Constant.tag_Class -> writeClassConstant( constantPool, bufferWriter, constant.asClassConstant() );
			case Constant.tag_String -> writeStringConstant( constantPool, bufferWriter, constant.asStringConstant() );
			case Constant.tag_FieldReference -> writeFieldReferenceConstant( constantPool, bufferWriter, constant.asFieldReferenceConstant() );
			case Constant.tag_PlainMethodReference, Constant.tag_InterfaceMethodReference -> writeMethodReferenceConstant( constantPool, bufferWriter, constant.asMethodReferenceConstant() );
			case Constant.tag_NameAndDescriptor -> writeNameAndDescriptorConstant( constantPool, bufferWriter, constant.asNameAndDescriptorConstant() );
			case Constant.tag_MethodHandle -> writeMethodHandleConstant( constantPool, bufferWriter, constant.asMethodHandleConstant() );
			case Constant.tag_MethodType -> writeMethodTypeConstant( constantPool, bufferWriter, constant.asMethodTypeConstant() );
			case Constant.tag_InvokeDynamic -> writeInvokeDynamicConstant( constantPool, bootstrapPool, bufferWriter, constant.asInvokeDynamicConstant() );
			default -> throw new AssertionError( constant );
		}
	}

	private static void writeMutf8Constant( BufferWriter bufferWriter, Mutf8Constant mutf8Constant )
	{
		Buffer buffer = mutf8Constant.buffer();
		bufferWriter.writeUnsignedShort( buffer.length() );
		bufferWriter.writeBuffer( buffer );
	}

	private static void writeIntegerConstant( BufferWriter bufferWriter, IntegerConstant integerConstant )
	{
		bufferWriter.writeInt( integerConstant.value );
	}

	private static void writeFloatConstant( BufferWriter bufferWriter, FloatConstant floatConstant )
	{
		bufferWriter.writeFloat( floatConstant.value );
	}

	private static void writeLongConstant( BufferWriter bufferWriter, LongConstant longConstant )
	{
		bufferWriter.writeLong( longConstant.value );
	}

	private static void writeDoubleConstant( BufferWriter bufferWriter, DoubleConstant doubleConstant )
	{
		bufferWriter.writeDouble( doubleConstant.value );
	}

	private static void writeClassConstant( ConstantPool constantPool, BufferWriter bufferWriter, ClassConstant classConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( classConstant.getInternalNameOrDescriptorStringConstant() ) );
	}

	private static void writeStringConstant( ConstantPool constantPool, BufferWriter bufferWriter, StringConstant stringConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( stringConstant.getValueConstant() ) );
	}

	private static void writeFieldReferenceConstant( ConstantPool constantPool, BufferWriter bufferWriter, FieldReferenceConstant fieldReferenceConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( fieldReferenceConstant.getDeclaringTypeConstant() ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( fieldReferenceConstant.getNameAndDescriptorConstant() ) );
	}

	private static void writeMethodReferenceConstant( ConstantPool constantPool, BufferWriter bufferWriter, MethodReferenceConstant methodReferenceConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodReferenceConstant.getDeclaringTypeConstant() ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodReferenceConstant.getNameAndDescriptorConstant() ) );
	}

	private static void writeNameAndDescriptorConstant( ConstantPool constantPool, BufferWriter bufferWriter, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nameAndDescriptorConstant.getNameConstant() ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nameAndDescriptorConstant.getDescriptorConstant() ) );
	}

	private static void writeMethodHandleConstant( ConstantPool constantPool, BufferWriter bufferWriter, MethodHandleConstant methodHandleConstant )
	{
		bufferWriter.writeUnsignedByte( methodHandleConstant.referenceKind().number );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodHandleConstant.getReferenceConstant() ) );
	}

	private static void writeMethodTypeConstant( ConstantPool constantPool, BufferWriter bufferWriter, MethodTypeConstant methodTypeConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodTypeConstant.getDescriptorConstant() ) );
	}

	private static void writeInvokeDynamicConstant( ConstantPool constantPool, BootstrapPool bootstrapPool, BufferWriter bufferWriter, InvokeDynamicConstant invokeDynamicConstant )
	{
		int bootstrapMethodIndex = bootstrapPool.getIndex( invokeDynamicConstant.getBootstrapMethod() );
		bufferWriter.writeUnsignedShort( bootstrapMethodIndex );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( invokeDynamicConstant.getNameAndDescriptorConstant() ) );
	}

	private static void writeAttribute( ConstantPool constantPool, Attribute attribute, BufferWriter bufferWriter, Optional<LocationMap> locationMap )
	{
		if( attribute.isKnown() )
		{
			KnownAttribute knownAttribute = attribute.asKnownAttribute();
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tag_AnnotationDefault -> writeAnnotationDefaultAttribute( constantPool, bufferWriter, knownAttribute.asAnnotationDefaultAttribute() );
				case KnownAttribute.tag_BootstrapMethods -> writeBootstrapMethodsAttribute( constantPool, bufferWriter, knownAttribute.asBootstrapMethodsAttribute() );
				case KnownAttribute.tag_Code -> writeCodeAttribute( constantPool, bufferWriter, knownAttribute.asCodeAttribute() );
				case KnownAttribute.tag_ConstantValue -> writeConstantValueAttribute( constantPool, bufferWriter, knownAttribute.asConstantValueAttribute() );
				case KnownAttribute.tag_Deprecated -> writeDeprecatedAttribute( knownAttribute.asDeprecatedAttribute() );
				case KnownAttribute.tag_EnclosingMethod -> writeEnclosingMethodAttribute( constantPool, bufferWriter, knownAttribute.asEnclosingMethodAttribute() );
				case KnownAttribute.tag_Exceptions -> writeExceptionsAttribute( constantPool, bufferWriter, knownAttribute.asExceptionsAttribute() );
				case KnownAttribute.tag_InnerClasses -> writeInnerClassesAttribute( constantPool, bufferWriter, knownAttribute.asInnerClassesAttribute() );
				case KnownAttribute.tag_LineNumberTable -> writeLineNumberTableAttribute( bufferWriter, locationMap, knownAttribute.asLineNumberTableAttribute() );
				case KnownAttribute.tag_LocalVariableTable -> writeLocalVariableTableAttribute( constantPool, bufferWriter, locationMap.orElseThrow(), knownAttribute.asLocalVariableTableAttribute() );
				case KnownAttribute.tag_LocalVariableTypeTable -> writeLocalVariableTypeTableAttribute( constantPool, bufferWriter, locationMap.orElseThrow(), knownAttribute.asLocalVariableTypeTableAttribute() );
				case KnownAttribute.tag_MethodParameters -> writeMethodParametersAttribute( constantPool, bufferWriter, knownAttribute.asMethodParametersAttribute() );
				case KnownAttribute.tag_NestHost -> writeNestHostAttribute( constantPool, bufferWriter, knownAttribute.asNestHostAttribute() );
				case KnownAttribute.tag_NestMembers -> writeNestMembersAttribute( constantPool, bufferWriter, knownAttribute.asNestMembersAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleAnnotations -> writeAnnotationsAttribute( constantPool, bufferWriter, knownAttribute.asRuntimeInvisibleAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleParameterAnnotations -> writeParameterAnnotationsAttribute( constantPool, bufferWriter, knownAttribute.asRuntimeInvisibleParameterAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> writeTypeAnnotations( constantPool, bufferWriter, knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute().typeAnnotations );
				case KnownAttribute.tag_RuntimeVisibleAnnotations -> writeAnnotationsAttribute( constantPool, bufferWriter, knownAttribute.asRuntimeVisibleAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleParameterAnnotations -> writeParameterAnnotationsAttribute( constantPool, bufferWriter, knownAttribute.asRuntimeVisibleParameterAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> writeTypeAnnotations( constantPool, bufferWriter, knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute().typeAnnotations );
				case KnownAttribute.tag_Signature -> writeSignatureAttribute( constantPool, bufferWriter, knownAttribute.asSignatureAttribute() );
				case KnownAttribute.tag_SourceFile -> writeSourceFileAttribute( constantPool, bufferWriter, knownAttribute.asSourceFileAttribute() );
				case KnownAttribute.tag_StackMapTable -> writeStackMapTableAttribute( constantPool, bufferWriter, locationMap.orElseThrow(), knownAttribute.asStackMapTableAttribute() );
				case KnownAttribute.tag_Synthetic -> writeSyntheticAttribute( knownAttribute.asSyntheticAttribute() );
				default -> throw new InvalidKnownAttributeTagException( knownAttribute.tag );
			}
		}
		else
		{
			UnknownAttribute unknownAttribute = attribute.asUnknownAttribute();
			bufferWriter.writeBuffer( unknownAttribute.buffer() );
		}
	}

	private static void writeParameterAnnotationsAttribute( ConstantPool constantPool, BufferWriter bufferWriter, ParameterAnnotationsAttribute parameterAnnotationsAttribute )
	{
		writeParameterAnnotationSets( constantPool, bufferWriter, parameterAnnotationsAttribute.parameterAnnotationSets );
	}

	private static void writeSourceFileAttribute( ConstantPool constantPool, BufferWriter bufferWriter, SourceFileAttribute sourceFileAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( sourceFileAttribute.valueConstant ) );
	}

	private static void writeSignatureAttribute( ConstantPool constantPool, BufferWriter bufferWriter, SignatureAttribute signatureAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( signatureAttribute.signatureConstant ) );
	}

	private static void writeAnnotationsAttribute( ConstantPool constantPool, BufferWriter bufferWriter, AnnotationsAttribute annotationsAttribute )
	{
		writeAnnotations( constantPool, bufferWriter, annotationsAttribute.annotations );
	}

	private static void writeSyntheticAttribute( SyntheticAttribute syntheticAttribute )
	{
		Kit.get( syntheticAttribute ); //nothing to do
	}

	private static void writeAnnotationDefaultAttribute( ConstantPool constantPool, BufferWriter bufferWriter, AnnotationDefaultAttribute annotationDefaultAttribute )
	{
		AnnotationValue annotationValue = annotationDefaultAttribute.annotationValue();
		bufferWriter.writeUnsignedByte( annotationValue.tag );
		switch( annotationValue.tag )
		{
			case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger, //
				AnnotationValue.tagLong, AnnotationValue.tagShort, AnnotationValue.tagString -> writeConstAnnotationValue( constantPool, bufferWriter, annotationValue.asConstAnnotationValue() );
			case AnnotationValue.tagAnnotation -> writeAnnotationAnnotationValue( constantPool, bufferWriter, annotationValue.asAnnotationAnnotationValue() );
			case AnnotationValue.tagArray -> writeArrayAnnotationValue( constantPool, bufferWriter, annotationValue.asArrayAnnotationValue() );
			case AnnotationValue.tagClass -> writeClassAnnotationValue( constantPool, bufferWriter, annotationValue.asClassAnnotationValue() );
			case AnnotationValue.tagEnum -> writeEnumAnnotationValue( constantPool, bufferWriter, annotationValue.asEnumAnnotationValue() );
			default -> throw new AssertionError( annotationValue );
		}
	}

	private static void writeBootstrapMethodsAttribute( ConstantPool constantPool, BufferWriter bufferWriter, BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		List<BootstrapMethod> bootstrapMethods = bootstrapMethodsAttribute.bootstrapMethods;
		bufferWriter.writeUnsignedShort( bootstrapMethods.size() );
		for( BootstrapMethod bootstrapMethod : bootstrapMethods )
		{
			bufferWriter.writeUnsignedShort( constantPool.getIndex( bootstrapMethod.methodHandleConstant ) );
			List<Constant> argumentConstants = bootstrapMethod.argumentConstants;
			bufferWriter.writeUnsignedShort( argumentConstants.size() );
			for( Constant argumentConstant : argumentConstants )
				bufferWriter.writeUnsignedShort( constantPool.getIndex( argumentConstant ) );
		}
	}

	private static void writeConstantValueAttribute( ConstantPool constantPool, BufferWriter bufferWriter, ConstantValueAttribute constantValueAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( constantValueAttribute.valueConstant ) );
	}

	private static void writeDeprecatedAttribute( DeprecatedAttribute deprecatedAttribute )
	{
		Kit.get( deprecatedAttribute ); // nothing to do
	}

	private static void writeEnclosingMethodAttribute( ConstantPool constantPool, BufferWriter bufferWriter, EnclosingMethodAttribute enclosingMethodAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enclosingMethodAttribute.enclosingClassConstant ) );
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = enclosingMethodAttribute.enclosingMethodNameAndDescriptorConstant;
		bufferWriter.writeUnsignedShort( methodNameAndDescriptorConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
	}

	private static void writeExceptionsAttribute( ConstantPool constantPool, BufferWriter bufferWriter, ExceptionsAttribute exceptionsAttribute )
	{
		bufferWriter.writeUnsignedShort( exceptionsAttribute.exceptionClassConstants.size() );
		for( ClassConstant exceptionClassConstant : exceptionsAttribute.exceptionClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( exceptionClassConstant ) );
	}

	private static void writeInnerClassesAttribute( ConstantPool constantPool, BufferWriter bufferWriter, InnerClassesAttribute innerClassesAttribute )
	{
		List<InnerClass> innerClasses = innerClassesAttribute.innerClasses;
		bufferWriter.writeUnsignedShort( innerClasses.size() );
		for( InnerClass innerClass : innerClasses )
		{
			bufferWriter.writeUnsignedShort( constantPool.getIndex( innerClass.innerClassConstant ) );
			bufferWriter.writeUnsignedShort( innerClass.outerClassConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
			bufferWriter.writeUnsignedShort( innerClass.innerNameConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
			bufferWriter.writeUnsignedShort( innerClass.modifiers.getBits() );
		}
	}

	private static void writeLineNumberTableAttribute( BufferWriter bufferWriter, Optional<LocationMap> locationMap, LineNumberTableAttribute lineNumberTableAttribute )
	{
		bufferWriter.writeUnsignedShort( lineNumberTableAttribute.entrys.size() );
		for( LineNumberTableEntry lineNumber : lineNumberTableAttribute.entrys )
		{
			int location = locationMap.orElseThrow().getLocation( lineNumber.instruction() );
			bufferWriter.writeUnsignedShort( location );
			bufferWriter.writeUnsignedShort( lineNumber.lineNumber() );
		}
	}

	private static void writeMethodParametersAttribute( ConstantPool constantPool, BufferWriter bufferWriter, MethodParametersAttribute methodParametersAttribute )
	{
		bufferWriter.writeUnsignedByte( methodParametersAttribute.methodParameters.size() );
		for( MethodParameter methodParameter : methodParametersAttribute.methodParameters )
		{
			bufferWriter.writeUnsignedShort( constantPool.getIndex( methodParameter.nameConstant ) );
			bufferWriter.writeUnsignedShort( methodParameter.modifiers.getBits() );
		}
	}

	private static void writeNestHostAttribute( ConstantPool constantPool, BufferWriter bufferWriter, NestHostAttribute nestHostAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nestHostAttribute.hostClassConstant ) );
	}

	private static void writeNestMembersAttribute( ConstantPool constantPool, BufferWriter bufferWriter, NestMembersAttribute nestMembersAttribute )
	{
		bufferWriter.writeUnsignedShort( nestMembersAttribute.memberClassConstants.size() );
		for( ClassConstant memberClassConstant : nestMembersAttribute.memberClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( memberClassConstant ) );
	}

	private static void writeStackMapTableAttribute( ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, StackMapTableAttribute stackMapTableAttribute )
	{
		List<StackMapFrame> frames = stackMapTableAttribute.frames();
		bufferWriter.writeUnsignedShort( frames.size() );
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( StackMapFrame frame : frames )
		{
			if( frame.isAppendStackMapFrame() ) //TODO use switch!
				writeAppendStackMapFrame( constantPool, bufferWriter, locationMap, previousFrame, frame.asAppendStackMapFrame() );
			else if( frame.isChopStackMapFrame() )
				writeChopStackMapFrame( bufferWriter, locationMap, previousFrame, frame.asChopStackMapFrame() );
			else if( frame.isFullStackMapFrame() )
				writeFullStackMapFrame( constantPool, bufferWriter, locationMap, previousFrame, frame.asFullStackMapFrame() );
			else if( frame.isSameStackMapFrame() )
				writeSameStackMapFrame( bufferWriter, locationMap, previousFrame, frame.asSameStackMapFrame() );
			else if( frame.isSameLocals1StackItemStackMapFrame() )
				writeSameLocals1StackItemStackMapFrame( constantPool, bufferWriter, locationMap, previousFrame, frame.asSameLocals1StackItemStackMapFrame() );
			else
				assert false;
			previousFrame = Optional.of( frame );
		}
	}

	private static void writeSameLocals1StackItemStackMapFrame( ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, Optional<StackMapFrame> previousFrame, SameLocals1StackItemStackMapFrame sameLocals1StackItemStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( sameLocals1StackItemStackMapFrame, previousFrame, locationMap );
		if( offsetDelta <= 127 )
			bufferWriter.writeUnsignedByte( 64 + offsetDelta );
		else
		{
			bufferWriter.writeUnsignedByte( SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE );
			bufferWriter.writeUnsignedShort( offsetDelta );
		}
		writeVerificationType( constantPool, bufferWriter, sameLocals1StackItemStackMapFrame.stackVerificationType(), locationMap );
	}

	private static void writeSameStackMapFrame( BufferWriter bufferWriter, LocationMap locationMap, Optional<StackMapFrame> previousFrame, SameStackMapFrame sameStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( sameStackMapFrame, previousFrame, locationMap );
		if( offsetDelta >= 0 && offsetDelta <= 63 )
			bufferWriter.writeUnsignedByte( offsetDelta );
		else
		{
			bufferWriter.writeUnsignedByte( SameStackMapFrame.EXTENDED_FRAME_TYPE );
			bufferWriter.writeUnsignedShort( offsetDelta );
		}
	}

	private static void writeFullStackMapFrame( ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, Optional<StackMapFrame> previousFrame, FullStackMapFrame fullStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( fullStackMapFrame, previousFrame, locationMap );
		bufferWriter.writeUnsignedByte( FullStackMapFrame.type );
		bufferWriter.writeUnsignedShort( offsetDelta );
		List<VerificationType> localVerificationTypes = fullStackMapFrame.localVerificationTypes();
		bufferWriter.writeUnsignedShort( localVerificationTypes.size() );
		for( VerificationType verificationType : localVerificationTypes )
			writeVerificationType( constantPool, bufferWriter, verificationType, locationMap );
		List<VerificationType> stackVerificationTypes = fullStackMapFrame.stackVerificationTypes();
		bufferWriter.writeUnsignedShort( stackVerificationTypes.size() );
		for( VerificationType verificationType : stackVerificationTypes )
			writeVerificationType( constantPool, bufferWriter, verificationType, locationMap );
	}

	private static void writeChopStackMapFrame( BufferWriter bufferWriter, LocationMap locationMap, Optional<StackMapFrame> previousFrame, ChopStackMapFrame chopStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( chopStackMapFrame, previousFrame, locationMap );
		bufferWriter.writeUnsignedByte( 251 - chopStackMapFrame.count() );
		bufferWriter.writeUnsignedShort( offsetDelta );
	}

	private static void writeAppendStackMapFrame( ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, Optional<StackMapFrame> previousFrame, AppendStackMapFrame appendStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( appendStackMapFrame, previousFrame, locationMap );
		List<VerificationType> verificationTypes = appendStackMapFrame.localVerificationTypes();
		assert verificationTypes.size() <= 3;
		bufferWriter.writeUnsignedByte( 251 + verificationTypes.size() );
		bufferWriter.writeUnsignedShort( offsetDelta );
		for( VerificationType verificationType : verificationTypes )
			writeVerificationType( constantPool, bufferWriter, verificationType, locationMap );
	}

	private static void writeLocalVariableTypeTableAttribute( ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, LocalVariableTypeTableAttribute localVariableTypeTableAttribute )
	{
		List<LocalVariableTypeTableEntry> localVariableTypes = localVariableTypeTableAttribute.localVariableTypes;
		bufferWriter.writeUnsignedShort( localVariableTypes.size() );
		for( LocalVariableTypeTableEntry localVariableType : localVariableTypes )
		{
			int startLocation = locationMap.getLocation( localVariableType.startInstruction );
			int endLocation = locationMap.getLocation( localVariableType.endInstruction );
			bufferWriter.writeUnsignedShort( startLocation );
			bufferWriter.writeUnsignedShort( endLocation - startLocation );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariableType.nameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariableType.signatureConstant ) );
			bufferWriter.writeUnsignedShort( localVariableType.index );
		}
	}

	private static void writeLocalVariableTableAttribute( ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, LocalVariableTableAttribute localVariableTableAttribute )
	{
		List<LocalVariableTableEntry> localVariables = localVariableTableAttribute.entrys;
		bufferWriter.writeUnsignedShort( localVariables.size() );
		for( LocalVariableTableEntry localVariable : localVariables )
		{
			int startLocation = locationMap.getLocation( localVariable.startInstruction );
			int endLocation = locationMap.getLocation( localVariable.endInstruction );
			bufferWriter.writeUnsignedShort( startLocation );
			bufferWriter.writeUnsignedShort( endLocation - startLocation );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariable.variableNameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariable.variableTypeDescriptorStringConstant ) );
			bufferWriter.writeUnsignedShort( localVariable.variableIndex );
		}
	}
	private static void writeCodeAttribute( ConstantPool constantPool, BufferWriter bufferWriter, CodeAttribute codeAttribute )
	{
		bufferWriter.writeUnsignedShort( codeAttribute.getMaxStack() );
		bufferWriter.writeUnsignedShort( codeAttribute.getMaxLocals() );

		LocationMap locationMap = getLocationMap( codeAttribute.instructions.all(), constantPool );
		RealInstructionWriter instructionWriter = new RealInstructionWriter( locationMap, constantPool );
		for( Instruction instruction : codeAttribute.instructions.all() )
			writeInstruction( instruction, instructionWriter );
		byte[] bytes = instructionWriter.toBytes();
		bufferWriter.writeInt( bytes.length );
		bufferWriter.writeBytes( bytes );

		List<ExceptionInfo> exceptionInfos = codeAttribute.exceptionInfos;
		bufferWriter.writeUnsignedShort( exceptionInfos.size() );
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos )
		{
			bufferWriter.writeUnsignedShort( locationMap.getLocation( exceptionInfo.startInstruction ) );
			bufferWriter.writeUnsignedShort( locationMap.getLocation( exceptionInfo.endInstruction ) );
			bufferWriter.writeUnsignedShort( locationMap.getLocation( exceptionInfo.handlerInstruction ) );
			bufferWriter.writeUnsignedShort( exceptionInfo.catchTypeConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
		}

		writeAttributeSet( bufferWriter, constantPool, codeAttribute.attributeSet, Optional.of( locationMap ) );
	}

	private static void writeParameterAnnotationSets( ConstantPool constantPool, BufferWriter bufferWriter, Collection<ParameterAnnotationSet> parameterAnnotationSets )
	{
		bufferWriter.writeUnsignedByte( parameterAnnotationSets.size() );
		for( ParameterAnnotationSet parameterAnnotationSet : parameterAnnotationSets )
			writeAnnotations( constantPool, bufferWriter, parameterAnnotationSet.annotations );
	}

	private static void writeTypeAnnotations( ConstantPool constantPool, BufferWriter bufferWriter, Collection<TypeAnnotation> typeAnnotations )
	{
		bufferWriter.writeUnsignedShort( typeAnnotations.size() );
		for( TypeAnnotation typeAnnotation : typeAnnotations )
		{
			Target target = typeAnnotation.target;
			bufferWriter.writeUnsignedByte( target.tag );
			switch( target.tag )
			{
				case Target.tag_ClassTypeParameter, Target.tag_MethodTypeParameter -> writeTypeParameterTarget( bufferWriter, target.asTypeParameterTarget() );
				case Target.tag_Supertype -> writeSupertypeTarget( bufferWriter, target.asSupertypeTarget() );
				case Target.tag_ClassTypeBound, Target.tag_MethodTypeBound -> writeTypeParameterBoundTarget( bufferWriter, target.asTypeParameterBoundTarget() );
				case Target.tag_FieldType, Target.tag_ReturnType, Target.tag_ReceiverType -> writeEmptyTarget( target.asEmptyTarget() );
				case Target.tag_FormalParameter -> writeFormalParameterTarget( bufferWriter, target.asFormalParameterTarget() );
				case Target.tag_Throws -> writeThrowsTarget( bufferWriter, target.asThrowsTarget() );
				case Target.tag_LocalVariable, Target.tag_ResourceLocalVariable -> writeLocalVariableTarget( bufferWriter, target.asLocalVariableTarget() );
				case Target.tag_Catch -> writeCatchTarget( bufferWriter, target.asCatchTarget() );
				case Target.tag_InstanceOfOffset, Target.tag_NewExpressionOffset, Target.tag_NewMethodOffset, Target.tag_IdentifierMethodOffset -> //
					writeOffsetTarget( bufferWriter, target.asOffsetTarget() );
				case Target.tag_CastArgument, Target.tag_ConstructorArgument, Target.tag_MethodArgument, Target.tag_NewMethodArgument, //
					Target.tag_IdentifierMethodArgument -> writeTypeArgumentTarget( bufferWriter, target.asTypeArgumentTarget() );
				default -> throw new AssertionError( target );
			}
			List<TypePathEntry> entries = typeAnnotation.typePath.entries();
			bufferWriter.writeUnsignedByte( entries.size() );
			for( TypePathEntry entry : entries )
			{
				bufferWriter.writeUnsignedByte( entry.pathKind() );
				bufferWriter.writeUnsignedByte( entry.argumentIndex() );
			}
			bufferWriter.writeUnsignedShort( typeAnnotation.typeIndex );
			writeAnnotationParameters( constantPool, bufferWriter, typeAnnotation.parameters );
		}
	}

	private static void writeTypeArgumentTarget( BufferWriter bufferWriter, TypeArgumentTarget typeArgumentTarget )
	{
		bufferWriter.writeUnsignedShort( typeArgumentTarget.offset );
		bufferWriter.writeUnsignedByte( typeArgumentTarget.typeArgumentIndex );
	}

	private static void writeOffsetTarget( BufferWriter bufferWriter, OffsetTarget offsetTarget )
	{
		bufferWriter.writeUnsignedShort( offsetTarget.offset );
	}

	private static void writeCatchTarget( BufferWriter bufferWriter, CatchTarget catchTarget )
	{
		bufferWriter.writeUnsignedShort( catchTarget.exceptionTableIndex );
	}

	private static void writeLocalVariableTarget( BufferWriter bufferWriter, LocalVariableTarget localVariableTarget )
	{
		bufferWriter.writeUnsignedShort( localVariableTarget.entries.size() );
		for( LocalVariableTargetEntry entry : localVariableTarget.entries )
		{
			bufferWriter.writeUnsignedShort( entry.startPc() );
			bufferWriter.writeUnsignedShort( entry.length() );
			bufferWriter.writeUnsignedShort( entry.index() );
		}
	}

	private static void writeThrowsTarget( BufferWriter bufferWriter, ThrowsTarget throwsTarget )
	{
		bufferWriter.writeUnsignedShort( throwsTarget.throwsTypeIndex );
	}

	private static void writeFormalParameterTarget( BufferWriter bufferWriter, FormalParameterTarget formalParameterTarget )
	{
		bufferWriter.writeUnsignedByte( formalParameterTarget.formalParameterIndex );
	}

	private static void writeEmptyTarget( EmptyTarget emptyTarget )
	{
		Kit.get( emptyTarget ); //nothing to do
	}

	private static void writeTypeParameterBoundTarget( BufferWriter bufferWriter, TypeParameterBoundTarget typeParameterBoundTarget )
	{
		bufferWriter.writeUnsignedByte( typeParameterBoundTarget.typeParameterIndex );
		bufferWriter.writeUnsignedByte( typeParameterBoundTarget.boundIndex );
	}

	private static void writeSupertypeTarget( BufferWriter bufferWriter, SupertypeTarget supertypeTarget )
	{
		bufferWriter.writeUnsignedByte( supertypeTarget.supertypeIndex );
	}

	private static void writeTypeParameterTarget( BufferWriter bufferWriter, TypeParameterTarget typeParameterTarget )
	{
		bufferWriter.writeUnsignedByte( typeParameterTarget.typeParameterIndex );
	}

	private static void writeAttributeSet( BufferWriter bufferWriter, ConstantPool constantPool, AttributeSet attributeSet, Optional<LocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( attributeSet.size() );
		for( Attribute attribute : attributeSet.allAttributes() )
		{
			Mutf8Constant nameConstant = attribute.mutf8Name;
			bufferWriter.writeUnsignedShort( constantPool.getIndex( nameConstant ) );
			BufferWriter attributeBufferWriter = new BufferWriter();
			writeAttribute( constantPool, attribute, attributeBufferWriter, locationMap );
			byte[] bytes = attributeBufferWriter.toBytes();
			bufferWriter.writeInt( bytes.length );
			bufferWriter.writeBytes( bytes );
		}
	}

	private static LocationMap getLocationMap( Iterable<Instruction> instructions, ConstantPool constantPool )
	{
		WritingLocationMap writingLocationMap = new WritingLocationMap();
		FakeInstructionWriter instructionWriter = new FakeInstructionWriter( constantPool, writingLocationMap );
		for( Instruction instruction : instructions )
		{
			int startLocation = instructionWriter.location;
			writingLocationMap.add( instruction );
			writeInstruction( instruction, instructionWriter );
			int length = instructionWriter.location - startLocation;
			writingLocationMap.setLength( instruction, length );
		}

		for( ; ; )
		{
			boolean anyWorkDone = false;
			for( Instruction instruction : instructionWriter.sourceInstructions )
			{
				int location = writingLocationMap.getLocation( instruction );
				instructionWriter.location = location;
				int oldLength = writingLocationMap.getLength( instruction );
				writeInstruction( instruction, instructionWriter );
				int newLength = instructionWriter.location - location;
				assert newLength <= oldLength;
				if( newLength == oldLength )
					continue;
				writingLocationMap.removeBytes( location + newLength, oldLength - newLength );
				anyWorkDone = true;
			}
			if( !anyWorkDone )
				break;
		}

		return writingLocationMap;
	}

	private static void writeInstruction( Instruction instruction, InstructionWriter instructionWriter )
	{
		switch( instruction.groupTag )
		{
			case Instruction.groupTag_Branch -> writeBranchInstruction( instructionWriter, instruction.asBranchInstruction() );
			case Instruction.groupTag_ConditionalBranch -> writeConditionalBranchInstruction( instructionWriter, instruction.asConditionalBranchInstruction() );
			case Instruction.groupTag_ClassConstantReferencing -> writeClassConstantReferencingInstruction( instructionWriter, instruction.asClassConstantReferencingInstruction() );
			case Instruction.groupTag_FieldConstantReferencing -> writeFieldConstantReferencingInstruction( instructionWriter, instruction.asFieldConstantReferencingInstruction() );
			case Instruction.groupTag_IInc -> writeIIncInstruction( instructionWriter, instruction.asIIncInstruction() );
			case Instruction.groupTag_InvokeDynamic -> writeInvokeDynamicInstruction( instructionWriter, instruction.asInvokeDynamicInstruction() );
			case Instruction.groupTag_InvokeInterface -> writeInvokeInterfaceInstruction( instructionWriter, instruction.asInvokeInterfaceInstruction() );
			case Instruction.groupTag_LocalVariable -> writeLocalVariableInstruction( instructionWriter, instruction.asLocalVariableInstruction() );
			case Instruction.groupTag_LookupSwitch -> writeLookupSwitchInstruction( instructionWriter, instruction.asLookupSwitchInstruction() );
			case Instruction.groupTag_MethodConstantReferencing -> writeMethodConstantReferencingInstruction( instructionWriter, instruction.asMethodConstantReferencingInstruction() );
			case Instruction.groupTag_MultiANewArray -> writeMultiANewArrayInstruction( instructionWriter, instruction.asMultiANewArrayInstruction() );
			case Instruction.groupTag_NewPrimitiveArray -> writeNewPrimitiveArrayInstruction( instructionWriter, instruction.asNewPrimitiveArrayInstruction() );
			case Instruction.groupTag_Operandless -> writeOperandlessInstruction( instructionWriter, instruction.asOperandlessInstruction() );
			case Instruction.groupTag_TableSwitch -> writeTableSwitchInstruction( instructionWriter, instruction.asTableSwitchInstruction() );
			case Instruction.groupTag_LoadConstant -> writeLoadConstantInstruction( instructionWriter, instruction.asLoadConstantInstruction() );
			default -> throw new AssertionError( instruction );
		}
	}

	private static void writeTableSwitchInstruction( InstructionWriter instructionWriter, TableSwitchInstruction tableSwitchInstruction )
	{
		instructionWriter.writeUnsignedByte( OpCode.TABLESWITCH );
		instructionWriter.skipToAlign();
		int defaultInstructionOffset = instructionWriter.getOffset( tableSwitchInstruction, tableSwitchInstruction.getDefaultInstruction() );
		instructionWriter.writeInt( defaultInstructionOffset );
		instructionWriter.writeInt( tableSwitchInstruction.lowValue );
		instructionWriter.writeInt( tableSwitchInstruction.lowValue + tableSwitchInstruction.getTargetInstructionCount() - 1 );
		for( Instruction targetInstruction : tableSwitchInstruction.targetInstructions() )
		{
			int targetInstructionOffset = instructionWriter.getOffset( tableSwitchInstruction, targetInstruction );
			instructionWriter.writeInt( targetInstructionOffset );
		}
	}

	private static void writeOperandlessInstruction( InstructionWriter instructionWriter, OperandlessInstruction operandlessInstruction )
	{
		instructionWriter.writeUnsignedByte( operandlessInstruction.opCode );
	}

	private static void writeNewPrimitiveArrayInstruction( InstructionWriter instructionWriter, NewPrimitiveArrayInstruction newPrimitiveArrayInstruction )
	{
		instructionWriter.writeUnsignedByte( OpCode.NEWARRAY );
		instructionWriter.writeUnsignedByte( newPrimitiveArrayInstruction.type );
	}

	private static void writeMultiANewArrayInstruction( InstructionWriter instructionWriter, MultiANewArrayInstruction multiANewArrayInstruction )
	{
		int constantIndex = instructionWriter.getIndex( multiANewArrayInstruction.targetClassConstant );
		instructionWriter.writeUnsignedByte( OpCode.MULTIANEWARRAY );
		instructionWriter.writeUnsignedShort( constantIndex );
		instructionWriter.writeUnsignedByte( multiANewArrayInstruction.dimensionCount );
	}

	private static void writeLookupSwitchInstruction( InstructionWriter instructionWriter, LookupSwitchInstruction lookupSwitchInstruction )
	{
		int defaultInstructionOffset = instructionWriter.getOffset( lookupSwitchInstruction, lookupSwitchInstruction.getDefaultInstruction() );
		instructionWriter.writeUnsignedByte( OpCode.LOOKUPSWITCH );
		instructionWriter.skipToAlign();
		instructionWriter.writeInt( defaultInstructionOffset );
		instructionWriter.writeInt( lookupSwitchInstruction.entries.size() );
		for( LookupSwitchEntry lookupSwitchEntry : lookupSwitchInstruction.entries )
		{
			instructionWriter.writeInt( lookupSwitchEntry.value() );
			int entryInstructionOffset = instructionWriter.getOffset( lookupSwitchInstruction, lookupSwitchEntry.getTargetInstruction() );
			instructionWriter.writeInt( entryInstructionOffset );
		}
	}

	private static void writeLocalVariableInstruction( InstructionWriter instructionWriter, LocalVariableInstruction localVariableInstruction )
	{
		boolean wide = localVariableInstruction.isWide();
		int opCode = localVariableInstruction.getActualOpcode();
		boolean hasOperand = localVariableInstruction.hasOperand();
		if( wide )
			instructionWriter.writeUnsignedByte( OpCode.WIDE );
		instructionWriter.writeUnsignedByte( opCode );
		if( hasOperand )
		{
			if( wide )
				instructionWriter.writeUnsignedShort( localVariableInstruction.index );
			else
				instructionWriter.writeUnsignedByte( localVariableInstruction.index );
		}
	}

	private static void writeInvokeInterfaceInstruction( InstructionWriter instructionWriter, InvokeInterfaceInstruction invokeInterfaceInstruction )
	{
		int constantIndex = instructionWriter.getIndex( invokeInterfaceInstruction.methodReferenceConstant );
		instructionWriter.writeUnsignedByte( OpCode.INVOKEINTERFACE );
		instructionWriter.writeUnsignedShort( constantIndex );
		instructionWriter.writeUnsignedByte( invokeInterfaceInstruction.argumentCount );
		instructionWriter.writeUnsignedByte( 0 );
	}

	private static void writeInvokeDynamicInstruction( InstructionWriter instructionWriter, InvokeDynamicInstruction invokeDynamicInstruction )
	{
		int constantIndex = instructionWriter.getIndex( invokeDynamicInstruction.invokeDynamicConstant );
		instructionWriter.writeUnsignedByte( OpCode.INVOKEDYNAMIC );
		instructionWriter.writeUnsignedShort( constantIndex );
		instructionWriter.writeUnsignedByte( 0 );
		instructionWriter.writeUnsignedByte( 0 );
	}

	private static void writeLoadConstantInstruction( InstructionWriter instructionWriter, LoadConstantInstruction loadConstantInstruction )
	{
		switch( loadConstantInstruction.constant.tag )
		{
			case Constant.tag_Integer:
			{
				IntegerConstant integerConstant = loadConstantInstruction.constant.asIntegerConstant();
				switch( integerConstant.value )
				{
					case -1:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_M1 );
						return;
					case 0:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_0 );
						return;
					case 1:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_1 );
						return;
					case 2:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_2 );
						return;
					case 3:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_3 );
						return;
					case 4:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_4 );
						return;
					case 5:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_5 );
						return;
					default:
						if( Helpers.isSignedByte( integerConstant.value ) )
						{
							instructionWriter.writeUnsignedByte( OpCode.BIPUSH );
							instructionWriter.writeUnsignedByte( integerConstant.value );
							return;
						}
						else if( Helpers.isSignedShort( integerConstant.value ) )
						{
							instructionWriter.writeUnsignedByte( OpCode.SIPUSH );
							instructionWriter.writeUnsignedShort( integerConstant.value );
							return;
						}
						break;
				}
				break;
			}
			case Constant.tag_Float:
			{
				FloatConstant floatConstant = loadConstantInstruction.constant.asFloatConstant();
				if( floatConstant.value == 0.0f )
				{
					instructionWriter.writeUnsignedByte( OpCode.FCONST_0 );
					return;
				}
				else if( floatConstant.value == 1.0f )
				{
					instructionWriter.writeUnsignedByte( OpCode.FCONST_1 );
					return;
				}
				else if( floatConstant.value == 2.0f )
				{
					instructionWriter.writeUnsignedByte( OpCode.FCONST_2 );
					return;
				}
				break;
			}
			case Constant.tag_Long:
			{
				LongConstant longConstant = loadConstantInstruction.constant.asLongConstant();
				if( longConstant.value == 0L )
				{
					instructionWriter.writeUnsignedByte( OpCode.LCONST_0 );
					return;
				}
				else if( longConstant.value == 1L )
				{
					instructionWriter.writeUnsignedByte( OpCode.LCONST_1 );
					return;
				}
				break;
			}
			case Constant.tag_Double:
			{
				DoubleConstant doubleConstant = loadConstantInstruction.constant.asDoubleConstant();
				if( doubleConstant.value == 0.0 )
				{
					instructionWriter.writeUnsignedByte( OpCode.DCONST_0 );
					return;
				}
				else if( doubleConstant.value == 1.0 )
				{
					instructionWriter.writeUnsignedByte( OpCode.DCONST_1 );
					return;
				}
				break;
			}
			case Constant.tag_String:
			case Constant.tag_Class:
				break;
			default:
				assert false;
				break;
		}

		int constantIndex = instructionWriter.getIndex( loadConstantInstruction.constant );
		if( loadConstantInstruction.constant.tag == Constant.tag_Long || loadConstantInstruction.constant.tag == Constant.tag_Double )
		{
			instructionWriter.writeUnsignedByte( OpCode.LDC2_W );
			instructionWriter.writeUnsignedShort( constantIndex );
		}
		else
		{
			if( Helpers.isUnsignedByte( constantIndex ) )
			{
				instructionWriter.writeUnsignedByte( OpCode.LDC );
				instructionWriter.writeUnsignedByte( constantIndex );
			}
			else
			{
				instructionWriter.writeUnsignedByte( OpCode.LDC_W );
				instructionWriter.writeUnsignedShort( constantIndex );
			}
		}
	}

	private static void writeIIncInstruction( InstructionWriter instructionWriter, IIncInstruction iIncInstruction )
	{
		boolean wide = iIncInstruction.isWide();
		if( wide )
			instructionWriter.writeUnsignedByte( OpCode.WIDE );
		instructionWriter.writeUnsignedByte( OpCode.IINC );
		if( wide )
		{
			instructionWriter.writeUnsignedShort( iIncInstruction.index );
			instructionWriter.writeSignedShort( iIncInstruction.delta );
		}
		else
		{
			instructionWriter.writeUnsignedByte( iIncInstruction.index );
			instructionWriter.writeSignedByte( iIncInstruction.delta );
		}
	}

	private static void writeClassConstantReferencingInstruction( InstructionWriter instructionWriter, ClassConstantReferencingInstruction classConstantReferencingInstruction )
	{
		instructionWriter.writeUnsignedByte( classConstantReferencingInstruction.opCode );
		int constantIndex = instructionWriter.getIndex( classConstantReferencingInstruction.targetClassConstant );
		instructionWriter.writeUnsignedShort( constantIndex );
	}

	private static void writeFieldConstantReferencingInstruction( InstructionWriter instructionWriter, FieldConstantReferencingInstruction fieldConstantReferencingInstruction )
	{
		instructionWriter.writeUnsignedByte( fieldConstantReferencingInstruction.opCode );
		int constantIndex = instructionWriter.getIndex( fieldConstantReferencingInstruction.fieldReferenceConstant );
		instructionWriter.writeUnsignedShort( constantIndex );
	}

	private static void writeMethodConstantReferencingInstruction( InstructionWriter instructionWriter, MethodConstantReferencingInstruction methodConstantReferencingInstruction )
	{
		instructionWriter.writeUnsignedByte( methodConstantReferencingInstruction.opCode );
		int constantIndex = instructionWriter.getIndex( methodConstantReferencingInstruction.methodReferenceConstant );
		instructionWriter.writeUnsignedShort( constantIndex );
	}

	private static void writeConditionalBranchInstruction( InstructionWriter instructionWriter, ConditionalBranchInstruction conditionalBranchInstruction )
	{
		int targetInstructionOffset = instructionWriter.getOffset( conditionalBranchInstruction, conditionalBranchInstruction.getTargetInstruction() );
		if( Kit.get( true ) || Helpers.isSignedShort( targetInstructionOffset ) ) //TODO
		{
			instructionWriter.writeUnsignedByte( conditionalBranchInstruction.opCode );
			instructionWriter.writeSignedShort( targetInstructionOffset );
		}
		else
		{
			instructionWriter.writeUnsignedByte( getConditionalBranchInstructionReverseOpCode( conditionalBranchInstruction.opCode ) );
			instructionWriter.writeSignedByte( 3 + 5 ); //length of this instruction plus length of GOTO_W instruction that follows
			if( targetInstructionOffset < 0 )
				targetInstructionOffset -= 3;
			else
				targetInstructionOffset += 2;
			instructionWriter.writeUnsignedByte( OpCode.GOTO_W );
			instructionWriter.writeInt( targetInstructionOffset );
		}
	}

	private static void writeBranchInstruction( InstructionWriter instructionWriter, BranchInstruction branchInstruction )
	{
		int offset = instructionWriter.getOffset( branchInstruction, branchInstruction.getTargetInstruction() );
		boolean isLong = !Helpers.isSignedShort( offset );
		instructionWriter.writeUnsignedByte( branchInstruction.getOpCode( isLong ) );
		if( isLong )
			instructionWriter.writeInt( offset );
		else
			instructionWriter.writeSignedShort( offset );
	}

	private static int getConditionalBranchInstructionReverseOpCode( int opCode )
	{
		return switch( opCode )
			{
				case OpCode.IFEQ -> OpCode.IFNE;
				case OpCode.IFNE -> OpCode.IFEQ;
				case OpCode.IFLT -> OpCode.IFGE;
				case OpCode.IFGE -> OpCode.IFLT;
				case OpCode.IFGT -> OpCode.IFLE;
				case OpCode.IFLE -> OpCode.IFGT;
				case OpCode.IF_ICMPEQ -> OpCode.IF_ICMPNE;
				case OpCode.IF_ICMPNE -> OpCode.IF_ICMPEQ;
				case OpCode.IF_ICMPLT -> OpCode.IF_ICMPGE;
				case OpCode.IF_ICMPGE -> OpCode.IF_ICMPLT;
				case OpCode.IF_ICMPGT -> OpCode.IF_ICMPLE;
				case OpCode.IF_ICMPLE -> OpCode.IF_ICMPGT;
				case OpCode.IF_ACMPEQ -> OpCode.IF_ACMPNE;
				case OpCode.IF_ACMPNE -> OpCode.IF_ACMPEQ;
				case OpCode.IFNULL -> OpCode.IFNONNULL;
				case OpCode.IFNONNULL -> OpCode.IFNULL;
				default -> throw new AssertionError( opCode );
			};
	}

	private static void writeConstAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, ConstAnnotationValue constAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( constAnnotationValue.valueConstant ) );
	}

	private static void writeEnumAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, EnumAnnotationValue enumAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enumAnnotationValue.enumClassDescriptorStringConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enumAnnotationValue.enumValueNameConstant ) );
	}

	private static void writeClassAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, ClassAnnotationValue classAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( classAnnotationValue.classDescriptorStringConstant ) );
	}

	private static void writeAnnotationAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, AnnotationAnnotationValue annotationAnnotationValue )
	{
		Annotation annotation = annotationAnnotationValue.annotation;
		bufferWriter.writeUnsignedShort( constantPool.getIndex( annotation.annotationTypeDescriptorStringConstant ) );
		bufferWriter.writeUnsignedShort( annotation.parameters.size() );
		for( AnnotationParameter annotationParameter : annotation.parameters )
			writeAnnotationParameter( constantPool, bufferWriter, annotationParameter );
	}

	private static void writeArrayAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, ArrayAnnotationValue arrayAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( arrayAnnotationValue.annotationValues.size() );
		for( AnnotationValue annotationValue : arrayAnnotationValue.annotationValues )
		{
			bufferWriter.writeUnsignedByte( annotationValue.tag );
			switch( annotationValue.tag )
			{
				case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger, //
					AnnotationValue.tagLong, AnnotationValue.tagShort, AnnotationValue.tagString -> writeConstAnnotationValue( constantPool, bufferWriter, annotationValue.asConstAnnotationValue() );
				case AnnotationValue.tagAnnotation -> writeAnnotationAnnotationValue( constantPool, bufferWriter, annotationValue.asAnnotationAnnotationValue() );
				case AnnotationValue.tagArray -> writeArrayAnnotationValue( constantPool, bufferWriter, annotationValue.asArrayAnnotationValue() );
				case AnnotationValue.tagClass -> writeClassAnnotationValue( constantPool, bufferWriter, annotationValue.asClassAnnotationValue() );
				case AnnotationValue.tagEnum -> writeEnumAnnotationValue( constantPool, bufferWriter, annotationValue.asEnumAnnotationValue() );
				default -> throw new AssertionError( annotationValue );
			}
		}
	}

	private static void writeVerificationType( ConstantPool constantPool, BufferWriter bufferWriter, VerificationType verificationType, LocationMap locationMap )
	{
		bufferWriter.writeUnsignedByte( verificationType.tag );
		switch( verificationType.tag )
		{
			case VerificationType.tag_Top, VerificationType.tag_Integer, VerificationType.tag_Float, VerificationType.tag_Double, VerificationType.tag_Long, //
				VerificationType.tag_Null, VerificationType.tag_UninitializedThis -> writeSimpleVerificationType( verificationType.asSimpleVerificationType() );
			case VerificationType.tag_Object -> writeObjectVerificationType( constantPool, bufferWriter, verificationType.asObjectVerificationType() );
			case VerificationType.tag_Uninitialized -> writeUninitializedVerificationType( bufferWriter, locationMap, verificationType.asUninitializedVerificationType() );
			default -> throw new AssertionError( verificationType );
		}
	}

	private static void writeUninitializedVerificationType( BufferWriter bufferWriter, LocationMap locationMap, UninitializedVerificationType uninitializedVerificationType )
	{
		int targetLocation = locationMap.getLocation( uninitializedVerificationType.instruction );
		bufferWriter.writeUnsignedShort( targetLocation );
	}

	private static void writeObjectVerificationType( ConstantPool constantPool, BufferWriter bufferWriter, ObjectVerificationType objectVerificationType )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( objectVerificationType.classConstant ) );
	}

	private static void writeSimpleVerificationType( SimpleVerificationType simpleVerificationType )
	{
		Kit.get( simpleVerificationType ); /* nothing to do */
	}

	private static void writeAnnotationParameter( ConstantPool constantPool, BufferWriter bufferWriter, AnnotationParameter annotationParameter )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( annotationParameter.nameConstant ) );
		bufferWriter.writeUnsignedByte( annotationParameter.value.tag );
		switch( annotationParameter.value.tag )
		{
			case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger, AnnotationValue.tagLong, //
				AnnotationValue.tagShort, AnnotationValue.tagString -> writeConstAnnotationValue( constantPool, bufferWriter, annotationParameter.value.asConstAnnotationValue() );
			case AnnotationValue.tagAnnotation -> writeAnnotationAnnotationValue( constantPool, bufferWriter, annotationParameter.value.asAnnotationAnnotationValue() );
			case AnnotationValue.tagArray -> writeArrayAnnotationValue( constantPool, bufferWriter, annotationParameter.value.asArrayAnnotationValue() );
			case AnnotationValue.tagClass -> writeClassAnnotationValue( constantPool, bufferWriter, annotationParameter.value.asClassAnnotationValue() );
			case AnnotationValue.tagEnum -> writeEnumAnnotationValue( constantPool, bufferWriter, annotationParameter.value.asEnumAnnotationValue() );
			default -> throw new AssertionError( annotationParameter.value );
		}
	}

	private static void writeAnnotations( ConstantPool constantPool, BufferWriter bufferWriter, Collection<Annotation> annotations )
	{
		bufferWriter.writeUnsignedShort( annotations.size() );
		for( Annotation annotation : annotations )
			writeAnnotation( constantPool, bufferWriter, annotation );
	}

	private static void writeAnnotation( ConstantPool constantPool, BufferWriter bufferWriter, Annotation byteCodeAnnotation )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( byteCodeAnnotation.annotationTypeDescriptorStringConstant ) );
		writeAnnotationParameters( constantPool, bufferWriter, byteCodeAnnotation.parameters );
	}

	private static void writeAnnotationParameters( ConstantPool constantPool, BufferWriter bufferWriter, Collection<AnnotationParameter> annotationParameters )
	{
		bufferWriter.writeUnsignedShort( annotationParameters.size() );
		for( AnnotationParameter annotationParameter : annotationParameters )
			writeAnnotationParameter( constantPool, bufferWriter, annotationParameter );
	}

	private static int getOffsetDelta( StackMapFrame stackMapFrame, Optional<StackMapFrame> previousFrame, LocationMap locationMap )
	{
		int offset = locationMap.getLocation( Optional.of( stackMapFrame.getTargetInstruction() ) );
		if( previousFrame.isEmpty() )
			return offset;
		int previousLocation = locationMap.getLocation( Optional.of( previousFrame.get().getTargetInstruction() ) );
		return offset - previousLocation - 1;
	}
}
