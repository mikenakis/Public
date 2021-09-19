package mikenakis.bytecode.reading;

import mikenakis.bytecode.exceptions.UnknownValueException;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.OmniSwitch3;
import mikenakis.bytecode.kit.OmniSwitch4;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.FlagSet;
import mikenakis.bytecode.model.annotationvalues.AnnotationAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ArrayAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ClassAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ConstAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.EnumAnnotationValue;
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
import mikenakis.bytecode.model.attributes.LineNumber;
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
import mikenakis.bytecode.model.attributes.TypeAnnotation;
import mikenakis.bytecode.model.attributes.UnknownAttribute;
import mikenakis.bytecode.model.attributes.code.AbsoluteInstructionReference;
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
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.Kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ByteCodeReader
{
	public static ByteCodeType read( byte[] bytes )
	{
		Buffer buffer = Buffer.of( bytes );
		BufferReader bufferReader = BufferReader.of( buffer );
		int magic = bufferReader.readInt();
		assert magic == ByteCodeType.MAGIC;
		int minorVersion = bufferReader.readUnsignedShort();
		int majorVersion = bufferReader.readUnsignedShort();
		ConstantPool constantPool = new ConstantPool( bufferReader );
		FlagSet<ByteCodeType.Modifier> modifierSet = ByteCodeType.modifierFlagsEnum.fromInt( bufferReader.readUnsignedShort() );
		ClassConstant thisClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		Optional<ClassConstant> superClassConstant = constantPool.tryReadIndexAndGetConstant( bufferReader ).map( Constant::asClassConstant );
		int interfaceCount = bufferReader.readUnsignedShort();
		Collection<ClassConstant> interfaceClassConstants = new LinkedHashSet<>( interfaceCount );
		for( int i = 0; i < interfaceCount; i++ )
		{
			ClassConstant interfaceClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
			Kit.collection.add( interfaceClassConstants, interfaceClassConstant );
		}
		int fieldCount = bufferReader.readUnsignedShort();
		Collection<ByteCodeField> fields = new LinkedHashSet<>( fieldCount );
		for( int i = 0; i < fieldCount; i++ )
		{
			FlagSet<ByteCodeField.Modifier> fieldModifierSet = ByteCodeField.modifierFlagsEnum.fromInt( bufferReader.readUnsignedShort() );
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			AttributeSet attributes = readAttributes( constantPool, ByteCodeReader::readFieldAttribute, bufferReader );
			ByteCodeField byteCodeField = ByteCodeField.of( fieldModifierSet, nameConstant, descriptorConstant, attributes );
			Kit.collection.add( fields, byteCodeField );
		}
		int methodCount = bufferReader.readUnsignedShort();
		Collection<ByteCodeMethod> methods = new LinkedHashSet<>( methodCount );
		for( int i = 0; i < methodCount; i++ )
		{
			FlagSet<ByteCodeMethod.Modifier> methodModifierSet = ByteCodeMethod.modifierFlagsEnum.fromInt( bufferReader.readUnsignedShort() );
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			AttributeSet attributes = readAttributes( constantPool, ByteCodeReader::readMethodAttribute, bufferReader );
			ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( methodModifierSet, nameConstant, descriptorConstant, attributes );
			Kit.collection.add( methods, byteCodeMethod );
		}
		AttributeSet attributes = readAttributes( constantPool, ByteCodeReader::readClassAttribute, bufferReader );
		assert bufferReader.isAtEnd();
		Collection<Constant> extraConstants = constantPool.getExtraConstants();
		return ByteCodeType.of( minorVersion, majorVersion, modifierSet, thisClassConstant, superClassConstant, interfaceClassConstants, fields, methods, //
			attributes, extraConstants );
	}

	private interface AttributeReader
	{
		Attribute readAttribute( Mutf8Constant nameConstant, ConstantPool constantPool, BufferReader bufferReader );
	}

	private static AttributeSet readAttributes( ConstantPool constantPool, AttributeReader attributeReader, BufferReader bufferReader )
	{
		int count = bufferReader.readUnsignedShort();
		Map<Mutf8Constant,Attribute> attributesFromNames = new LinkedHashMap<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			int attributeLength = bufferReader.readInt();
			Buffer attributeBuffer = bufferReader.readBuffer( attributeLength );
			BufferReader attributeBufferReader = BufferReader.of( attributeBuffer );
			Attribute attribute = attributeReader.readAttribute( nameConstant, constantPool, attributeBufferReader );
			assert attributeBufferReader.isAtEnd();
			Kit.map.add( attributesFromNames, nameConstant, attribute );
		}
		return AttributeSet.of( attributesFromNames );
	}

	private static final OmniSwitch3<Attribute,Mutf8Constant,ConstantPool,BufferReader> classAttributeSwitch = //
		OmniSwitch3.<Attribute,Mutf8Constant,ConstantPool,BufferReader>newBuilder() //
			.with( BootstrapMethodsAttribute.kind.mutf8Name, ByteCodeReader::readBootstrapMethodsAttribute )                               //
			.with( DeprecatedAttribute.kind.mutf8Name, ByteCodeReader::readDeprecatedAttribute )                                           //
			.with( EnclosingMethodAttribute.kind.mutf8Name, ByteCodeReader::readEnclosingMethodAttribute )                                 //
			.with( InnerClassesAttribute.kind.mutf8Name, ByteCodeReader::readInnerClassesAttribute )                                       //
			.with( NestHostAttribute.kind.mutf8Name, ByteCodeReader::readNestHostAttribute )                                               //
			.with( NestMembersAttribute.kind.mutf8Name, ByteCodeReader::readNestMembersAttribute )                                         //
			.with( RuntimeInvisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeInvisibleAnnotationsAttribute )         //
			.with( RuntimeVisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeVisibleAnnotationsAttribute )             //
			.with( RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeInvisibleTypeAnnotationsAttribute ) //
			.with( RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeVisibleTypeAnnotationsAttribute )     //
			.with( SignatureAttribute.kind.mutf8Name, ByteCodeReader::readSignatureAttribute )                                             //
			.with( SourceFileAttribute.kind.mutf8Name, ByteCodeReader::readSourceFileAttribute )                                           //
			.with( SyntheticAttribute.kind.mutf8Name, ByteCodeReader::readSyntheticAttribute )                                             //
			.withDefault( ByteCodeReader::readUnknownAttribute )                                                                     //
			.build();

	private static Attribute readClassAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		return classAttributeSwitch.on( attributeName, constantPool, bufferReader );
	}

	private static BootstrapMethodsAttribute readBootstrapMethodsAttribute( Mutf8Constant nameConstant, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert nameConstant.equalsMutf8Constant( BootstrapMethodsAttribute.kind.mutf8Name );
		int bootstrapMethodCount = bufferReader.readUnsignedShort();
		assert bootstrapMethodCount > 0;
		List<BootstrapMethod> entries = new ArrayList<>( bootstrapMethodCount );
		for( int i = 0; i < bootstrapMethodCount; i++ )
		{
			MethodHandleConstant methodHandleConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMethodHandleConstant();
			int argumentConstantCount = bufferReader.readUnsignedShort();
			assert argumentConstantCount > 0;
			List<Constant> argumentConstants = new ArrayList<>( argumentConstantCount );
			for( int j = 0; j < argumentConstantCount; j++ )
			{
				Constant argumentConstant = constantPool.readIndexAndGetConstant( bufferReader );
				argumentConstants.add( argumentConstant );
			}
			BootstrapMethod entry = BootstrapMethod.of( methodHandleConstant, argumentConstants );
			entries.add( entry );
		}
		return BootstrapMethodsAttribute.of( entries );
	}

	private static EnclosingMethodAttribute readEnclosingMethodAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( EnclosingMethodAttribute.kind.mutf8Name );
		ClassConstant classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
		return EnclosingMethodAttribute.of( classConstant, methodNameAndDescriptorConstant );
	}

	private static InnerClassesAttribute readInnerClassesAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( InnerClassesAttribute.kind.mutf8Name );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<InnerClass> innerClasses = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant innerClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
			Optional<ClassConstant> outerClassConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
			Optional<Mutf8Constant> innerNameConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
			FlagSet<InnerClass.InnerClassModifier> modifierSet = InnerClass.innerClassModifierFlagsEnum.fromInt( bufferReader.readUnsignedShort() );
			InnerClass innerClass = InnerClass.of( innerClassConstant, outerClassConstant, innerNameConstant, modifierSet );
			innerClasses.add( innerClass );
		}
		return InnerClassesAttribute.of( innerClasses );
	}

	private static NestHostAttribute readNestHostAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( NestHostAttribute.kind.mutf8Name );
		ClassConstant hostClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		return NestHostAttribute.of( hostClassConstant );
	}

	private static NestMembersAttribute readNestMembersAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( NestMembersAttribute.kind.mutf8Name );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<ClassConstant> memberClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant memberClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
			memberClassConstants.add( memberClassConstant );
		}
		return NestMembersAttribute.of( memberClassConstants );
	}

	private static RuntimeInvisibleAnnotationsAttribute readRuntimeInvisibleAnnotationsAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( RuntimeInvisibleAnnotationsAttribute.kind.mutf8Name );
		List<ByteCodeAnnotation> annotations = readAnnotations( constantPool, bufferReader );
		return RuntimeInvisibleAnnotationsAttribute.of( annotations );
	}

	private static RuntimeVisibleAnnotationsAttribute readRuntimeVisibleAnnotationsAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( RuntimeVisibleAnnotationsAttribute.kind.mutf8Name );
		List<ByteCodeAnnotation> annotations = readAnnotations( constantPool, bufferReader );
		return RuntimeVisibleAnnotationsAttribute.of( annotations );
	}

	private static RuntimeInvisibleTypeAnnotationsAttribute readRuntimeInvisibleTypeAnnotationsAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name );
		List<TypeAnnotation> entries = readTypeAnnotations( constantPool, bufferReader );
		return RuntimeInvisibleTypeAnnotationsAttribute.of( entries );
	}

	private static RuntimeVisibleTypeAnnotationsAttribute readRuntimeVisibleTypeAnnotationsAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name );
		List<TypeAnnotation> entries = readTypeAnnotations( constantPool, bufferReader );
		return RuntimeVisibleTypeAnnotationsAttribute.of( entries );
	}

	private static RuntimeInvisibleParameterAnnotationsAttribute readRuntimeInvisibleParameterAnnotationsAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( RuntimeInvisibleParameterAnnotationsAttribute.kind.mutf8Name );
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries( constantPool, bufferReader );
		return RuntimeInvisibleParameterAnnotationsAttribute.of( entries );
	}

	private static RuntimeVisibleParameterAnnotationsAttribute readRuntimeVisibleParameterAnnotationsAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( RuntimeVisibleParameterAnnotationsAttribute.kind.mutf8Name );
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries( constantPool, bufferReader );
		return RuntimeVisibleParameterAnnotationsAttribute.of( entries );
	}

	private static SignatureAttribute readSignatureAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( SignatureAttribute.kind.mutf8Name );
		Mutf8Constant signatureConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		return SignatureAttribute.of( signatureConstant );
	}

	private static SyntheticAttribute readSyntheticAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( SyntheticAttribute.kind.mutf8Name );
		return SyntheticAttribute.of();
	}

	private static SourceFileAttribute readSourceFileAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( SourceFileAttribute.kind.mutf8Name );
		Mutf8Constant valueConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		return SourceFileAttribute.of( valueConstant );
	}

	private static DeprecatedAttribute readDeprecatedAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( DeprecatedAttribute.kind.mutf8Name );
		return DeprecatedAttribute.of();
	}

	private static UnknownAttribute readUnknownAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		Buffer buffer = bufferReader.readBuffer();
		return UnknownAttribute.of( attributeName, buffer );
	}

	private static UnknownAttribute readUnknownAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap )
	{
		Buffer buffer = bufferReader.readBuffer();
		return UnknownAttribute.of( attributeName, buffer );
	}

	private static ConstantValueAttribute readConstantValueAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( ConstantValueAttribute.kind.mutf8Name );
		Constant constant = constantPool.readIndexAndGetConstant( bufferReader );
		ValueConstant<?> valueConstant = constant.asValueConstant();
		return ConstantValueAttribute.of( valueConstant );
	}

	private static AnnotationDefaultAttribute readAnnotationDefaultAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( AnnotationDefaultAttribute.kind.mutf8Name );
		AnnotationValue annotationValue = readAnnotationValue( constantPool, bufferReader );
		return AnnotationDefaultAttribute.of( annotationValue );
	}

	private static CodeAttribute readCodeAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( CodeAttribute.kind.mutf8Name );
		int maxStack = bufferReader.readUnsignedShort();
		int maxLocals = bufferReader.readUnsignedShort();
		int codeLength = bufferReader.readInt();
		Buffer codeBuffer = bufferReader.readBuffer( codeLength );

		List<Instruction> instructions = new ArrayList<>();
		BufferReader codeBufferReader = BufferReader.of( codeBuffer );
		ReadingLocationMap locationMap = new ReadingLocationMap( codeBuffer.length() );
		ConcreteInstructionReader.run( codeBufferReader, locationMap, constantPool, instructionReader -> //
		{
			while( !codeBufferReader.isAtEnd() )
			{
				int startLocation = codeBufferReader.getPosition();
				Instruction instruction = readInstruction( instructionReader );
				int endLocation = codeBufferReader.getPosition();
				locationMap.add( startLocation, instruction, endLocation - startLocation );
				instructions.add( instruction );
			}
		} );

		int count = bufferReader.readUnsignedShort();
		List<ExceptionInfo> exceptionInfos = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			AbsoluteInstructionReference startPc = readAbsoluteInstructionReference( bufferReader, locationMap );
			AbsoluteInstructionReference endPc = readAbsoluteInstructionReference( bufferReader, locationMap );
			AbsoluteInstructionReference handlerPc = readAbsoluteInstructionReference( bufferReader, locationMap );
			Optional<ClassConstant> catchTypeConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
			ExceptionInfo exceptionInfo = ExceptionInfo.of( startPc, endPc, handlerPc, catchTypeConstant );
			exceptionInfos.add( exceptionInfo );
		}
		AttributeSet attributes = readAttributes( constantPool, ( a, c, b ) -> readCodeAttribute( a, c, b, locationMap ), bufferReader );
		CodeAttribute codeAttribute = CodeAttribute.of( maxStack, maxLocals, instructions, exceptionInfos, attributes );
		return codeAttribute;
	}

	private static Instruction readInstruction( InstructionReader instructionReader )
	{
		boolean wide = false;
		int opCode = instructionReader.readUnsignedByte();
		if( opCode == OpCode.WIDE )
		{
			wide = true;
			opCode = instructionReader.readUnsignedByte();
		}
		Instruction.Group instructionGroup = Instruction.groupFromOpCode( opCode );
		switch( instructionGroup )
		{
			case Operandless: //
				assert !wide;
				return OperandlessInstruction.of( opCode );
			case OperandlessLoadConstant: //
				assert !wide;
				return OperandlessLoadConstantInstruction.of( opCode );
			case ImmediateLoadConstant: //
			{
				assert !wide;
				int immediateValue = switch( opCode )
					{
						case OpCode.BIPUSH -> instructionReader.readUnsignedByte();
						case OpCode.SIPUSH -> instructionReader.readUnsignedShort();
						default -> throw new IllegalArgumentException();
					};
				return ImmediateLoadConstantInstruction.of( opCode, immediateValue );
			}
			case IndirectLoadConstant:
			{
				assert !wide;
				int constantIndexValue = IndirectLoadConstantInstruction.isWide( opCode ) ? instructionReader.readUnsignedShort() : instructionReader.readUnsignedByte();
				Constant constant = instructionReader.getConstant( constantIndexValue );
				return IndirectLoadConstantInstruction.of( opCode, constant );
			}
			case LocalVariable: //
			{
				assert LocalVariableInstruction.hasOperand( opCode ) || !wide;
				LocalVariableInstruction.IndexType indexType = LocalVariableInstruction.getIndexType( opCode );
				int index;
				if( indexType == LocalVariableInstruction.IndexType.ByOperand )
				{
					if( wide )
						index = instructionReader.readUnsignedShort();
					else
						index = instructionReader.readUnsignedByte();
				}
				else
				{
					assert !wide;
					index = indexType.index();
				}
				return LocalVariableInstruction.of( opCode, index );
			}
			case IInc:
			{
				int index;
				int delta;
				if( wide )
				{
					index = instructionReader.readUnsignedShort();
					delta = instructionReader.readSignedShort();
				}
				else
				{
					index = instructionReader.readUnsignedByte();
					delta = instructionReader.readSignedByte();
				}
				return IIncInstruction.of( wide, index, delta );
			}
			case ConditionalBranch: //
			{
				assert !wide;
				ConditionalBranchInstruction conditionalBranchInstruction = ConditionalBranchInstruction.of( opCode );
				int targetInstructionOffset = instructionReader.readSignedShort();
				instructionReader.setRelativeTargetInstruction( conditionalBranchInstruction, targetInstructionOffset, conditionalBranchInstruction::setTargetInstruction );
				return conditionalBranchInstruction;
			}
			case Branch: //
			{
				assert !wide;
				BranchInstruction branchInstruction = BranchInstruction.of( opCode );
				int targetInstructionOffset = branchInstruction.isLong() ? instructionReader.readInt() : instructionReader.readSignedShort();
				instructionReader.setRelativeTargetInstruction( branchInstruction, targetInstructionOffset, branchInstruction::setTargetInstruction );
				return branchInstruction;
			}
			case TableSwitch:
			{
				assert !wide;
				instructionReader.skipToAlign();
				int defaultInstructionOffset = instructionReader.readInt();
				int lowValue = instructionReader.readInt();
				int highValue = instructionReader.readInt();
				int entryCount = highValue - lowValue + 1;
				TableSwitchInstruction tableSwitchInstruction = TableSwitchInstruction.of( entryCount, lowValue );
				instructionReader.setRelativeTargetInstruction( tableSwitchInstruction, defaultInstructionOffset, tableSwitchInstruction::setDefaultInstruction );
				for( int index = 0; index < entryCount; index++ )
				{
					int targetInstructionOffset = instructionReader.readInt();
					int targetInstructionIndex = index;
					instructionReader.setRelativeTargetInstruction( tableSwitchInstruction, targetInstructionOffset, //
						targetInstruction -> tableSwitchInstruction.targetInstructions().set( targetInstructionIndex, targetInstruction ) );
				}
				return tableSwitchInstruction;
			}
			case LookupSwitch:
			{
				assert !wide;
				assert opCode == OpCode.LOOKUPSWITCH;
				instructionReader.skipToAlign();
				int defaultInstructionOffset = instructionReader.readInt();
				int count = instructionReader.readInt();
				assert count > 0;
				LookupSwitchInstruction lookupSwitchInstruction = LookupSwitchInstruction.of( count );
				instructionReader.setRelativeTargetInstruction( lookupSwitchInstruction, defaultInstructionOffset, lookupSwitchInstruction::setDefaultInstruction );
				for( int index = 0; index < count; index++ )
				{
					int value = instructionReader.readInt();
					int entryInstructionOffset = instructionReader.readInt();
					LookupSwitchEntry lookupSwitchEntry = LookupSwitchEntry.of( value );
					instructionReader.setRelativeTargetInstruction( lookupSwitchInstruction, entryInstructionOffset, lookupSwitchEntry::setTargetInstruction );
					lookupSwitchInstruction.entries.add( lookupSwitchEntry );
				}
				return lookupSwitchInstruction;
			}
			case ConstantReferencing:
			{
				assert !wide;
				int constantIndex = instructionReader.readUnsignedShort();
				Constant constant = instructionReader.getConstant( constantIndex );
				return ConstantReferencingInstruction.of( opCode, constant );
			}
			case InvokeInterface:
			{
				assert !wide;
				assert opCode == OpCode.INVOKEINTERFACE;
				int constantIndex = instructionReader.readUnsignedShort();
				InterfaceMethodReferenceConstant constant = instructionReader.getConstant( constantIndex ).asInterfaceMethodReferenceConstant();
				int argumentCount = instructionReader.readUnsignedByte();
				int extraByte = instructionReader.readUnsignedByte(); //one extra byte, unused.
				assert extraByte == 0;
				return InvokeInterfaceInstruction.of( constant, argumentCount );
			}
			case InvokeDynamic:
			{
				assert !wide;
				assert opCode == OpCode.INVOKEDYNAMIC;
				int constantIndex = instructionReader.readUnsignedShort();
				InvokeDynamicConstant invokeDynamicConstant = instructionReader.getConstant( constantIndex ).asInvokeDynamicConstant();
				int operand2 = instructionReader.readUnsignedByte(); //one extra byte, unused.
				assert operand2 == 0;
				int operand3 = instructionReader.readUnsignedByte(); //one extra byte, unused.
				assert operand3 == 0;
				return InvokeDynamicInstruction.of( invokeDynamicConstant );
			}
			case NewPrimitiveArray:
			{
				assert !wide;
				assert opCode == OpCode.NEWARRAY;
				NewPrimitiveArrayInstruction.Type type = NewPrimitiveArrayInstruction.Type.fromNumber( instructionReader.readUnsignedByte() );
				return NewPrimitiveArrayInstruction.of( type );
			}
			case MultiANewArray:
			{
				assert !wide;
				assert opCode == OpCode.MULTIANEWARRAY;
				int constantIndex = instructionReader.readUnsignedShort();
				ClassConstant constant = instructionReader.getConstant( constantIndex ).asClassConstant();
				int dimensionCount = instructionReader.readUnsignedByte();
				return MultiANewArrayInstruction.of( constant, dimensionCount );
			}
			default:
				throw new AssertionError( opCode );
		}
	}

	private static final OmniSwitch4<Attribute,Mutf8Constant,ConstantPool,BufferReader,ReadingLocationMap> codeAttributeSwitch = //
		OmniSwitch4.<Attribute,Mutf8Constant,ConstantPool,BufferReader,ReadingLocationMap>newBuilder() //
			.with( LineNumberTableAttribute.kind.mutf8Name, ByteCodeReader::readLineNumberTableAttribute )               //
			.with( LocalVariableTableAttribute.kind.mutf8Name, ByteCodeReader::readLocalVariableTableAttribute )         //
			.with( LocalVariableTypeTableAttribute.kind.mutf8Name, ByteCodeReader::readLocalVariableTypeTableAttribute ) //
			.with( StackMapTableAttribute.kind.mutf8Name, ByteCodeReader::readStackMapTableAttribute )                   //
			.withDefault( ByteCodeReader::readUnknownAttribute ) //
			.build();

	private static Attribute readCodeAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		return codeAttributeSwitch.on( attributeName, constantPool, bufferReader, locationMap );
	}

	private static ExceptionsAttribute readExceptionsAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( ExceptionsAttribute.kind.mutf8Name );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<ClassConstant> exceptionClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant constant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
			exceptionClassConstants.add( constant );
		}
		return ExceptionsAttribute.of( exceptionClassConstants );
	}

	private static MethodParametersAttribute readMethodParametersAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		assert attributeName.equalsMutf8Constant( MethodParametersAttribute.kind.mutf8Name );
		int count = bufferReader.readUnsignedByte();
		assert count > 0;
		List<MethodParameter> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			FlagSet<MethodParameter.Modifier> modifierSet = MethodParameter.modifierFlagEnum.fromInt( bufferReader.readUnsignedShort() );
			MethodParameter entry = MethodParameter.of( nameConstant, modifierSet );
			entries.add( entry );
		}
		return MethodParametersAttribute.of( entries );
	}

	private static LineNumberTableAttribute readLineNumberTableAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		assert attributeName.equalsMutf8Constant( LineNumberTableAttribute.kind.mutf8Name );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<LineNumber> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			AbsoluteInstructionReference startPc = readAbsoluteInstructionReference( bufferReader, locationMap );
			int lineNumber1 = bufferReader.readUnsignedShort();
			var lineNumber = LineNumber.of( startPc, lineNumber1 );
			entries.add( lineNumber );
		}
		return LineNumberTableAttribute.of( entries );
	}

	private static LocalVariableTableAttribute readLocalVariableTableAttribute( Mutf8Constant attributeName, ConstantPool constantPool, //
		BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		assert attributeName.equalsMutf8Constant( LocalVariableTableAttribute.kind.mutf8Name );
		int count = bufferReader.readUnsignedShort();
		List<LocalVariable> localVariables = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			AbsoluteInstructionReference startInstructionReference = readAbsoluteInstructionReference( bufferReader, locationMap );
			int length = bufferReader.readUnsignedShort();
			int endLocation = locationMap.getLocation( startInstructionReference.targetInstruction() ) + length;
			Optional<Instruction> endInstruction = locationMap.getInstruction( endLocation );
			AbsoluteInstructionReference endInstructionReference = AbsoluteInstructionReference.of( endInstruction );
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			int index = bufferReader.readUnsignedShort();
			LocalVariable localVariable = LocalVariable.of( startInstructionReference, endInstructionReference, nameConstant, descriptorConstant, index );
			localVariables.add( localVariable );
		}
		return LocalVariableTableAttribute.of( localVariables );
	}

	private static LocalVariableTypeTableAttribute readLocalVariableTypeTableAttribute( Mutf8Constant attributeName, ConstantPool constantPool, //
		BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		assert attributeName.equalsMutf8Constant( LocalVariableTypeTableAttribute.kind.mutf8Name );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<LocalVariableType> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			AbsoluteInstructionReference startInstructionReference = readAbsoluteInstructionReference( bufferReader, locationMap );
			int length = bufferReader.readUnsignedShort();
			int endLocation = locationMap.getLocation( startInstructionReference.targetInstruction() ) + length;
			Optional<Instruction> endInstruction = locationMap.getInstruction( endLocation );
			AbsoluteInstructionReference endInstructionReference = AbsoluteInstructionReference.of( endInstruction );
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			Mutf8Constant signatureConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			int index = bufferReader.readUnsignedShort();
			LocalVariableType entry = LocalVariableType.of( startInstructionReference, endInstructionReference, nameConstant, signatureConstant, index );
			entries.add( entry );
		}
		return LocalVariableTypeTableAttribute.of( entries );
	}

	private static StackMapTableAttribute readStackMapTableAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader, //
		ReadingLocationMap locationMap )
	{
		assert attributeName.equalsMutf8Constant( StackMapTableAttribute.kind.mutf8Name );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<StackMapFrame> frames = new ArrayList<>( count );
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( int i = 0; i < count; i++ )
		{
			int frameType = bufferReader.readUnsignedByte();
			String typeName = StackMapFrame.getTypeNameFromType( frameType );
			StackMapFrame frame = switch( typeName )
				{
					case SameStackMapFrame.typeName, SameStackMapFrame.extendedTypeName -> //
						{
							assert (frameType >= 0 && frameType <= 63) || frameType == SameStackMapFrame.EXTENDED_FRAME_TYPE;
							int offsetDelta = frameType == SameStackMapFrame.EXTENDED_FRAME_TYPE ? bufferReader.readUnsignedShort() : frameType;
							Instruction targetInstruction = findTargetInstruction( previousFrame, offsetDelta, locationMap ).orElseThrow();
							yield SameStackMapFrame.of( targetInstruction );
						}
					case SameLocals1StackItemStackMapFrame.typeName, SameLocals1StackItemStackMapFrame.extendedTypeName -> //
						{
							assert (frameType >= 64 && frameType <= 127) || frameType == SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE;
							int offsetDelta = frameType == SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE ? bufferReader.readUnsignedShort() : frameType - 64;
							var targetInstruction = findTargetInstruction( previousFrame, offsetDelta, locationMap ).orElseThrow();
							VerificationType stackVerificationType = readVerificationType( constantPool, bufferReader, locationMap );
							yield SameLocals1StackItemStackMapFrame.of( targetInstruction, stackVerificationType );
						}
					case ChopStackMapFrame.typeName -> //
						{
							assert frameType >= 248 && frameType < 251;
							Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
							yield ChopStackMapFrame.of( targetInstruction, 251 - frameType );
						}
					case AppendStackMapFrame.typeName -> //
						{
							Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
							assert frameType >= 252 && frameType <= 254;
							int localCount = frameType - 251;
							List<VerificationType> localVerificationTypes = new ArrayList<>( localCount );
							for( int j = 0; j < localCount; j++ )
							{
								VerificationType verificationType = readVerificationType( constantPool, bufferReader, locationMap );
								localVerificationTypes.add( verificationType );
							}
							yield AppendStackMapFrame.of( targetInstruction, localVerificationTypes );
						}
					case FullStackMapFrame.typeName -> //
						{
							Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
							List<VerificationType> localVerificationTypes = readVerificationTypes( constantPool, bufferReader, locationMap );
							List<VerificationType> stackVerificationTypes = readVerificationTypes( constantPool, bufferReader, locationMap );
							yield FullStackMapFrame.of( targetInstruction, localVerificationTypes, stackVerificationTypes );
						}
					default -> throw new AssertionError( typeName );
				};
			frames.add( frame );
			previousFrame = Optional.of( frame );
		}
		return StackMapTableAttribute.of( frames );
	}

	private static Optional<Instruction> findTargetInstruction( Optional<StackMapFrame> previousFrame, int offsetDelta, ReadingLocationMap locationMap )
	{
		int previousLocation = previousFrame.isEmpty() ? 0 : (locationMap.getLocation( previousFrame.get().getTargetInstruction() ) + 1);
		int location = offsetDelta + previousLocation;
		return locationMap.getInstruction( location );
	}

	private static List<VerificationType> readVerificationTypes( ConstantPool constantPool, BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		List<VerificationType> verificationTypes = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			VerificationType verificationType = readVerificationType( constantPool, bufferReader, locationMap );
			verificationTypes.add( verificationType );
		}
		return verificationTypes;
	}

	private static List<ByteCodeAnnotation> readAnnotations( ConstantPool constantPool, BufferReader bufferReader )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<ByteCodeAnnotation> annotations = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ByteCodeAnnotation byteCodeAnnotation = readByteCodeAnnotation( constantPool, bufferReader );
			annotations.add( byteCodeAnnotation );
		}
		return annotations;
	}

	private static List<TypeAnnotation> readTypeAnnotations( ConstantPool constantPool, BufferReader bufferReader )
	{
		int typeAnnotationCount = bufferReader.readUnsignedShort();
		assert typeAnnotationCount > 0;
		List<TypeAnnotation> typeAnnotations = new ArrayList<>( typeAnnotationCount );
		for( int i = 0; i < typeAnnotationCount; i++ )
		{
			int targetType = bufferReader.readUnsignedByte();
			Target target = switch( targetType )
				{
					case 0x00, 0x01 -> //
						{
							int typeParameterIndex = bufferReader.readUnsignedByte();
							yield new TypeParameterTarget( typeParameterIndex );
						}
					case 0x10 -> //
						{
							int supertypeIndex = bufferReader.readUnsignedShort();
							yield new SupertypeTarget( supertypeIndex );
						}
					case 0x11, 0x12 -> //
						{
							int typeParameterIndex = bufferReader.readUnsignedByte();
							int boundIndex = bufferReader.readUnsignedByte();
							yield new TypeParameterBoundTarget( typeParameterIndex, boundIndex );
						}
					case 0x13, 0x14, 0x15 -> new EmptyTarget();
					case 0x16 -> //
						{
							int formalParameterIndex = bufferReader.readUnsignedByte();
							yield new FormalParameterTarget( formalParameterIndex );
						}
					case 0x17 -> //
						{
							int throwsTypeIndex = bufferReader.readUnsignedShort();
							yield new ThrowsTarget( throwsTypeIndex );
						}
					case 0x40, 0x41 -> //
						{
							int entryCount = bufferReader.readUnsignedShort();
							assert entryCount > 0;
							List<LocalVariableTarget.Entry> entries = new ArrayList<>( entryCount );
							for( int j = 0; j < entryCount; j++ )
							{
								int startPc = bufferReader.readUnsignedShort();
								int length = bufferReader.readUnsignedShort();
								int index = bufferReader.readUnsignedShort();
								LocalVariableTarget.Entry entry = new LocalVariableTarget.Entry( startPc, length, index );
								entries.add( entry );
							}
							yield new LocalVariableTarget( entries );
						}
					case 0x42 -> //
						{
							int exceptionTableIndex = bufferReader.readUnsignedShort();
							yield new CatchTarget( exceptionTableIndex );
						}
					case 0x43, 0x44, 0x45, 0x46 -> //
						{
							int offset = bufferReader.readUnsignedShort();
							yield new OffsetTarget( offset );
						}
					case 0x47, 0x48, 0x49, 0x4a, 0x4b -> //
						{
							int offset = bufferReader.readUnsignedShort();
							int typeArgumentIndex = bufferReader.readUnsignedByte();
							yield new TypeArgumentTarget( offset, typeArgumentIndex );
						}
					default -> throw new AssertionError();
				};
			int entryCount = bufferReader.readUnsignedByte();
			List<TypePath.Entry> entries = new ArrayList<>( entryCount );
			for( int j = 0; j < entryCount; j++ )
			{
				int pathKind = bufferReader.readUnsignedByte();
				int argumentIndex = bufferReader.readUnsignedByte();
				TypePath.Entry entry1 = new TypePath.Entry( pathKind, argumentIndex );
				entries.add( entry1 );
			}
			TypePath targetPath = new TypePath( entries );
			int typeIndex = bufferReader.readUnsignedShort();
			int pairCount = bufferReader.readUnsignedShort();
			List<TypeAnnotation.ElementValuePair> pairs = new ArrayList<>( pairCount );
			for( int j = 0; j < pairCount; j++ )
			{
				int elementNameIndex = bufferReader.readUnsignedShort();
				AnnotationParameter elementValue = readAnnotationParameter( constantPool, bufferReader );
				TypeAnnotation.ElementValuePair elementValuePair = TypeAnnotation.ElementValuePair.of( elementNameIndex, elementValue );
				pairs.add( elementValuePair );
			}
			TypeAnnotation entry = TypeAnnotation.of( targetType, target, targetPath, typeIndex, pairs );
			typeAnnotations.add( entry );
		}
		return typeAnnotations;
	}

	private static final OmniSwitch3<Attribute,Mutf8Constant,ConstantPool,BufferReader> fieldAttributeSwitch = //
		OmniSwitch3.<Attribute,Mutf8Constant,ConstantPool,BufferReader>newBuilder() //
			.with( ConstantValueAttribute.kind.mutf8Name, ByteCodeReader::readConstantValueAttribute )                                          //
			.with( DeprecatedAttribute.kind.mutf8Name, ByteCodeReader::readDeprecatedAttribute )                                           //
			.with( RuntimeInvisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeInvisibleAnnotationsAttribute )         //
			.with( RuntimeVisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeVisibleAnnotationsAttribute )             //
			.with( RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeInvisibleTypeAnnotationsAttribute ) //
			.with( RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeVisibleTypeAnnotationsAttribute )     //
			.with( SignatureAttribute.kind.mutf8Name, ByteCodeReader::readSignatureAttribute )                                             //
			.with( SyntheticAttribute.kind.mutf8Name, ByteCodeReader::readSyntheticAttribute )                                             //
			.withDefault( ByteCodeReader::readUnknownAttribute )                                                                          //
			.build();

	private static Attribute readFieldAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		return fieldAttributeSwitch.on( attributeName, constantPool, bufferReader );
	}

	private static final OmniSwitch3<Attribute,Mutf8Constant,ConstantPool,BufferReader> methodAttributeSwitch = //
		OmniSwitch3.<Attribute,Mutf8Constant,ConstantPool,BufferReader>newBuilder() //
			.with( AnnotationDefaultAttribute.kind.mutf8Name, ByteCodeReader::readAnnotationDefaultAttribute )                                        //
			.with( CodeAttribute.kind.mutf8Name, ByteCodeReader::readCodeAttribute )                                                                  //
			.with( DeprecatedAttribute.kind.mutf8Name, ByteCodeReader::readDeprecatedAttribute )                                                      //
			.with( ExceptionsAttribute.kind.mutf8Name, ByteCodeReader::readExceptionsAttribute )                                                      //
			.with( MethodParametersAttribute.kind.mutf8Name, ByteCodeReader::readMethodParametersAttribute )                                          //
			.with( RuntimeInvisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeInvisibleAnnotationsAttribute )                    //
			.with( RuntimeVisibleAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeVisibleAnnotationsAttribute )                        //
			.with( RuntimeInvisibleParameterAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeInvisibleParameterAnnotationsAttribute )  //
			.with( RuntimeVisibleParameterAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeVisibleParameterAnnotationsAttribute )      //
			.with( RuntimeInvisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeInvisibleTypeAnnotationsAttribute )            //
			.with( RuntimeVisibleTypeAnnotationsAttribute.kind.mutf8Name, ByteCodeReader::readRuntimeVisibleTypeAnnotationsAttribute )                //
			.with( SignatureAttribute.kind.mutf8Name, ByteCodeReader::readSignatureAttribute )                                                        //
			.with( SyntheticAttribute.kind.mutf8Name, ByteCodeReader::readSyntheticAttribute )                                                        //
			.withDefault( ByteCodeReader::readUnknownAttribute )                                                                                     //
			.build();

	private static Attribute readMethodAttribute( Mutf8Constant attributeName, ConstantPool constantPool, BufferReader bufferReader )
	{
		return methodAttributeSwitch.on( attributeName, constantPool, bufferReader );
	}

	private static AnnotationValue readAnnotationValue( ConstantPool constantPool, BufferReader bufferReader )
	{
		int tag = bufferReader.readUnsignedByte();
		return switch( tag )
			{
				case AnnotationValue.ByteTag, AnnotationValue.CharacterTag, AnnotationValue.DoubleTag, AnnotationValue.FloatTag, //
					AnnotationValue.IntTag, AnnotationValue.LongTag, AnnotationValue.ShortTag, AnnotationValue.BooleanTag, AnnotationValue.StringTag -> //
					{
						Constant constant = constantPool.readIndexAndGetConstant( bufferReader );
						ValueConstant<?> valueConstant = constant.asValueConstant();
						yield ConstAnnotationValue.of( tag, valueConstant );
					}
				case AnnotationValue.EnumTag -> //
					{
						Mutf8Constant typeNameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
						Mutf8Constant valueNameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
						yield EnumAnnotationValue.of( typeNameConstant, valueNameConstant );
					}
				case AnnotationValue.ClassTag -> //
					{
						Mutf8Constant classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
						yield ClassAnnotationValue.of( classConstant );
					}
				case AnnotationValue.AnnotationTag -> //
					{
						ByteCodeAnnotation annotation = readByteCodeAnnotation( constantPool, bufferReader );
						yield AnnotationAnnotationValue.of( annotation );
					}
				case AnnotationValue.ArrayTag -> //
					{
						int count = bufferReader.readUnsignedShort();
						assert count > 0;
						List<AnnotationValue> annotationValues = new ArrayList<>( count );
						for( int i = 0; i < count; i++ )
							annotationValues.add( readAnnotationValue( constantPool, bufferReader ) );
						yield ArrayAnnotationValue.of( annotationValues );
					}
				default -> throw new UnknownValueException( tag );
			};
	}

	private static ByteCodeAnnotation readByteCodeAnnotation( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		int count = bufferReader.readUnsignedShort();
		List<AnnotationParameter> annotationParameters = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			AnnotationParameter annotationParameter = readAnnotationParameter( constantPool, bufferReader );
			annotationParameters.add( annotationParameter );
		}
		return ByteCodeAnnotation.of( nameConstant, annotationParameters );
	}

	private static AnnotationParameter readAnnotationParameter( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		AnnotationValue annotationValue = readAnnotationValue( constantPool, bufferReader );
		return AnnotationParameter.of( nameConstant, annotationValue );
	}

	private static List<ParameterAnnotationSet> readParameterAnnotationsAttributeEntries( ConstantPool constantPool, BufferReader bufferReader )
	{
		int entryCount = bufferReader.readUnsignedByte();
		assert entryCount > 0;
		List<ParameterAnnotationSet> entries = new ArrayList<>( entryCount );
		for( int i = 0; i < entryCount; i++ )
		{
			int annotationCount = bufferReader.readUnsignedShort();
			assert annotationCount >= 0;
			List<ByteCodeAnnotation> annotations = new ArrayList<>( annotationCount );
			for( int i1 = 0; i1 < annotationCount; i1++ )
			{
				ByteCodeAnnotation byteCodeAnnotation = readByteCodeAnnotation( constantPool, bufferReader );
				annotations.add( byteCodeAnnotation );
			}
			ParameterAnnotationSet entry = ParameterAnnotationSet.of( annotations );
			entries.add( entry );
		}
		return entries;
	}

	static Constant readConstant( int tag, ConstantPool constantPool, BufferReader bufferReader )
	{
		return switch( tag )
			{
				case ClassConstant.TAG -> //
					{
						ConstantPool constantPool1 = constantPool;
						BufferReader bufferReader1 = bufferReader;
						int index = bufferReader1.readUnsignedShort();
						Mutf8Constant nameConstant = constantPool1.getConstant( index ).asMutf8Constant();
						yield ClassConstant.of( nameConstant );
					}
				case StringConstant.TAG -> //
					{
						int valueIndex = bufferReader.readUnsignedShort();
						Mutf8Constant valueConstant = constantPool.getConstant( valueIndex ).asMutf8Constant();
						yield StringConstant.of( valueConstant );
					}
				case MethodTypeConstant.TAG -> //
					{
						Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
						yield MethodTypeConstant.of( descriptorConstant );
					}
				case FieldReferenceConstant.TAG -> //
					{
						ClassConstant typeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
						NameAndDescriptorConstant nameAndDescriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndDescriptorConstant();
						yield FieldReferenceConstant.of( typeConstant, nameAndDescriptorConstant );
					}
				case InterfaceMethodReferenceConstant.TAG -> //
					{
						ClassConstant typeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
						NameAndDescriptorConstant nameAndDescriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndDescriptorConstant();
						yield InterfaceMethodReferenceConstant.of( typeConstant, nameAndDescriptorConstant );
					}
				case PlainMethodReferenceConstant.TAG -> //
					{
						ClassConstant typeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
						NameAndDescriptorConstant nameAndDescriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndDescriptorConstant();
						yield PlainMethodReferenceConstant.of( typeConstant, nameAndDescriptorConstant );
					}
				case InvokeDynamicConstant.TAG -> //
					{
						int bootstrapMethodIndex = bufferReader.readUnsignedShort();
						NameAndDescriptorConstant nameAndDescriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndDescriptorConstant();
						yield InvokeDynamicConstant.of( bootstrapMethodIndex, nameAndDescriptorConstant );
					}
				case DoubleConstant.TAG -> //
					{
						double value = bufferReader.readDouble();
						yield DoubleConstant.of( value );
					}
				case FloatConstant.TAG -> //
					{
						float value = bufferReader.readFloat();
						yield FloatConstant.of( value );
					}
				case IntegerConstant.TAG -> //
					{
						int value = bufferReader.readInt();
						yield IntegerConstant.of( value );
					}
				case LongConstant.TAG -> //
					{
						long value = bufferReader.readLong();
						yield LongConstant.of( value );
					}
				case Mutf8Constant.TAG -> //
					{
						Buffer buffer = bufferReader.readBuffer();
						yield Mutf8Constant.of( buffer );
					}
				case NameAndDescriptorConstant.TAG -> //
					{
						Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
						Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
						yield NameAndDescriptorConstant.of( nameConstant, descriptorConstant );
					}
				case MethodHandleConstant.TAG -> //
					{
						int referenceKindNumber = bufferReader.readUnsignedByte();
						MethodHandleConstant.ReferenceKind referenceKind = MethodHandleConstant.ReferenceKind.tryFromNumber( referenceKindNumber ).orElseThrow();
						ReferenceConstant referenceConstant = constantPool.readIndexAndGetConstant( bufferReader ).asReferenceConstant();
						assert referenceConstant.tag == PlainMethodReferenceConstant.TAG || referenceConstant.tag == InterfaceMethodReferenceConstant.TAG || referenceConstant.tag == FieldReferenceConstant.TAG;
						yield MethodHandleConstant.of( referenceKind, referenceConstant );
					}
				default -> throw new AssertionError();
			};
	}

	private static VerificationType readVerificationType( ConstantPool constantPool, BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		int tag = bufferReader.readUnsignedByte();
		return switch( tag )
			{
				case SimpleVerificationType.topTag, SimpleVerificationType.uninitializedThisTag, SimpleVerificationType.nullTag, //
					SimpleVerificationType.longTag, SimpleVerificationType.doubleTag, SimpleVerificationType.floatTag, SimpleVerificationType.integerTag //
					-> new SimpleVerificationType( tag );
				case ObjectVerificationType.tag -> //
					{
						ClassConstant classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
						yield ObjectVerificationType.of( classConstant );
					}
				case UninitializedVerificationType.tag -> //
					{
						AbsoluteInstructionReference instructionReference = readAbsoluteInstructionReference( bufferReader, locationMap );
						yield UninitializedVerificationType.of( instructionReference );
					}
				default -> throw new AssertionError( tag );
			};
	}

	private static AbsoluteInstructionReference readAbsoluteInstructionReference( BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		// Absolute instruction references are never used in code; they are only used in attributes.
		// Thus, by the time an absolute instruction reference is parsed, all instructions have already been parsed,
		// which means that we can directly reference the target instruction without the need for fix-ups.
		int targetLocation = bufferReader.readUnsignedShort(); //an unsigned short works here because java methods are limited to 64k.
		Optional<Instruction> targetInstruction = locationMap.getInstruction( targetLocation );
		AbsoluteInstructionReference absoluteInstructionReference = AbsoluteInstructionReference.of( targetInstruction );
		return absoluteInstructionReference;
	}
}
