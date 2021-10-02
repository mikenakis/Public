package mikenakis.bytecode.printing;

import mikenakis.bytecode.exceptions.InvalidKnownAttributeTagException;
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
import mikenakis.bytecode.model.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.UnknownAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.InstructionList;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ClassConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.FieldConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
//import mikenakis.bytecode.model.attributes.code.instructions.ImmediateLoadConstantInstruction;
//import mikenakis.bytecode.model.attributes.code.instructions.IndirectLoadConstantInstruction;
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
//import mikenakis.bytecode.model.attributes.code.instructions.OperandlessLoadConstantInstruction;
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
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.model.descriptors.MethodDescriptor;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;
import mikenakis.bytecode.printing.twig.Twig;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagSet;
import mikenakis.kit.functional.Procedure1;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodePrinter
{
	public static String printByteCodeType( ByteCodeType byteCodeType, Optional<Path> sourcePath )
	{
		ByteCodePrinter byteCodePrinter = new ByteCodePrinter( byteCodeType, sourcePath );
		Twig rootNode = byteCodePrinter.twigFromByteCodeType( byteCodeType );
		var builder = new StringBuilder();
		Kit.tree.print( rootNode, Twig::children, Twig::text, s -> builder.append( s ).append( '\n' ) );
		return builder.toString();
	}

	private static final class LabelInfo
	{
		final String label;
		final Optional<String> source;

		LabelInfo( String label, Optional<String> source )
		{
			assert label != null;
			this.label = label;
			this.source = source;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return label + ":    " + source;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final Optional<List<String>> sourceLines;

	private ByteCodePrinter( ByteCodeType byteCodeType, Optional<Path> sourcePath )
	{
		sourceLines = sourcePath.flatMap( p -> byteCodeType.tryGetSourceFileName().map( f -> p.resolve( f ) ) ) //
			.map( p -> Kit.unchecked( () -> Files.readAllLines( p ) ) );
	}

	private interface Labeler
	{
		Optional<LabelInfo> tryGetLabelInfo( Instruction instruction );

		default String getLabel( Instruction instruction )
		{
			return tryGetLabelInfo( instruction ).orElseThrow().label;
		}

		default String getLabel( Optional<Instruction> instruction )
		{
			if( instruction.isEmpty() )
				return CodeAttribute.END_LABEL;
			LabelInfo labelInfo = tryGetLabelInfo( instruction.get() ).orElseThrow();
			return labelInfo.label;
		}
	}

	private static void updateLabels( CodeAttribute codeAttribute, Map<Instruction,LabelInfo> labelInfoFromInstructionMap, Optional<List<String>> sourceLines )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = getLineNumberFromInstructionMap( codeAttribute );

		Collection<Instruction> targetInstructions = new HashSet<>();
		Procedure1<Instruction> targetInstructionConsumer = t -> Kit.collection.tryAdd( targetInstructions, t );
		targetsFromInstructions( targetInstructionConsumer, codeAttribute.instructions.all() );
		targetsFromExceptionInfos( targetInstructionConsumer, codeAttribute.exceptionInfos );
		targetsFromKnownAttributes( targetInstructionConsumer, codeAttribute.attributeSet.knownAttributes() );

		int targetInstructionLabelNumberSeed = 1;
		for( Instruction instruction : codeAttribute.instructions.all() )
		{
			Optional<Integer> lineNumber = Kit.map.getOptional( lineNumberFromInstructionMap, instruction );
			if( lineNumber.isPresent() )
			{
				String label = "L" + lineNumber.get(); //'L' stands for 'Line Number'
				LabelInfo labelInfo = new LabelInfo( label, sourceLines.map( s -> s.get( lineNumber.get() - 1 ).trim() ) );
				Kit.map.add( labelInfoFromInstructionMap, instruction, labelInfo );
			}
			else if( Kit.collection.contains( targetInstructions, instruction ) )
			{
				String label = "T" + targetInstructionLabelNumberSeed++; //'T' stands for 'Target'
				LabelInfo labelInfo = new LabelInfo( label, Optional.empty() );
				Kit.map.add( labelInfoFromInstructionMap, instruction, labelInfo );
			}
		}
	}

	private static void targetsFromInstructions( Procedure1<Instruction> targetInstructionConsumer, Iterable<Instruction> instructions )
	{
		for( Instruction instruction : instructions )
		{
			switch( instruction.groupTag )
			{
				case Instruction.groupTag_Branch -> targetInstructionConsumer.invoke( instruction.asBranchInstruction().getTargetInstruction() );
				case Instruction.groupTag_ConditionalBranch -> targetInstructionConsumer.invoke( instruction.asConditionalBranchInstruction().getTargetInstruction() );
				case Instruction.groupTag_LookupSwitch -> {
					LookupSwitchInstruction lookupSwitchInstruction = instruction.asLookupSwitchInstruction();
					targetInstructionConsumer.invoke( lookupSwitchInstruction.getDefaultInstruction() );
					for( LookupSwitchEntry entry : lookupSwitchInstruction.entries )
						targetInstructionConsumer.invoke( entry.getTargetInstruction() );
				}
				case Instruction.groupTag_TableSwitch -> {
					TableSwitchInstruction tableSwitchInstruction = instruction.asTableSwitchInstruction();
					targetInstructionConsumer.invoke( tableSwitchInstruction.getDefaultInstruction() );
					for( Instruction targetInstruction : tableSwitchInstruction.targetInstructions() )
						targetInstructionConsumer.invoke( targetInstruction );
				}
				default -> { /* nothing to do */ }
			}
		}
	}

	private static void targetsFromExceptionInfos( Procedure1<Instruction> targetInstructionConsumer, Iterable<ExceptionInfo> exceptionInfos )
	{
		for( ExceptionInfo exceptionInfo : exceptionInfos )
		{
			targetInstructionConsumer.invoke( exceptionInfo.startInstruction );
			exceptionInfo.endInstruction.ifPresent( t -> targetInstructionConsumer.invoke( t ) );
			targetInstructionConsumer.invoke( exceptionInfo.handlerInstruction );
		}
	}

	private static void targetsFromKnownAttributes( Procedure1<Instruction> targetInstructionConsumer, Iterable<KnownAttribute> knownAttributes )
	{
		for( KnownAttribute knownAttribute : knownAttributes )
		{
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tag_LocalVariableTable ->//
					{
						LocalVariableTableAttribute localVariableTableAttribute = knownAttribute.asLocalVariableTableAttribute();
						for( LocalVariableTableEntry localVariable : localVariableTableAttribute.entrys )
						{
							targetInstructionConsumer.invoke( localVariable.startInstruction );
							localVariable.endInstruction.ifPresent( t -> targetInstructionConsumer.invoke( t ) );
						}
					}
				case KnownAttribute.tag_LocalVariableTypeTable -> //
					{
						LocalVariableTypeTableAttribute localVariableTypeTableAttribute = knownAttribute.asLocalVariableTypeTableAttribute();
						for( LocalVariableTypeTableEntry entry : localVariableTypeTableAttribute.localVariableTypes )
						{
							targetInstructionConsumer.invoke( entry.startInstruction );
							//FIXME how come we are not feeding the endInstruction?
						}
					}
				case KnownAttribute.tag_StackMapTable -> //
					{
						StackMapTableAttribute stackMapTableAttribute = knownAttribute.asStackMapTableAttribute();
						for( StackMapFrame frame : stackMapTableAttribute.frames() )
						{
							targetInstructionConsumer.invoke( frame.getTargetInstruction() );
							if( frame.isSameLocals1StackItemStackMapFrame() ) //TODO use switch!
							{
								SameLocals1StackItemStackMapFrame sameLocals1StackItemStackMapFrame = frame.asSameLocals1StackItemStackMapFrame();
								targetsFromVerificationType( targetInstructionConsumer, sameLocals1StackItemStackMapFrame.stackVerificationType() );
							}
							else if( frame.isAppendStackMapFrame() )
							{
								AppendStackMapFrame appendStackMapFrame = frame.asAppendStackMapFrame();
								targetsFromVerificationTypes( targetInstructionConsumer, appendStackMapFrame.localVerificationTypes() );
							}
							else if( frame.isFullStackMapFrame() )
							{
								FullStackMapFrame fullStackMapFrame = frame.asFullStackMapFrame();
								targetsFromVerificationTypes( targetInstructionConsumer, fullStackMapFrame.localVerificationTypes() );
								targetsFromVerificationTypes( targetInstructionConsumer, fullStackMapFrame.stackVerificationTypes() );
							}
						}
					}
				case KnownAttribute.tag_LineNumberTable -> //
					{
						/* nothing to do */
					}
				default -> { }
			}
		}
	}

	private static void targetsFromVerificationTypes( Procedure1<Instruction> targetInstructionConsumer, Iterable<VerificationType> verificationTypes )
	{
		for( VerificationType verificationType : verificationTypes )
			targetsFromVerificationType( targetInstructionConsumer, verificationType );
	}

	private static void targetsFromVerificationType( Procedure1<Instruction> targetInstructionConsumer, VerificationType verificationType )
	{
		switch( verificationType.tag )
		{
			case VerificationType.tag_Top, VerificationType.tag_Integer, VerificationType.tag_Float, VerificationType.tag_Double, VerificationType.tag_Long, //
				VerificationType.tag_Null, VerificationType.tag_UninitializedThis -> Kit.get( verificationType.asSimpleVerificationType() ); /* nothing to do */
			case VerificationType.tag_Object -> Kit.get( verificationType.asObjectVerificationType() ); /* nothing to do */
			case VerificationType.tag_Uninitialized -> targetInstructionConsumer.invoke( verificationType.asUninitializedVerificationType().instruction );
			default -> throw new AssertionError( verificationType );
		}
	}

	private static Map<Instruction,Integer> getLineNumberFromInstructionMap( CodeAttribute codeAttribute )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = new HashMap<>();
		codeAttribute.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_LineNumberTable ) //
			.map( a -> a.asLineNumberTableAttribute() ) //
			.ifPresent( lineNumberTableAttribute -> //
			{
				for( LineNumberTableEntry entry : lineNumberTableAttribute.entrys )
					Kit.map.add( lineNumberFromInstructionMap, entry.instruction(), entry.lineNumber() );
			} );
		return lineNumberFromInstructionMap;
	}

	private enum IntegerValueHint
	{
		Byte,      //
		Boolean,   //
		Character, //
		Short,     //
		Integer
	}

	private static void appendValueConstant( ValueConstant<?> constant, StringBuilder builder )
	{
		switch( constant.tag )
		{
			case Constant.tag_Mutf8 -> appendMutf8Constant( constant.asMutf8Constant(), builder );
			case Constant.tag_Integer -> appendIntegerConstant( constant.asIntegerConstant(), builder, IntegerValueHint.Integer );
			case Constant.tag_Float -> appendFloatConstant( constant.asFloatConstant(), builder );
			case Constant.tag_Long -> appendLongConstant( constant.asLongConstant(), builder );
			case Constant.tag_Double -> appendDoubleConstant( constant.asDoubleConstant(), builder );
			case Constant.tag_String -> appendStringConstant( constant.asStringConstant(), builder );
			default -> throw new AssertionError( constant );
		}
	}

	private static void appendMutf8Constant( Mutf8Constant mutf8Constant, StringBuilder builder )
	{
		builder.append( Mutf8Constant.class.getSimpleName() ).append( "( " );
		Kit.stringBuilder.appendEscapedForJava( builder, mutf8Constant.stringValue(), '"' );
		builder.append( " )" );
	}

	private static void appendIntegerConstant( IntegerConstant integerConstant, StringBuilder builder, IntegerValueHint integerValueHint )
	{
		builder.append( IntegerConstant.class.getSimpleName() ).append( "( " );
		builder.append( integerConstant.value );
		switch( integerValueHint )
		{
			case Boolean:
			{
				builder.append( " (boolean" );
				switch( integerConstant.value )
				{
					case 0:
						builder.append( " false" );
						break;
					default:
						builder.append( " true" );
						break;
				}
				builder.append( ")" );
				break;
			}
			case Character:
			{
				builder.append( " (char" );
				if( integerConstant.value >= 0 && integerConstant.value < 65535 )
				{
					builder.append( " " );
					Kit.stringBuilder.appendEscapedForJava( builder, "" + (char)integerConstant.value, '\'' );
				}
				builder.append( ")" );
				break;
			}
			case Byte:
				builder.append( " (byte)" );
				break;
			case Short:
				builder.append( " (short)" );
				break;
			case Integer:
				break;
		}
		builder.append( " )" );
	}

	private static void appendFloatConstant( FloatConstant floatConstant, StringBuilder builder )
	{
		builder.append( FloatConstant.class.getSimpleName() ).append( "( " );
		builder.append( floatConstant.value ).append( 'f' );
		builder.append( " )" );
	}

	private static void appendLongConstant( LongConstant longConstant, StringBuilder builder )
	{
		builder.append( LongConstant.class.getSimpleName() ).append( "( " );
		builder.append( longConstant.value ).append( 'L' );
		builder.append( " )" );
	}

	private static void appendDoubleConstant( DoubleConstant doubleConstant, StringBuilder builder )
	{
		builder.append( DoubleConstant.class.getSimpleName() ).append( "( " );
		builder.append( doubleConstant.value );
		builder.append( " )" );
	}

	private static void appendStringConstant( StringConstant stringConstant, StringBuilder builder )
	{
		builder.append( StringConstant.class.getSimpleName() ).append( "( " );
		appendMutf8Constant( stringConstant.getValueConstant(), builder );
		builder.append( " )" );
	}

	private static <E extends Enum<E>> String stringFromFlags( FlagSet<E> flagSet )
	{
		StringBuilder stringBuilder = new StringBuilder();
		flagsToStringBuilder( flagSet, stringBuilder );
		return stringBuilder.toString();
	}

	private static <E extends Enum<E>> void flagsToStringBuilder( FlagSet<E> flagSet, StringBuilder stringBuilder )
	{
		stringBuilder.append( "[" );
		boolean first = true;
		for( E value : flagSet.values() )
		{
			first = Kit.stringBuilder.appendDelimiter( stringBuilder, first, ", " );
			stringBuilder.append( value.toString().toLowerCase( Locale.ROOT ) );
		}
		stringBuilder.append( "]" );
	}

	private static void summarizeAbsoluteInstruction( Optional<Instruction> instruction, StringBuilder builder, Labeler labeler )
	{
		String label = labeler.getLabel( instruction );
		builder.append( label );
	}

	private static void summarizeAbsoluteInstruction( Instruction instruction, StringBuilder builder, Labeler labeler )
	{
		summarizeAbsoluteInstruction( Optional.of( instruction ), builder, labeler );
	}

	private static Twig twigFromAnnotationParameter( AnnotationParameter annotationParameter )
	{
		return Twig.group( AnnotationParameter.class.getSimpleName() + " name = \"" + annotationParameter.name() + "\"", //
			Map.entry( "value", twigFromAnnotationValue( annotationParameter.value ) ) );
	}

	private Twig twigFromAttributeSet( AttributeSet attributeSet, Optional<Labeler> labeler )
	{
		return Twig.array( attributeSet.allAttributes().stream().map( a -> twigFromAttribute( a, labeler ) ).toList() );
	}

	private Twig twigFromAttribute( Attribute attribute, Optional<Labeler> labeler )
	{
		if( !attribute.isKnown() )
		{
			UnknownAttribute unknownAttribute = attribute.asUnknownAttribute();
			return twigFromUnknownAttribute( unknownAttribute );
		}
		KnownAttribute knownAttribute = attribute.asKnownAttribute();
		return switch( knownAttribute.tag )
			{
				case KnownAttribute.tag_AnnotationDefault -> twigFromAnnotationDefaultAttribute( knownAttribute.asAnnotationDefaultAttribute() );
				case KnownAttribute.tag_BootstrapMethods -> twigFromBootstrapMethodsAttribute( knownAttribute.asBootstrapMethodsAttribute() );
				case KnownAttribute.tag_Code -> twigFromCodeAttribute( knownAttribute.asCodeAttribute() );
				case KnownAttribute.tag_ConstantValue -> twigFromConstantValueAttribute( knownAttribute.asConstantValueAttribute() );
				case KnownAttribute.tag_Deprecated -> twigFromDeprecatedAttribute( knownAttribute.asDeprecatedAttribute() );
				case KnownAttribute.tag_EnclosingMethod -> twigFromEnclosingMethodAttribute( knownAttribute.asEnclosingMethodAttribute() );
				case KnownAttribute.tag_Exceptions -> twigFromExceptionsAttribute( knownAttribute.asExceptionsAttribute() );
				case KnownAttribute.tag_InnerClasses -> twigFromInnerClassesAttribute( knownAttribute.asInnerClassesAttribute() );
				case KnownAttribute.tag_LineNumberTable -> twigFromLineNumberTableAttribute( labeler.orElseThrow(), knownAttribute.asLineNumberTableAttribute() );
				case KnownAttribute.tag_LocalVariableTable -> twigFromLocalVariableTableAttribute( labeler.orElseThrow(), knownAttribute.asLocalVariableTableAttribute() );
				case KnownAttribute.tag_LocalVariableTypeTable -> twigFromLocalVariableTypeTableAttribute( labeler.orElseThrow(), knownAttribute.asLocalVariableTypeTableAttribute() );
				case KnownAttribute.tag_MethodParameters -> twigFromMethodParametersAttribute( knownAttribute.asMethodParametersAttribute() );
				case KnownAttribute.tag_NestHost -> twigFromNestHostAttribute( knownAttribute.asNestHostAttribute() );
				case KnownAttribute.tag_NestMembers -> twigFromNestMembersAttribute( knownAttribute.asNestMembersAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleAnnotations, KnownAttribute.tag_RuntimeVisibleAnnotations -> twigFromAnnotationsAttribute( knownAttribute.asAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleParameterAnnotations, KnownAttribute.tag_RuntimeInvisibleParameterAnnotations -> twigFromParameterAnnotationsAttribute( knownAttribute.asParameterAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleTypeAnnotations, KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> twigFromTypeAnnotationsAttribute( knownAttribute.asTypeAnnotationsAttribute() );
				case KnownAttribute.tag_Signature -> twigFromSignatureAttribute( knownAttribute.asSignatureAttribute() );
				case KnownAttribute.tag_SourceFile -> twigFromSourceFileAttribute( knownAttribute.asSourceFileAttribute() );
				case KnownAttribute.tag_StackMapTable -> twigFromStackMapTableAttribute( labeler.orElseThrow(), knownAttribute.asStackMapTableAttribute() );
				case KnownAttribute.tag_Synthetic -> twigFromSyntheticAttribute( knownAttribute.asSyntheticAttribute() );
				default -> throw new InvalidKnownAttributeTagException( knownAttribute.tag );
			};
	}

	private static Twig twigFromTypeAnnotationsAttribute( TypeAnnotationsAttribute typeAnnotationsAttribute )
	{
		return Twig.array( typeAnnotationsAttribute.getClass().getSimpleName(), //
			typeAnnotationsAttribute.typeAnnotations.stream().map( a -> twigFromTypeAnnotation( a ) ).toList() );
	}

	private Twig twigFromCodeAttribute( CodeAttribute codeAttribute )
	{
		Map<Instruction,LabelInfo> labelInfoFromInstructionMap = new HashMap<>();
		updateLabels( codeAttribute, labelInfoFromInstructionMap, sourceLines );
		Labeler labeler = instruction -> Kit.map.getOptional( labelInfoFromInstructionMap, instruction );
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( codeAttribute.getClass().getSimpleName() );
		headerBuilder.append( " maxStack = " ).append( codeAttribute.getMaxStack() );
		headerBuilder.append( ", maxLocals = " ).append( codeAttribute.getMaxLocals() );
		return Twig.group( headerBuilder.toString(), //
			Map.entry( "instructions", twigFromInstructionList( codeAttribute.instructions, labeler ) ), //
			Map.entry( "exceptionInfos", twigFromExceptionInfos( codeAttribute.exceptionInfos, labeler ) ), //
			Map.entry( "attributeSet", twigFromAttributeSet( codeAttribute.attributeSet, Optional.of( labeler ) ) ) );
	}

	private static Twig twigFromSyntheticAttribute( SyntheticAttribute syntheticAttribute )
	{
		return Twig.leaf( syntheticAttribute.getClass().getSimpleName() );
	}

	private static Twig twigFromSourceFileAttribute( SourceFileAttribute sourceFileAttribute )
	{
		return Twig.leaf( sourceFileAttribute.getClass().getSimpleName() + " " + Kit.string.escapeForJava( sourceFileAttribute.value() ) );
	}

	private static Twig twigFromSignatureAttribute( SignatureAttribute signatureAttribute )
	{
		return Twig.leaf( signatureAttribute.getClass().getSimpleName() + " " + signatureAttribute.signatureString() );
	}

	private static Twig twigFromParameterAnnotationsAttribute( ParameterAnnotationsAttribute parameterAnnotationsAttribute )
	{
		return Twig.array( parameterAnnotationsAttribute.getClass().getSimpleName(), //
			parameterAnnotationsAttribute.parameterAnnotationSets.stream().map( a -> twigFromParameterAnnotationSet( a ) ).toList() );
	}

	private static Twig twigFromInnerClassesAttribute( InnerClassesAttribute innerClassesAttribute )
	{
		return Twig.array( innerClassesAttribute.getClass().getSimpleName(), //
			innerClassesAttribute.innerClasses.stream().map( c -> twigFromInnerClass( c ) ).toList() );
	}

	private static Twig twigFromInnerClass( InnerClass innerClass )
	{
		var builder = new StringBuilder();
		builder.append( innerClass.getClass().getSimpleName() );
		builder.append( " " );
		builder.append( "accessFlags = " ).append( stringFromFlags( innerClass.modifiers ) );
		builder.append( ", innerClass = " ).append( innerClass.innerType().typeName() );
		innerClass.outerType().ifPresent( t -> builder.append( ", outerClass = " ).append( t.typeName() ) );
		innerClass.innerName().ifPresent( s -> builder.append( ", innerName = " ).append( s ) );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromExceptionsAttribute( ExceptionsAttribute exceptionsAttribute )
	{
		return Twig.array( exceptionsAttribute.getClass().getSimpleName(), //
			exceptionsAttribute.exceptions().stream().map( c -> Twig.leaf( c.typeName() ) ).toList() );
	}

	private static Twig twigFromEnclosingMethodAttribute( EnclosingMethodAttribute enclosingMethodAttribute )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( enclosingMethodAttribute.getClass().getSimpleName() );
		headerBuilder.append( " enclosingClass = " ).append( enclosingMethodAttribute.enclosingClassTypeDescriptor().typeName() );
		// Java-bollocks: the enclosing method attribute, if present, will always have an enclosing class,
		// but it may not necessarily have an enclosing method.
		enclosingMethodAttribute.enclosingMethodPrototype().ifPresent( p -> headerBuilder.append( ", enclosingMethodPrototype = " ).append( p.asString() ) );
		return Twig.leaf( headerBuilder.toString() );
	}

	private static Twig twigFromDeprecatedAttribute( DeprecatedAttribute deprecatedAttribute )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( deprecatedAttribute.getClass().getSimpleName() );
		return Twig.leaf( headerBuilder.toString() );
	}

	private static Twig twigFromConstantValueAttribute( ConstantValueAttribute constantValueAttribute )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( constantValueAttribute.getClass().getSimpleName() );
		headerBuilder.append( " " );
		ValueConstant<?> valueConstant = constantValueAttribute.valueConstant;
		appendValueConstant( valueConstant, headerBuilder );
		return Twig.leaf( headerBuilder.toString() );
	}

	private static Twig twigFromNestMembersAttribute( NestMembersAttribute nestMembersAttribute )
	{
		return Twig.array( nestMembersAttribute.getClass().getSimpleName(), //
			nestMembersAttribute.members().stream().map( t -> Twig.leaf( t.typeName() ) ).toList() );
	}

	private static Twig twigFromNestHostAttribute( NestHostAttribute nestHostAttribute )
	{
		return Twig.leaf( nestHostAttribute.getClass().getSimpleName() + " " + nestHostAttribute.hostClass().typeName() );
	}

	private static Twig twigFromLocalVariableTypeTableAttribute( Labeler labeler, LocalVariableTypeTableAttribute localVariableTypeTableAttribute )
	{
		return Twig.array( localVariableTypeTableAttribute.getClass().getSimpleName(), //
			localVariableTypeTableAttribute.localVariableTypes.stream().map( e -> twigFromLocalVariableTypeTableEntry( e, labeler ) ).toList() );
	}

	private static Twig twigFromLocalVariableTableAttribute( Labeler labeler, LocalVariableTableAttribute localVariableTableAttribute )
	{
		return Twig.array( localVariableTableAttribute.getClass().getSimpleName(), //
			localVariableTableAttribute.entrys.stream().map( e -> twigFromLocalVariableTableEntry( e, labeler ) ).toList() );
	}

	private static Twig twigFromLineNumberTableAttribute( Labeler labeler, LineNumberTableAttribute lineNumberTableAttribute )
	{
		return Twig.array( lineNumberTableAttribute.getClass().getSimpleName(), //
			lineNumberTableAttribute.entrys.stream().map( n -> twigFromLineNumberTableEntry( n, labeler ) ).toList() );
	}

	private static Twig twigFromStackMapTableAttribute( Labeler labeler, StackMapTableAttribute stackMapTableAttribute )
	{
		List<Twig> twigs = new ArrayList<>();
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( StackMapFrame frame : stackMapTableAttribute.frames() )
		{
			twigs.add( twigFromStackMapFrame( labeler, previousFrame, frame ) );
			previousFrame = Optional.of( frame );
		}
		return Twig.array( stackMapTableAttribute.getClass().getSimpleName(), //
			twigs );
	}

	private static Twig twigFromStackMapFrame( Labeler labeler, Optional<StackMapFrame> previousFrame, StackMapFrame frame )
	{
		if( frame instanceof ChopStackMapFrame ) //TODO use switch
			return twigFromChopStackMapFrame( frame.asChopStackMapFrame(), previousFrame, labeler );
		else if( frame instanceof FullStackMapFrame )
			return twigFromFullStackMapFrame( frame.asFullStackMapFrame(), previousFrame, labeler );
		else if( frame instanceof AppendStackMapFrame )
			return twigFromAppendStackMapFrame( frame.asAppendStackMapFrame(), previousFrame, labeler );
		else if( frame instanceof SameStackMapFrame )
			return twigFromSameStackMapFrame( frame.asSameStackMapFrame(), previousFrame, labeler );
		else if( frame instanceof SameLocals1StackItemStackMapFrame )
			return twigFromSameLocals1StackItemStackMapFrame( frame.asSameLocals1StackItemStackMapFrame(), previousFrame, labeler );
		else
			throw new AssertionError();
	}

	private static Twig twigFromAnnotationsAttribute( AnnotationsAttribute annotationsAttribute )
	{
		return Twig.array( annotationsAttribute.getClass().getSimpleName(), //
			annotationsAttribute.annotations.stream().map( a -> twigFromAnnotation( a ) ).toList() );
	}

	private static Twig twigFromAnnotationDefaultAttribute( AnnotationDefaultAttribute annotationDefaultAttribute )
	{
		return Twig.group( annotationDefaultAttribute.getClass().getSimpleName(), //
			Map.entry( "value", twigFromAnnotationValue( annotationDefaultAttribute.annotationValue() ) ) );
	}

	private static Twig twigFromMethodParametersAttribute( MethodParametersAttribute methodParametersAttribute )
	{
		return Twig.array( methodParametersAttribute.getClass().getSimpleName(), //
			methodParametersAttribute.methodParameters.stream().map( ByteCodePrinter::twigFromMethodParameter ).toList() );
	}

	private static Twig twigFromBootstrapMethodsAttribute( BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		return Twig.array( bootstrapMethodsAttribute.getClass().getSimpleName(), //
			bootstrapMethodsAttribute.bootstrapMethods.stream().map( bootstrapMethod -> twigFromBootstrapMethod( "", bootstrapMethod ) ).toList() );
	}

	private static Twig twigFromUnknownAttribute( UnknownAttribute unknownAttribute )
	{
		return Twig.group( unknownAttribute.getClass().getSimpleName(), //
			Map.entry( "content", Twig.leaf( unknownAttribute.buffer().length() + " bytes" ) ) );
	}

	private static Twig twigFromExceptionInfos( Collection<ExceptionInfo> exceptionInfos, Labeler labeler )
	{
		return Twig.array( exceptionInfos.stream().map( e -> twigFromExceptionInfo( e, labeler ) ).toList() );
	}

	private static Twig twigFromBootstrapMethod( String prefix, BootstrapMethod bootstrapMethod )
	{
		var builder = new StringBuilder();
		//builder.append( prefix );
		builder.append( bootstrapMethod.getClass().getSimpleName() );
		builder.append( " methodHandle = " );
		appendMethodHandleConstant( bootstrapMethod.methodHandleConstant, builder );
		List<Constant> argumentConstants = bootstrapMethod.argumentConstants;
		builder.append( " arguments: " );
		boolean first = true;
		for( Constant argumentConstant : argumentConstants )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			switch( argumentConstant.tag )
			{
				case Constant.tag_String -> appendStringConstant( argumentConstant.asStringConstant(), builder );
				case Constant.tag_MethodType -> appendMethodTypeConstant( argumentConstant.asMethodTypeConstant(), builder );
				case Constant.tag_MethodHandle -> appendMethodHandleConstant( argumentConstant.asMethodHandleConstant(), builder );
				case Constant.tag_Class -> appendClassConstant( argumentConstant.asClassConstant(), builder );
				default -> throw new AssertionError( argumentConstant );
			}
		}
		return Twig.leaf( builder.toString() );
	}

	private static Twig twigFromAnnotationAnnotationValue( AnnotationAnnotationValue annotationAnnotationValue )
	{
		return Twig.array( annotationAnnotationValue.getClass().getSimpleName() + //
				" type = " + annotationAnnotationValue.annotation.typeDescriptor().typeName(), //
			annotationAnnotationValue.annotation.parameters.stream().map( a -> twigFromAnnotationParameter( a ) ).toList(), "parameters" );
	}

	private static Twig twigFromAnnotation( Annotation byteCodeAnnotation )
	{
		return Twig.array( byteCodeAnnotation.getClass().getSimpleName() + //
				" type = " + byteCodeAnnotation.typeDescriptor().typeName(), //
			byteCodeAnnotation.parameters.stream().map( a -> twigFromAnnotationParameter( a ) ).toList(), "parameters" );
	}

	private Twig twigFromByteCodeField( ByteCodeField byteCodeField )
	{
		return Twig.group( byteCodeField.getClass().getSimpleName() + //
				" accessFlags = " + stringFromFlags( byteCodeField.modifiers ) + //
				", prototype = " + byteCodeField.prototype().asString(), //
			Map.entry( "attributeSet", //
				twigFromAttributeSet( byteCodeField.attributeSet, Optional.empty() ) ) );
	}

	private Twig twigFromByteCodeMethod( ByteCodeMethod byteCodeMethod )
	{
		return Twig.group( byteCodeMethod.getClass().getSimpleName() + //
			" accessFlags = " + stringFromFlags( byteCodeMethod.modifiers ) + //
			", prototype = " + byteCodeMethod.prototype().asString(),
			Map.entry( "attributes", twigFromAttributeSet( byteCodeMethod.attributeSet, Optional.empty() ) ) );
	}

	private static void appendMethodDescriptor( StringBuilder builder, MethodDescriptor descriptor )
	{
		builder.append( descriptor.returnTypeDescriptor.typeName() );
		List<TypeDescriptor> parameterDescriptors = descriptor.parameterTypeDescriptors;
		builder.append( parameterDescriptors.isEmpty() ? "(" : "( " );
		boolean first = true;
		for( TypeDescriptor parameterDescriptor : parameterDescriptors )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			builder.append( parameterDescriptor.typeName() );
		}
		builder.append( parameterDescriptors.isEmpty() ? ")" : " )" );
	}

	private Twig twigFromByteCodeType( ByteCodeType byteCodeType )
	{
		StringBuilder builder = new StringBuilder( 1024 );
		builder.append( ByteCodeType.class.getSimpleName() );
		builder.append( " version = " ).append( byteCodeType.version.major() ).append( '.' ).append( byteCodeType.version.minor() );
		builder.append( ", accessFlags = " ).append( stringFromFlags( byteCodeType.modifiers ) );
		builder.append( ", this = " ).append( byteCodeType.typeDescriptor().typeName() );
		builder.append( ", super = " ).append( byteCodeType.superTypeDescriptor().map( d -> d.typeName() ).orElse( "<none>" ) );
		return Twig.group( builder.toString(), //
			Map.entry( "interfaces", twigFromTerminalTypeDescriptors( byteCodeType.interfaces(), false ) ), //
			Map.entry( "extraTypes", twigFromTerminalTypeDescriptors( byteCodeType.extraTypes(), true ) ), //
			Map.entry( "fields", twigFromByteCodeFields( byteCodeType.fields ) ), //
			Map.entry( "methods", twigFromByteCodeMethods( byteCodeType.methods ) ), //
			Map.entry( "attributeSet", twigFromAttributeSet( byteCodeType.attributeSet, Optional.empty() ) ) );
	}

	private static Twig twigFromTerminalTypeDescriptors( Collection<TerminalTypeDescriptor> extraTypes, boolean sort )
	{
		return Twig.array( extraTypes.stream() //
			.sorted( sort ? Comparator.comparing( TerminalTypeDescriptor::typeName ) : ( a, b ) -> 0 ) //
			.map( t -> Twig.leaf( t.typeName() ) ) //
			.toList() );
	}

	private Twig twigFromByteCodeFields( Collection<ByteCodeField> fields )
	{
		return Twig.array( fields.stream().map( byteCodeField -> twigFromByteCodeField( byteCodeField ) ).toList() );
	}

	private Twig twigFromByteCodeMethods( Collection<ByteCodeMethod> methods )
	{
		return Twig.array( methods.stream().map( byteCodeMethod -> twigFromByteCodeMethod( byteCodeMethod ) ).toList() );
	}

	private static Twig twigFromExceptionInfo( ExceptionInfo exceptionInfo, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( exceptionInfo.getClass().getSimpleName() );
		builder.append( " start = " );
		summarizeAbsoluteInstruction( exceptionInfo.startInstruction, builder, labeler );
		builder.append( ", end = " );
		summarizeAbsoluteInstruction( exceptionInfo.endInstruction, builder, labeler );
		builder.append( ", handler = " );
		summarizeAbsoluteInstruction( exceptionInfo.handlerInstruction, builder, labeler );
		exceptionInfo.catchType().ifPresent( t -> builder.append( ", catchType = " ).append( t.typeName() ) );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromLineNumberTableEntry( LineNumberTableEntry lineNumberTableEntry, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( lineNumberTableEntry.getClass().getSimpleName() );
		builder.append( " lineNumber = " ).append( lineNumberTableEntry.lineNumber() );
		builder.append( ", start = " );
		summarizeAbsoluteInstruction( lineNumberTableEntry.instruction(), builder, labeler );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromLocalVariableTableEntry( LocalVariableTableEntry localVariableTableEntry, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( localVariableTableEntry.getClass().getSimpleName() );
		builder.append( " index = " ).append( localVariableTableEntry.index );
		builder.append( ", start = " );
		summarizeAbsoluteInstruction( localVariableTableEntry.startInstruction, builder, labeler );
		builder.append( ", end = " );
		summarizeAbsoluteInstruction( localVariableTableEntry.endInstruction, builder, labeler );
		builder.append( ", prototype = " ).append( localVariableTableEntry.prototype().asString() );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromLocalVariableTypeTableEntry( LocalVariableTypeTableEntry localVariableTypeTableEntry, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( localVariableTypeTableEntry.getClass().getSimpleName() );
		builder.append( " index = " ).append( localVariableTypeTableEntry.index );
		builder.append( ", start = " );
		summarizeAbsoluteInstruction( localVariableTypeTableEntry.startInstruction, builder, labeler );
		builder.append( ", end = " );
		summarizeAbsoluteInstruction( localVariableTypeTableEntry.endInstruction, builder, labeler );
		builder.append( ", name = " ).append( localVariableTypeTableEntry.nameConstant );
		builder.append( ", signature = " ).append( localVariableTypeTableEntry.signatureConstant );
		builder.append( " (" );
		builder.append( localVariableTypeTableEntry.nameConstant.stringValue() );
		builder.append( ' ' );
		builder.append( localVariableTypeTableEntry.signatureConstant.stringValue() );
		builder.append( ")" );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromMethodParameter( MethodParameter methodParameter )
	{
		return Twig.leaf( methodParameter.getClass().getSimpleName() + " accessFlags = " + stringFromFlags( methodParameter.modifiers ) + ", name = \"" + methodParameter.name() + "\"" );
	}

	private static Twig twigFromParameterAnnotationSet( ParameterAnnotationSet parameterAnnotationSet )
	{
		return Twig.array( parameterAnnotationSet.getClass().getSimpleName(), //
			parameterAnnotationSet.annotations.stream().map( a -> twigFromAnnotation( a ) ).toList(), "annotations" );
	}

	private static void appendRelativeInstructionReference( Instruction instruction, StringBuilder builder, Labeler labeler )
	{
		String label = labeler.getLabel( Optional.of( instruction ) );
		builder.append( label );
	}

	private static void appendClassConstant( ClassConstant classConstant, StringBuilder builder )
	{
		builder.append( classConstant.typeDescriptor().typeName() );
	}

	private static void appendMethodHandleConstant( MethodHandleConstant methodHandleConstant, StringBuilder builder )
	{
		builder.append( methodHandleConstant.getClass().getSimpleName() );
		builder.append( " referenceKind = " ).append( methodHandleConstant.referenceKind().name() );
		builder.append( ", referenceConstant = " );
		ReferenceConstant referenceConstant = methodHandleConstant.getReferenceConstant();
		switch( referenceConstant.tag )
		{
			case Constant.tag_FieldReference -> summarizeFieldReferenceConstant( referenceConstant.asFieldReferenceConstant(), builder );
			case Constant.tag_InterfaceMethodReference, Constant.tag_PlainMethodReference -> appendMethodReferenceConstant( referenceConstant.asMethodReferenceConstant(), builder );
			default -> throw new AssertionError( referenceConstant );
		}
	}

	private static void summarizeFieldReferenceConstant( FieldReferenceConstant fieldReferenceConstant, StringBuilder builder )
	{
		builder.append( fieldReferenceConstant.fieldDescriptor().asString() );
		builder.append( ' ' );
		builder.append( fieldReferenceConstant.declaringTypeDescriptor().typeName() );
		builder.append( '.' );
		builder.append( fieldReferenceConstant.fieldName() );
	}

	private static void appendMethodReferenceConstant( MethodReferenceConstant methodReferenceConstant, StringBuilder builder )
	{
		builder.append( "reference = " ).append( methodReferenceConstant.methodReference().asString() );
	}

	private static void appendMethodTypeConstant( MethodTypeConstant methodTypeConstant, StringBuilder builder )
	{
		builder.append( MethodTypeConstant.class.getSimpleName() );
		builder.append( " " );
		appendMutf8Constant( methodTypeConstant.getDescriptorConstant(), builder );
	}

	private static Twig twigFromAnnotationValue( AnnotationValue annotationValue )
	{
		return switch( annotationValue.tag )
			{
				case AnnotationValue.tagByte, AnnotationValue.tagBoolean, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger, AnnotationValue.tagLong, //
					AnnotationValue.tagShort, AnnotationValue.tagString -> twigFromConstAnnotationValue( annotationValue.asConstAnnotationValue() );
				case AnnotationValue.tagClass -> twigFromClassAnnotationValue( annotationValue.asClassAnnotationValue() );
				case AnnotationValue.tagEnum -> twigFromEnumAnnotationValue( annotationValue.asEnumAnnotationValue() );
				case AnnotationValue.tagAnnotation -> twigFromAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue() );
				case AnnotationValue.tagArray -> twigFromArrayAnnotationValue( annotationValue.asArrayAnnotationValue() );
				default -> throw new AssertionError( annotationValue );
			};
	}

	private static Twig twigFromConstAnnotationValue( ConstAnnotationValue constAnnotationValue )
	{
		return Twig.leaf( constAnnotationValue.getClass().getSimpleName() + //
			" tag = " + AnnotationValue.tagName( constAnnotationValue.tag ) + //
			" value = " + stringFromValueConstant( constAnnotationValue.valueConstant, constAnnotationValue.tag ) );
	}

	private static String stringFromValueConstant( ValueConstant<?> valueConstant, int annotationValueTag )
	{
		StringBuilder builder = new StringBuilder();
		switch( annotationValueTag )
		{
			case AnnotationValue.tagByte -> appendIntegerConstant( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Byte );
			case AnnotationValue.tagBoolean -> appendIntegerConstant( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Boolean );
			case AnnotationValue.tagCharacter -> appendIntegerConstant( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Character );
			case AnnotationValue.tagDouble -> appendDoubleConstant( valueConstant.asDoubleConstant(), builder );
			case AnnotationValue.tagFloat -> appendFloatConstant( valueConstant.asFloatConstant(), builder );
			case AnnotationValue.tagInteger -> appendIntegerConstant( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Integer );
			case AnnotationValue.tagLong -> appendLongConstant( valueConstant.asLongConstant(), builder );
			case AnnotationValue.tagShort -> appendIntegerConstant( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Short );
			case AnnotationValue.tagString -> appendMutf8Constant( valueConstant.asMutf8Constant(), builder );
			default -> throw new AssertionError( annotationValueTag );
		}
		return builder.toString();
	}

	private static Twig twigFromClassAnnotationValue( ClassAnnotationValue classAnnotationValue )
	{
		return Twig.leaf( classAnnotationValue.getClass().getSimpleName() + //
			" name = " + classAnnotationValue.typeDescriptor().typeName() );
	}

	private static Twig twigFromEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue )
	{
		return Twig.leaf( enumAnnotationValue.getClass().getSimpleName() + //
			" type = " + enumAnnotationValue.typeDescriptor().typeName() + //
			", value = " + enumAnnotationValue.valueName() );
	}

	private static Twig twigFromArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue )
	{
		return Twig.array( arrayAnnotationValue.getClass().getSimpleName(), //
			arrayAnnotationValue.annotationValues.stream().map( a -> twigFromAnnotationValue( a ) ).toList(), "values" );
	}

	private static Twig twigFromTypeAnnotation( TypeAnnotation typeAnnotation )
	{
		return Twig.group( TypeAnnotation.class.getSimpleName(), //
			Map.entry( "typePath", twigFromTypePath( typeAnnotation.typePath ) ), //
			Map.entry( "typeIndex", Twig.leaf( "" + typeAnnotation.typeIndex ) ), //
			Map.entry( "target", twigFromTarget( typeAnnotation.target ) ), //
			Map.entry( "parameters", Twig.array( typeAnnotation.parameters.stream().map( a -> twigFromAnnotationParameter( a ) ).toList() ) ) );
	}

	private static Twig twigFromTypePath( TypePath typePath )
	{
		return Twig.array( typePath.getClass().getSimpleName(), //
			typePath.entries().stream().map( e -> twigFromTypePathEntry( e ) ).toList() );
	}

	private static Twig twigFromTypePathEntry( TypePathEntry entry )
	{
		return Twig.leaf( TypePathEntry.class.getSimpleName() + //
			" pathKind = " + entry.pathKind() + //
			", argumentIndex = " + entry.argumentIndex() );
	}

	private static Twig twigFromTarget( Target target )
	{
		return switch( target.tag )
			{
				case Target.tag_ClassTypeParameter, Target.tag_MethodTypeParameter -> twigFromTypeParameterTarget( target.asTypeParameterTarget() );
				case Target.tag_Supertype -> twigFromSupertypeTarget( target.asSupertypeTarget() );
				case Target.tag_ClassTypeBound, Target.tag_MethodTypeBound -> twigFromTypeParameterBoundTarget( target.asTypeParameterBoundTarget() );
				case Target.tag_FieldType, Target.tag_ReturnType, Target.tag_ReceiverType -> twigFromEmptyTarget( target.asEmptyTarget() );
				case Target.tag_FormalParameter -> twigFromFormalParameterTarget( target.asFormalParameterTarget() );
				case Target.tag_Throws -> twigFromThrowsTarget( target.asThrowsTarget() );
				case Target.tag_LocalVariable, Target.tag_ResourceLocalVariable -> twigFromLocalVariableTarget( target.asLocalVariableTarget() );
				case Target.tag_Catch -> twigFromCatchTarget( target.asCatchTarget() );
				case Target.tag_InstanceOfOffset, Target.tag_NewExpressionOffset, Target.tag_NewMethodOffset, Target.tag_IdentifierMethodOffset -> //
					twigFromOffsetTarget( target.asOffsetTarget() );
				case Target.tag_CastArgument, Target.tag_ConstructorArgument, Target.tag_MethodArgument, Target.tag_NewMethodArgument, //
					Target.tag_IdentifierMethodArgument -> twigFromTypeArgumentTarget( target.asTypeArgumentTarget() );
				default -> throw new AssertionError( target );
			};
	}

	private static Twig twigFromTypeArgumentTarget( TypeArgumentTarget typeArgumentTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( typeArgumentTarget.tag ) );
		builder.append( " offset = " ).append( typeArgumentTarget.offset );
		builder.append( " , typeArgumentIndex = " ).append( typeArgumentTarget.typeArgumentIndex );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromOffsetTarget( OffsetTarget offsetTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( offsetTarget.tag ) );
		builder.append( " offset = " ).append( offsetTarget.offset );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromCatchTarget( CatchTarget catchTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( catchTarget.tag ) );
		builder.append( " exceptionTableIndex = " ).append( catchTarget.exceptionTableIndex );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromLocalVariableTarget( LocalVariableTarget localVariableTarget )
	{
		return Twig.array( localVariableTarget.getClass().getSimpleName() + " tag = " + Target.tagName( localVariableTarget.tag ), //
			localVariableTarget.entries.stream().map( e -> twigFromLocalVariableTargetEntry( e ) ).toList() );
	}

	private static Twig twigFromLocalVariableTargetEntry( LocalVariableTargetEntry localVariableTargetEntry )
	{
		var builder = new StringBuilder();
		builder.append( localVariableTargetEntry.getClass().getSimpleName() );
		builder.append( " start = " ).append( localVariableTargetEntry.startPc() );
		builder.append( ", length = " ).append( localVariableTargetEntry.length() );
		builder.append( ", index = " ).append( localVariableTargetEntry.index() );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromThrowsTarget( ThrowsTarget throwsTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( throwsTarget.tag ) );
		builder.append( " throwsTypeIndex = " ).append( throwsTarget.throwsTypeIndex );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromFormalParameterTarget( FormalParameterTarget formalParameterTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( formalParameterTarget.tag ) );
		builder.append( " formalParameterIndex = " ).append( formalParameterTarget.formalParameterIndex );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromTypeParameterBoundTarget( TypeParameterBoundTarget typeParameterBoundTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( typeParameterBoundTarget.tag ) );
		builder.append( " typeParameterIndex = " ).append( typeParameterBoundTarget.typeParameterIndex );
		builder.append( ", boundIndex = " ).append( typeParameterBoundTarget.boundIndex );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromEmptyTarget( EmptyTarget emptyTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( emptyTarget.tag ) );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromSupertypeTarget( SupertypeTarget supertypeTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( supertypeTarget.tag ) );
		builder.append( " superTypeIndex = " ).append( supertypeTarget.supertypeIndex );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromTypeParameterTarget( TypeParameterTarget typeParameterTarget )
	{
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( typeParameterTarget.tag ) );
		builder.append( " typeParameterIndex = " ).append( typeParameterTarget.typeParameterIndex );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromBranchInstruction( String prefix, BranchInstruction branchInstruction, Labeler labeler )
	{
		var builder = new StringBuilder();
		appendRelativeInstructionReference( branchInstruction.getTargetInstruction(), builder, labeler );
		return Twig.leaf( buildInstructionHeader( prefix, branchInstruction.opCode, builder.toString() ) );
	}

	private static Twig twigFromConditionalBranchInstruction( String prefix, ConditionalBranchInstruction conditionalBranchInstruction, Labeler labeler )
	{
		return Twig.leaf( buildInstructionHeader( prefix, conditionalBranchInstruction.getOpCode(), labeler.getLabel( conditionalBranchInstruction.getTargetInstruction() ) ) );
	}

	private static Twig twigFromClassConstantReferencingInstruction( String prefix, ClassConstantReferencingInstruction classConstantReferencingInstruction )
	{
		String targetName = classConstantReferencingInstruction.target().typeName();
		return Twig.leaf( buildInstructionHeader( prefix, classConstantReferencingInstruction.opCode, targetName ) );
	}

	private static Twig twigFromFieldConstantReferencingInstruction( String prefix, FieldConstantReferencingInstruction fieldConstantReferencingInstruction )
	{
		String text = fieldConstantReferencingInstruction.fieldType().typeName() + " " + fieldConstantReferencingInstruction.fieldDeclaringType().typeName() + "." + fieldConstantReferencingInstruction.fieldName();
		return Twig.leaf( buildInstructionHeader( prefix, fieldConstantReferencingInstruction.opCode, text ) );
	}

	private static Twig twigFromMethodConstantReferencingInstruction( String prefix, MethodConstantReferencingInstruction methodConstantReferencingInstruction )
	{
		var builder = new StringBuilder();
		MethodReferenceConstant constant = methodConstantReferencingInstruction.methodReferenceConstant;
		switch( constant.tag )
		{
			case Constant.tag_InterfaceMethodReference, Constant.tag_PlainMethodReference -> appendMethodReferenceConstant( constant, builder );
			default -> throw new AssertionError( constant );
		}
		return Twig.leaf( buildInstructionHeader( prefix, methodConstantReferencingInstruction.opCode, builder.toString() ) );
	}

	private static Twig twigFromIIncInstruction( String prefix, IIncInstruction iIncInstruction )
	{
		return Twig.leaf( buildInstructionHeader( prefix, OpCode.IINC, "index = " + iIncInstruction.index, "delta = " + iIncInstruction.delta ) );
	}

//	private static Twig twigFromImmediateLoadConstantInstruction( String prefix, ImmediateLoadConstantInstruction immediateLoadConstantInstruction )
//	{
//		return Twig.leaf( buildInstructionHeader( prefix, immediateLoadConstantInstruction.opCode, "immediateValue = " + immediateLoadConstantInstruction.immediateValue ) );
//	}
//
//	private static Twig twigFromIndirectLoadConstantInstruction( String prefix, IndirectLoadConstantInstruction indirectLoadConstantInstruction )
//	{
//		return Twig.leaf( buildInstructionHeader( prefix, indirectLoadConstantInstruction.opCode, stringFromConstant( indirectLoadConstantInstruction.constant ) ) );
//	}

	private static Twig twigFromLoadConstantInstruction( String prefix, LoadConstantInstruction loadConstantInstruction )
	{
		String valueAsString = switch( loadConstantInstruction.constant.tag )
			{
				case Constant.tag_Integer -> String.valueOf( loadConstantInstruction.constant.asIntegerConstant().value );
				case Constant.tag_Float -> String.valueOf( loadConstantInstruction.constant.asFloatConstant().value );
				case Constant.tag_Long -> String.valueOf( loadConstantInstruction.constant.asLongConstant().value );
				case Constant.tag_Double -> String.valueOf( loadConstantInstruction.constant.asDoubleConstant().value );
				case Constant.tag_String -> Kit.string.escapeForJava( loadConstantInstruction.constant.asStringConstant().getValueConstant().stringValue() );
				case Constant.tag_Class -> loadConstantInstruction.constant.asClassConstant().typeDescriptor().typeName();
				default -> throw new AssertionError( loadConstantInstruction.constant );
			};
		return Twig.leaf( buildInstructionHeader( prefix, LoadConstantInstruction.opCode, valueAsString ) );
	}

	private static Twig twigFromInvokeDynamicInstruction( String prefix, InvokeDynamicInstruction invokeDynamicInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "prototype = " ).append( invokeDynamicInstruction.methodPrototype().asString() );
		return Twig.group( buildInstructionHeader( prefix, OpCode.INVOKEDYNAMIC, builder.toString() ), //
			Map.entry( indentation + indentation + "bootstrapMethod", twigFromBootstrapMethod( indentation + indentation, invokeDynamicInstruction.invokeDynamicConstant.getBootstrapMethod() ) ) );
	}

	private static Twig twigFromInvokeInterfaceInstruction( String prefix, InvokeInterfaceInstruction invokeInterfaceInstruction )
	{
		var builder = new StringBuilder();
		appendMethodReferenceConstant( invokeInterfaceInstruction.methodReferenceConstant, builder );
		return Twig.leaf( buildInstructionHeader( prefix, OpCode.INVOKEINTERFACE, "interfaceMethodReferenceConstant = " + builder.toString(), invokeInterfaceInstruction.argumentCount + " arguments" ) );
	}

	private static Twig twigFromLocalVariableInstruction( String prefix, LocalVariableInstruction localVariableInstruction )
	{
		String[] arguments = localVariableInstruction.index <= 3 && localVariableInstruction.getOpCode() != OpCode.RET ? new String[] { "" + localVariableInstruction.index } : Kit.ARRAY_OF_ZERO_STRINGS;
		return Twig.leaf( buildInstructionHeader( prefix, localVariableInstruction.getOpCode(), arguments ) );
	}

	private static Twig twigFromLookupSwitchInstruction( String prefix, LookupSwitchInstruction lookupSwitchInstruction, Labeler labeler )
	{
		return Twig.array( buildInstructionHeader( prefix, OpCode.LOOKUPSWITCH ) + //
				" default = " + labeler.getLabel( lookupSwitchInstruction.getDefaultInstruction() ), //
			lookupSwitchInstruction.entries.stream().map( e -> twigFromLookupSwitchEntry( e, labeler ) ).toList() );
	}

	private static Twig twigFromLookupSwitchEntry( LookupSwitchEntry lookupSwitchEntry, Labeler labeler )
	{
		return Twig.leaf( indentation + lookupSwitchEntry.getClass().getSimpleName() + " value = " + lookupSwitchEntry.value() + ", target = " + labeler.getLabel( lookupSwitchEntry.getTargetInstruction() ) );
	}

	private static Twig twigFromMultiANewArrayInstruction( String prefix, MultiANewArrayInstruction multiANewArrayInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "class = " ).append( multiANewArrayInstruction.targetType().typeName() );
		//appendClassConstant( multiANewArrayInstruction.targetClassConstant, builder );
		return Twig.leaf( buildInstructionHeader( prefix, OpCode.MULTIANEWARRAY, builder.toString(), multiANewArrayInstruction.dimensionCount + " dimensions" ) );
	}

	private static Twig twigFromNewPrimitiveArrayInstruction( String prefix, NewPrimitiveArrayInstruction newPrimitiveArrayInstruction )
	{
		return Twig.leaf( buildInstructionHeader( prefix, OpCode.NEWARRAY, "type = " + NewPrimitiveArrayInstruction.Type.fromNumber( newPrimitiveArrayInstruction.type ).name() ) );
	}

	private static Twig twigFromOperandlessInstruction( String prefix, OperandlessInstruction operandlessInstruction )
	{
		return Twig.leaf( buildInstructionHeader( prefix, operandlessInstruction.opCode ) );
	}

//	private static Twig twigFromOperandlessLoadConstantInstruction( String prefix, OperandlessLoadConstantInstruction operandlessLoadConstantInstruction )
//	{
//		return Twig.leaf( buildInstructionHeader( prefix, operandlessLoadConstantInstruction.opCode ) );
//	}

	private static Twig twigFromRelativeInstructionReference( String prefix, Instruction instruction, Labeler labeler )
	{
		String label = labeler.getLabel( instruction );
		return Twig.leaf( prefix + label );
	}

	private static String buildInstructionHeader( String prefix, int opCode, String... parts )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( prefix );
		stringBuilder.append( OpCode.getOpCodeName( opCode ) );
		if( parts.length > 0 )
			stringBuilder.append( " " );
		boolean first = true;
		for( String part : parts )
		{
			first = Kit.stringBuilder.appendDelimiter( stringBuilder, first, ", " );
			stringBuilder.append( part );
		}
		return stringBuilder.toString();
	}

	private static Twig twigFromTableSwitchInstruction( String prefix, TableSwitchInstruction tableSwitchInstruction, Labeler labeler )
	{
		return Twig.array( buildInstructionHeader( prefix, OpCode.TABLESWITCH ) + //
				" lowValue = " + tableSwitchInstruction.lowValue + //
				", default = " + labeler.getLabel( tableSwitchInstruction.getDefaultInstruction() ), //
			tableSwitchInstruction.targetInstructions().stream().map( i -> twigFromRelativeInstructionReference( indentation, i, labeler ) ).toList() );
	}

	private static Twig twigFromInstructionList( InstructionList instructionList, Labeler labeler )
	{
		Collection<Instruction> instructions = instructionList.all();
		return Twig.group( instructions.size() + " entries", //
			instructions.stream().flatMap( instruction -> //
				Stream.concat( //
					labeler.tryGetLabelInfo( instruction ) //
						.filter( labelInfo -> labelInfo.source.isPresent() ) //
						.map( labelInfo -> Map.entry( labelInfo.label, Twig.leaf( "// " + labelInfo.source.get() ) ) ) //
						.stream(), //
					Stream.of( Map.entry( "", twigFromInstruction( instruction, labeler ) ) ) //
				) ).toList() );
	}

	private static final String indentation = "        ";

	private static Twig twigFromInstruction( Instruction instruction, Labeler labeler )
	{
		Optional<LabelInfo> labelInfo = labeler.tryGetLabelInfo( instruction );
		String prefix = labelInfo.filter( l -> l.source.isEmpty() ).map( l -> l.label + ":" + indentation.substring( l.label.length() + 1 ) ).orElse( indentation );
		Twig twig = switch( instruction.groupTag )
			{
				case Instruction.groupTag_TableSwitch -> twigFromTableSwitchInstruction( prefix, instruction.asTableSwitchInstruction(), labeler );
				case Instruction.groupTag_ConditionalBranch -> twigFromConditionalBranchInstruction( prefix, instruction.asConditionalBranchInstruction(), labeler );
				case Instruction.groupTag_ClassConstantReferencing -> twigFromClassConstantReferencingInstruction( prefix, instruction.asClassConstantReferencingInstruction() );
				case Instruction.groupTag_FieldConstantReferencing -> twigFromFieldConstantReferencingInstruction( prefix, instruction.asFieldConstantReferencingInstruction() );
				case Instruction.groupTag_MethodConstantReferencing -> twigFromMethodConstantReferencingInstruction( prefix, instruction.asMethodConstantReferencingInstruction() );
				case Instruction.groupTag_IInc -> twigFromIIncInstruction( prefix, instruction.asIIncInstruction() );
//				case Instruction.groupTag_ImmediateLoadConstant -> twigFromImmediateLoadConstantInstruction( prefix, instruction.asImmediateLoadConstantInstruction() );
//				case Instruction.groupTag_IndirectLoadConstant -> twigFromIndirectLoadConstantInstruction( prefix, instruction.asIndirectLoadConstantInstruction() );
				case Instruction.groupTag_InvokeDynamic -> twigFromInvokeDynamicInstruction( prefix, instruction.asInvokeDynamicInstruction() );
				case Instruction.groupTag_InvokeInterface -> twigFromInvokeInterfaceInstruction( prefix, instruction.asInvokeInterfaceInstruction() );
				case Instruction.groupTag_LocalVariable -> twigFromLocalVariableInstruction( prefix, instruction.asLocalVariableInstruction() );
				case Instruction.groupTag_LookupSwitch -> twigFromLookupSwitchInstruction( prefix, instruction.asLookupSwitchInstruction(), labeler );
				case Instruction.groupTag_MultiANewArray -> twigFromMultiANewArrayInstruction( prefix, instruction.asMultiANewArrayInstruction() );
				case Instruction.groupTag_NewPrimitiveArray -> twigFromNewPrimitiveArrayInstruction( prefix, instruction.asNewPrimitiveArrayInstruction() );
				case Instruction.groupTag_Operandless -> twigFromOperandlessInstruction( prefix, instruction.asOperandlessInstruction() );
//				case Instruction.groupTag_OperandlessLoadConstant -> twigFromOperandlessLoadConstantInstruction( prefix, instruction.asOperandlessLoadConstantInstruction() );
				case Instruction.groupTag_LoadConstant -> twigFromLoadConstantInstruction( prefix, instruction.asLoadConstantInstruction() );
				case Instruction.groupTag_Branch -> twigFromBranchInstruction( prefix, instruction.asBranchInstruction(), labeler );
				default -> throw new AssertionError( instruction );
			};
		return twig;
	}

	private static Twig twigFromAppendStackMapFrame( AppendStackMapFrame appendFrame, Optional<StackMapFrame> previousFrame, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( appendFrame.getName( previousFrame ) ).append( ' ' );
		String label = labeler.getLabel( Optional.of( appendFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.group( header, //
			Map.entry( "localVerificationTypes", twigFromVerificationTypes( appendFrame.localVerificationTypes(), labeler ) ) );
	}

	private static Twig twigFromChopStackMapFrame( ChopStackMapFrame chopFrame, Optional<StackMapFrame> previousFrame, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( chopFrame.getName( previousFrame ) ).append( ' ' );
		String label = labeler.getLabel( Optional.of( chopFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		builder.append( ", count = " ).append( chopFrame.count() );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromFullStackMapFrame( FullStackMapFrame fullFrame, Optional<StackMapFrame> previousFrame, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( fullFrame.getName( previousFrame ) ).append( ' ' );
		String label = labeler.getLabel( Optional.of( fullFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.group( header, //
			Map.entry( "localVerificationTypes", twigFromVerificationTypes( fullFrame.localVerificationTypes(), labeler ) ), //
			Map.entry( "stackVerificationTypes", twigFromVerificationTypes( fullFrame.stackVerificationTypes(), labeler ) ) );
	}

	private static Twig twigFromVerificationTypes( Collection<VerificationType> verificationTypes, Labeler labeler )
	{
		return Twig.array( verificationTypes.stream().map( t -> twigFromVerificationType( t, labeler ) ).toList() );
	}

	private static Twig twigFromSameLocals1StackItemStackMapFrame( SameLocals1StackItemStackMapFrame sameLocals1StackItemFrame, Optional<StackMapFrame> previousFrame, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( sameLocals1StackItemFrame.getName( previousFrame ) ).append( ' ' );
		String label = labeler.getLabel( Optional.of( sameLocals1StackItemFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.group( header, //
			Map.entry( "stackVerificationType", twigFromVerificationType( sameLocals1StackItemFrame.stackVerificationType(), labeler ) ) );
	}

	private static Twig twigFromSameStackMapFrame( SameStackMapFrame sameFrame, Optional<StackMapFrame> previousFrame, Labeler labeler )
	{
		var builder = new StringBuilder();
		builder.append( sameFrame.getName( previousFrame ) ).append( ' ' );
		String label = labeler.getLabel( Optional.of( sameFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromVerificationType( VerificationType verificationType, Labeler labeler )
	{
		return switch( verificationType.tag )
			{
				case VerificationType.tag_Top, VerificationType.tag_Integer, VerificationType.tag_Float, VerificationType.tag_Double, VerificationType.tag_Long, //
					VerificationType.tag_Null, VerificationType.tag_UninitializedThis -> twigFromSimpleVerificationType( verificationType.asSimpleVerificationType() );
				case VerificationType.tag_Object -> twigFromObjectVerificationType( verificationType.asObjectVerificationType() );
				case VerificationType.tag_Uninitialized -> twigFromUninitializedVerificationType( verificationType.asUninitializedVerificationType(), labeler );
				default -> throw new AssertionError( verificationType );
			};
	}

	private static Twig twigFromObjectVerificationType( ObjectVerificationType objectVerificationType )
	{
		return Twig.leaf( objectVerificationType.getClass().getSimpleName() + " type = " + objectVerificationType.typeDescriptor().typeName() );
	}

	private static Twig twigFromSimpleVerificationType( SimpleVerificationType simpleVerificationType )
	{
		return Twig.leaf( simpleVerificationType.getClass().getSimpleName() + " " + VerificationType.tagName( simpleVerificationType.tag ) );
	}

	private static Twig twigFromUninitializedVerificationType( UninitializedVerificationType uninitializedVerificationType, Labeler labeler )
	{
		return Twig.leaf( uninitializedVerificationType.getClass().getSimpleName() + " " + labeler.getLabel( uninitializedVerificationType.instruction ) );
	}
}
