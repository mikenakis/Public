package mikenakis.bytecode.reading;

import mikenakis.bytecode.exceptions.InvalidAnnotationValueTagException;
import mikenakis.bytecode.exceptions.InvalidConstantTagException;
import mikenakis.bytecode.exceptions.InvalidKnownAttributeTagException;
import mikenakis.bytecode.exceptions.InvalidTargetTagException;
import mikenakis.bytecode.exceptions.InvalidVerificationTypeTagException;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
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
import mikenakis.bytecode.model.attributes.target.TypePath;
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
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.kit.Kit;
import mikenakis.kit.collections.FlagEnumSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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
		FlagEnumSet<ByteCodeType.Modifier> modifierSet = ByteCodeType.modifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
		ClassConstant thisClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		TerminalTypeDescriptor thisClassDescriptor = TerminalTypeDescriptor.of( thisClassConstant );
		Optional<ClassConstant> superClassConstant = constantPool.tryReadIndexAndGetConstant( bufferReader ).map( Constant::asClassConstant );
		Optional<TerminalTypeDescriptor> superClassDescriptor = superClassConstant.map( c -> TerminalTypeDescriptor.of( c ) );
		int interfaceCount = bufferReader.readUnsignedShort();
		List<TerminalTypeDescriptor> interfaces = new ArrayList<>( interfaceCount );
		for( int i = 0; i < interfaceCount; i++ )
		{
			ClassConstant interfaceClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
			TerminalTypeDescriptor interfaceDescriptor = TerminalTypeDescriptor.of( interfaceClassConstant.classDescriptor() );
			interfaces.add( interfaceDescriptor );
		}
		int fieldCount = bufferReader.readUnsignedShort();
		List<ByteCodeField> fields = new ArrayList<>( fieldCount );
		for( int i = 0; i < fieldCount; i++ )
		{
			FlagEnumSet<ByteCodeField.Modifier> fieldModifierSet = ByteCodeField.modifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			AttributeSet attributes = readAttributes( constantPool, bufferReader, Optional.empty() );
			ByteCodeField byteCodeField = ByteCodeField.of( fieldModifierSet, nameConstant, descriptorConstant, attributes );
			fields.add( byteCodeField );
		}
		int methodCount = bufferReader.readUnsignedShort();
		List<ByteCodeMethod> methods = new ArrayList<>( methodCount );
		for( int i = 0; i < methodCount; i++ )
		{
			FlagEnumSet<ByteCodeMethod.Modifier> methodModifierSet = ByteCodeMethod.modifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			AttributeSet attributes = readAttributes( constantPool, bufferReader, Optional.empty() );
			ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( methodModifierSet, nameConstant, descriptorConstant, attributes );
			methods.add( byteCodeMethod );
		}
		AttributeSet attributes = readAttributes( constantPool, bufferReader, Optional.empty() );
		assert bufferReader.isAtEnd();
		Collection<Constant> extraConstants = constantPool.getExtraConstants();
		return ByteCodeType.of( minorVersion, majorVersion, modifierSet, thisClassDescriptor, superClassDescriptor, interfaces, fields, methods, //
			attributes, extraConstants );
	}

	private static AttributeSet readAttributes( ConstantPool constantPool, BufferReader bufferReader, Optional<LocationMap> locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		Map<Mutf8Constant,Attribute> attributesFromNames = new LinkedHashMap<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			int attributeLength = bufferReader.readInt();
			Buffer attributeBuffer = bufferReader.readBuffer( attributeLength );
			BufferReader attributeBufferReader = BufferReader.of( attributeBuffer );
			Attribute attribute = readAttribute( nameConstant, constantPool, attributeBufferReader, locationMap );
			assert attributeBufferReader.isAtEnd();
			Kit.map.add( attributesFromNames, nameConstant, attribute );
		}
		return AttributeSet.of( attributesFromNames );
	}

	private static Attribute readAttribute( Mutf8Constant nameConstant, ConstantPool constantPool, BufferReader bufferReader, Optional<LocationMap> locationMap )
	{
		Optional<Integer> knownAttributeTag = KnownAttribute.tagFromName( nameConstant );
		if( knownAttributeTag.isPresent() )
		{
			return switch( knownAttributeTag.get() )
				{
					case KnownAttribute.tagAnnotationDefault -> readAnnotationDefaultAttribute( constantPool, bufferReader );
					case KnownAttribute.tagBootstrapMethods -> readBootstrapMethodsAttribute( constantPool, bufferReader );
					case KnownAttribute.tagCode -> readCodeAttribute( constantPool, bufferReader );
					case KnownAttribute.tagConstantValue -> readConstantValueAttribute( constantPool, bufferReader );
					case KnownAttribute.tagDeprecated -> DeprecatedAttribute.of();
					case KnownAttribute.tagEnclosingMethod -> readEnclosingMethodAttribute( constantPool, bufferReader );
					case KnownAttribute.tagExceptions -> readExceptionsAttribute( constantPool, bufferReader );
					case KnownAttribute.tagInnerClasses -> readInnerClassesAttribute( constantPool, bufferReader );
					case KnownAttribute.tagLineNumberTable -> readLineNumberTableAttribute( bufferReader, locationMap.orElseThrow() );
					case KnownAttribute.tagLocalVariableTable -> readLocalVariableTableAttribute( constantPool, bufferReader, locationMap.orElseThrow() );
					case KnownAttribute.tagLocalVariableTypeTable -> readLocalVariableTypeTableAttribute( constantPool, bufferReader, locationMap.orElseThrow() );
					case KnownAttribute.tagMethodParameters -> readMethodParametersAttribute( constantPool, bufferReader );
					case KnownAttribute.tagNestHost -> readNestHostAttribute( constantPool, bufferReader );
					case KnownAttribute.tagNestMembers -> readNestMembersAttribute( constantPool, bufferReader );
					case KnownAttribute.tagRuntimeInvisibleAnnotations -> readRuntimeInvisibleAnnotationsAttribute( constantPool, bufferReader );
					case KnownAttribute.tagRuntimeInvisibleParameterAnnotations -> readRuntimeInvisibleParameterAnnotationsAttribute( constantPool, bufferReader );
					case KnownAttribute.tagRuntimeInvisibleTypeAnnotations -> readRuntimeInvisibleTypeAnnotationsAttribute( constantPool, bufferReader );
					case KnownAttribute.tagRuntimeVisibleAnnotations -> readRuntimeVisibleAnnotationsAttribute( constantPool, bufferReader );
					case KnownAttribute.tagRuntimeVisibleParameterAnnotations -> readRuntimeVisibleParameterAnnotationsAttribute( constantPool, bufferReader );
					case KnownAttribute.tagRuntimeVisibleTypeAnnotations -> readRuntimeVisibleTypeAnnotationsAttribute( constantPool, bufferReader );
					case KnownAttribute.tagSignature -> readSignatureAttribute( constantPool, bufferReader );
					case KnownAttribute.tagSourceFile -> readSourceFileAttribute( constantPool, bufferReader );
					case KnownAttribute.tagStackMapTable -> readStackMapTableAttribute( constantPool, bufferReader, locationMap.orElseThrow() );
					case KnownAttribute.tagSynthetic -> SyntheticAttribute.of();
					default -> throw new InvalidKnownAttributeTagException( knownAttributeTag.get() );
				};
		}
		else
		{
			Buffer buffer = bufferReader.readBuffer();
			return UnknownAttribute.of( nameConstant, buffer );
		}
	}

	private static SourceFileAttribute readSourceFileAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant valueConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		return SourceFileAttribute.of( valueConstant );
	}

	private static SignatureAttribute readSignatureAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant signatureConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		return SignatureAttribute.of( signatureConstant );
	}

	private static RuntimeVisibleTypeAnnotationsAttribute readRuntimeVisibleTypeAnnotationsAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		List<TypeAnnotation> entries = readTypeAnnotations( constantPool, bufferReader );
		return RuntimeVisibleTypeAnnotationsAttribute.of( entries );
	}

	private static RuntimeVisibleParameterAnnotationsAttribute readRuntimeVisibleParameterAnnotationsAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries( constantPool, bufferReader );
		return RuntimeVisibleParameterAnnotationsAttribute.of( entries );
	}

	private static RuntimeVisibleAnnotationsAttribute readRuntimeVisibleAnnotationsAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		List<Annotation> annotations = readAnnotations( constantPool, bufferReader );
		return RuntimeVisibleAnnotationsAttribute.of( annotations );
	}

	private static RuntimeInvisibleTypeAnnotationsAttribute readRuntimeInvisibleTypeAnnotationsAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		List<TypeAnnotation> entries = readTypeAnnotations( constantPool, bufferReader );
		return RuntimeInvisibleTypeAnnotationsAttribute.of( entries );
	}

	private static RuntimeInvisibleParameterAnnotationsAttribute readRuntimeInvisibleParameterAnnotationsAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries( constantPool, bufferReader );
		return RuntimeInvisibleParameterAnnotationsAttribute.of( entries );
	}

	private static RuntimeInvisibleAnnotationsAttribute readRuntimeInvisibleAnnotationsAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		List<Annotation> annotations = readAnnotations( constantPool, bufferReader );
		return RuntimeInvisibleAnnotationsAttribute.of( annotations );
	}

	private static NestMembersAttribute readNestMembersAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
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

	private static NestHostAttribute readNestHostAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		ClassConstant hostClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		return NestHostAttribute.of( hostClassConstant );
	}

	private static MethodParametersAttribute readMethodParametersAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		int count = bufferReader.readUnsignedByte();
		assert count > 0;
		List<MethodParameter> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8Constant nameConstant1 = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			FlagEnumSet<MethodParameter.Modifier> modifierSet = MethodParameter.modifierFlagEnum.fromBits( bufferReader.readUnsignedShort() );
			MethodParameter entry = MethodParameter.of( nameConstant1, modifierSet );
			entries.add( entry );
		}
		return MethodParametersAttribute.of( entries );
	}

	private static InnerClassesAttribute readInnerClassesAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<InnerClass> innerClasses = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant innerClassConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
			Optional<ClassConstant> outerClassConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
			Optional<Mutf8Constant> innerNameConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
			FlagEnumSet<InnerClass.InnerClassModifier> modifierSet = InnerClass.innerClassModifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
			InnerClass innerClass = InnerClass.of( innerClassConstant, outerClassConstant, innerNameConstant, modifierSet );
			innerClasses.add( innerClass );
		}
		return InnerClassesAttribute.of( innerClasses );
	}

	private static ExceptionsAttribute readExceptionsAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
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

	private static EnclosingMethodAttribute readEnclosingMethodAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		ClassConstant classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
		return EnclosingMethodAttribute.of( classConstant, methodNameAndDescriptorConstant );
	}

	private static ConstantValueAttribute readConstantValueAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		Constant constant = constantPool.readIndexAndGetConstant( bufferReader );
		ValueConstant<?> valueConstant = constant.asValueConstant();
		return ConstantValueAttribute.of( valueConstant );
	}

	private static AnnotationDefaultAttribute readAnnotationDefaultAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		AnnotationValue annotationValue = readAnnotationValue( constantPool, bufferReader );
		return AnnotationDefaultAttribute.of( annotationValue );
	}

	private static LineNumberTableAttribute readLineNumberTableAttribute( BufferReader bufferReader, LocationMap locationMap1 )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<LineNumberTableEntry> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = readAbsoluteInstruction( bufferReader, locationMap1 ).orElseThrow();
			int lineNumber = bufferReader.readUnsignedShort();
			var lineNumberEntry = LineNumberTableEntry.of( startInstruction, lineNumber );
			entries.add( lineNumberEntry );
		}
		return LineNumberTableAttribute.of( entries );
	}

	private static LocalVariableTableAttribute readLocalVariableTableAttribute( ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap1 )
	{
		int count = bufferReader.readUnsignedShort();
		List<LocalVariableTableEntry> localVariables = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = readAbsoluteInstruction( bufferReader, locationMap1 ).orElseThrow();
			int length = bufferReader.readUnsignedShort();
			int endLocation = locationMap1.getLocation( startInstruction ) + length;
			Optional<Instruction> endInstruction = locationMap1.getInstruction( endLocation );
			Mutf8Constant nameConstant1 = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			int index = bufferReader.readUnsignedShort();
			LocalVariableTableEntry localVariable = LocalVariableTableEntry.of( startInstruction, endInstruction, nameConstant1, descriptorConstant, index );
			localVariables.add( localVariable );
		}
		return LocalVariableTableAttribute.of( localVariables );
	}

	private static LocalVariableTypeTableAttribute readLocalVariableTypeTableAttribute( ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap1 )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<LocalVariableTypeTableEntry> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = readAbsoluteInstruction( bufferReader, locationMap1 ).orElseThrow();
			int length = bufferReader.readUnsignedShort();
			int endLocation = locationMap1.getLocation( startInstruction ) + length;
			Optional<Instruction> endInstruction = locationMap1.getInstruction( endLocation );
			Mutf8Constant nameConstant1 = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			Mutf8Constant signatureConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			int index = bufferReader.readUnsignedShort();
			LocalVariableTypeTableEntry entry = LocalVariableTypeTableEntry.of( startInstruction, endInstruction, nameConstant1, signatureConstant, index );
			entries.add( entry );
		}
		return LocalVariableTypeTableAttribute.of( entries );
	}

	private static BootstrapMethodsAttribute readBootstrapMethodsAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
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

	private static CodeAttribute readCodeAttribute( ConstantPool constantPool, BufferReader bufferReader )
	{
		int maxStack = bufferReader.readUnsignedShort();
		int maxLocals = bufferReader.readUnsignedShort();
		int codeLength = bufferReader.readInt();
		Buffer codeBuffer = bufferReader.readBuffer( codeLength );

		List<Instruction> instructions = new ArrayList<>();
		BufferReader codeBufferReader = BufferReader.of( codeBuffer );
		ReadingLocationMap locationMap = new ReadingLocationMap( codeBuffer.length() );
		InstructionReader.run( codeBufferReader, locationMap, constantPool, instructionReader -> //
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
			Instruction startInstruction = readAbsoluteInstruction( bufferReader, locationMap ).orElseThrow();
			Optional<Instruction> endInstruction = readAbsoluteInstruction( bufferReader, locationMap );
			assert endInstruction.isPresent();
			Instruction handlerInstruction = readAbsoluteInstruction( bufferReader, locationMap ).orElseThrow();
			Optional<ClassConstant> catchTypeConstant = Kit.upCast( constantPool.tryReadIndexAndGetConstant( bufferReader ) );
			ExceptionInfo exceptionInfo = ExceptionInfo.of( startInstruction, endInstruction, handlerInstruction, catchTypeConstant );
			exceptionInfos.add( exceptionInfo );
		}
		AttributeSet attributes = readAttributes( constantPool, bufferReader, Optional.of( locationMap ) );
		return CodeAttribute.of( maxStack, maxLocals, instructions, exceptionInfos, attributes );
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
		return switch( instructionGroup )
			{
				case Operandless -> readOperandlessInstruction( wide, opCode );
				case OperandlessLoadConstant -> readOperandlessLoadConstantInstruction( wide, opCode );
				case ImmediateLoadConstant -> readImmediateLoadConstantInstruction( instructionReader, wide, opCode );
				case IndirectLoadConstant -> readIndirectLoadConstantInstruction( instructionReader, wide, opCode );
				case LocalVariable -> readLocalVariableInstruction( instructionReader, wide, opCode );
				case IInc -> readIIncInstruction( instructionReader, wide );
				case ConditionalBranch -> readConditionalBranchInstruction( instructionReader, wide, opCode );
				case Branch -> readBranchInstruction( instructionReader, wide, opCode );
				case TableSwitch -> readTableSwitchInstruction( instructionReader, wide );
				case LookupSwitch -> readLookupSwitchInstruction( instructionReader, wide, opCode );
				case ConstantReferencing -> readConstantReferencingInstruction( instructionReader, wide, opCode );
				case InvokeInterface -> readInvokeInterfaceInstruction( instructionReader, wide, opCode );
				case InvokeDynamic -> readInvokeDynamicInstruction( instructionReader, wide, opCode );
				case NewPrimitiveArray -> readNewPrimitiveArrayInstruction( instructionReader, wide, opCode );
				case MultiANewArray -> readMultiANewArrayInstruction( instructionReader, wide, opCode );
			};
	}
	private static MultiANewArrayInstruction readMultiANewArrayInstruction( InstructionReader instructionReader, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.MULTIANEWARRAY;
		int constantIndex = instructionReader.readUnsignedShort();
		ClassConstant constant = instructionReader.getConstant( constantIndex ).asClassConstant();
		int dimensionCount = instructionReader.readUnsignedByte();
		return MultiANewArrayInstruction.of( constant, dimensionCount );
	}
	private static NewPrimitiveArrayInstruction readNewPrimitiveArrayInstruction( InstructionReader instructionReader, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.NEWARRAY;
		NewPrimitiveArrayInstruction.Type type = NewPrimitiveArrayInstruction.Type.fromNumber( instructionReader.readUnsignedByte() );
		return NewPrimitiveArrayInstruction.of( type );
	}
	private static InvokeDynamicInstruction readInvokeDynamicInstruction( InstructionReader instructionReader, boolean wide, int opCode )
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
	private static InvokeInterfaceInstruction readInvokeInterfaceInstruction( InstructionReader instructionReader, boolean wide, int opCode )
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
	private static ConstantReferencingInstruction readConstantReferencingInstruction( InstructionReader instructionReader, boolean wide, int opCode )
	{
		assert !wide;
		int constantIndex = instructionReader.readUnsignedShort();
		Constant constant = instructionReader.getConstant( constantIndex );
		return ConstantReferencingInstruction.of( opCode, constant );
	}
	private static LookupSwitchInstruction readLookupSwitchInstruction( InstructionReader instructionReader, boolean wide, int opCode )
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
	private static TableSwitchInstruction readTableSwitchInstruction( InstructionReader instructionReader, boolean wide )
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
	private static BranchInstruction readBranchInstruction( InstructionReader instructionReader, boolean wide, int opCode )
	{
		assert !wide;
		BranchInstruction branchInstruction = BranchInstruction.of( opCode );
		int targetInstructionOffset = BranchInstruction.isLong( opCode ) ? instructionReader.readInt() : instructionReader.readSignedShort();
		instructionReader.setRelativeTargetInstruction( branchInstruction, targetInstructionOffset, branchInstruction::setTargetInstruction );
		return branchInstruction;
	}
	private static ConditionalBranchInstruction readConditionalBranchInstruction( InstructionReader instructionReader, boolean wide, int opCode )
	{
		assert !wide;
		ConditionalBranchInstruction conditionalBranchInstruction = ConditionalBranchInstruction.of( opCode );
		int targetInstructionOffset = instructionReader.readSignedShort();
		instructionReader.setRelativeTargetInstruction( conditionalBranchInstruction, targetInstructionOffset, conditionalBranchInstruction::setTargetInstruction );
		return conditionalBranchInstruction;
	}
	private static IIncInstruction readIIncInstruction( InstructionReader instructionReader, boolean wide )
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
	private static LocalVariableInstruction readLocalVariableInstruction( InstructionReader instructionReader, boolean wide, int opCode )
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
	private static IndirectLoadConstantInstruction readIndirectLoadConstantInstruction( InstructionReader instructionReader, boolean wide, int opCode )
	{
		assert !wide;
		int constantIndexValue = IndirectLoadConstantInstruction.isWide( opCode ) ? instructionReader.readUnsignedShort() : instructionReader.readUnsignedByte();
		Constant constant = instructionReader.getConstant( constantIndexValue );
		return IndirectLoadConstantInstruction.of( opCode, constant );
	}
	private static ImmediateLoadConstantInstruction readImmediateLoadConstantInstruction( InstructionReader instructionReader, boolean wide, int opCode )
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
	private static OperandlessLoadConstantInstruction readOperandlessLoadConstantInstruction( boolean wide, int opCode )
	{
		assert !wide;
		return OperandlessLoadConstantInstruction.of( opCode );
	}
	private static OperandlessInstruction readOperandlessInstruction( boolean wide, int opCode )
	{
		assert !wide;
		return OperandlessInstruction.of( opCode );
	}

	private static StackMapTableAttribute readStackMapTableAttribute( ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap )
	{
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
					case SameStackMapFrame.typeName, SameStackMapFrame.extendedTypeName -> readSameStackMapFrame( bufferReader, locationMap, previousFrame, frameType );
					case SameLocals1StackItemStackMapFrame.typeName, SameLocals1StackItemStackMapFrame.extendedTypeName -> readSameLocals1StackItemStackMapFrame( constantPool, bufferReader, locationMap, previousFrame, frameType );
					case ChopStackMapFrame.typeName -> readChopStackMapFrame( bufferReader, locationMap, previousFrame, frameType );
					case AppendStackMapFrame.typeName -> readAppendStackMapFrame( constantPool, bufferReader, locationMap, previousFrame, frameType );
					case FullStackMapFrame.typeName -> readFullStackMapFrame( constantPool, bufferReader, locationMap, previousFrame );
					default -> throw new AssertionError( typeName );
				};
			frames.add( frame );
			previousFrame = Optional.of( frame );
		}
		return StackMapTableAttribute.of( frames );
	}

	private static FullStackMapFrame readFullStackMapFrame( ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap, Optional<StackMapFrame> previousFrame )
	{
		Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
		List<VerificationType> localVerificationTypes = readVerificationTypes( constantPool, bufferReader, locationMap );
		List<VerificationType> stackVerificationTypes = readVerificationTypes( constantPool, bufferReader, locationMap );
		return FullStackMapFrame.of( targetInstruction, localVerificationTypes, stackVerificationTypes );
	}

	private static AppendStackMapFrame readAppendStackMapFrame( ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
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
		return AppendStackMapFrame.of( targetInstruction, localVerificationTypes );
	}

	private static ChopStackMapFrame readChopStackMapFrame( BufferReader bufferReader, LocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert frameType >= 248 && frameType < 251;
		Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
		return ChopStackMapFrame.of( targetInstruction, 251 - frameType );
	}

	private static SameLocals1StackItemStackMapFrame readSameLocals1StackItemStackMapFrame( ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert (frameType >= 64 && frameType <= 127) || frameType == SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE;
		int offsetDelta = frameType == SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE ? bufferReader.readUnsignedShort() : frameType - 64;
		var targetInstruction = findTargetInstruction( previousFrame, offsetDelta, locationMap ).orElseThrow();
		VerificationType stackVerificationType = readVerificationType( constantPool, bufferReader, locationMap );
		return SameLocals1StackItemStackMapFrame.of( targetInstruction, stackVerificationType );
	}

	private static SameStackMapFrame readSameStackMapFrame( BufferReader bufferReader, LocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert (frameType >= 0 && frameType <= 63) || frameType == SameStackMapFrame.EXTENDED_FRAME_TYPE;
		int offsetDelta = frameType == SameStackMapFrame.EXTENDED_FRAME_TYPE ? bufferReader.readUnsignedShort() : frameType;
		Instruction targetInstruction = findTargetInstruction( previousFrame, offsetDelta, locationMap ).orElseThrow();
		return SameStackMapFrame.of( targetInstruction );
	}

	private static Optional<Instruction> findTargetInstruction( Optional<StackMapFrame> previousFrame, int offsetDelta, LocationMap locationMap )
	{
		int previousLocation = previousFrame.isEmpty() ? 0 : (locationMap.getLocation( previousFrame.get().getTargetInstruction() ) + 1);
		int location = offsetDelta + previousLocation;
		return locationMap.getInstruction( location );
	}

	private static List<VerificationType> readVerificationTypes( ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap )
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

	private static List<Annotation> readAnnotations( ConstantPool constantPool, BufferReader bufferReader )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<Annotation> annotations = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Annotation byteCodeAnnotation = readByteCodeAnnotation( constantPool, bufferReader );
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
			int targetTag = bufferReader.readUnsignedByte();
			Target target = switch( targetTag )
				{
					case Target.tagTypeParameterDeclarationOfGenericClassOrInterface, //
						Target.tagTypeParameterDeclarationOfGenericMethodOrConstructor -> //
						readTypeParameterTarget( bufferReader, targetTag );
					case Target.tagTypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration -> //
						readSupertypeTarget( bufferReader, targetTag );
					case Target.tagTypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface, //
						Target.tagTypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor -> //
						readTypeParameterBoundTarget( bufferReader, targetTag );
					case Target.tagTypeInFieldDeclaration, //
						Target.tagReturnTypeOfMethodOrTypeOfNewlyConstructedObject, //
						Target.tagReceiverTypeOfMethodOrConstructor -> //
						new EmptyTarget( targetTag );
					case Target.tagTypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression -> //
						readFormalParameterTarget( bufferReader, targetTag );
					case Target.tagTypeInThrowsClauseOfMethodOrConstructor -> //
						readThrowsTarget( bufferReader, targetTag );
					case Target.tagTypeInLocalVariableDeclaration, //
						Target.tagTypeInResourceVariableDeclaration -> //
						readLocalVariableTarget( bufferReader, targetTag );
					case Target.tagTypeInExceptionParameterDeclaration -> //
						readCatchTarget( bufferReader, targetTag );
					case Target.tagTypeInInstanceofExpression, //
						Target.tagTypeInNewExpression, //
						Target.tagTypeInMethodReferenceExpressionUsingNew, //
						Target.tagTypeInMethodReferenceExpressionUsingIdentifier -> //
						readOffsetTarget( bufferReader, targetTag );
					case Target.tagTypeInCastExpression, //
						Target.tagTypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement, //
						Target.tagTypeArgumentForGenericMethodInMethodInvocationExpression, //
						Target.tagTypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew, //
						Target.tagTypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier -> //
						readTypeArgumentTarget( bufferReader, targetTag );
					default -> throw new InvalidTargetTagException( targetTag );
				};
			int entryCount = bufferReader.readUnsignedByte();
			List<TypePathEntry> entries = new ArrayList<>( entryCount );
			for( int j = 0; j < entryCount; j++ )
			{
				TypePathEntry typePathEntry = readTypePathEntry( bufferReader );
				entries.add( typePathEntry );
			}
			TypePath targetPath = new TypePath( entries );
			int typeIndex = bufferReader.readUnsignedShort();
			List<AnnotationParameter> pairs = readAnnotationParameters( constantPool, bufferReader );
			TypeAnnotation entry = TypeAnnotation.of( target, targetPath, typeIndex, pairs );
			typeAnnotations.add( entry );
		}
		return typeAnnotations;
	}

	private static TypePathEntry readTypePathEntry( BufferReader bufferReader )
	{
		int pathKind = bufferReader.readUnsignedByte();
		int argumentIndex = bufferReader.readUnsignedByte();
		return new TypePathEntry( pathKind, argumentIndex );
	}

	private static TypeArgumentTarget readTypeArgumentTarget( BufferReader bufferReader, int targetTag )
	{
		int offset = bufferReader.readUnsignedShort();
		int typeArgumentIndex = bufferReader.readUnsignedByte();
		return new TypeArgumentTarget( targetTag, offset, typeArgumentIndex );
	}

	private static OffsetTarget readOffsetTarget( BufferReader bufferReader, int targetTag )
	{
		int offset = bufferReader.readUnsignedShort();
		return new OffsetTarget( targetTag, offset );
	}

	private static CatchTarget readCatchTarget( BufferReader bufferReader, int targetTag )
	{
		int exceptionTableIndex = bufferReader.readUnsignedShort();
		return new CatchTarget( targetTag, exceptionTableIndex );
	}

	private static LocalVariableTarget readLocalVariableTarget( BufferReader bufferReader, int targetTag )
	{
		int entryCount = bufferReader.readUnsignedShort();
		assert entryCount > 0;
		List<LocalVariableTargetEntry> entries = new ArrayList<>( entryCount );
		for( int j = 0; j < entryCount; j++ )
		{
			int startPc = bufferReader.readUnsignedShort();
			int length = bufferReader.readUnsignedShort();
			int index = bufferReader.readUnsignedShort();
			LocalVariableTargetEntry entry = new LocalVariableTargetEntry( startPc, length, index );
			entries.add( entry );
		}
		return new LocalVariableTarget( targetTag, entries );
	}

	private static ThrowsTarget readThrowsTarget( BufferReader bufferReader, int targetTag )
	{
		int throwsTypeIndex = bufferReader.readUnsignedShort();
		return new ThrowsTarget( targetTag, throwsTypeIndex );
	}

	private static FormalParameterTarget readFormalParameterTarget( BufferReader bufferReader, int targetTag )
	{
		int formalParameterIndex = bufferReader.readUnsignedByte();
		return new FormalParameterTarget( targetTag, formalParameterIndex );
	}

	private static TypeParameterBoundTarget readTypeParameterBoundTarget( BufferReader bufferReader, int targetTag )
	{
		int typeParameterIndex = bufferReader.readUnsignedByte();
		int boundIndex = bufferReader.readUnsignedByte();
		return new TypeParameterBoundTarget( targetTag, typeParameterIndex, boundIndex );
	}

	private static SupertypeTarget readSupertypeTarget( BufferReader bufferReader, int targetTag )
	{
		int supertypeIndex = bufferReader.readUnsignedShort();
		return new SupertypeTarget( targetTag, supertypeIndex );
	}

	private static TypeParameterTarget readTypeParameterTarget( BufferReader bufferReader, int targetTag )
	{
		int typeParameterIndex = bufferReader.readUnsignedByte();
		return new TypeParameterTarget( targetTag, typeParameterIndex );
	}

	private static List<AnnotationParameter> readAnnotationParameters( ConstantPool constantPool, BufferReader bufferReader )
	{
		int pairCount = bufferReader.readUnsignedShort();
		List<AnnotationParameter> annotationParameters = new ArrayList<>( pairCount );
		for( int i = 0; i < pairCount; i++ )
		{
			Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
			AnnotationValue annotationValue = readAnnotationValue( constantPool, bufferReader );
			AnnotationParameter annotationParameter = AnnotationParameter.of( nameConstant, annotationValue );
			annotationParameters.add( annotationParameter );
		}
		return annotationParameters;
	}

	private static AnnotationValue readAnnotationValue( ConstantPool constantPool, BufferReader bufferReader )
	{
		char annotationValueTag = (char)bufferReader.readUnsignedByte();
		return switch( annotationValueTag )
			{
				case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, //
					AnnotationValue.tagFloat, AnnotationValue.tagInteger, AnnotationValue.tagLong, AnnotationValue.tagShort, //
					AnnotationValue.tagString -> readConstAnnotationValue( constantPool, bufferReader, annotationValueTag );
				case AnnotationValue.tagAnnotation -> readAnnotationAnnotationValue( constantPool, bufferReader );
				case AnnotationValue.tagArray -> readArrayAnnotationValue( constantPool, bufferReader );
				case AnnotationValue.tagClass -> readClassAnnotationValue( constantPool, bufferReader );
				case AnnotationValue.tagEnum -> readEnumAnnotationValue( constantPool, bufferReader );
				default -> throw new InvalidAnnotationValueTagException( annotationValueTag );
			};
	}

	private static EnumAnnotationValue readEnumAnnotationValue( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant typeNameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		Mutf8Constant valueNameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		return EnumAnnotationValue.of( typeNameConstant, valueNameConstant );
	}

	private static ClassAnnotationValue readClassAnnotationValue( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		return ClassAnnotationValue.of( classConstant );
	}

	private static ArrayAnnotationValue readArrayAnnotationValue( ConstantPool constantPool, BufferReader bufferReader )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<AnnotationValue> annotationValues = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
			annotationValues.add( readAnnotationValue( constantPool, bufferReader ) );
		return ArrayAnnotationValue.of( annotationValues );
	}

	private static AnnotationAnnotationValue readAnnotationAnnotationValue( ConstantPool constantPool, BufferReader bufferReader )
	{
		Annotation annotation = readByteCodeAnnotation( constantPool, bufferReader );
		return AnnotationAnnotationValue.of( annotation );
	}

	private static ConstAnnotationValue readConstAnnotationValue( ConstantPool constantPool, BufferReader bufferReader, char annotationValueTag )
	{
		Constant constant = constantPool.readIndexAndGetConstant( bufferReader );
		ValueConstant<?> valueConstant = constant.asValueConstant();
		return ConstAnnotationValue.of( annotationValueTag, valueConstant );
	}

	private static Annotation readByteCodeAnnotation( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		List<AnnotationParameter> annotationParameters = readAnnotationParameters( constantPool, bufferReader );
		return Annotation.of( nameConstant, annotationParameters );
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
			List<Annotation> annotations = new ArrayList<>( annotationCount );
			for( int i1 = 0; i1 < annotationCount; i1++ )
			{
				Annotation byteCodeAnnotation = readByteCodeAnnotation( constantPool, bufferReader );
				annotations.add( byteCodeAnnotation );
			}
			ParameterAnnotationSet entry = ParameterAnnotationSet.of( annotations );
			entries.add( entry );
		}
		return entries;
	}

	static Constant readConstant( int constantTag, ConstantPool constantPool, BufferReader bufferReader )
	{
		return switch( constantTag )
			{
				case Constant.tagClass -> readClassConstant( constantPool, bufferReader );
				case Constant.tagString -> readStringConstant( constantPool, bufferReader );
				case Constant.tagMethodType -> readMethodTypeConstant( constantPool, bufferReader );
				case Constant.tagFieldReference -> readFieldReferenceConstant( constantPool, bufferReader );
				case Constant.tagInterfaceMethodReference -> readInterfaceMethodReferenceConstant( constantPool, bufferReader );
				case Constant.tagMethodReference -> readPlainMethodReferenceConstant( constantPool, bufferReader );
				case Constant.tagInvokeDynamic -> readInvokeDynamicConstant( constantPool, bufferReader );
				case Constant.tagDouble -> readDoubleConstant( bufferReader );
				case Constant.tagFloat -> readFloatConstant( bufferReader );
				case Constant.tagInteger -> readIntegerConstant( bufferReader );
				case Constant.tagLong -> readLongConstant( bufferReader );
				case Constant.tagMutf8 -> readMutf8Constant( bufferReader );
				case Constant.tagNameAndDescriptor -> readNameAndDescriptorConstant( constantPool, bufferReader );
				case Constant.tagMethodHandle -> readMethodHandleConstant( constantPool, bufferReader );
				default -> throw new InvalidConstantTagException( constantTag );
			};
	}
	private static MethodHandleConstant readMethodHandleConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		int referenceKindNumber = bufferReader.readUnsignedByte();
		MethodHandleConstant.ReferenceKind referenceKind = MethodHandleConstant.ReferenceKind.tryFromNumber( referenceKindNumber ).orElseThrow();
		ReferenceConstant referenceConstant = constantPool.readIndexAndGetConstant( bufferReader ).asReferenceConstant();
		return MethodHandleConstant.of( referenceKind, referenceConstant );
	}

	private static NameAndDescriptorConstant readNameAndDescriptorConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		return NameAndDescriptorConstant.of( nameConstant, descriptorConstant );
	}

	private static Mutf8Constant readMutf8Constant( BufferReader bufferReader )
	{
		Buffer buffer = bufferReader.readBuffer();
		return Mutf8Constant.of( buffer );
	}

	private static LongConstant readLongConstant( BufferReader bufferReader )
	{
		long value = bufferReader.readLong();
		return LongConstant.of( value );
	}

	private static IntegerConstant readIntegerConstant( BufferReader bufferReader )
	{
		int value = bufferReader.readInt();
		return IntegerConstant.of( value );
	}

	private static FloatConstant readFloatConstant( BufferReader bufferReader )
	{
		float value = bufferReader.readFloat();
		return FloatConstant.of( value );
	}

	private static DoubleConstant readDoubleConstant( BufferReader bufferReader )
	{
		double value = bufferReader.readDouble();
		return DoubleConstant.of( value );
	}

	private static InvokeDynamicConstant readInvokeDynamicConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		int bootstrapMethodIndex = bufferReader.readUnsignedShort();
		NameAndDescriptorConstant nameAndDescriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndDescriptorConstant();
		return InvokeDynamicConstant.of( bootstrapMethodIndex, nameAndDescriptorConstant );
	}

	private static PlainMethodReferenceConstant readPlainMethodReferenceConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		ClassConstant typeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		NameAndDescriptorConstant nameAndDescriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndDescriptorConstant();
		return PlainMethodReferenceConstant.of( typeConstant, nameAndDescriptorConstant );
	}

	private static InterfaceMethodReferenceConstant readInterfaceMethodReferenceConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		ClassConstant typeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		NameAndDescriptorConstant nameAndDescriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndDescriptorConstant();
		return InterfaceMethodReferenceConstant.of( typeConstant, nameAndDescriptorConstant );
	}

	private static FieldReferenceConstant readFieldReferenceConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		ClassConstant typeConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		NameAndDescriptorConstant nameAndDescriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asNameAndDescriptorConstant();
		return FieldReferenceConstant.of( typeConstant, nameAndDescriptorConstant );
	}

	private static MethodTypeConstant readMethodTypeConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		Mutf8Constant descriptorConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMutf8Constant();
		return MethodTypeConstant.of( descriptorConstant );
	}

	private static StringConstant readStringConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		int valueIndex = bufferReader.readUnsignedShort();
		Mutf8Constant valueConstant = constantPool.getConstant( valueIndex ).asMutf8Constant();
		return StringConstant.of( valueConstant );
	}

	private static ClassConstant readClassConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		ConstantPool constantPool1 = constantPool;
		BufferReader bufferReader1 = bufferReader;
		int index = bufferReader1.readUnsignedShort();
		Mutf8Constant nameConstant = constantPool1.getConstant( index ).asMutf8Constant();
		return ClassConstant.of( nameConstant );
	}

	private static VerificationType readVerificationType( ConstantPool constantPool, BufferReader bufferReader, LocationMap locationMap )
	{
		int verificationTypeTag = bufferReader.readUnsignedByte();
		return switch( verificationTypeTag )
			{
				case VerificationType.tagTop, VerificationType.tagInteger, VerificationType.tagFloat, //
					VerificationType.tagDouble, VerificationType.tagLong, VerificationType.tagNull, //
					VerificationType.tagUninitializedThis -> new SimpleVerificationType( verificationTypeTag );
				case VerificationType.tagObject -> readObjectVerificationType( constantPool, bufferReader );
				case VerificationType.tagUninitialized -> readUninitializedVerificationType( bufferReader, locationMap );
				default -> throw new InvalidVerificationTypeTagException( verificationTypeTag );
			};
	}

	private static UninitializedVerificationType readUninitializedVerificationType( BufferReader bufferReader, LocationMap locationMap )
	{
		Instruction instruction = readAbsoluteInstruction( bufferReader, locationMap ).orElseThrow();
		return UninitializedVerificationType.of( instruction );
	}

	private static ObjectVerificationType readObjectVerificationType( ConstantPool constantPool, BufferReader bufferReader )
	{
		ClassConstant classConstant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
		return ObjectVerificationType.of( classConstant );
	}

	private static Optional<Instruction> readAbsoluteInstruction( BufferReader bufferReader, LocationMap locationMap )
	{
		// Absolute instruction references are never used in code; they are only used in attributes.
		// Thus, by the time an absolute instruction reference is parsed, all instructions have already been parsed,
		// which means that we can directly reference the target instruction without the need for fix-ups.
		int targetLocation = bufferReader.readUnsignedShort(); //an unsigned short works here because java methods are limited to 64k.
		return locationMap.getInstruction( targetLocation );
	}
}
