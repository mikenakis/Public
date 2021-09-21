package mikenakis.bytecode.writing;

import mikenakis.bytecode.exceptions.UnknownConstantException;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.kit.OmniSwitch4;
import mikenakis.bytecode.kit.OmniSwitch5;
import mikenakis.bytecode.model.ElementValuePair;
import mikenakis.bytecode.model.ElementValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.annotationvalues.AnnotationElementValue;
import mikenakis.bytecode.model.annotationvalues.ArrayElementValue;
import mikenakis.bytecode.model.annotationvalues.ClassElementValue;
import mikenakis.bytecode.model.annotationvalues.ConstElementValue;
import mikenakis.bytecode.model.annotationvalues.EnumElementValue;
import mikenakis.bytecode.model.attributes.AnnotationDefaultAttribute;
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
import mikenakis.bytecode.model.attributes.LineNumberEntry;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariable;
import mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableType;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.model.attributes.MethodParameter;
import mikenakis.bytecode.model.attributes.MethodParametersAttribute;
import mikenakis.bytecode.model.attributes.NestHostAttribute;
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
import mikenakis.bytecode.model.TypeAnnotation;
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
import mikenakis.bytecode.model.attributes.target.OffsetTarget;
import mikenakis.bytecode.model.attributes.target.SupertypeTarget;
import mikenakis.bytecode.model.attributes.target.Target;
import mikenakis.bytecode.model.attributes.target.ThrowsTarget;
import mikenakis.bytecode.model.attributes.target.TypeArgumentTarget;
import mikenakis.bytecode.model.attributes.target.TypeParameterBoundTarget;
import mikenakis.bytecode.model.attributes.target.TypeParameterTarget;
import mikenakis.bytecode.model.attributes.target.TypePath;
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
		constantPool.internConstant( byteCodeType.thisClassConstant );
		byteCodeType.superClassConstant.ifPresent( c -> constantPool.internConstant( c ) );
		for( Constant extraConstant : byteCodeType.extraConstants() )
			constantPool.internConstant( extraConstant );
		for( ClassConstant interfaceClassConstant : byteCodeType.interfaceClassConstants() )
			constantPool.internConstant( interfaceClassConstant );
		for( ByteCodeField field : byteCodeType.fields() )
			constantPool.internMember( field );
		for( ByteCodeMethod method : byteCodeType.methods() )
			constantPool.internMember( method );
		constantPool.internAttributeSet( byteCodeType.attributeSet() );

		bufferWriter.writeUnsignedShort( constantPool.size() );
		for( Constant constant : constantPool.constants() )
			writeConstant( constantPool, bufferWriter, constant );
		bufferWriter.writeUnsignedShort( byteCodeType.modifierSet().toInt() );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( byteCodeType.thisClassConstant ) );
		bufferWriter.writeUnsignedShort( byteCodeType.superClassConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
		bufferWriter.writeUnsignedShort( byteCodeType.interfaceClassConstants().size() );
		for( ClassConstant interfaceClassConstant : byteCodeType.interfaceClassConstants() )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( interfaceClassConstant ) );
		bufferWriter.writeUnsignedShort( byteCodeType.fields().size() );
		for( ByteCodeField field : byteCodeType.fields() )
		{
			bufferWriter.writeUnsignedShort( field.modifierSet.toInt() );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( field.nameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( field.descriptorConstant ) );
			writeAttributeSet( bufferWriter, constantPool, field.attributeSet, ByteCodeWriter::writeFieldAttribute );
		}
		bufferWriter.writeUnsignedShort( byteCodeType.methods().size() );
		for( ByteCodeMethod method : byteCodeType.methods() )
		{
			bufferWriter.writeUnsignedShort( method.modifierSet.toInt() );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( method.nameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( method.descriptorConstant ) );
			writeAttributeSet( bufferWriter, constantPool, method.attributeSet, ByteCodeWriter::writeMethodAttribute );
		}
		writeAttributeSet( bufferWriter, constantPool, byteCodeType.attributeSet(), ByteCodeWriter::writeClassAttribute );
		return bufferWriter.toBytes();
	}

	private static void writeConstant( ConstantPool constantPool, BufferWriter bufferWriter, Constant constant )
	{
		bufferWriter.writeUnsignedByte( constant.tag.tagNumber() );
		switch( constant.tag )
		{
			case Mutf8:
			{
				Mutf8Constant mutf8Constant = constant.asMutf8Constant();
				Buffer buffer = mutf8Constant.buffer();
				bufferWriter.writeUnsignedShort( buffer.length() );
				bufferWriter.writeBuffer( buffer );
				break;
			}
			case Integer:
			{
				IntegerConstant integerConstant = constant.asIntegerConstant();
				bufferWriter.writeInt( integerConstant.value );
				break;
			}
			case Float:
			{
				FloatConstant floatConstant = constant.asFloatConstant();
				bufferWriter.writeFloat( floatConstant.value );
				break;
			}
			case Long:
			{
				LongConstant longConstant = constant.asLongConstant();
				bufferWriter.writeLong( longConstant.value );
				break;
			}
			case Double:
			{
				DoubleConstant doubleConstant = constant.asDoubleConstant();
				bufferWriter.writeDouble( doubleConstant.value );
				break;
			}
			case Class:
			{
				ClassConstant classConstant = constant.asClassConstant();
				bufferWriter.writeUnsignedShort( constantPool.getIndex( classConstant.nameConstant() ) );
				break;
			}
			case String:
			{
				StringConstant stringConstant = constant.asStringConstant();
				bufferWriter.writeUnsignedShort( constantPool.getIndex( stringConstant.valueConstant() ) );
				break;
			}
			case FieldReference:
			{
				FieldReferenceConstant fieldReferenceConstant = constant.asFieldReferenceConstant();
				bufferWriter.writeUnsignedShort( constantPool.getIndex( fieldReferenceConstant.typeConstant() ) );
				bufferWriter.writeUnsignedShort( constantPool.getIndex( fieldReferenceConstant.nameAndDescriptorConstant() ) );
				break;
			}
			case MethodReference:
			{
				MethodReferenceConstant methodReferenceConstant = constant.asMethodReferenceConstant();
				bufferWriter.writeUnsignedShort( constantPool.getIndex( methodReferenceConstant.typeConstant() ) );
				bufferWriter.writeUnsignedShort( constantPool.getIndex( methodReferenceConstant.nameAndDescriptorConstant() ) );
				break;
			}
			case InterfaceMethodReference:
			{
				InterfaceMethodReferenceConstant interfaceMethodReferenceConstant = constant.asInterfaceMethodReferenceConstant();
				bufferWriter.writeUnsignedShort( constantPool.getIndex( interfaceMethodReferenceConstant.typeConstant() ) );
				bufferWriter.writeUnsignedShort( constantPool.getIndex( interfaceMethodReferenceConstant.nameAndDescriptorConstant() ) );
				break;
			}
			case NameAndDescriptor:
			{
				NameAndDescriptorConstant nameAndDescriptorConstant = constant.asNameAndDescriptorConstant();
				bufferWriter.writeUnsignedShort( constantPool.getIndex( nameAndDescriptorConstant.nameConstant() ) );
				bufferWriter.writeUnsignedShort( constantPool.getIndex( nameAndDescriptorConstant.descriptorConstant() ) );
				break;
			}
			case MethodHandle:
			{
				MethodHandleConstant methodHandleConstant = constant.asMethodHandleConstant();
				bufferWriter.writeUnsignedByte( methodHandleConstant.referenceKind().number );
				bufferWriter.writeUnsignedShort( constantPool.getIndex( methodHandleConstant.referenceConstant() ) );
				break;
			}
			case MethodType:
			{
				MethodTypeConstant methodTypeConstant = constant.asMethodTypeConstant();
				bufferWriter.writeUnsignedShort( constantPool.getIndex( methodTypeConstant.descriptorConstant ) );
				break;
			}
			case InvokeDynamic:
			{
				InvokeDynamicConstant invokeDynamicConstant = constant.asInvokeDynamicConstant();
				bufferWriter.writeUnsignedShort( invokeDynamicConstant.bootstrapMethodIndex() );
				bufferWriter.writeUnsignedShort( constantPool.getIndex( invokeDynamicConstant.nameAndDescriptorConstant() ) );
				break;
			}
			default:
				throw new UnknownConstantException( constant.tag.tagNumber() );
		}
	}

	interface AttributeWriter
	{
		void writeAttribute( ConstantPool constantPool, Attribute attribute, BufferWriter bufferWriter );
	}

	private static void writeAttributeSet( BufferWriter bufferWriter, ConstantPool constantPool, AttributeSet attributeSet, AttributeWriter attributeWriter )
	{
		bufferWriter.writeUnsignedShort( attributeSet.size() );
		for( Attribute attribute : attributeSet )
		{
			Mutf8Constant nameConstant = attribute.kind.mutf8Name;
			bufferWriter.writeUnsignedShort( constantPool.getIndex( nameConstant ) );
			BufferWriter attributeBufferWriter = new BufferWriter();
			attributeWriter.writeAttribute( constantPool, attribute, attributeBufferWriter );
			byte[] bytes = attributeBufferWriter.toBytes();
			bufferWriter.writeInt( bytes.length );
			bufferWriter.writeBytes( bytes );
		}
	}

	private static final OmniSwitch4<Void,Mutf8Constant,ConstantPool,BufferWriter,Attribute> fieldAttributeSwitch = //
		OmniSwitch4.<Void,Mutf8Constant,ConstantPool,BufferWriter,Attribute>newBuilder() //
			.with( ConstantValueAttribute.kind.mutf8Name, ByteCodeWriter::writeConstantValueAttribute )                                     //
			.with( DeprecatedAttribute.kind.mutf8Name, ByteCodeWriter::writeDeprecatedAttribute )                                           //
			.with( RuntimeInvisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeInvisibleAnnotationsAttribute )         //
			.with( RuntimeVisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeVisibleAnnotationsAttribute )             //
			.with( RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeInvisibleTypeAnnotationsAttribute ) //
			.with( RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeVisibleTypeAnnotationsAttribute )     //
			.with( SignatureAttribute.kind.mutf8Name, ByteCodeWriter::writeSignatureAttribute )                                             //
			.with( SyntheticAttribute.kind.mutf8Name, ByteCodeWriter::writeSyntheticAttribute )                                             //
			.withDefault( ByteCodeWriter::writeUnknownAttribute )                                                                     //
			.build();

	private static void writeFieldAttribute( ConstantPool constantPool, Attribute attribute, BufferWriter bufferWriter )
	{
		fieldAttributeSwitch.on( attribute.kind.mutf8Name, constantPool, bufferWriter, attribute );
	}

	private static final OmniSwitch4<Void,Mutf8Constant,ConstantPool,BufferWriter,Attribute> methodAttributeSwitch = //
		OmniSwitch4.<Void,Mutf8Constant,ConstantPool,BufferWriter,Attribute>newBuilder() //
			.with( AnnotationDefaultAttribute.kind.mutf8Name, ByteCodeWriter::writeAnnotationDefaultAttribute )                                        //
			.with( CodeAttribute.kind.mutf8Name, ByteCodeWriter::writeCodeAttribute )                                                             //
			.with( DeprecatedAttribute.kind.mutf8Name, ByteCodeWriter::writeDeprecatedAttribute )                                                      //
			.with( ExceptionsAttribute.kind.mutf8Name, ByteCodeWriter::writeExceptionsAttribute )                                                      //
			.with( MethodParametersAttribute.kind.mutf8Name, ByteCodeWriter::writeMethodParametersAttribute )                                     //
			.with( RuntimeInvisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeInvisibleAnnotationsAttribute )               //
			.with( RuntimeVisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeVisibleAnnotationsAttribute )                   //
			.with( RuntimeInvisibleParameterAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeInvisibleParameterAnnotationsAttribute )  //
			.with( RuntimeVisibleParameterAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeVisibleParameterAnnotationsAttribute )      //
			.with( RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeInvisibleTypeAnnotationsAttribute )       //
			.with( RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeVisibleTypeAnnotationsAttribute )           //
			.with( SignatureAttribute.kind.mutf8Name, ByteCodeWriter::writeSignatureAttribute )                                                        //
			.with( SyntheticAttribute.kind.mutf8Name, ByteCodeWriter::writeSyntheticAttribute )                                                        //
			.withDefault( ByteCodeWriter::writeUnknownAttribute )                                                                                //
			.build();

	private static void writeMethodAttribute( ConstantPool constantPool, Attribute attribute, BufferWriter bufferWriter )
	{
		methodAttributeSwitch.on( attribute.kind.mutf8Name, constantPool, bufferWriter, attribute );
	}

	private static final OmniSwitch4<Void,Mutf8Constant,ConstantPool,BufferWriter,Attribute> classAttributeSwitch = //
		OmniSwitch4.<Void,Mutf8Constant,ConstantPool,BufferWriter,Attribute>newBuilder() //
			.with( BootstrapMethodsAttribute.kind.mutf8Name, ByteCodeWriter::writeBootstrapMethodsAttribute )                               //
			.with( DeprecatedAttribute.kind.mutf8Name, ByteCodeWriter::writeDeprecatedAttribute )                                                //
			.with( EnclosingMethodAttribute.kind.mutf8Name, ByteCodeWriter::writeEnclosingMethodAttribute )                                      //
			.with( InnerClassesAttribute.kind.mutf8Name, ByteCodeWriter::writeInnerClassesAttribute )                                            //
			.with( NestHostAttribute.kind.mutf8Name, ByteCodeWriter::writeNestHostAttribute )                                                    //
			.with( NestMembersAttribute.kind.mutf8Name, ByteCodeWriter::writeNestMembersAttribute )                                              //
			.with( RuntimeInvisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeInvisibleAnnotationsAttribute )         //
			.with( RuntimeVisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeVisibleAnnotationsAttribute )             //
			.with( RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeInvisibleTypeAnnotationsAttribute ) //
			.with( RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeVisibleTypeAnnotationsAttribute )     //
			.with( SignatureAttribute.kind.mutf8Name, ByteCodeWriter::writeSignatureAttribute )                                                  //
			.with( SourceFileAttribute.kind.mutf8Name, ByteCodeWriter::writeSourceFileAttribute )                                           //
			.with( SyntheticAttribute.kind.mutf8Name, ByteCodeWriter::writeSyntheticAttribute )                                                  //
			.withDefault( ByteCodeWriter::writeUnknownAttribute )                                                                          //
			.build();

	private static void writeClassAttribute( ConstantPool constantPool, Attribute attribute, BufferWriter bufferWriter )
	{
		classAttributeSwitch.on( attribute.kind.mutf8Name, constantPool, bufferWriter, attribute );
	}

	private static Void writeDeprecatedAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == DeprecatedAttribute.kind.mutf8Name;
		Kit.get( attribute ); // nothing to do
		return null;
	}

	private static Void writeConstantValueAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == ConstantValueAttribute.kind.mutf8Name;
		ConstantValueAttribute constantValueAttribute = attribute.asConstantValueAttribute();
		bufferWriter.writeUnsignedShort( constantPool.getIndex( constantValueAttribute.valueConstant() ) );
		return null;
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

	private static Void writeCodeAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == CodeAttribute.kind.mutf8Name;
		CodeAttribute codeAttribute = attribute.asCodeAttribute();
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

		writeAttributeSet( bufferWriter, constantPool, codeAttribute.attributeSet(), ( c, a, b ) -> writeCodeAttributeAttribute( c, a, b, locationMap ) );
		return null;
	}


	private static void writeInstruction( Instruction instruction, InstructionWriter instructionWriter )
	{
		switch( instruction.group )
		{
			case Branch:
			{
				BranchInstruction branchInstruction = instruction.asBranchInstruction();
				assert !branchInstruction.isAny();
				int offset = instructionWriter.getOffset( branchInstruction, branchInstruction.getTargetInstruction() );
				instructionWriter.writeUnsignedByte( branchInstruction.getOpCode() );
				if( branchInstruction.isLong() )
					instructionWriter.writeInt( offset );
				else
					instructionWriter.writeSignedShort( offset );
				break;
			}
			case ConditionalBranch:
			{
				ConditionalBranchInstruction conditionalBranchInstruction = instruction.asConditionalBranchInstruction();
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
				break;
			}
			case ConstantReferencing:
			{
				ConstantReferencingInstruction constantReferencingInstruction = instruction.asConstantReferencingInstruction();
				instructionWriter.writeUnsignedByte( constantReferencingInstruction.opCode );
				int constantIndex = instructionWriter.getIndex( constantReferencingInstruction.constant );
				instructionWriter.writeUnsignedShort( constantIndex );
				break;
			}
			case IInc:
			{
				IIncInstruction iIncInstruction = instruction.asIIncInstruction();
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
				break;
			}
			case ImmediateLoadConstant:
			{
				ImmediateLoadConstantInstruction immediateLoadConstantInstruction = instruction.asImmediateLoadConstantInstruction();
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
				break;
			}
			case IndirectLoadConstant:
			{
				IndirectLoadConstantInstruction indirectLoadConstantInstruction = instruction.asIndirectLoadConstantInstruction();
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
				break;
			}
			case InvokeDynamic:
			{
				InvokeDynamicInstruction invokeDynamicInstruction = instruction.asInvokeDynamicInstruction();
				int constantIndex = instructionWriter.getIndex( invokeDynamicInstruction.invokeDynamicConstant );
				instructionWriter.writeUnsignedByte( OpCode.INVOKEDYNAMIC );
				instructionWriter.writeUnsignedShort( constantIndex );
				instructionWriter.writeUnsignedByte( 0 );
				instructionWriter.writeUnsignedByte( 0 );
				break;
			}
			case InvokeInterface:
			{
				InvokeInterfaceInstruction invokeInterfaceInstruction = instruction.asInvokeInterfaceInstruction();
				int constantIndex = instructionWriter.getIndex( invokeInterfaceInstruction.interfaceMethodReferenceConstant );
				instructionWriter.writeUnsignedByte( OpCode.INVOKEINTERFACE );
				instructionWriter.writeUnsignedShort( constantIndex );
				instructionWriter.writeUnsignedByte( invokeInterfaceInstruction.argumentCount );
				instructionWriter.writeUnsignedByte( 0 );
				break;
			}
			case LocalVariable:
			{
				LocalVariableInstruction localVariableInstruction = instruction.asLocalVariableInstruction();
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
				break;
			}
			case LookupSwitch:
			{
				LookupSwitchInstruction lookupSwitchInstruction = instruction.asLookupSwitchInstruction();
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
				break;
			}
			case MultiANewArray:
			{
				MultiANewArrayInstruction multiANewArrayInstruction = instruction.asMultiANewArrayInstruction();
				int constantIndex = instructionWriter.getIndex( multiANewArrayInstruction.classConstant );
				instructionWriter.writeUnsignedByte( OpCode.MULTIANEWARRAY );
				instructionWriter.writeUnsignedShort( constantIndex );
				instructionWriter.writeUnsignedByte( multiANewArrayInstruction.dimensionCount );
				break;
			}
			case NewPrimitiveArray:
			{
				NewPrimitiveArrayInstruction newPrimitiveArrayInstruction = instruction.asNewPrimitiveArrayInstruction();
				instructionWriter.writeUnsignedByte( OpCode.NEWARRAY );
				instructionWriter.writeUnsignedByte( newPrimitiveArrayInstruction.type );
				break;
			}
			case Operandless:
			{
				OperandlessInstruction operandlessInstruction = instruction.asOperandlessInstruction();
				instructionWriter.writeUnsignedByte( operandlessInstruction.opCode );
				break;
			}
			case OperandlessLoadConstant:
			{
				OperandlessLoadConstantInstruction operandlessLoadConstantInstruction = instruction.asOperandlessLoadConstantInstruction();
				instructionWriter.writeUnsignedByte( operandlessLoadConstantInstruction.opCode );
				break;
			}
			case TableSwitch:
			{
				TableSwitchInstruction tableSwitchInstruction = instruction.asTableSwitchInstruction();
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
				break;
			}
			default:
				assert false;
		}
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

	private static final OmniSwitch5<Void,Mutf8Constant,ConstantPool,BufferWriter,LocationMap,Attribute> codeAttributeSwitch = //
		OmniSwitch5.<Void,Mutf8Constant,ConstantPool,BufferWriter,LocationMap,Attribute>newBuilder() //
			.with( LineNumberTableAttribute.kind.mutf8Name, ByteCodeWriter::writeLineNumberTableAttribute )    //
			.with( LocalVariableTableAttribute.kind.mutf8Name, ByteCodeWriter::writeLocalVariableTableAttribute )    //
			.with( LocalVariableTypeTableAttribute.kind.mutf8Name, ByteCodeWriter::writeLocalVariableTypeTableAttribute )    //
			.with( RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeInvisibleTypeAnnotationsAttribute )    //
			.with( RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeWriter::writeRuntimeVisibleTypeAnnotationsAttribute )    //
			.with( StackMapTableAttribute.kind.mutf8Name, ByteCodeWriter::writeStackMapTableAttribute )    //
			.withDefault( ByteCodeWriter::writeUnknownAttribute )                                             //
			.build();

	private static void writeCodeAttributeAttribute( ConstantPool constantPool, Attribute attribute, BufferWriter bufferWriter, LocationMap locationMap )
	{
		codeAttributeSwitch.on( attribute.kind.mutf8Name, constantPool, bufferWriter, locationMap, attribute );
	}

	private static Void writeBootstrapMethodsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == BootstrapMethodsAttribute.kind.mutf8Name;
		BootstrapMethodsAttribute bootstrapMethodsAttribute = attribute.asBootstrapMethodsAttribute();
		List<BootstrapMethod> bootstrapMethods = bootstrapMethodsAttribute.bootstrapMethods();
		bufferWriter.writeUnsignedShort( bootstrapMethods.size() );
		for( BootstrapMethod bootstrapMethod : bootstrapMethods )
		{
			bufferWriter.writeUnsignedShort( constantPool.getIndex( bootstrapMethod.methodHandleConstant() ) );
			List<Constant> argumentConstants = bootstrapMethod.argumentConstants();
			bufferWriter.writeUnsignedShort( argumentConstants.size() );
			for( Constant argumentConstant : argumentConstants )
				bufferWriter.writeUnsignedShort( constantPool.getIndex( argumentConstant ) );
		}
		return null;
	}

	private static Void writeAnnotationDefaultAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == AnnotationDefaultAttribute.kind.mutf8Name;
		AnnotationDefaultAttribute annotationDefaultAttribute = attribute.asAnnotationDefaultAttribute();
		writeAnnotationValue( constantPool, bufferWriter, annotationDefaultAttribute.annotationValue() );
		return null;
	}

	private static void writeAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, ElementValue annotationValue )
	{
		bufferWriter.writeUnsignedByte( annotationValue.tag.character );
		switch( annotationValue.tag )
		{
			case Byte, Character, Double, Float, Integer, Long, Short, Boolean, String -> //
				writeConstAnnotationValue( constantPool, bufferWriter, annotationValue.asConstAnnotationValue() );
			case Annotation -> //
				writeAnnotationAnnotationValue( constantPool, bufferWriter, annotationValue.asAnnotationAnnotationValue() );
			case Array -> //
				writeArrayAnnotationValue( constantPool, bufferWriter, annotationValue.asArrayAnnotationValue() );
			case Class -> //
				writeClassAnnotationValue( constantPool, bufferWriter, annotationValue.asClassAnnotationValue() );
			case Enum -> //
				writeEnumAnnotationValue( constantPool, bufferWriter, annotationValue.asEnumAnnotationValue() );
			default -> throw new AssertionError( annotationValue );
		}
	}

	private static void writeConstAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, ConstElementValue constAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( constAnnotationValue.valueConstant() ) );
	}

	private static void writeEnumAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, EnumElementValue enumAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enumAnnotationValue.typeNameConstant() ) );
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enumAnnotationValue.valueNameConstant() ) );
	}

	private static void writeClassAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, ClassElementValue classAnnotationValue )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( classAnnotationValue.nameConstant() ) );
	}

	private static void writeAnnotationAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, AnnotationElementValue annotationAnnotationValue )
	{
		Annotation annotation = annotationAnnotationValue.annotation();
		bufferWriter.writeUnsignedShort( constantPool.getIndex( annotation.typeConstant ) );
		bufferWriter.writeUnsignedShort( annotation.elementValuePairs().size() );
		for( ElementValuePair annotationParameter : annotation.elementValuePairs() )
			writeAnnotationParameter( constantPool, bufferWriter, annotationParameter );
	}

	private static void writeArrayAnnotationValue( ConstantPool constantPool, BufferWriter bufferWriter, ArrayElementValue arrayAnnotationValue )
	{
		List<ElementValue> annotationValues = arrayAnnotationValue.annotationValues();
		bufferWriter.writeUnsignedShort( annotationValues.size() );
		for( ElementValue annotationValue : annotationValues )
			writeAnnotationValue( constantPool, bufferWriter, annotationValue );
	}

	private static Void writeStackMapTableAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, //
		Attribute attribute )
	{
		assert attributeNameConstant == StackMapTableAttribute.kind.mutf8Name;
		StackMapTableAttribute stackMapTableAttribute = attribute.asStackMapTableAttribute();
		List<StackMapFrame> frames = stackMapTableAttribute.frames();
		bufferWriter.writeUnsignedShort( frames.size() );
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( StackMapFrame frame : frames )
		{
			writeStackMapFrame( constantPool, bufferWriter, locationMap, frame, previousFrame );
			previousFrame = Optional.of( frame );
		}
		return null;
	}

	private static void writeStackMapFrame( ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, StackMapFrame stackMapFrame, //
		Optional<StackMapFrame> previousFrame )
	{
		if( stackMapFrame.isAppendStackMapFrame() )
		{
			AppendStackMapFrame appendStackMapFrame = stackMapFrame.asAppendStackMapFrame();
			int offsetDelta = getOffsetDelta( appendStackMapFrame, previousFrame, locationMap );
			List<VerificationType> verificationTypes = appendStackMapFrame.localVerificationTypes();
			assert verificationTypes.size() <= 3;
			bufferWriter.writeUnsignedByte( 251 + verificationTypes.size() );
			bufferWriter.writeUnsignedShort( offsetDelta );
			for( VerificationType verificationType : verificationTypes )
				writeVerificationType( constantPool, bufferWriter, verificationType, locationMap );
		}
		else if( stackMapFrame.isChopStackMapFrame() )
		{
			ChopStackMapFrame chopStackMapFrame = stackMapFrame.asChopStackMapFrame();
			int offsetDelta = getOffsetDelta( chopStackMapFrame, previousFrame, locationMap );
			bufferWriter.writeUnsignedByte( 251 - chopStackMapFrame.count() );
			bufferWriter.writeUnsignedShort( offsetDelta );
		}
		else if( stackMapFrame.isFullStackMapFrame() )
		{
			FullStackMapFrame fullStackMapFrame = stackMapFrame.asFullStackMapFrame();
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
		else if( stackMapFrame.isSameStackMapFrame() )
		{
			SameStackMapFrame sameStackMapFrame = stackMapFrame.asSameStackMapFrame();
			int offsetDelta = getOffsetDelta( sameStackMapFrame, previousFrame, locationMap );
			if( offsetDelta >= 0 && offsetDelta <= 63 )
				bufferWriter.writeUnsignedByte( offsetDelta );
			else
			{
				bufferWriter.writeUnsignedByte( SameStackMapFrame.EXTENDED_FRAME_TYPE );
				bufferWriter.writeUnsignedShort( offsetDelta );
			}
		}
		else if( stackMapFrame.isSameLocals1StackItemStackMapFrame() )
		{
			SameLocals1StackItemStackMapFrame sameLocals1StackItemStackMapFrame = stackMapFrame.asSameLocals1StackItemStackMapFrame();
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
		else
			assert false;
	}

	private static void writeVerificationType( ConstantPool constantPool, BufferWriter bufferWriter, VerificationType verificationType, LocationMap locationMap )
	{
		bufferWriter.writeUnsignedByte( verificationType.tag.number() );
		verificationType.visit( new VerificationType.Visitor<Void>()
		{
			@Override public Void visit( SimpleVerificationType simpleVerificationType )
			{
				Kit.get( true ); //nothing to do
				return null;
			}
			@Override public Void visit( ObjectVerificationType objectVerificationType )
			{
				bufferWriter.writeUnsignedShort( constantPool.getIndex( objectVerificationType.classConstant() ) );
				return null;
			}
			@Override public Void visit( UninitializedVerificationType uninitializedVerificationType )
			{
				int targetLocation = locationMap.getLocation( uninitializedVerificationType.instruction );
				bufferWriter.writeUnsignedShort( targetLocation );
				return null;
			}
		} );
	}

	private static Void writeSyntheticAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == SyntheticAttribute.kind.mutf8Name;
		Kit.get( attribute ); // nothing to do
		return null;
	}

	private static Void writeSourceFileAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == SourceFileAttribute.kind.mutf8Name;
		SourceFileAttribute sourceFileAttribute = attribute.asSourceFileAttribute();
		bufferWriter.writeUnsignedShort( constantPool.getIndex( sourceFileAttribute.valueConstant() ) );
		return null;
	}

	private static Void writeSignatureAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		SignatureAttribute signatureAttribute = attribute.asSignatureAttribute();
		bufferWriter.writeUnsignedShort( constantPool.getIndex( signatureAttribute.signatureConstant() ) );
		return null;
	}

	private static Void writeRuntimeVisibleTypeAnnotationsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, Attribute attribute )
	{
		return writeRuntimeVisibleTypeAnnotationsAttribute( attributeNameConstant, constantPool, bufferWriter, attribute );
	}

	private static Void writeRuntimeVisibleTypeAnnotationsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name;
		RuntimeVisibleTypeAnnotationsAttribute runtimeVisibleTypeAnnotationsAttribute = attribute.asRuntimeVisibleTypeAnnotationsAttribute();
		List<TypeAnnotation> typeAnnotations = runtimeVisibleTypeAnnotationsAttribute.typeAnnotations();
		bufferWriter.writeUnsignedShort( typeAnnotations.size() );
		for( TypeAnnotation typeAnnotation : typeAnnotations )
			writeTypeAnnotation( constantPool, bufferWriter, typeAnnotation );
		return null;
	}

	private static Void writeRuntimeInvisibleTypeAnnotationsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, Attribute attribute )
	{
		return writeRuntimeInvisibleTypeAnnotationsAttribute( attributeNameConstant, constantPool, bufferWriter, attribute );
	}

	private static Void writeRuntimeInvisibleTypeAnnotationsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name;
		RuntimeInvisibleTypeAnnotationsAttribute runtimeInvisibleTypeAnnotationsAttribute = attribute.asRuntimeInvisibleTypeAnnotationsAttribute();
		List<TypeAnnotation> typeAnnotations = runtimeInvisibleTypeAnnotationsAttribute.typeAnnotations();
		bufferWriter.writeUnsignedShort( typeAnnotations.size() );
		for( TypeAnnotation typeAnnotation : typeAnnotations )
			writeTypeAnnotation( constantPool, bufferWriter, typeAnnotation );
		return null;
	}

	private static void writeTypeAnnotation( ConstantPool constantPool, BufferWriter bufferWriter, TypeAnnotation typeAnnotation )
	{
		Target target = typeAnnotation.target();
		bufferWriter.writeUnsignedByte( target.type.number );
		target.visit( new Target.Visitor<Void>()
		{
			@Override public Void visit( CatchTarget catchTarget )
			{
				bufferWriter.writeUnsignedShort( catchTarget.exceptionTableIndex );
				return null;
			}
			@Override public Void visit( EmptyTarget emptyTarget )
			{
				Kit.get( emptyTarget ); //nothing to do
				return null;
			}
			@Override public Void visit( FormalParameterTarget formalParameterTarget )
			{
				bufferWriter.writeUnsignedByte( formalParameterTarget.formalParameterIndex );
				return null;
			}
			@Override public Void visit( LocalVariableTarget localVariableTarget )
			{
				bufferWriter.writeUnsignedShort( localVariableTarget.entries.size() );
				for( LocalVariableTarget.Entry entry : localVariableTarget.entries )
				{
					bufferWriter.writeUnsignedShort( entry.startPc() );
					bufferWriter.writeUnsignedShort( entry.length() );
					bufferWriter.writeUnsignedShort( entry.index() );
				}
				return null;
			}
			@Override public Void visit( OffsetTarget offsetTarget )
			{
				bufferWriter.writeUnsignedShort( offsetTarget.offset );
				return null;
			}
			@Override public Void visit( SupertypeTarget supertypeTarget )
			{
				bufferWriter.writeUnsignedByte( supertypeTarget.supertypeIndex );
				return null;
			}
			@Override public Void visit( ThrowsTarget throwsTarget )
			{
				bufferWriter.writeUnsignedShort( throwsTarget.throwsTypeIndex );
				return null;
			}
			@Override public Void visit( TypeArgumentTarget typeArgumentTarget )
			{
				bufferWriter.writeUnsignedShort( typeArgumentTarget.offset );
				bufferWriter.writeUnsignedByte( typeArgumentTarget.typeArgumentIndex );
				return null;
			}
			@Override public Void visit( TypeParameterBoundTarget typeParameterBoundTarget )
			{
				bufferWriter.writeUnsignedByte( typeParameterBoundTarget.typeParameterIndex );
				bufferWriter.writeUnsignedByte( typeParameterBoundTarget.boundIndex );
				return null;
			}
			@Override public Void visit( TypeParameterTarget typeParameterTarget )
			{
				bufferWriter.writeUnsignedByte( typeParameterTarget.typeParameterIndex );
				return null;
			}
		} );
		List<TypePath.Entry> entries = typeAnnotation.typePath().entries();
		bufferWriter.writeUnsignedByte( entries.size() );
		for( TypePath.Entry entry : entries )
		{
			bufferWriter.writeUnsignedByte( entry.pathKind() );
			bufferWriter.writeUnsignedByte( entry.argumentIndex() );
		}
		bufferWriter.writeUnsignedShort( typeAnnotation.typeIndex() );
		writeElementValuePairs( constantPool, bufferWriter, typeAnnotation.elementValuePairs() );
	}

	private static void writeAnnotationParameter( ConstantPool constantPool, BufferWriter bufferWriter, ElementValuePair annotationParameter )
	{
		bufferWriter.writeUnsignedShort( constantPool.getIndex( annotationParameter.nameConstant() ) );
		writeAnnotationValue( constantPool, bufferWriter, annotationParameter.elementValue() );
	}

	private static Void writeRuntimeVisibleParameterAnnotationsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == RuntimeVisibleParameterAnnotationsAttribute.kind.mutf8Name;
		RuntimeVisibleParameterAnnotationsAttribute runtimeVisibleParameterAnnotationsAttribute = attribute.asRuntimeVisibleParameterAnnotationsAttribute();
		List<ParameterAnnotationSet> parameterAnnotationSets = runtimeVisibleParameterAnnotationsAttribute.parameterAnnotationSets();
		bufferWriter.writeUnsignedByte( parameterAnnotationSets.size() );
		for( ParameterAnnotationSet parameterAnnotationSet : parameterAnnotationSets )
			writeParameterAnnotationSet( constantPool, bufferWriter, parameterAnnotationSet );
		return null;
	}

	private static Void writeRuntimeInvisibleParameterAnnotationsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == RuntimeInvisibleParameterAnnotationsAttribute.kind.mutf8Name;
		RuntimeInvisibleParameterAnnotationsAttribute runtimeInvisibleParameterAnnotationsAttribute = attribute.asRuntimeInvisibleParameterAnnotationsAttribute();
		List<ParameterAnnotationSet> parameterAnnotationSets = runtimeInvisibleParameterAnnotationsAttribute.parameterAnnotationSets();
		bufferWriter.writeUnsignedByte( parameterAnnotationSets.size() );
		for( ParameterAnnotationSet parameterAnnotationSet : parameterAnnotationSets )
			writeParameterAnnotationSet( constantPool, bufferWriter, parameterAnnotationSet );
		return null;
	}

	private static void writeParameterAnnotationSet( ConstantPool constantPool, BufferWriter bufferWriter, ParameterAnnotationSet parameterAnnotationSet )
	{
		List<Annotation> annotations = parameterAnnotationSet.annotations();
		writeAnnotations( constantPool, bufferWriter, annotations );
	}

	private static Void writeRuntimeInvisibleAnnotationsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == RuntimeInvisibleAnnotationsAttribute.kind.mutf8Name;
		RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute = attribute.asRuntimeInvisibleAnnotationsAttribute();
		Collection<Annotation> annotations = runtimeInvisibleAnnotationsAttribute.annotations();
		writeAnnotations( constantPool, bufferWriter, annotations );
		return null;
	}

	private static Void writeRuntimeVisibleAnnotationsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute = attribute.asRuntimeVisibleAnnotationsAttribute();
		Collection<Annotation> annotations = runtimeVisibleAnnotationsAttribute.annotations();
		writeAnnotations( constantPool, bufferWriter, annotations );
		return null;
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
		writeElementValuePairs( constantPool, bufferWriter, byteCodeAnnotation.elementValuePairs() );
	}

	private static void writeElementValuePairs( ConstantPool constantPool, BufferWriter bufferWriter, Collection<ElementValuePair> elementValuePairs )
	{
		bufferWriter.writeUnsignedShort( elementValuePairs.size() );
		for( ElementValuePair annotationParameter : elementValuePairs )
			writeAnnotationParameter( constantPool, bufferWriter, annotationParameter );
	}

	private static Void writeMethodParametersAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == MethodParametersAttribute.kind.mutf8Name;
		MethodParametersAttribute methodParametersAttribute = attribute.asMethodParametersAttribute();
		bufferWriter.writeUnsignedByte( methodParametersAttribute.methodParameters().size() );
		for( MethodParameter methodParameter : methodParametersAttribute.methodParameters() )
		{
			bufferWriter.writeUnsignedShort( constantPool.getIndex( methodParameter.nameConstant ) );
			bufferWriter.writeUnsignedShort( methodParameter.modifierSet.toInt() );
		}
		return null;
	}

	private static Void writeUnknownAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		UnknownAttribute unknownAttribute = attribute.asUnknownAttribute();
		bufferWriter.writeBuffer( unknownAttribute.buffer() );
		return null;
	}

	private static Void writeUnknownAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, Attribute attribute )
	{
		UnknownAttribute unknownAttribute = attribute.asUnknownAttribute();
		bufferWriter.writeBuffer( unknownAttribute.buffer() );
		return null;
	}

	private static Void writeLocalVariableTypeTableAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, //
		Attribute attribute )
	{
		assert attributeNameConstant == LocalVariableTypeTableAttribute.kind.mutf8Name;
		LocalVariableTypeTableAttribute localVariableTypeTableAttribute = attribute.asLocalVariableTypeTableAttribute();
		List<LocalVariableType> localVariableTypes = localVariableTypeTableAttribute.localVariableTypes();
		bufferWriter.writeUnsignedShort( localVariableTypes.size() );
		for( LocalVariableType localVariableType : localVariableTypes )
		{
			int startLocation = locationMap.getLocation( localVariableType.startInstruction );
			int endLocation = locationMap.getLocation( localVariableType.endInstruction );
			bufferWriter.writeUnsignedShort( startLocation );
			bufferWriter.writeUnsignedShort( endLocation - startLocation );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariableType.nameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariableType.signatureConstant ) );
			bufferWriter.writeUnsignedShort( localVariableType.index );
		}
		return null;
	}

	private static Void writeLocalVariableTableAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, //
		Attribute attribute )
	{
		assert attributeNameConstant == LocalVariableTableAttribute.kind.mutf8Name;
		LocalVariableTableAttribute localVariableTableAttribute = attribute.asLocalVariableTableAttribute();
		List<LocalVariable> localVariables = localVariableTableAttribute.localVariables();
		bufferWriter.writeUnsignedShort( localVariables.size() );
		for( LocalVariable localVariable : localVariables )
		{
			int startLocation = locationMap.getLocation( localVariable.startInstruction );
			int endLocation = locationMap.getLocation( localVariable.endInstruction );
			bufferWriter.writeUnsignedShort( startLocation );
			bufferWriter.writeUnsignedShort( endLocation - startLocation );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariable.nameConstant ) );
			bufferWriter.writeUnsignedShort( constantPool.getIndex( localVariable.descriptorConstant ) );
			bufferWriter.writeUnsignedShort( localVariable.index );
		}
		return null;
	}

	private static Void writeLineNumberTableAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, LocationMap locationMap, //
		Attribute attribute )
	{
		assert attributeNameConstant == LineNumberTableAttribute.kind.mutf8Name;
		LineNumberTableAttribute lineNumberTableAttribute = attribute.asLineNumberTableAttribute();
		bufferWriter.writeUnsignedShort( lineNumberTableAttribute.lineNumbers().size() );
		for( LineNumberEntry lineNumber : lineNumberTableAttribute.lineNumbers() )
		{
			int location = locationMap.getLocation( lineNumber.instruction() );
			bufferWriter.writeUnsignedShort( location );
			bufferWriter.writeUnsignedShort( lineNumber.lineNumber() );
		}
		return null;
	}

	private static Void writeInnerClassesAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == InnerClassesAttribute.kind.mutf8Name;
		InnerClassesAttribute innerClassesAttribute = attribute.asInnerClassesAttribute();
		List<InnerClass> innerClasses = innerClassesAttribute.innerClasses();
		bufferWriter.writeUnsignedShort( innerClasses.size() );
		for( InnerClass innerClass : innerClasses )
		{
			bufferWriter.writeUnsignedShort( constantPool.getIndex( innerClass.innerClassConstant() ) );
			bufferWriter.writeUnsignedShort( innerClass.outerClassConstant().map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
			bufferWriter.writeUnsignedShort( innerClass.innerNameConstant().map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
			bufferWriter.writeUnsignedShort( innerClass.modifierSet().toInt() );
		}
		return null;
	}

	private static Void writeNestHostAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == NestHostAttribute.kind.mutf8Name;
		NestHostAttribute nestHostAttribute = attribute.asNestHostAttribute();
		bufferWriter.writeUnsignedShort( constantPool.getIndex( nestHostAttribute.hostClassConstant ) );
		return null;
	}

	private static Void writeNestMembersAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == NestMembersAttribute.kind.mutf8Name;
		NestMembersAttribute nestMembersAttribute = attribute.asNestMembersAttribute();
		List<ClassConstant> memberClassConstants = nestMembersAttribute.memberClassConstants();
		bufferWriter.writeUnsignedShort( memberClassConstants.size() );
		for( ClassConstant memberClassConstant : memberClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( memberClassConstant ) );
		return null;
	}

	private static Void writeExceptionsAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == ExceptionsAttribute.kind.mutf8Name;
		ExceptionsAttribute exceptionsAttribute = attribute.asExceptionsAttribute();
		List<ClassConstant> exceptionClassConstants = exceptionsAttribute.exceptionClassConstants();
		bufferWriter.writeUnsignedShort( exceptionClassConstants.size() );
		for( ClassConstant exceptionClassConstant : exceptionClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getIndex( exceptionClassConstant ) );
		return null;
	}

	private static Void writeEnclosingMethodAttribute( Mutf8Constant attributeNameConstant, ConstantPool constantPool, BufferWriter bufferWriter, Attribute attribute )
	{
		assert attributeNameConstant == EnclosingMethodAttribute.kind.mutf8Name;
		EnclosingMethodAttribute enclosingMethodAttribute = attribute.asEnclosingMethodAttribute();
		bufferWriter.writeUnsignedShort( constantPool.getIndex( enclosingMethodAttribute.classConstant() ) );
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = enclosingMethodAttribute.methodNameAndDescriptorConstant();
		bufferWriter.writeUnsignedShort( methodNameAndDescriptorConstant.map( c -> constantPool.getIndex( c ) ).orElse( 0 ) );
		return null;
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
