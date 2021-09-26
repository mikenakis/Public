package mikenakis.bytecode.reading;

import mikenakis.bytecode.exceptions.InvalidAnnotationValueTagException;
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
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableEntry;
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
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
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
		ByteCodeReader byteCodeReader = new ByteCodeReader( bytes );
		return byteCodeReader.readByteCodeType();
	}

	private final BufferReader bufferReader;
	private final ByteCodeType.Version version;
	private final ConstantPool constantPool;

	private ByteCodeReader( byte[] bytes )
	{
		bufferReader = BufferReader.of( bytes, 0, bytes.length );
		int magic = bufferReader.readInt();
		assert magic == ByteCodeType.MAGIC;
		version = readVersion( bufferReader );
		constantPool = ConstantPoolReader.read( bufferReader );
	}

	private ByteCodeType readByteCodeType()
	{
		FlagEnumSet<ByteCodeType.Modifier> modifierSet = ByteCodeType.modifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
		ClassConstant thisClassConstant = readIndexAndGetConstant().asClassConstant();
		Optional<ClassConstant> superClassConstant = tryReadIndexAndGetConstant().map( Constant::asClassConstant );
		int interfaceCount = bufferReader.readUnsignedShort();
		List<ClassConstant> interfaceClassConstants = new ArrayList<>( interfaceCount );
		for( int i = 0; i < interfaceCount; i++ )
		{
			ClassConstant interfaceClassConstant = readIndexAndGetConstant().asClassConstant();
			interfaceClassConstants.add( interfaceClassConstant );
		}
		int fieldCount = bufferReader.readUnsignedShort();
		List<ByteCodeField> fields = new ArrayList<>( fieldCount );
		for( int i = 0; i < fieldCount; i++ )
		{
			FlagEnumSet<ByteCodeField.Modifier> fieldModifierSet = ByteCodeField.modifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
			Mutf8Constant nameConstant = readIndexAndGetConstant().asMutf8Constant();
			Mutf8Constant descriptorConstant = readIndexAndGetConstant().asMutf8Constant();
			AttributeSet attributes = readAttributes( Optional.empty() );
			ByteCodeField byteCodeField = ByteCodeField.of( fieldModifierSet, nameConstant, descriptorConstant, attributes );
			fields.add( byteCodeField );
		}
		int methodCount = bufferReader.readUnsignedShort();
		List<ByteCodeMethod> methods = new ArrayList<>( methodCount );
		for( int i = 0; i < methodCount; i++ )
		{
			FlagEnumSet<ByteCodeMethod.Modifier> methodModifierSet = ByteCodeMethod.modifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
			Mutf8Constant nameConstant = readIndexAndGetConstant().asMutf8Constant();
			Mutf8Constant descriptorConstant = readIndexAndGetConstant().asMutf8Constant();
			AttributeSet attributes = readAttributes( Optional.empty() );
			ByteCodeMethod byteCodeMethod = ByteCodeMethod.of( methodModifierSet, nameConstant, descriptorConstant, attributes );
			methods.add( byteCodeMethod );
		}
		AttributeSet attributes = readAttributes( Optional.empty() );
		assert bufferReader.isAtEnd();

		attributes.tryGetKnownAttributeByTag( KnownAttribute.tag_BootstrapMethods ) //
			.map( attribute -> attribute.asBootstrapMethodsAttribute() ) //
			.ifPresent( bootstrapMethodsAttribute -> //
			{
				constantPool.runBootstrapFixUps( bootstrapMethodsAttribute );
				if( Kit.get( false ) ) //TODO: enable this once the troubleshooting is over.
					attributes.removeAttribute( bootstrapMethodsAttribute );
			} );
		Collection<Constant> extraConstants = constantPool.getExtraConstants();
		return ByteCodeType.of( version, modifierSet, thisClassConstant, superClassConstant, interfaceClassConstants, fields, methods, //
			attributes, extraConstants );
	}

	private static ByteCodeType.Version readVersion( BufferReader bufferReader )
	{
		int minor = bufferReader.readUnsignedShort();
		int major = bufferReader.readUnsignedShort();
		return new ByteCodeType.Version( major, minor );
	}

	private AttributeSet readAttributes( Optional<LocationMap> locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		Map<Mutf8Constant,Attribute> attributesFromNames = new LinkedHashMap<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8Constant nameConstant = readIndexAndGetConstant().asMutf8Constant();
			int attributeLength = bufferReader.readInt();
			int endPosition = bufferReader.getPosition() + attributeLength;
			Attribute attribute = readAttribute( nameConstant, locationMap, attributeLength );
			assert bufferReader.getPosition() == endPosition;
			Kit.map.add( attributesFromNames, nameConstant, attribute );
		}
		return AttributeSet.of( attributesFromNames );
	}

	private Attribute readAttribute( Mutf8Constant nameConstant, Optional<LocationMap> locationMap, int attributeLength )
	{
		Optional<Integer> knownAttributeTag = KnownAttribute.tagFromName( nameConstant );
		if( knownAttributeTag.isPresent() )
		{
			return switch( knownAttributeTag.get() )
				{
					case KnownAttribute.tag_AnnotationDefault -> readAnnotationDefaultAttribute();
					case KnownAttribute.tag_BootstrapMethods -> readBootstrapMethodsAttribute();
					case KnownAttribute.tag_Code -> readCodeAttribute();
					case KnownAttribute.tag_ConstantValue -> readConstantValueAttribute();
					case KnownAttribute.tag_Deprecated -> DeprecatedAttribute.of();
					case KnownAttribute.tag_EnclosingMethod -> readEnclosingMethodAttribute();
					case KnownAttribute.tag_Exceptions -> readExceptionsAttribute();
					case KnownAttribute.tag_InnerClasses -> readInnerClassesAttribute();
					case KnownAttribute.tag_LineNumberTable -> readLineNumberTableAttribute( locationMap.orElseThrow() );
					case KnownAttribute.tag_LocalVariableTable -> readLocalVariableTableAttribute( locationMap.orElseThrow() );
					case KnownAttribute.tag_LocalVariableTypeTable -> readLocalVariableTypeTableAttribute( locationMap.orElseThrow() );
					case KnownAttribute.tag_MethodParameters -> readMethodParametersAttribute();
					case KnownAttribute.tag_NestHost -> readNestHostAttribute();
					case KnownAttribute.tag_NestMembers -> readNestMembersAttribute();
					case KnownAttribute.tag_RuntimeInvisibleAnnotations -> readRuntimeInvisibleAnnotationsAttribute();
					case KnownAttribute.tag_RuntimeInvisibleParameterAnnotations -> readRuntimeInvisibleParameterAnnotationsAttribute();
					case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> readRuntimeInvisibleTypeAnnotationsAttribute();
					case KnownAttribute.tag_RuntimeVisibleAnnotations -> readRuntimeVisibleAnnotationsAttribute();
					case KnownAttribute.tag_RuntimeVisibleParameterAnnotations -> readRuntimeVisibleParameterAnnotationsAttribute();
					case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> readRuntimeVisibleTypeAnnotationsAttribute();
					case KnownAttribute.tag_Signature -> readSignatureAttribute();
					case KnownAttribute.tag_SourceFile -> readSourceFileAttribute();
					case KnownAttribute.tag_StackMapTable -> readStackMapTableAttribute( locationMap.orElseThrow() );
					case KnownAttribute.tag_Synthetic -> SyntheticAttribute.of();
					default -> throw new InvalidKnownAttributeTagException( knownAttributeTag.get() );
				};
		}
		else
		{
			Buffer buffer = bufferReader.readBuffer( attributeLength );
			return UnknownAttribute.of( nameConstant, buffer );
		}
	}

	private SourceFileAttribute readSourceFileAttribute()
	{
		Mutf8Constant valueConstant = readIndexAndGetConstant().asMutf8Constant();
		return SourceFileAttribute.of( valueConstant );
	}

	private SignatureAttribute readSignatureAttribute()
	{
		Mutf8Constant signatureConstant = readIndexAndGetConstant().asMutf8Constant();
		return SignatureAttribute.of( signatureConstant );
	}

	private RuntimeVisibleTypeAnnotationsAttribute readRuntimeVisibleTypeAnnotationsAttribute()
	{
		List<TypeAnnotation> entries = readTypeAnnotations();
		return RuntimeVisibleTypeAnnotationsAttribute.of( entries );
	}

	private RuntimeVisibleParameterAnnotationsAttribute readRuntimeVisibleParameterAnnotationsAttribute()
	{
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries();
		return RuntimeVisibleParameterAnnotationsAttribute.of( entries );
	}

	private RuntimeVisibleAnnotationsAttribute readRuntimeVisibleAnnotationsAttribute()
	{
		List<Annotation> annotations = readAnnotations();
		return RuntimeVisibleAnnotationsAttribute.of( annotations );
	}

	private RuntimeInvisibleTypeAnnotationsAttribute readRuntimeInvisibleTypeAnnotationsAttribute()
	{
		List<TypeAnnotation> entries = readTypeAnnotations();
		return RuntimeInvisibleTypeAnnotationsAttribute.of( entries );
	}

	private RuntimeInvisibleParameterAnnotationsAttribute readRuntimeInvisibleParameterAnnotationsAttribute()
	{
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries();
		return RuntimeInvisibleParameterAnnotationsAttribute.of( entries );
	}

	private RuntimeInvisibleAnnotationsAttribute readRuntimeInvisibleAnnotationsAttribute()
	{
		List<Annotation> annotations = readAnnotations();
		return RuntimeInvisibleAnnotationsAttribute.of( annotations );
	}

	private NestMembersAttribute readNestMembersAttribute()
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<ClassConstant> memberClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant memberClassConstant = readIndexAndGetConstant().asClassConstant();
			memberClassConstants.add( memberClassConstant );
		}
		return NestMembersAttribute.of( memberClassConstants );
	}

	private NestHostAttribute readNestHostAttribute()
	{
		ClassConstant hostClassConstant = readIndexAndGetConstant().asClassConstant();
		return NestHostAttribute.of( hostClassConstant );
	}

	private MethodParametersAttribute readMethodParametersAttribute()
	{
		int count = bufferReader.readUnsignedByte();
		assert count > 0;
		List<MethodParameter> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8Constant nameConstant1 = readIndexAndGetConstant().asMutf8Constant();
			FlagEnumSet<MethodParameter.Modifier> modifierSet = MethodParameter.modifierFlagEnum.fromBits( bufferReader.readUnsignedShort() );
			MethodParameter entry = MethodParameter.of( nameConstant1, modifierSet );
			entries.add( entry );
		}
		return MethodParametersAttribute.of( entries );
	}

	private InnerClassesAttribute readInnerClassesAttribute()
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<InnerClass> innerClasses = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant innerClassConstant = readIndexAndGetConstant().asClassConstant();
			Optional<ClassConstant> outerClassConstant = Kit.upCast( tryReadIndexAndGetConstant() );
			Optional<Mutf8Constant> innerNameConstant = Kit.upCast( tryReadIndexAndGetConstant() );
			FlagEnumSet<InnerClass.InnerClassModifier> modifierSet = InnerClass.innerClassModifierFlagsEnum.fromBits( bufferReader.readUnsignedShort() );
			InnerClass innerClass = InnerClass.of( innerClassConstant, outerClassConstant, innerNameConstant, modifierSet );
			innerClasses.add( innerClass );
		}
		return InnerClassesAttribute.of( innerClasses );
	}

	private ExceptionsAttribute readExceptionsAttribute()
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<ClassConstant> exceptionClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant constant = readIndexAndGetConstant().asClassConstant();
			exceptionClassConstants.add( constant );
		}
		return ExceptionsAttribute.of( exceptionClassConstants );
	}

	private EnclosingMethodAttribute readEnclosingMethodAttribute()
	{
		ClassConstant classConstant = readIndexAndGetConstant().asClassConstant();
		Optional<NameAndDescriptorConstant> methodNameAndDescriptorConstant = Kit.upCast( tryReadIndexAndGetConstant() );
		return EnclosingMethodAttribute.of( classConstant, methodNameAndDescriptorConstant );
	}

	private ConstantValueAttribute readConstantValueAttribute()
	{
		ValueConstant<?> valueConstant = readIndexAndGetConstant().asValueConstant();
		return ConstantValueAttribute.of( valueConstant );
	}

	private AnnotationDefaultAttribute readAnnotationDefaultAttribute()
	{
		AnnotationValue annotationValue = readAnnotationValue();
		return AnnotationDefaultAttribute.of( annotationValue );
	}

	private LineNumberTableAttribute readLineNumberTableAttribute( LocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<LineNumberTableEntry> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = readAbsoluteInstruction( locationMap ).orElseThrow();
			int lineNumber = bufferReader.readUnsignedShort();
			var lineNumberEntry = LineNumberTableEntry.of( startInstruction, lineNumber );
			entries.add( lineNumberEntry );
		}
		return LineNumberTableAttribute.of( entries );
	}

	private LocalVariableTableAttribute readLocalVariableTableAttribute( LocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		List<LocalVariableTableEntry> localVariables = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = readAbsoluteInstruction( locationMap ).orElseThrow();
			int length = bufferReader.readUnsignedShort();
			int endLocation = locationMap.getLocation( startInstruction ) + length;
			Optional<Instruction> endInstruction = locationMap.getInstruction( endLocation );
			Mutf8Constant nameConstant1 = readIndexAndGetConstant().asMutf8Constant();
			Mutf8Constant descriptorConstant = readIndexAndGetConstant().asMutf8Constant();
			int index = bufferReader.readUnsignedShort();
			LocalVariableTableEntry localVariable = LocalVariableTableEntry.of( startInstruction, endInstruction, nameConstant1, descriptorConstant, index );
			localVariables.add( localVariable );
		}
		return LocalVariableTableAttribute.of( localVariables );
	}

	private LocalVariableTypeTableAttribute readLocalVariableTypeTableAttribute( LocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<LocalVariableTypeTableEntry> entries = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = readAbsoluteInstruction( locationMap ).orElseThrow();
			int length = bufferReader.readUnsignedShort();
			int endLocation = locationMap.getLocation( startInstruction ) + length;
			Optional<Instruction> endInstruction = locationMap.getInstruction( endLocation );
			Mutf8Constant nameConstant1 = readIndexAndGetConstant().asMutf8Constant();
			Mutf8Constant signatureConstant = readIndexAndGetConstant().asMutf8Constant();
			int index = bufferReader.readUnsignedShort();
			LocalVariableTypeTableEntry entry = LocalVariableTypeTableEntry.of( startInstruction, endInstruction, nameConstant1, signatureConstant, index );
			entries.add( entry );
		}
		return LocalVariableTypeTableAttribute.of( entries );
	}

	private BootstrapMethodsAttribute readBootstrapMethodsAttribute()
	{
		int bootstrapMethodCount = bufferReader.readUnsignedShort();
		assert bootstrapMethodCount > 0;
		List<BootstrapMethod> entries = new ArrayList<>( bootstrapMethodCount );
		for( int i = 0; i < bootstrapMethodCount; i++ )
		{
			MethodHandleConstant methodHandleConstant = readIndexAndGetConstant().asMethodHandleConstant();
			int argumentConstantCount = bufferReader.readUnsignedShort();
			assert argumentConstantCount > 0;
			List<Constant> argumentConstants = new ArrayList<>( argumentConstantCount );
			for( int j = 0; j < argumentConstantCount; j++ )
			{
				Constant argumentConstant = readIndexAndGetConstant();
				argumentConstants.add( argumentConstant );
			}
			BootstrapMethod entry = BootstrapMethod.of( methodHandleConstant, argumentConstants );
			entries.add( entry );
		}
		return BootstrapMethodsAttribute.of( entries );
	}

	private CodeAttribute readCodeAttribute()
	{
		int maxStack = bufferReader.readUnsignedShort();
		int maxLocals = bufferReader.readUnsignedShort();
		int codeLength = bufferReader.readInt();
		Buffer codeBuffer = bufferReader.readBuffer( codeLength );

		BufferReader codeBufferReader = BufferReader.of( codeBuffer );
		ReadingLocationMap locationMap = new ReadingLocationMap( codeBuffer.length() );
		List<Instruction> instructions = InstructionReader.readInstructions( codeBufferReader, locationMap, constantPool );

		int count = bufferReader.readUnsignedShort();
		List<ExceptionInfo> exceptionInfos = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Instruction startInstruction = readAbsoluteInstruction( locationMap ).orElseThrow();
			Optional<Instruction> endInstruction = readAbsoluteInstruction( locationMap );
			assert endInstruction.isPresent();
			Instruction handlerInstruction = readAbsoluteInstruction( locationMap ).orElseThrow();
			Optional<ClassConstant> catchTypeConstant = Kit.upCast( tryReadIndexAndGetConstant() );
			ExceptionInfo exceptionInfo = ExceptionInfo.of( startInstruction, endInstruction, handlerInstruction, catchTypeConstant );
			exceptionInfos.add( exceptionInfo );
		}
		AttributeSet attributes = readAttributes( Optional.of( locationMap ) );
		return CodeAttribute.of( maxStack, maxLocals, instructions, exceptionInfos, attributes );
	}

	private StackMapTableAttribute readStackMapTableAttribute( LocationMap locationMap )
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
					case SameLocals1StackItemStackMapFrame.typeName, SameLocals1StackItemStackMapFrame.extendedTypeName -> readSameLocals1StackItemStackMapFrame( locationMap, previousFrame, frameType );
					case ChopStackMapFrame.typeName -> readChopStackMapFrame( locationMap, previousFrame, frameType );
					case AppendStackMapFrame.typeName -> readAppendStackMapFrame( locationMap, previousFrame, frameType );
					case FullStackMapFrame.typeName -> readFullStackMapFrame( locationMap, previousFrame );
					default -> throw new AssertionError( typeName );
				};
			frames.add( frame );
			previousFrame = Optional.of( frame );
		}
		return StackMapTableAttribute.of( frames );
	}

	private FullStackMapFrame readFullStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame )
	{
		Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
		List<VerificationType> localVerificationTypes = readVerificationTypes( locationMap );
		List<VerificationType> stackVerificationTypes = readVerificationTypes( locationMap );
		return FullStackMapFrame.of( targetInstruction, localVerificationTypes, stackVerificationTypes );
	}

	private AppendStackMapFrame readAppendStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
	{
		Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
		assert frameType >= 252 && frameType <= 254;
		int localCount = frameType - 251;
		List<VerificationType> localVerificationTypes = new ArrayList<>( localCount );
		for( int j = 0; j < localCount; j++ )
		{
			VerificationType verificationType = readVerificationType( locationMap );
			localVerificationTypes.add( verificationType );
		}
		return AppendStackMapFrame.of( targetInstruction, localVerificationTypes );
	}

	private ChopStackMapFrame readChopStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert frameType >= 248 && frameType < 251;
		Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
		return ChopStackMapFrame.of( targetInstruction, 251 - frameType );
	}

	private SameLocals1StackItemStackMapFrame readSameLocals1StackItemStackMapFrame( LocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert (frameType >= 64 && frameType <= 127) || frameType == SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE;
		int offsetDelta = frameType == SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE ? bufferReader.readUnsignedShort() : frameType - 64;
		var targetInstruction = findTargetInstruction( previousFrame, offsetDelta, locationMap ).orElseThrow();
		VerificationType stackVerificationType = readVerificationType( locationMap );
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

	private List<VerificationType> readVerificationTypes( LocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		List<VerificationType> verificationTypes = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			VerificationType verificationType = readVerificationType( locationMap );
			verificationTypes.add( verificationType );
		}
		return verificationTypes;
	}

	private List<Annotation> readAnnotations()
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<Annotation> annotations = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Annotation byteCodeAnnotation = readByteCodeAnnotation();
			annotations.add( byteCodeAnnotation );
		}
		return annotations;
	}

	private List<TypeAnnotation> readTypeAnnotations()
	{
		int typeAnnotationCount = bufferReader.readUnsignedShort();
		assert typeAnnotationCount > 0;
		List<TypeAnnotation> typeAnnotations = new ArrayList<>( typeAnnotationCount );
		for( int i = 0; i < typeAnnotationCount; i++ )
		{
			int targetTag = bufferReader.readUnsignedByte();
			Target target = switch( targetTag )
				{
					case Target.tag_ClassTypeParameter, Target.tag_MethodTypeParameter -> readTypeParameterTarget( bufferReader, targetTag );
					case Target.tag_Supertype -> readSupertypeTarget( bufferReader, targetTag );
					case Target.tag_ClassTypeBound, Target.tag_MethodTypeBound -> readTypeParameterBoundTarget( bufferReader, targetTag );
					case Target.tag_FieldType, Target.tag_ReturnType, Target.tag_ReceiverType -> new EmptyTarget( targetTag );
					case Target.tag_FormalParameter -> readFormalParameterTarget( bufferReader, targetTag );
					case Target.tag_Throws -> readThrowsTarget( bufferReader, targetTag );
					case Target.tag_LocalVariable, Target.tag_ResourceLocalVariable -> readLocalVariableTarget( bufferReader, targetTag );
					case Target.tag_Catch -> readCatchTarget( bufferReader, targetTag );
					case Target.tag_InstanceOfOffset, Target.tag_NewExpressionOffset, Target.tag_NewMethodOffset, Target.tag_IdentifierMethodOffset -> //
						readOffsetTarget( bufferReader, targetTag );
					case Target.tag_CastArgument, Target.tag_ConstructorArgument, Target.tag_MethodArgument, Target.tag_NewMethodArgument, //
						Target.tag_IdentifierMethodArgument -> readTypeArgumentTarget( bufferReader, targetTag );
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
			List<AnnotationParameter> pairs = readAnnotationParameters();
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

	private List<AnnotationParameter> readAnnotationParameters()
	{
		int pairCount = bufferReader.readUnsignedShort();
		List<AnnotationParameter> annotationParameters = new ArrayList<>( pairCount );
		for( int i = 0; i < pairCount; i++ )
		{
			Mutf8Constant nameConstant = readIndexAndGetConstant().asMutf8Constant();
			AnnotationValue annotationValue = readAnnotationValue();
			AnnotationParameter annotationParameter = AnnotationParameter.of( nameConstant, annotationValue );
			annotationParameters.add( annotationParameter );
		}
		return annotationParameters;
	}

	private AnnotationValue readAnnotationValue()
	{
		char annotationValueTag = (char)bufferReader.readUnsignedByte();
		return switch( annotationValueTag )
			{
				case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, //
					AnnotationValue.tagFloat, AnnotationValue.tagInteger, AnnotationValue.tagLong, AnnotationValue.tagShort, //
					AnnotationValue.tagString -> readConstAnnotationValue( annotationValueTag );
				case AnnotationValue.tagAnnotation -> readAnnotationAnnotationValue();
				case AnnotationValue.tagArray -> readArrayAnnotationValue();
				case AnnotationValue.tagClass -> readClassAnnotationValue();
				case AnnotationValue.tagEnum -> readEnumAnnotationValue( );
				default -> throw new InvalidAnnotationValueTagException( annotationValueTag );
			};
	}

	private EnumAnnotationValue readEnumAnnotationValue()
	{
		Mutf8Constant typeNameConstant = readIndexAndGetConstant().asMutf8Constant();
		Mutf8Constant valueNameConstant = readIndexAndGetConstant().asMutf8Constant();
		return EnumAnnotationValue.of( typeNameConstant, valueNameConstant );
	}

	private ClassAnnotationValue readClassAnnotationValue()
	{
		Mutf8Constant classConstant = readIndexAndGetConstant().asMutf8Constant();
		return ClassAnnotationValue.of( classConstant );
	}

	private ArrayAnnotationValue readArrayAnnotationValue()
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<AnnotationValue> annotationValues = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
			annotationValues.add( readAnnotationValue() );
		return ArrayAnnotationValue.of( annotationValues );
	}

	private AnnotationAnnotationValue readAnnotationAnnotationValue()
	{
		Annotation annotation = readByteCodeAnnotation();
		return AnnotationAnnotationValue.of( annotation );
	}

	private ConstAnnotationValue readConstAnnotationValue( char annotationValueTag )
	{
		Constant constant = readIndexAndGetConstant();
		ValueConstant<?> valueConstant = constant.asValueConstant();
		return ConstAnnotationValue.of( annotationValueTag, valueConstant );
	}

	private Annotation readByteCodeAnnotation()
	{
		Mutf8Constant nameConstant = readIndexAndGetConstant().asMutf8Constant();
		List<AnnotationParameter> annotationParameters = readAnnotationParameters();
		return Annotation.of( nameConstant, annotationParameters );
	}

	private List<ParameterAnnotationSet> readParameterAnnotationsAttributeEntries()
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
				Annotation byteCodeAnnotation = readByteCodeAnnotation();
				annotations.add( byteCodeAnnotation );
			}
			ParameterAnnotationSet entry = ParameterAnnotationSet.of( annotations );
			entries.add( entry );
		}
		return entries;
	}

	private VerificationType readVerificationType( LocationMap locationMap )
	{
		int verificationTypeTag = bufferReader.readUnsignedByte();
		return switch( verificationTypeTag )
			{
				case VerificationType.tag_Top, VerificationType.tag_Integer, VerificationType.tag_Float, //
					VerificationType.tag_Double, VerificationType.tag_Long, VerificationType.tag_Null, //
					VerificationType.tag_UninitializedThis -> new SimpleVerificationType( verificationTypeTag );
				case VerificationType.tag_Object -> readObjectVerificationType();
				case VerificationType.tag_Uninitialized -> readUninitializedVerificationType( locationMap );
				default -> throw new InvalidVerificationTypeTagException( verificationTypeTag );
			};
	}

	private UninitializedVerificationType readUninitializedVerificationType( LocationMap locationMap )
	{
		Instruction instruction = readAbsoluteInstruction( locationMap ).orElseThrow();
		return UninitializedVerificationType.of( instruction );
	}

	private ObjectVerificationType readObjectVerificationType()
	{
		ClassConstant classConstant = readIndexAndGetConstant().asClassConstant();
		return ObjectVerificationType.of( classConstant );
	}

	private Optional<Instruction> readAbsoluteInstruction( LocationMap locationMap )
	{
		// Absolute instruction references are never used in code; they are only used in attributes.
		// Thus, by the time an absolute instruction reference is parsed, all instructions have already been parsed,
		// which means that we can directly reference the target instruction without the need for fix-ups.
		int targetLocation = bufferReader.readUnsignedShort(); //an unsigned short works here because java methods are limited to 64k.
		return locationMap.getInstruction( targetLocation );
	}

	private Optional<Constant> tryReadIndexAndGetConstant()
	{
		int constantIndex = bufferReader.readUnsignedShort();
		return constantIndex == 0 ? Optional.empty() : Optional.of( constantPool.getConstant( constantIndex ) );
	}

	private Constant readIndexAndGetConstant()
	{
		int constantIndex = bufferReader.readUnsignedShort();
		return constantPool.getConstant( constantIndex );
	}
}
