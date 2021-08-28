package mikenakis.bytecode.dumping;

import mikenakis.bytecode.AnnotationParameter;
import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ByteCodeAnnotation;
import mikenakis.bytecode.ByteCodeMember;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.Constant;
import mikenakis.bytecode.Descriptor;
import mikenakis.bytecode.attributes.BootstrapMethod;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.ExceptionInfo;
import mikenakis.bytecode.attributes.InnerClass;
import mikenakis.bytecode.attributes.LineNumber;
import mikenakis.bytecode.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.attributes.LocalVariable;
import mikenakis.bytecode.attributes.LocalVariableType;
import mikenakis.bytecode.attributes.MethodParameter;
import mikenakis.bytecode.attributes.ParameterAnnotationsAttribute;
import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionReference;
import mikenakis.bytecode.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.attributes.stackmap.AppendFrame;
import mikenakis.bytecode.attributes.stackmap.ChopFrame;
import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.attributes.stackmap.FullFrame;
import mikenakis.bytecode.attributes.stackmap.SameFrame;
import mikenakis.bytecode.attributes.stackmap.SameLocals1StackItemFrame;
import mikenakis.bytecode.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.constants.DoubleConstant;
import mikenakis.bytecode.constants.FieldReferenceConstant;
import mikenakis.bytecode.constants.FloatConstant;
import mikenakis.bytecode.constants.IntegerConstant;
import mikenakis.bytecode.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.constants.InvokeDynamicConstant;
import mikenakis.bytecode.constants.LongConstant;
import mikenakis.bytecode.constants.MethodHandleConstant;
import mikenakis.bytecode.constants.MethodTypeConstant;
import mikenakis.bytecode.constants.NameAndTypeConstant;
import mikenakis.bytecode.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.constants.StringConstant;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.dumping.printers.AnnotationAnnotationValuePrinter;
import mikenakis.bytecode.dumping.printers.AnnotationDefaultAttributePrinter;
import mikenakis.bytecode.dumping.printers.AnnotationParameterPrinter;
import mikenakis.bytecode.dumping.printers.AnnotationValuePrinter;
import mikenakis.bytecode.dumping.printers.AnnotationsAttributePrinter;
import mikenakis.bytecode.dumping.printers.AppendFramePrinter;
import mikenakis.bytecode.dumping.printers.ArrayAnnotationValuePrinter;
import mikenakis.bytecode.dumping.printers.AttributePrinter;
import mikenakis.bytecode.dumping.printers.AttributesPrinter;
import mikenakis.bytecode.dumping.printers.BootstrapMethodPrinter;
import mikenakis.bytecode.dumping.printers.BootstrapMethodsAttributePrinter;
import mikenakis.bytecode.dumping.printers.BranchInstructionPrinter;
import mikenakis.bytecode.dumping.printers.ByteCodeAnnotationPrinter;
import mikenakis.bytecode.dumping.printers.ByteCodeMemberPrinter;
import mikenakis.bytecode.dumping.printers.ChopFramePrinter;
import mikenakis.bytecode.dumping.printers.ClassAnnotationValuePrinter;
import mikenakis.bytecode.dumping.printers.ClassConstantPrinter;
import mikenakis.bytecode.dumping.printers.CodeAttributePrinter;
import mikenakis.bytecode.dumping.printers.ConditionalBranchInstructionPrinter;
import mikenakis.bytecode.dumping.printers.ConstAnnotationValuePrinter;
import mikenakis.bytecode.dumping.printers.ConstantPrinter;
import mikenakis.bytecode.dumping.printers.ConstantReferencingInstructionPrinter;
import mikenakis.bytecode.dumping.printers.ConstantValueAttributePrinter;
import mikenakis.bytecode.dumping.printers.DeprecatedAttributePrinter;
import mikenakis.bytecode.dumping.printers.EnclosingMethodAttributePrinter;
import mikenakis.bytecode.dumping.printers.EnumAnnotationValuePrinter;
import mikenakis.bytecode.dumping.printers.ExceptionInfoPrinter;
import mikenakis.bytecode.dumping.printers.ExceptionsAttributePrinter;
import mikenakis.bytecode.dumping.printers.FullFramePrinter;
import mikenakis.bytecode.dumping.printers.IIncInstructionPrinter;
import mikenakis.bytecode.dumping.printers.ImmediateLoadConstantInstructionPrinter;
import mikenakis.bytecode.dumping.printers.IndirectLoadConstantInstructionPrinter;
import mikenakis.bytecode.dumping.printers.InnerClassPrinter;
import mikenakis.bytecode.dumping.printers.InnerClassesAttributePrinter;
import mikenakis.bytecode.dumping.printers.InstructionPrinter;
import mikenakis.bytecode.dumping.printers.InstructionReferencePrinter;
import mikenakis.bytecode.dumping.printers.InvokeDynamicConstantPrinter;
import mikenakis.bytecode.dumping.printers.InvokeDynamicInstructionPrinter;
import mikenakis.bytecode.dumping.printers.InvokeInterfaceInstructionPrinter;
import mikenakis.bytecode.dumping.printers.LineNumberPrinter;
import mikenakis.bytecode.dumping.printers.LineNumberTableAttributePrinter;
import mikenakis.bytecode.dumping.printers.LocalVariableInstructionPrinter;
import mikenakis.bytecode.dumping.printers.LocalVariablePrinter;
import mikenakis.bytecode.dumping.printers.LocalVariableTableAttributePrinter;
import mikenakis.bytecode.dumping.printers.LocalVariableTypePrinter;
import mikenakis.bytecode.dumping.printers.LocalVariableTypeTableAttributePrinter;
import mikenakis.bytecode.dumping.printers.LookupSwitchInstructionEntryPrinter;
import mikenakis.bytecode.dumping.printers.LookupSwitchInstructionPrinter;
import mikenakis.bytecode.dumping.printers.MethodHandleConstantPrinter;
import mikenakis.bytecode.dumping.printers.MethodParameterPrinter;
import mikenakis.bytecode.dumping.printers.MethodParametersAttributePrinter;
import mikenakis.bytecode.dumping.printers.MethodTypeConstantPrinter;
import mikenakis.bytecode.dumping.printers.MultiANewArrayInstructionPrinter;
import mikenakis.bytecode.dumping.printers.NameAndTypeConstantPrinter;
import mikenakis.bytecode.dumping.printers.NewPrimitiveArrayInstructionPrinter;
import mikenakis.bytecode.dumping.printers.OperandlessInstructionPrinter;
import mikenakis.bytecode.dumping.printers.OperandlessLoadConstantInstructionPrinter;
import mikenakis.bytecode.dumping.printers.ParameterAnnotationsAttributeEntryPrinter;
import mikenakis.bytecode.dumping.printers.ParameterAnnotationsAttributePrinter;
import mikenakis.bytecode.dumping.printers.ReferenceConstantPrinter;
import mikenakis.bytecode.dumping.printers.SameFramePrinter;
import mikenakis.bytecode.dumping.printers.SameLocals1StackItemFramePrinter;
import mikenakis.bytecode.dumping.printers.SignatureAttributePrinter;
import mikenakis.bytecode.dumping.printers.SourceFileAttributePrinter;
import mikenakis.bytecode.dumping.printers.StackMapTableAttributePrinter;
import mikenakis.bytecode.dumping.printers.StringConstantPrinter;
import mikenakis.bytecode.dumping.printers.SyntheticAttributePrinter;
import mikenakis.bytecode.dumping.printers.TableSwitchInstructionPrinter;
import mikenakis.bytecode.dumping.printers.TypeAnnotationsAttributeElementValuePairPrinter;
import mikenakis.bytecode.dumping.printers.TypeAnnotationsAttributeEntryPrinter;
import mikenakis.bytecode.dumping.printers.TypeAnnotationsAttributeLocalVariableTargetEntryPrinter;
import mikenakis.bytecode.dumping.printers.TypeAnnotationsAttributePrinter;
import mikenakis.bytecode.dumping.printers.TypeAnnotationsAttributeTargetPrinter;
import mikenakis.bytecode.dumping.printers.TypeAnnotationsAttributeTypePathEntryPrinter;
import mikenakis.bytecode.dumping.printers.TypeAnnotationsAttributeTypePathPrinter;
import mikenakis.bytecode.dumping.printers.UnknownAttributePrinter;
import mikenakis.bytecode.dumping.printers.ValueConstantPrinter;
import mikenakis.bytecode.dumping.printers.VerificationTypePrinter;
import mikenakis.kit.Kit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Contains information for rendering byteCode to String.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RenderingContext
{
	public static final String GILDING_PREFIX = "/* ";
	public static final String GILDING_SUFFIX = " */";

	public static final class Data
	{
		public final String label;
		public final Optional<String> source;

		Data( String label, Optional<String> source )
		{
			assert label != null;
			this.label = label;
			this.source = source;
		}

		@Override public String toString()
		{
			return label + ":    " + source;
		}
	}

	public final Style style;
	public final ByteCodeType byteCodeType;
	private final Optional<List<String>> sourceLines;
	private final Map<Instruction,Data> dataFromInstructionMap = new HashMap<>();

	public RenderingContext( Style style, ByteCodeType byteCodeType )
	{
		this.style = style;
		this.byteCodeType = byteCodeType;
		Optional<Path> sourcePathName = byteCodeType.sourcePath.map( p -> p.resolve( byteCodeType.getSourceFileName() ) );
		sourceLines = sourcePathName.map( p -> Kit.unchecked( () -> Files.readAllLines( p ) ) );
		Kit.map.add( dataFromInstructionMap, InstructionReference.END_INSTRUCTION, new Data( InstructionReference.END_LABEL, Optional.empty() ) );
		for( ByteCodeMethod byteCodeMethod : byteCodeType.methods )
		{
			Optional<CodeAttribute> codeAttribute = byteCodeMethod.tryGetCodeAttribute();
			if( codeAttribute.isEmpty() )
				continue;
			updateLabels( codeAttribute.get() );
		}
	}

	public Optional<Data> tryGetData( Instruction instruction )
	{
		return Optional.ofNullable( Kit.map.tryGet( dataFromInstructionMap, instruction ) );
	}

	public Optional<String> tryGetLabel( Instruction instruction )
	{
		Optional<Data> data = Optional.ofNullable( Kit.map.tryGet( dataFromInstructionMap, instruction ) );
		return data.map( d -> d.label );
	}

	public String getLabel( Instruction instruction )
	{
		return tryGetLabel( instruction ).orElseThrow();
	}

	private void updateLabels( CodeAttribute codeAttribute )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = getLineNumberFromInstructionMap( codeAttribute );
		Collection<Instruction> targets = new HashSet<>();
		codeAttribute.collectTargets( instruction -> //
		{
			assert instruction != null;
			if( instruction == InstructionReference.END_INSTRUCTION )
				return;
			assert codeAttribute.instructions.contains( instruction );
			Kit.collection.tryAdd( targets, instruction );
		} );

		int targetInstructionLabelNumberSeed = 1;
		for( Instruction instruction : codeAttribute.instructions )
		{
			Optional<Integer> lineNumber = Optional.ofNullable( Kit.map.tryGet( lineNumberFromInstructionMap, instruction ) );
			if( lineNumber.isPresent() )
			{
				String label = "L" + lineNumber;
				Data data = new Data( label, sourceLines.map( s -> s.get( lineNumber.get() - 1 ).trim() ) );
				Kit.map.add( dataFromInstructionMap, instruction, data );
			}
			else if( Kit.collection.contains( targets, instruction ) )
			{
				String label = "T" + targetInstructionLabelNumberSeed++; //'T' stands for 'Target'
				Data data = new Data( label, Optional.empty() );
				Kit.map.add( dataFromInstructionMap, instruction, data );
			}
		}
	}

	private static Map<Instruction,Integer> getLineNumberFromInstructionMap( CodeAttribute codeAttribute )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = new HashMap<>();
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute.tryGetLineNumberTableAttribute();
		if( lineNumberTableAttribute.isPresent() )
		{
			for( LineNumber entry : lineNumberTableAttribute.get().entries )
			{
				Instruction instruction = entry.startPc.getTargetInstruction();
				Kit.map.add( lineNumberFromInstructionMap, instruction, entry.lineNumber );
			}
		}
		return lineNumberFromInstructionMap;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static TypeAnnotationsAttributeEntryPrinter newPrinter( TypeAnnotationsAttribute.Entry entry )
	{
		return new TypeAnnotationsAttributeEntryPrinter( entry );
	}

	public static Printer newPrinter( TypeAnnotationsAttribute.Target target )
	{
		if( target instanceof TypeAnnotationsAttribute.CatchTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.EmptyTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.FormalParameterTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.LocalVariableTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.OffsetTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.SupertypeTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.ThrowsTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.TypeArgumentTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.TypeParameterBoundTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		if( target instanceof TypeAnnotationsAttribute.TypeParameterTarget )
			return new TypeAnnotationsAttributeTargetPrinter( target );
		throw new AssertionError();
	}

	public static Printer newPrinter( TypeAnnotationsAttribute.LocalVariableTarget.Entry entry )
	{
		return new TypeAnnotationsAttributeLocalVariableTargetEntryPrinter( entry );
	}

	//	public ValueConstantPrinter newPrinter( ValueConstant<?> valueConstant )
	//	{
	//		return new ValueConstantPrinter( valueConstant );
	//	}

	public static ConstantPrinter newPrinter( Constant constant )
	{
		return switch( constant.kind.tag )
			{
				case ClassConstant.TAG -> new ClassConstantPrinter( constant.asClassConstant() );
				case StringConstant.TAG -> new StringConstantPrinter( constant.asStringConstant() );
				case MethodTypeConstant.TAG -> new MethodTypeConstantPrinter( constant.asMethodTypeConstant() );
				case FieldReferenceConstant.TAG, //
					InterfaceMethodReferenceConstant.TAG, //
					PlainMethodReferenceConstant.TAG -> new ReferenceConstantPrinter( constant.asReferenceConstant() );
				case InvokeDynamicConstant.TAG -> new InvokeDynamicConstantPrinter( constant.asInvokeDynamicConstant() );
				case DoubleConstant.TAG, //
					FloatConstant.TAG, //
					IntegerConstant.TAG, //
					LongConstant.TAG, //
					Utf8Constant.TAG -> new ValueConstantPrinter( constant.asValueConstant() );
				case NameAndTypeConstant.TAG -> new NameAndTypeConstantPrinter( constant.asNameAndTypeConstant() );
				case MethodHandleConstant.TAG -> new MethodHandleConstantPrinter( constant.asMethodHandleConstant() );
				default -> throw new AssertionError();
			};
	}

	public static Printer newPrinter( ByteCodeMember byteCodeMember )
	{
		return new ByteCodeMemberPrinter( byteCodeMember );
	}

	public static AttributesPrinter newPrinter( Attributes attributes )
	{
		return new AttributesPrinter( attributes );
	}

	public static BootstrapMethodPrinter newPrinter( BootstrapMethod bootstrapMethod )
	{
		return new BootstrapMethodPrinter( bootstrapMethod );
	}

	public static InstructionPrinter newPrinter( Instruction instruction )
	{
		if( instruction.isTableSwitchInstruction() )
			return new TableSwitchInstructionPrinter( instruction.asTableSwitchInstruction() );
		if( instruction.isConditionalBranchInstruction() )
			return new ConditionalBranchInstructionPrinter( instruction.asConditionalBranchInstruction() );
		if( instruction.isConstantReferencingInstruction() )
			return new ConstantReferencingInstructionPrinter( instruction.asConstantReferencingInstruction() );
		if( instruction.isIIncInstruction() )
			return new IIncInstructionPrinter( instruction.asIIncInstruction() );
		if( instruction.isImmediateLoadConstantInstruction() )
			return new ImmediateLoadConstantInstructionPrinter( instruction.asImmediateLoadConstantInstruction() );
		if( instruction.isIndirectLoadConstantInstruction() )
			return new IndirectLoadConstantInstructionPrinter( instruction.asIndirectLoadConstantInstruction() );
		if( instruction.isInvokeDynamicInstruction() )
			return new InvokeDynamicInstructionPrinter( instruction.asInvokeDynamicInstruction() );
		if( instruction.isInvokeInterfaceInstruction() )
			return new InvokeInterfaceInstructionPrinter( instruction.asInvokeInterfaceInstruction() );
		if( instruction.isLocalVariableInstruction() )
			return new LocalVariableInstructionPrinter( instruction.asLocalVariableInstruction() );
		if( instruction.isLookupSwitchInstruction() )
			return new LookupSwitchInstructionPrinter( instruction.asLookupSwitchInstruction() );
		if( instruction.isMultiANewArrayInstruction() )
			return new MultiANewArrayInstructionPrinter( instruction.asMultiANewArrayInstruction() );
		if( instruction.isNewPrimitiveArrayInstruction() )
			return new NewPrimitiveArrayInstructionPrinter( instruction.asNewPrimitiveArrayInstruction() );
		if( instruction.isOperandlessInstruction() )
			return new OperandlessInstructionPrinter( instruction.asOperandlessInstruction() );
		if( instruction.isOperandlessLoadConstantInstruction() )
			return new OperandlessLoadConstantInstructionPrinter( instruction.asOperandlessLoadConstantInstruction() );
		if( instruction.isBranchInstruction() )
			return new BranchInstructionPrinter( instruction.asBranchInstruction() );
		throw new AssertionError();
	}

	public static AttributePrinter newPrinter( Attribute attribute )
	{
		if( attribute.isBootstrapMethodsAttribute() )
			return new BootstrapMethodsAttributePrinter( attribute.asBootstrapMethodsAttribute() );
		if( attribute.isMethodParametersAttribute() )
			return new MethodParametersAttributePrinter( attribute.asMethodParametersAttribute() );
		if( attribute.isAnnotationDefaultAttribute() )
			return new AnnotationDefaultAttributePrinter( attribute.asAnnotationDefaultAttribute() );
		if( attribute.isAnnotationsAttribute() )
			return new AnnotationsAttributePrinter( attribute.asAnnotationsAttribute() );
		if( attribute.isStackMapTableAttribute() )
			return new StackMapTableAttributePrinter( attribute.asStackMapTableAttribute() );
		if( attribute.isLineNumberTableAttribute() )
			return new LineNumberTableAttributePrinter( attribute.asLineNumberTableAttribute() );
		if( attribute.isLocalVariableTableAttribute() )
			return new LocalVariableTableAttributePrinter( attribute.asLocalVariableTableAttribute() );
		if( attribute.isLocalVariableTypeTableAttribute() )
			return new LocalVariableTypeTableAttributePrinter( attribute.asLocalVariableTypeTableAttribute() );
		if( attribute.isConstantValueAttribute() )
			return new ConstantValueAttributePrinter( attribute.asConstantValueAttribute() );
		if( attribute.isDeprecatedAttribute() )
			return new DeprecatedAttributePrinter( attribute.asDeprecatedAttribute() );
		if( attribute.isEnclosingMethodAttribute() )
			return new EnclosingMethodAttributePrinter( attribute.asEnclosingMethodAttribute() );
		if( attribute.isExceptionsAttribute() )
			return new ExceptionsAttributePrinter( attribute.asExceptionsAttribute() );
		if( attribute.isInnerClassesAttribute() )
			return new InnerClassesAttributePrinter( attribute.asInnerClassesAttribute() );
		if( attribute.isAnnotationsAttribute() )
			return new AnnotationsAttributePrinter( attribute.asAnnotationsAttribute() );
		if( attribute.isParameterAnnotationsAttribute() )
			return new ParameterAnnotationsAttributePrinter( attribute.asParameterAnnotationsAttribute() );
		if( attribute.isSignatureAttribute() )
			return new SignatureAttributePrinter( attribute.asSignatureAttribute() );
		if( attribute.isSourceFileAttribute() )
			return new SourceFileAttributePrinter( attribute.asSourceFileAttribute() );
		if( attribute.isSyntheticAttribute() )
			return new SyntheticAttributePrinter( attribute.asSyntheticAttribute() );
		if( attribute.isUnknownAttribute() )
			return new UnknownAttributePrinter( attribute.asUnknownAttribute() );
		if( attribute.isCodeAttribute() )
			return new CodeAttributePrinter( attribute.asCodeAttribute() );
		if( attribute.isTypeAnnotationsAttribute() )
			return new TypeAnnotationsAttributePrinter( attribute.asTypeAnnotationsAttribute() );
		throw new AssertionError();
	}

	public static Printer newPrinter( AnnotationParameter annotationParameter )
	{
		return new AnnotationParameterPrinter( annotationParameter );
	}

	public static AnnotationValuePrinter newPrinter( AnnotationValue annotationValue )
	{
		if( annotationValue.isArrayAnnotationValue() )
			return new ArrayAnnotationValuePrinter( annotationValue.asArrayAnnotationValue() );
		if( annotationValue.isEnumAnnotationValue() )
			return new EnumAnnotationValuePrinter( annotationValue.asEnumAnnotationValue() );
		if( annotationValue.isConstAnnotationValue() )
			return new ConstAnnotationValuePrinter( annotationValue.asConstAnnotationValue() );
		if( annotationValue.isClassAnnotationValue() )
			return new ClassAnnotationValuePrinter( annotationValue.asClassAnnotationValue() );
		if( annotationValue.isAnnotationAnnotationValue() )
			return new AnnotationAnnotationValuePrinter( annotationValue.asAnnotationAnnotationValue() );
		throw new AssertionError();
	}

	public static ByteCodeAnnotationPrinter newPrinter( ByteCodeAnnotation byteCodeAnnotation )
	{
		return new ByteCodeAnnotationPrinter( byteCodeAnnotation );
	}

	public static MethodParameterPrinter newPrinter( MethodParameter methodParameter )
	{
		return new MethodParameterPrinter( methodParameter );
	}

	public static ExceptionInfoPrinter newPrinter( ExceptionInfo exceptionInfo )
	{
		return new ExceptionInfoPrinter( exceptionInfo );
	}

	public static LocalVariableTypePrinter newPrinter( LocalVariableType localVariableType )
	{
		return new LocalVariableTypePrinter( localVariableType );
	}

	public static LocalVariablePrinter newPrinter( LocalVariable localVariable )
	{
		return new LocalVariablePrinter( localVariable );
	}

	public static LineNumberPrinter newPrinter( LineNumber lineNumber )
	{
		return new LineNumberPrinter( lineNumber );
	}

	public static Printer newPrinter( ParameterAnnotationsAttribute.Entry entry )
	{
		return new ParameterAnnotationsAttributeEntryPrinter( entry );
	}

	public static InnerClassPrinter newPrinter( InnerClass innerClass )
	{
		return new InnerClassPrinter( innerClass );
	}

	public static InstructionReferencePrinter newPrinter( InstructionReference instructionReference )
	{
		return new InstructionReferencePrinter( instructionReference );
	}

	public static Printer newPrinter( LookupSwitchInstruction.Entry entry )
	{
		return new LookupSwitchInstructionEntryPrinter( entry );
	}

	public static Printer newPrinter( TypeAnnotationsAttribute.TypePath typePath )
	{
		return new TypeAnnotationsAttributeTypePathPrinter( typePath );
	}

	public static Printer newPrinter( TypeAnnotationsAttribute.TypePath.Entry entry )
	{
		return new TypeAnnotationsAttributeTypePathEntryPrinter( entry );
	}

	public static Printer newPrinter( TypeAnnotationsAttribute.ElementValuePair elementValuePair )
	{
		return new TypeAnnotationsAttributeElementValuePairPrinter( elementValuePair );
	}

	public static Printer newPrinter( Frame frame, Optional<Frame> previousFrame )
	{
		if( frame instanceof ChopFrame )
			return new ChopFramePrinter( (ChopFrame)frame, previousFrame );
		if( frame instanceof FullFrame )
			return new FullFramePrinter( (FullFrame)frame, previousFrame );
		if( frame instanceof AppendFrame )
			return new AppendFramePrinter( (AppendFrame)frame, previousFrame );
		if( frame instanceof SameFrame )
			return new SameFramePrinter( (SameFrame)frame, previousFrame );
		if( frame instanceof SameLocals1StackItemFrame )
			return new SameLocals1StackItemFramePrinter( (SameLocals1StackItemFrame)frame, previousFrame );
		throw new AssertionError();
	}

	public static VerificationTypePrinter newPrinter( VerificationType verificationType )
	{
		return new VerificationTypePrinter( verificationType );
	}

	public void appendNameAndDescriptor( StringBuilder builder, Utf8Constant nameConstant, Utf8Constant descriptorConstant )
	{
		if( style.raw )
		{
			builder.append( ", name = " );
			newPrinter( nameConstant ).appendRawIndexTo( this, builder );
			builder.append( ", descriptor = " );
			newPrinter( descriptorConstant ).appendRawIndexTo( this, builder );
		}
		if( style == Style.MIXED )
			builder.append( ' ' );
		if( style.gild )
		{
			builder.append( GILDING_PREFIX );
			appendGildedNameAndTypeAndDescriptor( builder, nameConstant, descriptorConstant );
			builder.append( GILDING_SUFFIX );
		}
	}

	public static void appendGildedNameAndTypeAndDescriptor( StringBuilder builder, Utf8Constant nameConstant, Utf8Constant descriptorConstant )
	{
		Descriptor descriptor = Descriptor.from( descriptorConstant.getStringValue() );
		appendDescriptorTo( descriptor, builder, nameConstant.getStringValue() );
	}

	public static void appendGildedNameAndTypeAndDescriptor( StringBuilder builder, Utf8Constant nameConstant, Utf8Constant descriptorConstant, ClassConstant typeConstant )
	{
		Descriptor descriptor = Descriptor.from( descriptorConstant.getStringValue() );
		appendDescriptorTo( descriptor, builder, nameConstant.getStringValue(), Optional.of( typeConstant.getClassName() ) );
	}

	public static void appendGildedNameAndSignature( StringBuilder builder, Utf8Constant nameConstant, Utf8Constant signatureConstant )
	{
		//TODO
		builder.append( nameConstant.getStringValue() );
		builder.append( ' ' );
		builder.append( signatureConstant.getStringValue() );
	}

	public static void appendAccessFlags( StringBuilder builder, int accessFlags, Function<Integer,String> accessFlagNameSupplier )
	{
		if( accessFlags != 0 )
		{
			appendBitsAsString( builder, accessFlags, accessFlagNameSupplier, " " );
			builder.append( ' ' );
		}
	}

	private static void appendDescriptorTo( Descriptor descriptor, StringBuilder builder, String name )
	{
		appendDescriptorTo( descriptor, builder, name, Optional.empty() );
	}

	public static void appendDescriptorTo( Descriptor descriptor, StringBuilder builder, String name, Optional<String> declaringTypeName )
	{
		builder.append( descriptor.typeName );
		builder.append( ' ' );
		if( declaringTypeName.isPresent() )
		{
			builder.append( declaringTypeName.get() );
			builder.append( '.' );
		}
		builder.append( name );
		if( descriptor.argumentTypeNames != null )
		{
			builder.append( '(' );
			if( !descriptor.argumentTypeNames.isEmpty() )
			{
				builder.append( ' ' );
				boolean first = true;
				for( String argumentTypeName : descriptor.argumentTypeNames )
				{
					first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
					builder.append( argumentTypeName );
				}
				builder.append( ' ' );
			}
			builder.append( ')' );
		}
	}

	public static void appendBitsAsString( StringBuilder builder, int accessFlags, Function<Integer,String> nameFromMask, String delimiter )
	{
		boolean first = true;
		for( int i = 0; i < 32; i++ )
		{
			int mask = 1 << i;
			if( (accessFlags & mask) == 0 )
				continue;
			first = Kit.stringBuilder.appendDelimiter( builder, first, delimiter );
			String name = nameFromMask.apply( mask );
			assert name != null;
			builder.append( name );
		}
	}

	public static <T> void treeDump( T rootNode, Function<T,Collection<T>> breeder, Function<T,String> stringizer, Consumer<String> emitter )
	{
		String rootString = stringizer.apply( rootNode );
		emitter.accept( rootString );
		treeDumpRecursive( rootNode, "", breeder, stringizer, emitter );
	}

	private static final String[][] PREFIXES = { { " ├─ ", " │  " }, { " └─ ", "    " } };

	private static <T> void treeDumpRecursive( T node, String parentPrefix, Function<T,Collection<T>> breeder, Function<T,String> stringizer, Consumer<String> emitter )
	{
		for( Iterator<T> iterator = breeder.apply( node ).iterator(); iterator.hasNext(); )
		{
			T childNode = iterator.next();
			String[] prefixes = PREFIXES[iterator.hasNext() ? 0 : 1];
			String payload = stringizer.apply( childNode );
			emitter.accept( parentPrefix + prefixes[0] + payload );
			treeDumpRecursive( childNode, parentPrefix + prefixes[1], breeder, stringizer, emitter );
		}
	}
}
