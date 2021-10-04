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
import mikenakis.bytecode.model.attributes.code.instructions.ClassReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.FieldReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchEntry;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MethodReferencingInstruction;
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
		ByteCodeWriter byteCodeWriter = new ByteCodeWriter( byteCodeType );
		return byteCodeWriter.run();
	}

	private final BufferWriter bufferWriter = new BufferWriter();
	private final ByteCodeType byteCodeType;
	private final ConstantPool constantPool = new ConstantPool();
	private final BootstrapPool bootstrapPool = new BootstrapPool();

	private ByteCodeWriter( ByteCodeType byteCodeType )
	{
		this.byteCodeType = byteCodeType;
	}

	private byte[] run()
	{
		bufferWriter.writeInt( ByteCodeType.MAGIC );
		bufferWriter.writeUnsignedShort( byteCodeType.version.minor() );
		bufferWriter.writeUnsignedShort( byteCodeType.version.major() );

		constantPool.internClassConstant( byteCodeType.classConstant() );
		byteCodeType.superClassConstant().ifPresent( c -> constantPool.internClassConstant( c ) );
		for( ClassConstant classConstant : byteCodeType.interfaceClassConstants() )
			constantPool.internClassConstant( classConstant );
		for( ByteCodeField field : byteCodeType.fields )
			constantPool.internField( field );

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
			writeConstant( constant );
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
			writeAttributeSet( field.attributeSet, Optional.empty() );
		}
		bufferWriter.writeUnsignedShort( byteCodeType.methods.size() );
		for( ByteCodeMethod method : byteCodeType.methods )
		{
			bufferWriter.writeUnsignedShort( method.modifiers.getBits() );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( method.memberNameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( method.methodDescriptorStringConstant ) );
			writeAttributeSet( method.attributeSet, Optional.empty() );
		}
		writeAttributeSet( byteCodeType.attributeSet, Optional.empty() );
		return bufferWriter.toBytes();
	}

	private void writeConstant( Constant constant )
	{
		bufferWriter.writeUnsignedByte( constant.tag );
		switch( constant.tag )
		{
			case Constant.tag_Mutf8 -> writeMutf8Constant( constant.asMutf8Constant() );
			case Constant.tag_Integer -> writeIntegerConstant( constant.asIntegerConstant() );
			case Constant.tag_Float -> writeFloatConstant( constant.asFloatConstant() );
			case Constant.tag_Long -> writeLongConstant( constant.asLongConstant() );
			case Constant.tag_Double -> writeDoubleConstant( constant.asDoubleConstant() );
			case Constant.tag_Class -> writeClassConstant( constant.asClassConstant() );
			case Constant.tag_String -> writeStringConstant( constant.asStringConstant() );
			case Constant.tag_FieldReference -> writeFieldReferenceConstant( constant.asFieldReferenceConstant() );
			case Constant.tag_PlainMethodReference, Constant.tag_InterfaceMethodReference -> writeMethodReferenceConstant( constant.asMethodReferenceConstant() );
			case Constant.tag_NameAndDescriptor -> writeNameAndDescriptorConstant( constant.asNameAndDescriptorConstant() );
			case Constant.tag_MethodHandle -> writeMethodHandleConstant( constant.asMethodHandleConstant() );
			case Constant.tag_MethodType -> writeMethodTypeConstant( constant.asMethodTypeConstant() );
			case Constant.tag_InvokeDynamic -> writeInvokeDynamicConstant( constant.asInvokeDynamicConstant() );
			default -> throw new AssertionError( constant );
		}
	}

	private void writeMutf8Constant( Mutf8Constant mutf8Constant )
	{
		Buffer buffer = mutf8Constant.buffer();
		bufferWriter.writeUnsignedShort( buffer.length() );
		bufferWriter.writeBuffer( buffer );
	}

	private void writeIntegerConstant( IntegerConstant integerConstant )
	{
		bufferWriter.writeInt( integerConstant.value );
	}

	private void writeFloatConstant( FloatConstant floatConstant )
	{
		bufferWriter.writeFloat( floatConstant.value );
	}

	private void writeLongConstant( LongConstant longConstant )
	{
		bufferWriter.writeLong( longConstant.value );
	}

	private void writeDoubleConstant( DoubleConstant doubleConstant )
	{
		bufferWriter.writeDouble( doubleConstant.value );
	}

	private void writeClassConstant( ClassConstant classConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( classConstant.getInternalNameOrDescriptorStringConstant() ) );
	}

	private void writeStringConstant( StringConstant stringConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( stringConstant.getValueConstant() ) );
	}

	private void writeFieldReferenceConstant( FieldReferenceConstant fieldReferenceConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( fieldReferenceConstant.getDeclaringTypeConstant() ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( fieldReferenceConstant.getNameAndDescriptorConstant() ) );
	}

	private void writeMethodReferenceConstant( MethodReferenceConstant methodReferenceConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodReferenceConstant.getDeclaringTypeConstant() ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodReferenceConstant.getNameAndDescriptorConstant() ) );
	}

	private void writeNameAndDescriptorConstant( NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nameAndDescriptorConstant.getNameConstant() ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nameAndDescriptorConstant.getDescriptorConstant() ) );
	}

	private void writeMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		bufferWriter.writeUnsignedByte( methodHandleConstant.referenceKind().number );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodHandleConstant.getReferenceConstant() ) );
	}

	private void writeMethodTypeConstant( MethodTypeConstant methodTypeConstant )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( methodTypeConstant.getDescriptorConstant() ) );
	}

	private void writeInvokeDynamicConstant( InvokeDynamicConstant invokeDynamicConstant )
	{
		int bootstrapMethodIndex = bootstrapPool.getIndex( invokeDynamicConstant.getBootstrapMethod() );
		bufferWriter.writeUnsignedShort( bootstrapMethodIndex );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( invokeDynamicConstant.getNameAndDescriptorConstant() ) );
	}

	private void writeAttribute( Attribute attribute, Optional<LocationMap> locationMap )
	{
		if( attribute.isKnown() )
		{
			KnownAttribute knownAttribute = attribute.asKnownAttribute();
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tag_AnnotationDefault -> writeAnnotationDefaultAttribute( knownAttribute.asAnnotationDefaultAttribute() );
				case KnownAttribute.tag_BootstrapMethods -> writeBootstrapMethodsAttribute( knownAttribute.asBootstrapMethodsAttribute() );
				case KnownAttribute.tag_Code -> writeCodeAttribute( knownAttribute.asCodeAttribute() );
				case KnownAttribute.tag_ConstantValue -> writeConstantValueAttribute( knownAttribute.asConstantValueAttribute() );
				case KnownAttribute.tag_Deprecated -> writeDeprecatedAttribute( knownAttribute.asDeprecatedAttribute() );
				case KnownAttribute.tag_EnclosingMethod -> writeEnclosingMethodAttribute( knownAttribute.asEnclosingMethodAttribute() );
				case KnownAttribute.tag_Exceptions -> writeExceptionsAttribute( knownAttribute.asExceptionsAttribute() );
				case KnownAttribute.tag_InnerClasses -> writeInnerClassesAttribute( knownAttribute.asInnerClassesAttribute() );
				case KnownAttribute.tag_LineNumberTable -> writeLineNumberTableAttribute( locationMap, knownAttribute.asLineNumberTableAttribute() );
				case KnownAttribute.tag_LocalVariableTable -> writeLocalVariableTableAttribute( locationMap.orElseThrow(), knownAttribute.asLocalVariableTableAttribute() );
				case KnownAttribute.tag_LocalVariableTypeTable -> writeLocalVariableTypeTableAttribute( locationMap.orElseThrow(), knownAttribute.asLocalVariableTypeTableAttribute() );
				case KnownAttribute.tag_MethodParameters -> writeMethodParametersAttribute( knownAttribute.asMethodParametersAttribute() );
				case KnownAttribute.tag_NestHost -> writeNestHostAttribute( knownAttribute.asNestHostAttribute() );
				case KnownAttribute.tag_NestMembers -> writeNestMembersAttribute( knownAttribute.asNestMembersAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleAnnotations -> writeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleParameterAnnotations -> writeParameterAnnotationsAttribute( knownAttribute.asRuntimeInvisibleParameterAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> writeTypeAnnotations( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute().typeAnnotations );
				case KnownAttribute.tag_RuntimeVisibleAnnotations -> writeAnnotationsAttribute( knownAttribute.asRuntimeVisibleAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleParameterAnnotations -> writeParameterAnnotationsAttribute( knownAttribute.asRuntimeVisibleParameterAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> writeTypeAnnotations( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute().typeAnnotations );
				case KnownAttribute.tag_Signature -> writeSignatureAttribute( knownAttribute.asSignatureAttribute() );
				case KnownAttribute.tag_SourceFile -> writeSourceFileAttribute( knownAttribute.asSourceFileAttribute() );
				case KnownAttribute.tag_StackMapTable -> writeStackMapTableAttribute( locationMap.orElseThrow(), knownAttribute.asStackMapTableAttribute() );
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

	private void writeParameterAnnotationsAttribute( ParameterAnnotationsAttribute parameterAnnotationsAttribute )
	{
		writeParameterAnnotationSets( parameterAnnotationsAttribute.parameterAnnotationSets );
	}

	private void writeSourceFileAttribute( SourceFileAttribute sourceFileAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( sourceFileAttribute.valueConstant ) );
	}

	private void writeSignatureAttribute( SignatureAttribute signatureAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( signatureAttribute.signatureConstant ) );
	}

	private void writeAnnotationsAttribute( AnnotationsAttribute annotationsAttribute )
	{
		writeAnnotations( annotationsAttribute.annotations );
	}

	private static void writeSyntheticAttribute( SyntheticAttribute syntheticAttribute )
	{
		Kit.get( syntheticAttribute ); //nothing to do
	}

	private void writeAnnotationDefaultAttribute( AnnotationDefaultAttribute annotationDefaultAttribute )
	{
		AnnotationValue annotationValue = annotationDefaultAttribute.annotationValue;
		bufferWriter.writeUnsignedByte( annotationValue.tag );
		switch( annotationValue.tag )
		{
			case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger, //
				AnnotationValue.tagLong, AnnotationValue.tagShort, AnnotationValue.tagString -> writeConstAnnotationValue( annotationValue.asConstAnnotationValue() );
			case AnnotationValue.tagAnnotation -> writeAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue() );
			case AnnotationValue.tagArray -> writeArrayAnnotationValue( annotationValue.asArrayAnnotationValue() );
			case AnnotationValue.tagClass -> writeClassAnnotationValue( annotationValue.asClassAnnotationValue() );
			case AnnotationValue.tagEnum -> writeEnumAnnotationValue( annotationValue.asEnumAnnotationValue() );
			default -> throw new AssertionError( annotationValue );
		}
	}

	private void writeBootstrapMethodsAttribute( BootstrapMethodsAttribute bootstrapMethodsAttribute )
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

	private void writeConstantValueAttribute( ConstantValueAttribute constantValueAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( constantValueAttribute.valueConstant ) );
	}

	private static void writeDeprecatedAttribute( DeprecatedAttribute deprecatedAttribute )
	{
		Kit.get( deprecatedAttribute ); // nothing to do
	}

	private void writeEnclosingMethodAttribute( EnclosingMethodAttribute enclosingMethodAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enclosingMethodAttribute.enclosingClassConstant ) );
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = enclosingMethodAttribute.enclosingMethodNameAndDescriptorConstant;
		bufferWriter.writeUnsignedShort( methodNameAndDescriptorConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
	}

	private void writeExceptionsAttribute( ExceptionsAttribute exceptionsAttribute )
	{
		bufferWriter.writeUnsignedShort( exceptionsAttribute.exceptionClassConstants.size() );
		for( ClassConstant exceptionClassConstant : exceptionsAttribute.exceptionClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( exceptionClassConstant ) );
	}

	private void writeInnerClassesAttribute( InnerClassesAttribute innerClassesAttribute )
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

	private void writeLineNumberTableAttribute( Optional<LocationMap> locationMap, LineNumberTableAttribute lineNumberTableAttribute )
	{
		bufferWriter.writeUnsignedShort( lineNumberTableAttribute.entrys.size() );
		for( LineNumberTableEntry lineNumber : lineNumberTableAttribute.entrys )
		{
			int location = locationMap.orElseThrow().getLocation( lineNumber.instruction );
			bufferWriter.writeUnsignedShort( location );
			bufferWriter.writeUnsignedShort( lineNumber.lineNumber );
		}
	}

	private void writeMethodParametersAttribute( MethodParametersAttribute methodParametersAttribute )
	{
		bufferWriter.writeUnsignedByte( methodParametersAttribute.methodParameters.size() );
		for( MethodParameter methodParameter : methodParametersAttribute.methodParameters )
		{
			bufferWriter.writeUnsignedShort( constantPool.getIndex( methodParameter.nameConstant ) );
			bufferWriter.writeUnsignedShort( methodParameter.modifiers.getBits() );
		}
	}

	private void writeNestHostAttribute( NestHostAttribute nestHostAttribute )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nestHostAttribute.hostClassConstant ) );
	}

	private void writeNestMembersAttribute( NestMembersAttribute nestMembersAttribute )
	{
		bufferWriter.writeUnsignedShort( nestMembersAttribute.memberClassConstants.size() );
		for( ClassConstant memberClassConstant : nestMembersAttribute.memberClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( memberClassConstant ) );
	}

	private void writeStackMapTableAttribute( LocationMap locationMap, StackMapTableAttribute stackMapTableAttribute )
	{
		List<StackMapFrame> frames = stackMapTableAttribute.frames();
		bufferWriter.writeUnsignedShort( frames.size() );
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( StackMapFrame frame : frames )
		{
			switch( frame.tag )
			{
				case StackMapFrame.tag_Append:
					writeAppendStackMapFrame( locationMap, previousFrame, frame.asAppendStackMapFrame() );
					break;
				case StackMapFrame.tag_Chop:
					writeChopStackMapFrame( locationMap, previousFrame, frame.asChopStackMapFrame() );
					break;
				case StackMapFrame.tag_Full:
					writeFullStackMapFrame( locationMap, previousFrame, frame.asFullStackMapFrame() );
					break;
				case StackMapFrame.tag_Same, StackMapFrame.tag_SameExtended:
					writeSameStackMapFrame( locationMap, previousFrame, frame.asSameStackMapFrame() );
					break;
				case StackMapFrame.tag_SameLocals1StackItem, StackMapFrame.tag_SameLocals1StackItemExtended:
					writeSameLocals1StackItemStackMapFrame( locationMap, previousFrame, frame.asSameLocals1StackItemStackMapFrame() );
					break;
				default:
					throw new AssertionError( frame );
			}
			previousFrame = Optional.of( frame );
		}
	}

	private void writeSameLocals1StackItemStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame, SameLocals1StackItemStackMapFrame sameLocals1StackItemStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( sameLocals1StackItemStackMapFrame, previousFrame, locationMap );
		if( offsetDelta <= 127 )
			bufferWriter.writeUnsignedByte( 64 + offsetDelta );
		else
		{
			bufferWriter.writeUnsignedByte( SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE );
			bufferWriter.writeUnsignedShort( offsetDelta );
		}
		writeVerificationType( sameLocals1StackItemStackMapFrame.stackVerificationType, locationMap );
	}

	private void writeSameStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame, SameStackMapFrame sameStackMapFrame )
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

	private void writeFullStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame, FullStackMapFrame fullStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( fullStackMapFrame, previousFrame, locationMap );
		bufferWriter.writeUnsignedByte( FullStackMapFrame.type );
		bufferWriter.writeUnsignedShort( offsetDelta );
		List<VerificationType> localVerificationTypes = fullStackMapFrame.localVerificationTypes;
		bufferWriter.writeUnsignedShort( localVerificationTypes.size() );
		for( VerificationType verificationType : localVerificationTypes )
			writeVerificationType( verificationType, locationMap );
		List<VerificationType> stackVerificationTypes = fullStackMapFrame.stackVerificationTypes;
		bufferWriter.writeUnsignedShort( stackVerificationTypes.size() );
		for( VerificationType verificationType : stackVerificationTypes )
			writeVerificationType( verificationType, locationMap );
	}

	private void writeChopStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame, ChopStackMapFrame chopStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( chopStackMapFrame, previousFrame, locationMap );
		bufferWriter.writeUnsignedByte( 251 - chopStackMapFrame.count() );
		bufferWriter.writeUnsignedShort( offsetDelta );
	}

	private void writeAppendStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame, AppendStackMapFrame appendStackMapFrame )
	{
		int offsetDelta = getOffsetDelta( appendStackMapFrame, previousFrame, locationMap );
		List<VerificationType> verificationTypes = appendStackMapFrame.localVerificationTypes();
		assert verificationTypes.size() <= 3;
		bufferWriter.writeUnsignedByte( 251 + verificationTypes.size() );
		bufferWriter.writeUnsignedShort( offsetDelta );
		for( VerificationType verificationType : verificationTypes )
			writeVerificationType( verificationType, locationMap );
	}

	private void writeLocalVariableTypeTableAttribute( LocationMap locationMap, LocalVariableTypeTableAttribute localVariableTypeTableAttribute )
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

	private void writeLocalVariableTableAttribute( LocationMap locationMap, LocalVariableTableAttribute localVariableTableAttribute )
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

	private void writeCodeAttribute( CodeAttribute codeAttribute )
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

		writeAttributeSet( codeAttribute.attributeSet, Optional.of( locationMap ) );
	}

	private void writeParameterAnnotationSets( Collection<ParameterAnnotationSet> parameterAnnotationSets )
	{
		bufferWriter.writeUnsignedByte( parameterAnnotationSets.size() );
		for( ParameterAnnotationSet parameterAnnotationSet : parameterAnnotationSets )
			writeAnnotations( parameterAnnotationSet.annotations );
	}

	private void writeTypeAnnotations( Collection<TypeAnnotation> typeAnnotations )
	{
		bufferWriter.writeUnsignedShort( typeAnnotations.size() );
		for( TypeAnnotation typeAnnotation : typeAnnotations )
		{
			Target target = typeAnnotation.target;
			bufferWriter.writeUnsignedByte( target.tag );
			switch( target.tag )
			{
				case Target.tag_ClassTypeParameter, Target.tag_MethodTypeParameter -> writeTypeParameterTarget( target.asTypeParameterTarget() );
				case Target.tag_Supertype -> writeSupertypeTarget( target.asSupertypeTarget() );
				case Target.tag_ClassTypeBound, Target.tag_MethodTypeBound -> writeTypeParameterBoundTarget( target.asTypeParameterBoundTarget() );
				case Target.tag_FieldType, Target.tag_ReturnType, Target.tag_ReceiverType -> writeEmptyTarget( target.asEmptyTarget() );
				case Target.tag_FormalParameter -> writeFormalParameterTarget( target.asFormalParameterTarget() );
				case Target.tag_Throws -> writeThrowsTarget( target.asThrowsTarget() );
				case Target.tag_LocalVariable, Target.tag_ResourceLocalVariable -> writeLocalVariableTarget( target.asLocalVariableTarget() );
				case Target.tag_Catch -> writeCatchTarget( target.asCatchTarget() );
				case Target.tag_InstanceOfOffset, Target.tag_NewExpressionOffset, Target.tag_NewMethodOffset, Target.tag_IdentifierMethodOffset -> //
					writeOffsetTarget( target.asOffsetTarget() );
				case Target.tag_CastArgument, Target.tag_ConstructorArgument, Target.tag_MethodArgument, Target.tag_NewMethodArgument, //
					Target.tag_IdentifierMethodArgument -> writeTypeArgumentTarget( target.asTypeArgumentTarget() );
				default -> throw new AssertionError( target );
			}
			List<TypePathEntry> entries = typeAnnotation.typePath.entries;
			bufferWriter.writeUnsignedByte( entries.size() );
			for( TypePathEntry entry : entries )
			{
				bufferWriter.writeUnsignedByte( entry.pathKind );
				bufferWriter.writeUnsignedByte( entry.argumentIndex );
			}
			bufferWriter.writeUnsignedShort( typeAnnotation.typeIndex );
			writeAnnotationParameters( typeAnnotation.parameters );
		}
	}

	private void writeTypeArgumentTarget( TypeArgumentTarget typeArgumentTarget )
	{
		bufferWriter.writeUnsignedShort( typeArgumentTarget.offset );
		bufferWriter.writeUnsignedByte( typeArgumentTarget.typeArgumentIndex );
	}

	private void writeOffsetTarget( OffsetTarget offsetTarget )
	{
		bufferWriter.writeUnsignedShort( offsetTarget.offset );
	}

	private void writeCatchTarget( CatchTarget catchTarget )
	{
		bufferWriter.writeUnsignedShort( catchTarget.exceptionTableIndex );
	}

	private void writeLocalVariableTarget( LocalVariableTarget localVariableTarget )
	{
		bufferWriter.writeUnsignedShort( localVariableTarget.entries.size() );
		for( LocalVariableTargetEntry entry : localVariableTarget.entries )
		{
			bufferWriter.writeUnsignedShort( entry.startPc );
			bufferWriter.writeUnsignedShort( entry.length );
			bufferWriter.writeUnsignedShort( entry.index );
		}
	}

	private void writeThrowsTarget( ThrowsTarget throwsTarget )
	{
		bufferWriter.writeUnsignedShort( throwsTarget.throwsTypeIndex );
	}

	private void writeFormalParameterTarget( FormalParameterTarget formalParameterTarget )
	{
		bufferWriter.writeUnsignedByte( formalParameterTarget.formalParameterIndex );
	}

	private static void writeEmptyTarget( EmptyTarget emptyTarget )
	{
		Kit.get( emptyTarget ); //nothing to do
	}

	private void writeTypeParameterBoundTarget( TypeParameterBoundTarget typeParameterBoundTarget )
	{
		bufferWriter.writeUnsignedByte( typeParameterBoundTarget.typeParameterIndex );
		bufferWriter.writeUnsignedByte( typeParameterBoundTarget.boundIndex );
	}

	private void writeSupertypeTarget( SupertypeTarget supertypeTarget )
	{
		bufferWriter.writeUnsignedByte( supertypeTarget.supertypeIndex );
	}

	private void writeTypeParameterTarget( TypeParameterTarget typeParameterTarget )
	{
		bufferWriter.writeUnsignedByte( typeParameterTarget.typeParameterIndex );
	}

	private void writeAttributeSet( AttributeSet attributeSet, Optional<LocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( attributeSet.size() );
		for( Attribute attribute : attributeSet.allAttributes() )
		{
			Mutf8Constant nameConstant = attribute.mutf8Name;
			bufferWriter.writeUnsignedShort( constantPool.getIndex( nameConstant ) );
			int position = bufferWriter.getPosition();
			bufferWriter.writeInt( 0 );
			writeAttribute( attribute, locationMap );
			int length = bufferWriter.getPosition() - position - 4;
			bufferWriter.writeInt( position, length );
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
			case Instruction.groupTag_ClassConstantReferencing -> writeClassReferencingInstruction( instructionWriter, instruction.asClassReferencingInstruction() );
			case Instruction.groupTag_FieldConstantReferencing -> writeFieldReferencingInstruction( instructionWriter, instruction.asFieldReferencingInstruction() );
			case Instruction.groupTag_IInc -> writeIIncInstruction( instructionWriter, instruction.asIIncInstruction() );
			case Instruction.groupTag_InvokeDynamic -> writeInvokeDynamicInstruction( instructionWriter, instruction.asInvokeDynamicInstruction() );
			case Instruction.groupTag_InvokeInterface -> writeInvokeInterfaceInstruction( instructionWriter, instruction.asInvokeInterfaceInstruction() );
			case Instruction.groupTag_LocalVariable -> writeLocalVariableInstruction( instructionWriter, instruction.asLocalVariableInstruction() );
			case Instruction.groupTag_LookupSwitch -> writeLookupSwitchInstruction( instructionWriter, instruction.asLookupSwitchInstruction() );
			case Instruction.groupTag_MethodConstantReferencing -> writeMethodReferencingInstruction( instructionWriter, instruction.asMethodReferencingInstruction() );
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
		instructionWriter.writeInt( tableSwitchInstruction.lowValue + tableSwitchInstruction.targetInstructions.size() - 1 );
		for( Instruction targetInstruction : tableSwitchInstruction.targetInstructions )
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
			instructionWriter.writeInt( lookupSwitchEntry.value );
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

	private static void writeClassReferencingInstruction( InstructionWriter instructionWriter, ClassReferencingInstruction classReferencingInstruction )
	{
		instructionWriter.writeUnsignedByte( classReferencingInstruction.opCode );
		int constantIndex = instructionWriter.getIndex( classReferencingInstruction.targetClassConstant );
		instructionWriter.writeUnsignedShort( constantIndex );
	}

	private static void writeFieldReferencingInstruction( InstructionWriter instructionWriter, FieldReferencingInstruction fieldReferencingInstruction )
	{
		instructionWriter.writeUnsignedByte( fieldReferencingInstruction.opCode );
		int constantIndex = instructionWriter.getIndex( fieldReferencingInstruction.fieldReferenceConstant );
		instructionWriter.writeUnsignedShort( constantIndex );
	}

	private static void writeMethodReferencingInstruction( InstructionWriter instructionWriter, MethodReferencingInstruction methodReferencingInstruction )
	{
		instructionWriter.writeUnsignedByte( methodReferencingInstruction.opCode );
		int constantIndex = instructionWriter.getIndex( methodReferencingInstruction.methodReferenceConstant );
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

	private void writeConstAnnotationValue( ConstAnnotationValue constAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( constAnnotationValue.valueConstant ) );
	}

	private void writeEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enumAnnotationValue.enumClassDescriptorStringConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enumAnnotationValue.enumValueNameConstant ) );
	}

	private void writeClassAnnotationValue( ClassAnnotationValue classAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( classAnnotationValue.classDescriptorStringConstant ) );
	}

	private void writeAnnotationAnnotationValue( AnnotationAnnotationValue annotationAnnotationValue )
	{
		Annotation annotation = annotationAnnotationValue.annotation;
		bufferWriter.writeUnsignedShort( constantPool.getIndex( annotation.annotationTypeDescriptorStringConstant ) );
		bufferWriter.writeUnsignedShort( annotation.parameters.size() );
		for( AnnotationParameter annotationParameter : annotation.parameters )
			writeAnnotationParameter( annotationParameter );
	}

	private void writeArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( arrayAnnotationValue.annotationValues.size() );
		for( AnnotationValue annotationValue : arrayAnnotationValue.annotationValues )
		{
			bufferWriter.writeUnsignedByte( annotationValue.tag );
			switch( annotationValue.tag )
			{
				case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger, //
					AnnotationValue.tagLong, AnnotationValue.tagShort, AnnotationValue.tagString -> writeConstAnnotationValue( annotationValue.asConstAnnotationValue() );
				case AnnotationValue.tagAnnotation -> writeAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue() );
				case AnnotationValue.tagArray -> writeArrayAnnotationValue( annotationValue.asArrayAnnotationValue() );
				case AnnotationValue.tagClass -> writeClassAnnotationValue( annotationValue.asClassAnnotationValue() );
				case AnnotationValue.tagEnum -> writeEnumAnnotationValue( annotationValue.asEnumAnnotationValue() );
				default -> throw new AssertionError( annotationValue );
			}
		}
	}

	private void writeVerificationType( VerificationType verificationType, LocationMap locationMap )
	{
		bufferWriter.writeUnsignedByte( verificationType.tag );
		switch( verificationType.tag )
		{
			case VerificationType.tag_Top, VerificationType.tag_Integer, VerificationType.tag_Float, VerificationType.tag_Double, VerificationType.tag_Long, //
				VerificationType.tag_Null, VerificationType.tag_UninitializedThis -> writeSimpleVerificationType( verificationType.asSimpleVerificationType() );
			case VerificationType.tag_Object -> writeObjectVerificationType( verificationType.asObjectVerificationType() );
			case VerificationType.tag_Uninitialized -> writeUninitializedVerificationType( locationMap, verificationType.asUninitializedVerificationType() );
			default -> throw new AssertionError( verificationType );
		}
	}

	private void writeUninitializedVerificationType( LocationMap locationMap, UninitializedVerificationType uninitializedVerificationType )
	{
		int targetLocation = locationMap.getLocation( uninitializedVerificationType.instruction );
		bufferWriter.writeUnsignedShort( targetLocation );
	}

	private void writeObjectVerificationType( ObjectVerificationType objectVerificationType )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( objectVerificationType.classConstant ) );
	}

	private static void writeSimpleVerificationType( SimpleVerificationType simpleVerificationType )
	{
		Kit.get( simpleVerificationType ); /* nothing to do */
	}

	private void writeAnnotationParameter( AnnotationParameter annotationParameter )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( annotationParameter.nameConstant ) );
		bufferWriter.writeUnsignedByte( annotationParameter.value.tag );
		switch( annotationParameter.value.tag )
		{
			case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger, AnnotationValue.tagLong, //
				AnnotationValue.tagShort, AnnotationValue.tagString -> writeConstAnnotationValue( annotationParameter.value.asConstAnnotationValue() );
			case AnnotationValue.tagAnnotation -> writeAnnotationAnnotationValue( annotationParameter.value.asAnnotationAnnotationValue() );
			case AnnotationValue.tagArray -> writeArrayAnnotationValue( annotationParameter.value.asArrayAnnotationValue() );
			case AnnotationValue.tagClass -> writeClassAnnotationValue( annotationParameter.value.asClassAnnotationValue() );
			case AnnotationValue.tagEnum -> writeEnumAnnotationValue( annotationParameter.value.asEnumAnnotationValue() );
			default -> throw new AssertionError( annotationParameter.value );
		}
	}

	private void writeAnnotations( Collection<Annotation> annotations )
	{
		bufferWriter.writeUnsignedShort( annotations.size() );
		for( Annotation annotation : annotations )
			writeAnnotation( annotation );
	}

	private void writeAnnotation( Annotation byteCodeAnnotation )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( byteCodeAnnotation.annotationTypeDescriptorStringConstant ) );
		writeAnnotationParameters( byteCodeAnnotation.parameters );
	}

	private void writeAnnotationParameters( Collection<AnnotationParameter> annotationParameters )
	{
		bufferWriter.writeUnsignedShort( annotationParameters.size() );
		for( AnnotationParameter annotationParameter : annotationParameters )
			writeAnnotationParameter( annotationParameter );
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
