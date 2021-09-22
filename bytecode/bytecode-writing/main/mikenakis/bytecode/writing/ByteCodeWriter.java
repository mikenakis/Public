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
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableEntry;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.model.attributes.MethodParameter;
import mikenakis.bytecode.model.attributes.MethodParametersAttribute;
import mikenakis.bytecode.model.attributes.NestHostAttribute;
import mikenakis.bytecode.model.attributes.NestMembersAttribute;
import mikenakis.bytecode.model.attributes.ParameterAnnotationSet;
import mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import mikenakis.bytecode.model.attributes.SyntheticAttribute;
import mikenakis.bytecode.model.attributes.UnknownAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ImmediateLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchEntry;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.OperandlessLoadConstantInstruction;
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
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
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
		bufferWriter.writeUnsignedShort( byteCodeType.minorVersion );
		bufferWriter.writeUnsignedShort( byteCodeType.majorVersion );

		ConstantPool constantPool = new ConstantPool();
		ClassConstant thisClassConstant = ClassConstant.of( byteCodeType.thisClassDescriptor.classDesc );
		constantPool.internClassConstant( thisClassConstant );
		Optional<ClassConstant> superClassConstant = byteCodeType.superClassDescriptor.map( c -> ClassConstant.of( c.classDesc ) );
		superClassConstant.ifPresent( c -> constantPool.internClassConstant( c ) );
		for( Constant extraConstant : byteCodeType.extraConstants )
			constantPool.internExtraConstant( extraConstant );
		for( TerminalTypeDescriptor interfaceDescriptor : byteCodeType.interfaces )
			constantPool.internTerminalTypeDescriptor( interfaceDescriptor );
		for( ByteCodeField field : byteCodeType.fields )
			constantPool.internField( field );
		for( ByteCodeMethod method : byteCodeType.methods )
			constantPool.internMethod( method );
		constantPool.internAttributeSet( byteCodeType.attributeSet );

		bufferWriter.writeUnsignedShort( constantPool.size() );
		for( Constant constant : constantPool.constants() )
			writeConstant( constantPool, bufferWriter, constant );
		bufferWriter.writeUnsignedShort( byteCodeType.modifierSet.getBits() );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( thisClassConstant ) );
		bufferWriter.writeUnsignedShort( superClassConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
		bufferWriter.writeUnsignedShort( byteCodeType.interfaces.size() );
		for( TerminalTypeDescriptor interfaceDescriptor : byteCodeType.interfaces )
		{
			ClassConstant interfaceClassConstant = ClassConstant.of( interfaceDescriptor.classDesc );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( interfaceClassConstant ) );
		}
		bufferWriter.writeUnsignedShort( byteCodeType.fields.size() );
		for( ByteCodeField field : byteCodeType.fields )
		{
			bufferWriter.writeUnsignedShort( field.modifierSet.getBits() );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( field.nameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( field.descriptorConstant ) );
			writeAttributeSet( bufferWriter, constantPool, field.attributeSet, Optional.empty() );
		}
		bufferWriter.writeUnsignedShort( byteCodeType.methods.size() );
		for( ByteCodeMethod method : byteCodeType.methods )
		{
			bufferWriter.writeUnsignedShort( method.modifierSet.getBits() );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( method.nameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( method.descriptorConstant ) );
			writeAttributeSet( bufferWriter, constantPool, method.attributeSet, Optional.empty() );
		}
		writeAttributeSet( bufferWriter, constantPool, byteCodeType.attributeSet, Optional.empty() );
		return bufferWriter.toBytes();
	}

	private static void writeConstant( ConstantPool constantPool, BufferWriter bufferWriter, Constant constant )
	{
		bufferWriter.writeUnsignedByte( constant.tag );
		switch( constant.tag )
		{
			case Constant.tagMutf8 -> writeMutf8Constant( bufferWriter, constant.asMutf8Constant() );
			case Constant.tagInteger -> writeIntegerConstant( bufferWriter, constant.asIntegerConstant() );
			case Constant.tagFloat -> writeFloatConstant( bufferWriter, constant.asFloatConstant() );
			case Constant.tagLong -> writeLongConstant( bufferWriter, constant.asLongConstant() );
			case Constant.tagDouble -> writeDoubleConstant( bufferWriter, constant.asDoubleConstant() );
			case Constant.tagClass -> writeClassConstant( constantPool, bufferWriter, constant.asClassConstant() );
			case Constant.tagString -> writeStringConstant( constantPool, bufferWriter, constant.asStringConstant() );
			case Constant.tagFieldReference -> writeFieldReferenceConstant( constantPool, bufferWriter, constant.asFieldReferenceConstant() );
			case Constant.tagMethodReference -> writeMethodReferenceConstant( constantPool, bufferWriter, constant.asMethodReferenceConstant() );
			case Constant.tagInterfaceMethodReference -> writeInterfaceMethodReferenceConstant( constantPool, bufferWriter, constant.asInterfaceMethodReferenceConstant() );
			case Constant.tagNameAndDescriptor -> writeNameAndDescriptorConstant( constantPool, bufferWriter, constant.asNameAndDescriptorConstant() );
			case Constant.tagMethodHandle -> writeMethodHandleConstant( constantPool, bufferWriter, constant.asMethodHandleConstant() );
			case Constant.tagMethodType -> writeMethodTypeConstant( constantPool, bufferWriter, constant.asMethodTypeConstant() );
			case Constant.tagInvokeDynamic -> writeInvokeDynamicConstant( constantPool, bufferWriter, constant.asInvokeDynamicConstant() );
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
		bufferWriter.writeUnsignedShort( constantPool.getIndex( classConstant.nameConstant() ) );
	}

	private static void writeStringConstant( ConstantPool constantPool, BufferWriter bufferWriter, StringConstant stringConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( stringConstant.valueConstant() ) );
	}

	private static void writeFieldReferenceConstant( ConstantPool constantPool, BufferWriter bufferWriter, FieldReferenceConstant fieldReferenceConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( fieldReferenceConstant.typeConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( fieldReferenceConstant.nameAndDescriptorConstant ) );
	}

	private static void writeMethodReferenceConstant( ConstantPool constantPool, BufferWriter bufferWriter, MethodReferenceConstant methodReferenceConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodReferenceConstant.typeConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodReferenceConstant.nameAndDescriptorConstant ) );
	}

	private static void writeInterfaceMethodReferenceConstant( ConstantPool constantPool, BufferWriter bufferWriter, InterfaceMethodReferenceConstant interfaceMethodReferenceConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( interfaceMethodReferenceConstant.typeConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( interfaceMethodReferenceConstant.nameAndDescriptorConstant ) );
	}

	private static void writeNameAndDescriptorConstant( ConstantPool constantPool, BufferWriter bufferWriter, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nameAndDescriptorConstant.nameConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nameAndDescriptorConstant.descriptorConstant ) );
	}

	private static void writeMethodHandleConstant( ConstantPool constantPool, BufferWriter bufferWriter, MethodHandleConstant methodHandleConstant )
	{
		bufferWriter.writeUnsignedByte( methodHandleConstant.referenceKind().number );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodHandleConstant.referenceConstant() ) );
	}

	private static void writeMethodTypeConstant( ConstantPool constantPool, BufferWriter bufferWriter, MethodTypeConstant methodTypeConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodTypeConstant.descriptorConstant ) );
	}

	private static void writeInvokeDynamicConstant( ConstantPool constantPool, BufferWriter bufferWriter, InvokeDynamicConstant invokeDynamicConstant )
	{
		bufferWriter.writeUnsignedShort( invokeDynamicConstant.bootstrapMethodIndex() );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( invokeDynamicConstant.nameAndDescriptorConstant() ) );
	}

	private static void writeAttribute( ConstantPool constantPool, Attribute attribute, BufferWriter bufferWriter, Optional<LocationMap> locationMap )
	{
		if( attribute.isKnown() )
		{
			KnownAttribute knownAttribute = attribute.asKnownAttribute();
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tagAnnotationDefault -> writeAnnotationDefaultAttribute( constantPool, bufferWriter, knownAttribute.asAnnotationDefaultAttribute() );
				case KnownAttribute.tagBootstrapMethods -> writeBootstrapMethodsAttribute( constantPool, bufferWriter, knownAttribute.asBootstrapMethodsAttribute() );
				case KnownAttribute.tagCode -> writeCodeAttribute( constantPool, bufferWriter, knownAttribute.asCodeAttribute() );
				case KnownAttribute.tagConstantValue -> writeConstantValueAttribute( constantPool, bufferWriter, knownAttribute.asConstantValueAttribute() );
				case KnownAttribute.tagDeprecated -> writeDeprecatedAttribute( knownAttribute.asDeprecatedAttribute() );
				case KnownAttribute.tagEnclosingMethod -> writeEnclosingMethodAttribute( constantPool, bufferWriter, knownAttribute.asEnclosingMethodAttribute() );
				case KnownAttribute.tagExceptions -> writeExceptionsAttribute( constantPool, bufferWriter, knownAttribute.asExceptionsAttribute() );
				case KnownAttribute.tagInnerClasses -> writeInnerClassesAttribute( constantPool, bufferWriter, knownAttribute.asInnerClassesAttribute() );
				case KnownAttribute.tagLineNumberTable -> writeLineNumberTableAttribute( bufferWriter, locationMap, knownAttribute.asLineNumberTableAttribute() );
				case KnownAttribute.tagLocalVariableTable -> writeLocalVariableTableAttribute( constantPool, bufferWriter, locationMap.orElseThrow(), knownAttribute.asLocalVariableTableAttribute() );
				case KnownAttribute.tagLocalVariableTypeTable -> writeLocalVariableTypeTableAttribute( constantPool, bufferWriter, locationMap.orElseThrow(), knownAttribute.asLocalVariableTypeTableAttribute() );
				case KnownAttribute.tagMethodParameters -> writeMethodParametersAttribute( constantPool, bufferWriter, knownAttribute.asMethodParametersAttribute() );
				case KnownAttribute.tagNestHost -> writeNestHostAttribute( constantPool, bufferWriter, knownAttribute.asNestHostAttribute() );
				case KnownAttribute.tagNestMembers -> writeNestMembersAttribute( constantPool, bufferWriter, knownAttribute.asNestMembersAttribute() );
				case KnownAttribute.tagRuntimeInvisibleAnnotations -> writeAnnotationsAttribute( constantPool, bufferWriter, knownAttribute.asRuntimeInvisibleAnnotationsAttribute() );
				case KnownAttribute.tagRuntimeInvisibleParameterAnnotations -> writeParameterAnnotationSets( constantPool, bufferWriter, knownAttribute.asRuntimeInvisibleParameterAnnotationsAttribute().parameterAnnotationSets() );
				case KnownAttribute.tagRuntimeInvisibleTypeAnnotations -> writeTypeAnnotations( constantPool, bufferWriter, knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute().typeAnnotations );
				case KnownAttribute.tagRuntimeVisibleAnnotations -> writeAnnotationsAttribute( constantPool, bufferWriter, knownAttribute.asRuntimeVisibleAnnotationsAttribute() );
				case KnownAttribute.tagRuntimeVisibleParameterAnnotations -> writeParameterAnnotationSets( constantPool, bufferWriter, knownAttribute.asRuntimeVisibleParameterAnnotationsAttribute().parameterAnnotationSets() );
				case KnownAttribute.tagRuntimeVisibleTypeAnnotations -> writeTypeAnnotations( constantPool, bufferWriter, knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute().typeAnnotations );
				case KnownAttribute.tagSignature -> bufferWriter.writeUnsignedShort( constantPool.getIndex( knownAttribute.asSignatureAttribute().signatureConstant() ) );
				case KnownAttribute.tagSourceFile -> bufferWriter.writeUnsignedShort( constantPool.getIndex( knownAttribute.asSourceFileAttribute().valueConstant() ) );
				case KnownAttribute.tagStackMapTable -> writeStackMapTableAttribute( constantPool, bufferWriter, locationMap.orElseThrow(), knownAttribute.asStackMapTableAttribute() );
				case KnownAttribute.tagSynthetic -> writeSyntheticAttribute( knownAttribute.asSyntheticAttribute() );
				default -> throw new InvalidKnownAttributeTagException( knownAttribute.tag );
			}
		}
		else
		{
			UnknownAttribute unknownAttribute = attribute.asUnknownAttribute();
			bufferWriter.writeBuffer( unknownAttribute.buffer() );
		}
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
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enclosingMethodAttribute.classConstant() ) );
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = enclosingMethodAttribute.methodNameAndDescriptorConstant();
		bufferWriter.writeUnsignedShort( methodNameAndDescriptorConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
	}

	private static void writeExceptionsAttribute( ConstantPool constantPool, BufferWriter bufferWriter, ExceptionsAttribute exceptionsAttribute )
	{
		List<ClassConstant> exceptionClassConstants = exceptionsAttribute.exceptionClassConstants();
		bufferWriter.writeUnsignedShort( exceptionClassConstants.size() );
		for( ClassConstant exceptionClassConstant : exceptionClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( exceptionClassConstant ) );
	}

	private static void writeInnerClassesAttribute( ConstantPool constantPool, BufferWriter bufferWriter, InnerClassesAttribute innerClassesAttribute )
	{
		List<InnerClass> innerClasses = innerClassesAttribute.innerClasses;
		bufferWriter.writeUnsignedShort( innerClasses.size() );
		for( InnerClass innerClass : innerClasses )
		{
			bufferWriter.writeUnsignedShort( constantPool.getIndex( innerClass.innerClassConstant() ) );
			bufferWriter.writeUnsignedShort( innerClass.outerClassConstant().map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
			bufferWriter.writeUnsignedShort( innerClass.innerNameConstant().map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
			bufferWriter.writeUnsignedShort( innerClass.modifierSet().getBits() );
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
			bufferWriter.writeUnsignedShort( methodParameter.modifierSet.getBits() );
		}
	}

	private static void writeNestHostAttribute( ConstantPool constantPool, BufferWriter bufferWriter, NestHostAttribute nestHostAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nestHostAttribute.hostClassConstant ) );
	}

	private static void writeNestMembersAttribute( ConstantPool constantPool, BufferWriter bufferWriter, NestMembersAttribute nestMembersAttribute )
	{
		List<ClassConstant> memberClassConstants = nestMembersAttribute.memberClassConstants;
		bufferWriter.writeUnsignedShort( memberClassConstants.size() );
		for( ClassConstant memberClassConstant : memberClassConstants )
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
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariable.nameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariable.descriptorConstant ) );
			bufferWriter.writeUnsignedShort( localVariable.index );
		}
	}
	private static void writeCodeAttribute( ConstantPool constantPool, BufferWriter bufferWriter, CodeAttribute codeAttribute )
	{
		bufferWriter.writeUnsignedShort( codeAttribute.getMaxStack() );
		bufferWriter.writeUnsignedShort( codeAttribute.getMaxLocals() );

		//TODO: improve this algorithm.
		//      Currently, we do one pass over all instructions obtaining the pessimistic length of each instruction,
		//      and then one more pass obtaining the actual length of each instruction, which may be slightly shorter.
		//      However, as a result of some of the instructions shortening in the second pass, it could be that more instructions
		//      can now also be shortened, but we are not handling this.
		//      A naive solution would be to keep repeating the second pass until there is no change, but it would be wasteful, since
		//      the size of most instructions is fixed.
		LocationMap locationMap = getLocationMap( codeAttribute.instructions().all(), constantPool );
		ConcreteInstructionWriter instructionWriter = new ConcreteInstructionWriter( locationMap, constantPool );
		for( Instruction instruction : codeAttribute.instructions().all() )
			writeInstruction( instruction, instructionWriter );
		byte[] bytes = instructionWriter.toBytes();
		bufferWriter.writeInt( bytes.length );
		bufferWriter.writeBytes( bytes );

		List<ExceptionInfo> exceptionInfos = codeAttribute.exceptionInfos();
		bufferWriter.writeUnsignedShort( exceptionInfos.size() );
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos() )
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
				case Target.tagTypeParameterDeclarationOfGenericClassOrInterface, //
					Target.tagTypeParameterDeclarationOfGenericMethodOrConstructor -> //
					writeTypeParameterTarget( bufferWriter, target.asTypeParameterTarget() );
				case Target.tagTypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration -> //
					writeSupertypeTarget( bufferWriter, target.asSupertypeTarget() );
				case Target.tagTypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface, //
					Target.tagTypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor -> //
					writeTypeParameterBoundTarget( bufferWriter, target.asTypeParameterBoundTarget() );
				case Target.tagTypeInFieldDeclaration, //
					Target.tagReturnTypeOfMethodOrTypeOfNewlyConstructedObject, //
					Target.tagReceiverTypeOfMethodOrConstructor -> //
					writeEmptyTarget( target.asEmptyTarget() );
				case Target.tagTypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression -> //
					writeFormalParameterTarget( bufferWriter, target.asFormalParameterTarget() );
				case Target.tagTypeInThrowsClauseOfMethodOrConstructor -> //
					writeThrowsTarget( bufferWriter, target.asThrowsTarget() );
				case Target.tagTypeInLocalVariableDeclaration, //
					Target.tagTypeInResourceVariableDeclaration -> //
					writeLocalVariableTarget( bufferWriter, target.asLocalVariableTarget() );
				case Target.tagTypeInExceptionParameterDeclaration -> //
					writeCatchTarget( bufferWriter, target.asCatchTarget() );
				case Target.tagTypeInInstanceofExpression, //
					Target.tagTypeInNewExpression, //
					Target.tagTypeInMethodReferenceExpressionUsingNew, //
					Target.tagTypeInMethodReferenceExpressionUsingIdentifier -> //
					writeOffsetTarget( bufferWriter, target.asOffsetTarget() );
				case Target.tagTypeInCastExpression, //
					Target.tagTypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement, //
					Target.tagTypeArgumentForGenericMethodInMethodInvocationExpression, //
					Target.tagTypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew, //
					Target.tagTypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier -> //
					writeTypeArgumentTarget( bufferWriter, target.asTypeArgumentTarget() );
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
		WritingLocationMap locationMap = new WritingLocationMap();
		InterimInstructionWriter instructionWriter = new InterimInstructionWriter( constantPool );
		for( Instruction instruction : instructions )
		{
			int startLocation = instructionWriter.getLocation();
			writeInstruction( instruction, instructionWriter );
			int length = instructionWriter.getLocation() - startLocation;
			locationMap.add( instruction, length );
		}
		return locationMap;
	}

	private static void writeInstruction( Instruction instruction, InstructionWriter instructionWriter )
	{
		switch( instruction.group )
		{
			case Branch -> writeBranchInstruction( instructionWriter, instruction.asBranchInstruction() );
			case ConditionalBranch -> writeConditionalBranchInstruction( instructionWriter, instruction.asConditionalBranchInstruction() );
			case ConstantReferencing -> writeConstantReferencingInstruction( instructionWriter, instruction.asConstantReferencingInstruction() );
			case IInc -> writeIIncInstruction( instructionWriter, instruction.asIIncInstruction() );
			case ImmediateLoadConstant -> writeImmediateLoadConstantInstruction( instructionWriter, instruction.asImmediateLoadConstantInstruction() );
			case IndirectLoadConstant -> writeIndirectLoadConstantInstruction( instructionWriter, instruction.asIndirectLoadConstantInstruction() );
			case InvokeDynamic -> writeInvokeDynamicInstruction( instructionWriter, instruction.asInvokeDynamicInstruction() );
			case InvokeInterface -> writeInvokeInterfaceInstruction( instructionWriter, instruction.asInvokeInterfaceInstruction() );
			case LocalVariable -> writeLocalVariableInstruction( instructionWriter, instruction.asLocalVariableInstruction() );
			case LookupSwitch -> writeLookupSwitchInstruction( instructionWriter, instruction.asLookupSwitchInstruction() );
			case MultiANewArray -> writeMultiANewArrayInstruction( instructionWriter, instruction.asMultiANewArrayInstruction() );
			case NewPrimitiveArray -> writeNewPrimitiveArrayInstruction( instructionWriter, instruction.asNewPrimitiveArrayInstruction() );
			case Operandless -> writeOperandlessInstruction( instructionWriter, instruction.asOperandlessInstruction() );
			case OperandlessLoadConstant -> writeOperandlessLoadConstantInstruction( instructionWriter, instruction.asOperandlessLoadConstantInstruction() );
			case TableSwitch -> writeTableSwitchInstruction( instructionWriter, instruction.asTableSwitchInstruction() );
			default -> throw new AssertionError( instruction );
		}
	}

	private static void writeTableSwitchInstruction( InstructionWriter instructionWriter, TableSwitchInstruction tableSwitchInstruction )
	{
		instructionWriter.writeUnsignedByte( OpCode.TABLESWITCH );
		instructionWriter.skipToAlign();
		instructionWriter.writeInt( instructionWriter.getOffset( tableSwitchInstruction, tableSwitchInstruction.getDefaultInstruction() ) );
		instructionWriter.writeInt( tableSwitchInstruction.lowValue );
		instructionWriter.writeInt( tableSwitchInstruction.lowValue + tableSwitchInstruction.getTargetInstructionCount() - 1 );
		for( Instruction targetInstruction : tableSwitchInstruction.targetInstructions() )
		{
			int targetInstructionOffset = instructionWriter.getOffset( tableSwitchInstruction, targetInstruction );
			instructionWriter.writeInt( targetInstructionOffset );
		}
	}

	private static void writeOperandlessLoadConstantInstruction( InstructionWriter instructionWriter, OperandlessLoadConstantInstruction operandlessLoadConstantInstruction )
	{
		instructionWriter.writeUnsignedByte( operandlessLoadConstantInstruction.opCode );
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
		int constantIndex = instructionWriter.getIndex( multiANewArrayInstruction.classConstant );
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
		int constantIndex = instructionWriter.getIndex( invokeInterfaceInstruction.interfaceMethodReferenceConstant );
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

	private static void writeIndirectLoadConstantInstruction( InstructionWriter instructionWriter, IndirectLoadConstantInstruction indirectLoadConstantInstruction )
	{
		int constantIndex = instructionWriter.getIndex( indirectLoadConstantInstruction.constant );
		if( indirectLoadConstantInstruction.opCode == OpCode.LDC2_W ) //FIXME whether the "2" form should be used depends on the type of the constant!
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

	private static void writeImmediateLoadConstantInstruction( InstructionWriter instructionWriter, ImmediateLoadConstantInstruction immediateLoadConstantInstruction )
	{
		instructionWriter.writeUnsignedByte( immediateLoadConstantInstruction.opCode );
		switch( immediateLoadConstantInstruction.opCode )
		{
			case OpCode.BIPUSH:
				instructionWriter.writeUnsignedByte( immediateLoadConstantInstruction.immediateValue );
				break;
			case OpCode.SIPUSH:
				instructionWriter.writeUnsignedShort( immediateLoadConstantInstruction.immediateValue );
				break;
			default:
				assert false;
				break;
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

	private static void writeConstantReferencingInstruction( InstructionWriter instructionWriter, ConstantReferencingInstruction constantReferencingInstruction )
	{
		instructionWriter.writeUnsignedByte( constantReferencingInstruction.opCode );
		int constantIndex = instructionWriter.getIndex( constantReferencingInstruction.constant );
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
		assert !branchInstruction.isAny();
		int offset = instructionWriter.getOffset( branchInstruction, branchInstruction.getTargetInstruction() );
		instructionWriter.writeUnsignedByte( branchInstruction.getOpCode() );
		if( branchInstruction.isLong() )
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
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enumAnnotationValue.typeNameConstant() ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enumAnnotationValue.valueNameConstant() ) );
	}

	private static void writeClassAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, ClassAnnotationValue classAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( classAnnotationValue.nameConstant() ) );
	}

	private static void writeAnnotationAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, AnnotationAnnotationValue annotationAnnotationValue )
	{
		Annotation annotation = annotationAnnotationValue.annotation;
		bufferWriter.writeUnsignedShort( constantPool.getIndex( annotation.typeConstant ) );
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
			case VerificationType.tagTop, VerificationType.tagInteger, VerificationType.tagFloat, VerificationType.tagDouble, VerificationType.tagLong, //
				VerificationType.tagNull, VerificationType.tagUninitializedThis -> writeSimpleVerificationType( verificationType.asSimpleVerificationType() );
			case VerificationType.tagObject -> writeObjectVerificationType( constantPool, bufferWriter, verificationType.asObjectVerificationType() );
			case VerificationType.tagUninitialized -> writeUninitializedVerificationType( bufferWriter, locationMap, verificationType.asUninitializedVerificationType() );
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
		bufferWriter.writeUnsignedShort( constantPool.getIndex( objectVerificationType.classConstant() ) );
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
		bufferWriter.writeUnsignedShort( constantPool.getIndex( byteCodeAnnotation.typeConstant ) );
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
