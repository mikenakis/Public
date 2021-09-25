package mikenakis.bytecode.printing;

import mikenakis.bytecode.exceptions.InvalidKnownAttributeTagException;
import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.TypeAnnotation;
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
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.bytecode.printing.twig.Twig;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagEnumSet;

import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodePrinter
{
	public static String printByteCodeType( ByteCodeType byteCodeType, Optional<Path> sourcePath )
	{
		ByteCodePrinter byteCodePrinter = new ByteCodePrinter( byteCodeType, sourcePath );
		Twig rootNode = twigFromByteCodeType( byteCodeType, byteCodePrinter::tryGetLabelInfo );
		var builder = new StringBuilder();
		printTree( rootNode, Twig::children, Twig::text, s -> builder.append( s ).append( '\n' ) );
		return builder.toString();
	}

	private static <T> void printTree( T rootNode, Function<T,Collection<T>> breeder, Function<T,String> stringizer, Consumer<String> emitter )
	{
		String rootString = stringizer.apply( rootNode );
		emitter.accept( rootString );
		printTreeRecursive( rootNode, "", breeder, stringizer, emitter );
	}

	private static final String[][] PREFIXES = { { " ├─ ", " │  " }, { " └─ ", "    " } };

	private static <T> void printTreeRecursive( T node, String parentPrefix, Function<T,Collection<T>> breeder, Function<T,String> stringizer, Consumer<String> emitter )
	{
		for( Iterator<T> iterator = breeder.apply( node ).iterator(); iterator.hasNext(); )
		{
			T childNode = iterator.next();
			String[] prefixes = PREFIXES[iterator.hasNext() ? 0 : 1];
			String payload = stringizer.apply( childNode );
			emitter.accept( parentPrefix + prefixes[0] + payload );
			printTreeRecursive( childNode, parentPrefix + prefixes[1], breeder, stringizer, emitter );
		}
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
	private final Map<Instruction,LabelInfo> labelInfoFromInstructionMap = new HashMap<>();

	private ByteCodePrinter( ByteCodeType byteCodeType, Optional<Path> sourcePath )
	{
		Optional<Path> sourcePathName = sourcePath.map( p -> p.resolve( byteCodeType.tryGetSourceFileName().orElse( "" ) ) );
		sourceLines = sourcePathName.map( p -> Kit.unchecked( () -> Files.readAllLines( p ) ) );
		for( ByteCodeMethod byteCodeMethod : byteCodeType.methods )
			byteCodeMethod.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tagCode ) //
				.map( a -> a.asCodeAttribute() ) //
				.ifPresent( c -> updateLabels( c ) );
	}

	private Optional<LabelInfo> tryGetLabelInfo( Instruction instruction )
	{
		return Kit.map.getOptional( labelInfoFromInstructionMap, instruction );
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

	private void updateLabels( CodeAttribute codeAttribute )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = getLineNumberFromInstructionMap( codeAttribute );
		Collection<Instruction> targets = new HashSet<>();

		Consumer<Optional<Instruction>> targetInstructionConsumer = instruction -> //
			instruction.ifPresent( value -> //
			{
				assert codeAttribute.instructions().contains( value );
				Kit.collection.tryAdd( targets, value );
			} );

		for( Instruction instruction : codeAttribute.instructions().all() )
		{
			switch( instruction.group )
			{
				case Branch:
				{
					BranchInstruction branchInstruction = instruction.asBranchInstruction();
					targetInstructionConsumer.accept( Optional.of( branchInstruction.getTargetInstruction() ) );
					break;
				}
				case ConditionalBranch:
				{
					ConditionalBranchInstruction conditionalBranchInstruction = instruction.asConditionalBranchInstruction();
					targetInstructionConsumer.accept( Optional.of( conditionalBranchInstruction.getTargetInstruction() ) );
					break;
				}
				case ConstantReferencing:
				{
					ConstantReferencingInstruction constantReferencingInstruction = instruction.asConstantReferencingInstruction();
					Kit.get( constantReferencingInstruction ); //nothing to do
					break;
				}
				case IInc:
				{
					IIncInstruction iIncInstruction = instruction.asIIncInstruction();
					Kit.get( iIncInstruction ); //nothing to do
					break;
				}
				case ImmediateLoadConstant:
				{
					ImmediateLoadConstantInstruction immediateLoadConstantInstruction = instruction.asImmediateLoadConstantInstruction();
					Kit.get( immediateLoadConstantInstruction ); //nothing to do
					break;
				}
				case IndirectLoadConstant:
				{
					IndirectLoadConstantInstruction indirectLoadConstantInstruction = instruction.asIndirectLoadConstantInstruction();
					Kit.get( indirectLoadConstantInstruction ); //nothing to do
					break;
				}
				case InvokeDynamic:
				{
					InvokeDynamicInstruction invokeDynamicInstruction = instruction.asInvokeDynamicInstruction();
					Kit.get( invokeDynamicInstruction ); //nothing to do
					break;
				}
				case InvokeInterface:
				{
					InvokeInterfaceInstruction invokeInterfaceInstruction = instruction.asInvokeInterfaceInstruction();
					Kit.get( invokeInterfaceInstruction ); //nothing to do
					break;
				}
				case LocalVariable:
				{
					LocalVariableInstruction localVariableInstruction = instruction.asLocalVariableInstruction();
					Kit.get( localVariableInstruction ); //nothing to do
					break;
				}
				case LookupSwitch:
				{
					LookupSwitchInstruction lookupSwitchInstruction = instruction.asLookupSwitchInstruction();
					targetInstructionConsumer.accept( Optional.of( lookupSwitchInstruction.getDefaultInstruction() ) );
					for( LookupSwitchEntry entry : lookupSwitchInstruction.entries )
						targetInstructionConsumer.accept( Optional.of( entry.getTargetInstruction() ) );
					break;
				}
				case MultiANewArray:
				{
					MultiANewArrayInstruction multiANewArrayInstruction = instruction.asMultiANewArrayInstruction();
					Kit.get( multiANewArrayInstruction ); //nothing to do
					break;
				}
				case NewPrimitiveArray:
				{
					NewPrimitiveArrayInstruction newPrimitiveArrayInstruction = instruction.asNewPrimitiveArrayInstruction();
					Kit.get( newPrimitiveArrayInstruction ); //nothing to do
					break;
				}
				case Operandless:
				{
					OperandlessInstruction operandlessInstruction = instruction.asOperandlessInstruction();
					Kit.get( operandlessInstruction ); //nothing to do
					break;
				}
				case OperandlessLoadConstant:
				{
					OperandlessLoadConstantInstruction operandlessLoadConstantInstruction = instruction.asOperandlessLoadConstantInstruction();
					Kit.get( operandlessLoadConstantInstruction );
					break;
				}
				case TableSwitch:
				{
					TableSwitchInstruction tableSwitchInstruction = instruction.asTableSwitchInstruction();
					targetInstructionConsumer.accept( Optional.of( tableSwitchInstruction.getDefaultInstruction() ) );
					for( Instruction targetInstruction : tableSwitchInstruction.targetInstructions() )
						targetInstructionConsumer.accept( Optional.of( targetInstruction ) );
					break;
				}
			}
		}
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos() )
		{
			targetInstructionConsumer.accept( Optional.of( exceptionInfo.startInstruction ) );
			targetInstructionConsumer.accept( exceptionInfo.endInstruction );
			targetInstructionConsumer.accept( Optional.of( exceptionInfo.handlerInstruction ) );
		}
		for( KnownAttribute knownAttribute : codeAttribute.attributeSet.knownAttributes() )
		{
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tagLocalVariableTable ->//
					{
						LocalVariableTableAttribute localVariableTableAttribute = knownAttribute.asLocalVariableTableAttribute();
						for( LocalVariableTableEntry localVariable : localVariableTableAttribute.entrys )
						{
							targetInstructionConsumer.accept( Optional.of( localVariable.startInstruction ) );
							targetInstructionConsumer.accept( localVariable.endInstruction );
						}
					}
				case KnownAttribute.tagLocalVariableTypeTable -> //
					{
						LocalVariableTypeTableAttribute localVariableTypeTableAttribute = knownAttribute.asLocalVariableTypeTableAttribute();
						for( LocalVariableTypeTableEntry entry : localVariableTypeTableAttribute.localVariableTypes )
						{
							targetInstructionConsumer.accept( Optional.of( entry.startInstruction ) );
							//FIXME how come we are not feeding the endInstruction?
						}
					}
				case KnownAttribute.tagStackMapTable -> //
					{
						StackMapTableAttribute stackMapTableAttribute = knownAttribute.asStackMapTableAttribute();
						for( StackMapFrame frame : stackMapTableAttribute.frames() )
						{
							targetInstructionConsumer.accept( Optional.of( frame.getTargetInstruction() ) );
							if( frame.isSameLocals1StackItemStackMapFrame() )
							{
								SameLocals1StackItemStackMapFrame sameLocals1StackItemStackMapFrame = frame.asSameLocals1StackItemStackMapFrame();
								collectTargetInstructionsFromVerificationType( targetInstructionConsumer, sameLocals1StackItemStackMapFrame.stackVerificationType() );
							}
							else if( frame.isAppendStackMapFrame() )
							{
								AppendStackMapFrame appendStackMapFrame = frame.asAppendStackMapFrame();
								collectTargetInstructionsFromVerificationTypes( targetInstructionConsumer, appendStackMapFrame.localVerificationTypes() );
							}
							else if( frame.isFullStackMapFrame() )
							{
								FullStackMapFrame fullStackMapFrame = frame.asFullStackMapFrame();
								collectTargetInstructionsFromVerificationTypes( targetInstructionConsumer, fullStackMapFrame.localVerificationTypes() );
								collectTargetInstructionsFromVerificationTypes( targetInstructionConsumer, fullStackMapFrame.stackVerificationTypes() );
							}
						}
					}
				case KnownAttribute.tagLineNumberTable -> //
					{
						/* nothing to do */
					}
				default -> { }
			}
		}

		int targetInstructionLabelNumberSeed = 1;
		for( Instruction instruction : codeAttribute.instructions().all() )
		{
			Optional<Integer> lineNumber = Kit.map.getOptional( lineNumberFromInstructionMap, instruction );
			if( lineNumber.isPresent() )
			{
				String label = "L" + lineNumber.get(); //'L' stands for 'Line Number'
				LabelInfo labelInfo = new LabelInfo( label, sourceLines.map( s -> s.get( lineNumber.get() - 1 ).trim() ) );
				Kit.map.add( labelInfoFromInstructionMap, instruction, labelInfo );
			}
			else if( Kit.collection.contains( targets, instruction ) )
			{
				String label = "T" + targetInstructionLabelNumberSeed++; //'T' stands for 'Target'
				LabelInfo labelInfo = new LabelInfo( label, Optional.empty() );
				Kit.map.add( labelInfoFromInstructionMap, instruction, labelInfo );
			}
		}
	}

	private static void collectTargetInstructionsFromVerificationTypes( Consumer<Optional<Instruction>> targetInstructionConsumer, Iterable<VerificationType> verificationTypes )
	{
		for( VerificationType verificationType : verificationTypes )
			collectTargetInstructionsFromVerificationType( targetInstructionConsumer, verificationType );
	}

	private static void collectTargetInstructionsFromVerificationType( Consumer<Optional<Instruction>> targetInstructionConsumer, VerificationType verificationType )
	{
		switch( verificationType.tag )
		{
			case VerificationType.tagTop, VerificationType.tagInteger, VerificationType.tagFloat, VerificationType.tagDouble, VerificationType.tagLong, //
				VerificationType.tagNull, VerificationType.tagUninitializedThis -> //
				{
					SimpleVerificationType simpleVerificationType = verificationType.asSimpleVerificationType();
					Kit.get( simpleVerificationType ); /* nothing to do */
				}
			case VerificationType.tagObject -> //
				{
					ObjectVerificationType objectVerificationType = verificationType.asObjectVerificationType();
					Kit.get( objectVerificationType ); /* nothing to do */
				}
			case VerificationType.tagUninitialized -> //
				{
					UninitializedVerificationType uninitializedVerificationType = verificationType.asUninitializedVerificationType();
					targetInstructionConsumer.accept( Optional.of( uninitializedVerificationType.instruction ) );
				}
			default -> throw new AssertionError( verificationType );
		}
	}

	private static Map<Instruction,Integer> getLineNumberFromInstructionMap( CodeAttribute codeAttribute )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = new HashMap<>();
		codeAttribute.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tagLineNumberTable ) //
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

	private static void valueConstantToStringBuilder( ValueConstant<?> constant, StringBuilder builder )
	{
		switch( constant.tag )
		{
			case Constant.tagMutf8 -> mutf8ConstantToStringBuilder( constant.asMutf8Constant(), builder );
			case Constant.tagInteger -> integerConstantToStringBuilder( constant.asIntegerConstant(), builder, IntegerValueHint.Integer );
			case Constant.tagFloat -> floatConstantToStringBuilder( constant.asFloatConstant(), builder );
			case Constant.tagLong -> longConstantToStringBuilder( constant.asLongConstant(), builder );
			case Constant.tagDouble -> doubleConstantToStringBuilder( constant.asDoubleConstant(), builder );
			case Constant.tagString -> stringConstantToStringBuilder( constant.asStringConstant(), builder );
			default -> throw new AssertionError( constant );
		}
	}

	private static void mutf8ConstantToStringBuilder( Mutf8Constant mutf8Constant, StringBuilder builder )
	{
		builder.append( Mutf8Constant.class.getSimpleName() ).append( "( " );
		Kit.stringBuilder.appendEscapedForJava( builder, mutf8Constant.stringValue(), '"' );
		builder.append( " )" );
	}

	private static void integerConstantToStringBuilder( IntegerConstant integerConstant, StringBuilder builder, IntegerValueHint integerValueHint )
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

	private static void floatConstantToStringBuilder( FloatConstant floatConstant, StringBuilder builder )
	{
		builder.append( FloatConstant.class.getSimpleName() ).append( "( " );
		builder.append( floatConstant.value ).append( 'f' );
		builder.append( " )" );
	}

	private static void longConstantToStringBuilder( LongConstant longConstant, StringBuilder builder )
	{
		builder.append( LongConstant.class.getSimpleName() ).append( "( " );
		builder.append( longConstant.value ).append( 'L' );
		builder.append( " )" );
	}

	private static void doubleConstantToStringBuilder( DoubleConstant doubleConstant, StringBuilder builder )
	{
		builder.append( DoubleConstant.class.getSimpleName() ).append( "( " );
		builder.append( doubleConstant.value );
		builder.append( " )" );
	}

	private static void stringConstantToStringBuilder( StringConstant stringConstant, StringBuilder builder )
	{
		builder.append( StringConstant.class.getSimpleName() ).append( "( " );
		mutf8ConstantToStringBuilder( stringConstant.valueConstant(), builder );
		builder.append( " )" );
	}

	private static <E extends Enum<E>> String stringFromFlags( FlagEnumSet<E> flagSet )
	{
		StringBuilder stringBuilder = new StringBuilder();
		flagsToStringBuilder( flagSet, stringBuilder );
		return stringBuilder.toString();
	}

	private static <E extends Enum<E>> void flagsToStringBuilder( FlagEnumSet<E> flagSet, StringBuilder stringBuilder )
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
		return Twig.group( AnnotationParameter.class.getSimpleName(), //
			Map.entry( "name", Twig.leaf( "\"" + annotationParameter.nameConstant.stringValue() + "\"" ) ), //
			Map.entry( "value", twigFromAnnotationValue( annotationParameter.value ) ) );
	}

	private static Twig twigFromAttributeSet( AttributeSet attributeSet, ByteCodeType byteCodeType, Labeler labeler )
	{
		return Twig.array( attributeSet.allAttributes().stream().map( a -> twigFromAttribute( a, byteCodeType, labeler ) ).toList() );
	}

	private static Twig twigFromAttribute( Attribute attribute, ByteCodeType byteCodeType, Labeler labeler )
	{
		if( !attribute.isKnown() )
		{
			UnknownAttribute unknownAttribute = attribute.asUnknownAttribute();
			return twigFromUnknownAttribute( unknownAttribute );
		}
		KnownAttribute knownAttribute = attribute.asKnownAttribute();
		return switch( knownAttribute.tag )
			{
				case KnownAttribute.tagBootstrapMethods -> twigFromBootstrapMethodsAttribute( knownAttribute.asBootstrapMethodsAttribute() );
				case KnownAttribute.tagMethodParameters -> twigFromMethodParametersAttribute( knownAttribute.asMethodParametersAttribute() );
				case KnownAttribute.tagAnnotationDefault -> twigFromAnnotationDefaultAttribute( knownAttribute.asAnnotationDefaultAttribute() );
				case KnownAttribute.tagRuntimeInvisibleAnnotations, KnownAttribute.tagRuntimeVisibleAnnotations -> twigFromAnnotationsAttribute( knownAttribute.asAnnotationsAttribute() );
				case KnownAttribute.tagStackMapTable -> twigFromStackMapTableAttribute( labeler, knownAttribute.asStackMapTableAttribute() );
				case KnownAttribute.tagLineNumberTable -> twigFromLineNumberTableAttribute( labeler, knownAttribute.asLineNumberTableAttribute() );
				case KnownAttribute.tagLocalVariableTable -> twigFromLocalVariableTableAttribute( labeler, knownAttribute.asLocalVariableTableAttribute() );
				case KnownAttribute.tagLocalVariableTypeTable -> twigFromLocalVariableTypeTableAttribute( labeler, knownAttribute.asLocalVariableTypeTableAttribute() );
				case KnownAttribute.tagNestHost -> twigFromNestHostAttribute( knownAttribute.asNestHostAttribute() );
				case KnownAttribute.tagNestMembers -> twigFromNestMembersAttribute( knownAttribute.asNestMembersAttribute() );
				case KnownAttribute.tagConstantValue -> twigFromConstantValueAttribute( knownAttribute.asConstantValueAttribute() );
				case KnownAttribute.tagDeprecated -> twigFromDeprecatedAttribute( knownAttribute.asDeprecatedAttribute() );
				case KnownAttribute.tagEnclosingMethod -> twigFromEnclosingMethodAttribute( knownAttribute.asEnclosingMethodAttribute() );
				case KnownAttribute.tagExceptions -> twigFromExceptionsAttribute( knownAttribute.asExceptionsAttribute() );
				case KnownAttribute.tagInnerClasses -> twigFromInnerClassesAttribute( knownAttribute.asInnerClassesAttribute() );
				case KnownAttribute.tagRuntimeVisibleParameterAnnotations, KnownAttribute.tagRuntimeInvisibleParameterAnnotations -> twigFromParameterAnnotationsAttribute( knownAttribute.asParameterAnnotationsAttribute() );
				case KnownAttribute.tagSignature -> twigFromSignatureAttribute( knownAttribute.asSignatureAttribute() );
				case KnownAttribute.tagSourceFile -> twigFromSourceFileAttribute( knownAttribute.asSourceFileAttribute() );
				case KnownAttribute.tagSynthetic -> twigFromSyntheticAttribute( knownAttribute.asSyntheticAttribute() );
				case KnownAttribute.tagCode -> twigFromCodeAttribute( byteCodeType, labeler, knownAttribute.asCodeAttribute() );
				case KnownAttribute.tagRuntimeVisibleTypeAnnotations, KnownAttribute.tagRuntimeInvisibleTypeAnnotations -> twigFromTypeAnnotationsAttribute( knownAttribute.asTypeAnnotationsAttribute() );
				default -> throw new InvalidKnownAttributeTagException( knownAttribute.tag );
			};
	}

	private static Twig twigFromTypeAnnotationsAttribute( TypeAnnotationsAttribute typeAnnotationsAttribute )
	{
		return Twig.group( typeAnnotationsAttribute.getClass().getSimpleName(), //
			Map.entry( "typeAnnotations", twigFromTypeAnnotations( typeAnnotationsAttribute.typeAnnotations ) ) );
	}

	private static Twig twigFromTypeAnnotations( Collection<TypeAnnotation> typeAnnotations )
	{
		return Twig.array( typeAnnotations.stream().map( a -> twigFromTypeAnnotation( a ) ).toList() );
	}

	private static Twig twigFromCodeAttribute( ByteCodeType byteCodeType, Labeler labeler, CodeAttribute codeAttribute )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( codeAttribute.getClass().getSimpleName() );
		headerBuilder.append( " maxStack = " ).append( codeAttribute.getMaxStack() );
		headerBuilder.append( ", maxLocals = " ).append( codeAttribute.getMaxLocals() );
		List<ExceptionInfo> exceptionInfos = codeAttribute.exceptionInfos();
		return Twig.group( headerBuilder.toString(), //
			Map.entry( "instructions", twigFromInstructionList( codeAttribute.instructions(), byteCodeType, labeler ) ), //
			Map.entry( "exceptionInfos", twigFromExceptionInfos( exceptionInfos, labeler ) ), //
			Map.entry( "attributes", twigFromAttributeSet( codeAttribute.attributeSet, byteCodeType, labeler ) ) );
	}

	private static Twig twigFromSyntheticAttribute( SyntheticAttribute syntheticAttribute )
	{
		return Twig.leaf( syntheticAttribute.getClass().getSimpleName() );
	}

	private static Twig twigFromSourceFileAttribute( SourceFileAttribute sourceFileAttribute )
	{
		return Twig.group( sourceFileAttribute.getClass().getSimpleName(), //
			Map.entry( "value", twigFromValueConstant( sourceFileAttribute.valueConstant() ) ) );
	}

	private static Twig twigFromSignatureAttribute( SignatureAttribute signatureAttribute )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( signatureAttribute.getClass().getSimpleName() );
		headerBuilder.append( " " ).append( signatureAttribute.signatureConstant().stringValue() );
		return Twig.leaf( headerBuilder.toString() );
	}

	private static Twig twigFromParameterAnnotationsAttribute( ParameterAnnotationsAttribute parameterAnnotationsAttribute )
	{
		return Twig.group( parameterAnnotationsAttribute.getClass().getSimpleName(), //
			Map.entry( "parameterAnnotationSets", twigFromParameterAnnotationSets( parameterAnnotationsAttribute.parameterAnnotationSets() ) ) );
	}

	private static Twig twigFromParameterAnnotationSets( Collection<ParameterAnnotationSet> parameterAnnotationSets )
	{
		return Twig.array( parameterAnnotationSets.stream().map( a -> twigFromParameterAnnotationSet( a ) ).toList() );
	}

	private static Twig twigFromInnerClassesAttribute( InnerClassesAttribute innerClassesAttribute )
	{
		return Twig.group( innerClassesAttribute.getClass().getSimpleName(), //
			Map.entry( "innerClasses", twigFromInnerClasses( innerClassesAttribute.innerClasses ) ) );
	}

	private static Twig twigFromInnerClasses( Collection<InnerClass> innerClasses )
	{
		return Twig.array( innerClasses.stream().map( c -> twigFromInnerClass( c ) ).toList() );
	}

	private static Twig twigFromInnerClass( InnerClass innerClass )
	{
		var builder = new StringBuilder();
		builder.append( innerClass.getClass().getSimpleName() );
		builder.append( " " );
		Optional<ClassConstant> outerClassConstant = innerClass.outerClassConstant();
		if( outerClassConstant.isPresent() )
		{
			builder.append( "outerClass = " );
			summarizeClassConstant( outerClassConstant.get(), builder );
			builder.append( ", " );
		}
		builder.append( "accessFlags = " ).append( stringFromFlags( innerClass.modifierSet() ) );
		builder.append( ", innerClass=" );
		summarizeClassConstant( innerClass.innerClassConstant(), builder );
		Optional<Mutf8Constant> innerNameConstant = innerClass.innerNameConstant();
		if( innerNameConstant.isPresent() )
		{
			builder.append( ", innerName = " );
			mutf8ConstantToStringBuilder( innerNameConstant.get(), builder );
		}
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromExceptionsAttribute( ExceptionsAttribute exceptionsAttribute )
	{
		List<ClassConstant> exceptionClassConstants = exceptionsAttribute.exceptionClassConstants();
		return Twig.group( exceptionsAttribute.getClass().getSimpleName(), //
			Map.entry( "exceptions", twigFromExceptionClassConstants( exceptionClassConstants ) ) );
	}

	private static Twig twigFromExceptionClassConstants( Collection<ClassConstant> exceptionClassConstants )
	{
		return Twig.array( exceptionClassConstants.stream().map( c -> twigFromClassConstant( c ) ).toList() );
	}

	private static Twig twigFromEnclosingMethodAttribute( EnclosingMethodAttribute enclosingMethodAttribute )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( enclosingMethodAttribute.getClass().getSimpleName() );
		headerBuilder.append( " type = " ).append( ByteCodeHelpers.typeNameFromClassDesc( enclosingMethodAttribute.classDescriptor() ) );
		if( enclosingMethodAttribute.hasMethod() )
		{
			headerBuilder.append( ", name = \"" ).append( enclosingMethodAttribute.methodName() ).append( "\"" );
			headerBuilder.append( ", descriptor = " );
			appendMethodDescriptor( headerBuilder, enclosingMethodAttribute.methodDescriptor() );
		}
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
		valueConstantToStringBuilder( valueConstant, headerBuilder );
		return Twig.leaf( headerBuilder.toString() );
	}

	private static Twig twigFromNestMembersAttribute( NestMembersAttribute nestMembersAttribute )
	{
		return Twig.group( nestMembersAttribute.getClass().getSimpleName(), //
			Map.entry( "members", //
				Twig.array( nestMembersAttribute.memberClassConstants.stream().map( c -> twigFromNestMemberEntry( c ) ).toList() ) ) );
	}

	private static Twig twigFromNestMemberEntry( ClassConstant classConstant )
	{
		return Twig.leaf( classConstant.getClass().getSimpleName() + " " + ByteCodeHelpers.typeNameFromClassDesc( classConstant.classDescriptor() ) );
	}

	private static Twig twigFromNestHostAttribute( NestHostAttribute nestHostAttribute )
	{
		return Twig.group( nestHostAttribute.getClass().getSimpleName(), //
			Map.entry( "class", twigFromClassConstant( nestHostAttribute.hostClassConstant ) ) );
	}

	private static Twig twigFromLocalVariableTypeTableAttribute( Labeler labeler, LocalVariableTypeTableAttribute localVariableTypeTableAttribute )
	{
		return Twig.group( localVariableTypeTableAttribute.getClass().getSimpleName(), //
			Map.entry( "entrys", //
				Twig.array( localVariableTypeTableAttribute.localVariableTypes.stream().map( e -> twigFromLocalVariableTypeTableEntry( e, labeler ) ).toList() ) ) );
	}

	private static Twig twigFromLocalVariableTableAttribute( Labeler labeler, LocalVariableTableAttribute localVariableTableAttribute )
	{
		return Twig.group( localVariableTableAttribute.getClass().getSimpleName(), //
			Map.entry( "entrys", //
				Twig.array( localVariableTableAttribute.entrys.stream().map( e -> twigFromLocalVariableTableEntry( e, labeler ) ).toList() ) ) );
	}

	private static Twig twigFromLineNumberTableAttribute( Labeler labeler, LineNumberTableAttribute lineNumberTableAttribute )
	{
		return Twig.group( lineNumberTableAttribute.getClass().getSimpleName(), //
			Map.entry( "entrys", //
				Twig.array( lineNumberTableAttribute.entrys.stream().map( n -> twigFromLineNumberTableEntry( n, labeler ) ).toList() ) ) );
	}

	private static Twig twigFromStackMapTableAttribute( Labeler labeler, StackMapTableAttribute stackMapTableAttribute )
	{
		List<Twig> twigs = new ArrayList<>();
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( StackMapFrame frame : stackMapTableAttribute.frames() )
		{
			Twig result = twigFromStackMapFrame( labeler, previousFrame, frame );
			twigs.add( result );
			previousFrame = Optional.of( frame );
		}
		return Twig.group( stackMapTableAttribute.getClass().getSimpleName(), //
			Map.entry( "frames", Twig.array( twigs ) ) );
	}

	private static Twig twigFromStackMapFrame( Labeler labeler, Optional<StackMapFrame> previousFrame, StackMapFrame frame )
	{
		if( frame instanceof ChopStackMapFrame )
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
		return Twig.group( annotationsAttribute.getClass().getSimpleName(), //
			Map.entry( "annotations", //
				Twig.array( annotationsAttribute.annotations.stream().map( a -> twigFromAnnotation( a ) ).toList() ) ) );
	}

	private static Twig twigFromAnnotationDefaultAttribute( AnnotationDefaultAttribute annotationDefaultAttribute )
	{
		AnnotationValue annotationValue = annotationDefaultAttribute.annotationValue();
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( annotationDefaultAttribute.getClass().getSimpleName() );
		return Twig.group( headerBuilder.toString(), //
			Map.entry( "value", twigFromAnnotationValue( annotationValue ) ) );
	}

	private static Twig twigFromMethodParametersAttribute( MethodParametersAttribute methodParametersAttribute )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( methodParametersAttribute.getClass().getSimpleName() );
		return Twig.group( headerBuilder.toString(), //
			Map.entry( "parameters", twigFromMethodParameters( methodParametersAttribute ) ) );
	}

	private static Twig twigFromMethodParameters( MethodParametersAttribute methodParametersAttribute )
	{
		return Twig.array( methodParametersAttribute.methodParameters.stream().map( ByteCodePrinter::twigFromMethodParameter ).toList() );
	}

	private static Twig twigFromBootstrapMethodsAttribute( BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		return Twig.group( bootstrapMethodsAttribute.getClass().getSimpleName(), //
			Map.entry( "bootstrapMethods", //
				Twig.array( bootstrapMethodsAttribute.bootstrapMethods.stream().map( ByteCodePrinter::twigFromBootstrapMethod ).toList() ) ) );
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

	private static Twig twigFromBootstrapMethod( BootstrapMethod bootstrapMethod )
	{
		var builder = new StringBuilder();
		builder.append( bootstrapMethod.getClass().getSimpleName() );
		builder.append( " methodHandle = " );
		summarizeMethodHandleConstant( bootstrapMethod.methodHandleConstant, builder );
		List<Constant> argumentConstants = bootstrapMethod.argumentConstants;
		builder.append( " arguments: " );
		boolean first = true;
		for( Constant argumentConstant : argumentConstants )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			switch( argumentConstant.tag )
			{
				case Constant.tagString -> stringConstantToStringBuilder( argumentConstant.asStringConstant(), builder );
				case Constant.tagMethodType -> methodTypeConstantToStringBuilder( argumentConstant.asMethodTypeConstant(), builder );
				case Constant.tagMethodHandle -> summarizeMethodHandleConstant( argumentConstant.asMethodHandleConstant(), builder );
				case Constant.tagClass -> summarizeClassConstant( argumentConstant.asClassConstant(), builder );
				default -> throw new AssertionError( argumentConstant );
			}
		}
		return Twig.leaf( builder.toString() );
	}

	private static Twig twigFromAnnotation( Annotation byteCodeAnnotation )
	{
		return Twig.group( byteCodeAnnotation.getClass().getSimpleName(), //
			Map.entry( "type", Twig.leaf( ByteCodeHelpers.typeNameFromClassDesc( byteCodeAnnotation.typeDescriptor() ) ) ), //
			Map.entry( "parameters", twigFromAnnotationParameters( byteCodeAnnotation.parameters ) ) );
	}

	private static Twig twigFromByteCodeField( ByteCodeField byteCodeField, ByteCodeType byteCodeType, Labeler labeler )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( byteCodeField.getClass().getSimpleName() );
		headerBuilder.append( " accessFlags = " ).append( stringFromFlags( byteCodeField.modifierSet ) );
		headerBuilder.append( ", name = \"" ).append( byteCodeField.name() ).append( "\"" );
		headerBuilder.append( ", descriptor = " );
		appendClassDescriptor( headerBuilder, byteCodeField.descriptor() );
		String header = headerBuilder.toString();
		return Twig.group( header, //
			Map.entry( "attributes", //
				twigFromAttributeSet( byteCodeField.attributeSet, byteCodeType, labeler ) ) );
	}

	private static Twig twigFromByteCodeMethod( ByteCodeMethod byteCodeMethod, ByteCodeType byteCodeType, Labeler labeler )
	{
		StringBuilder headerBuilder = new StringBuilder();
		headerBuilder.append( byteCodeMethod.getClass().getSimpleName() );
		headerBuilder.append( " accessFlags = " ).append( stringFromFlags( byteCodeMethod.modifierSet ) );
		headerBuilder.append( ", name = \"" ).append( byteCodeMethod.name() ).append( "\"" );
		headerBuilder.append( ", descriptor = " );
		appendMethodDescriptor( headerBuilder, byteCodeMethod.descriptor() );
		String header = headerBuilder.toString();
		return Twig.group( header, Map.entry( "attributes", twigFromAttributeSet( byteCodeMethod.attributeSet, byteCodeType, labeler ) ) );
	}

	private static void appendClassDescriptor( StringBuilder builder, ClassDesc descriptor )
	{
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( descriptor ) );
	}

	private static void appendMethodDescriptor( StringBuilder builder, MethodTypeDesc descriptor )
	{
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( descriptor.returnType() ) );
		List<ClassDesc> parameterDescriptors = descriptor.parameterList();
		builder.append( parameterDescriptors.isEmpty() ? "(" : "( " );
		boolean first = true;
		for( ClassDesc parameterDescriptor : parameterDescriptors )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			builder.append( ByteCodeHelpers.typeNameFromClassDesc( parameterDescriptor ) );
		}
		builder.append( parameterDescriptors.isEmpty() ? ")" : " )" );
	}

	private static Twig twigFromByteCodeType( ByteCodeType byteCodeType, Labeler labeler )
	{
		StringBuilder builder = new StringBuilder( 1024 );
		builder.append( ByteCodeType.class.getSimpleName() );
		builder.append( " version = " ).append( byteCodeType.majorVersion ).append( '.' ).append( byteCodeType.minorVersion );
		builder.append( ", accessFlags = " ).append( stringFromFlags( byteCodeType.modifierSet ) );
		builder.append( ", thisClass = " ).append( byteCodeType.thisClassDescriptor.name() );
		builder.append( ", superClass = " ).append( byteCodeType.superClassDescriptor.map( c -> c.name() ).orElse( "<none>" ) );
		return Twig.group( builder.toString(), //
			Map.entry( "interfaces", twigFromInterfaces( byteCodeType.interfaces ) ), //
			Map.entry( "extraConstants", twigFromExtraConstants( byteCodeType.extraConstants ) ), //
			Map.entry( "fields", twigFromFields( byteCodeType.fields, byteCodeType, labeler ) ), //
			Map.entry( "methods", twigFromMethods( byteCodeType.methods, byteCodeType, labeler ) ), //
			Map.entry( "attributes", twigFromAttributeSet( byteCodeType.attributeSet, byteCodeType, labeler ) ) );
	}

	private static Twig twigFromInterfaces( Collection<TerminalTypeDescriptor> interfaces )
	{
		List<TerminalTypeDescriptor> list = interfaces.stream().toList();
		return Twig.array( list.stream().map( ByteCodePrinter::twigFromTerminalTypeDescriptor ).toList() );
	}

	private static Twig twigFromTerminalTypeDescriptor( TerminalTypeDescriptor terminalTypeDescriptor )
	{
		return Twig.leaf( terminalTypeDescriptor.name() );
	}

	private static Twig twigFromExtraConstants( Collection<Constant> extraConstants )
	{
		if( extraConstants.isEmpty() )
			return Twig.array( List.of() );
		List<Constant> list = extraConstants.stream().sorted( ByteCodePrinter::extraConstantComparator ).toList();
		return Twig.array( list.stream().map( ByteCodePrinter::twigFromExtraConstant ).toList() );
	}

	private static int extraConstantComparator( Constant a, Constant b )
	{
		int d = Integer.compare( a.tag, b.tag );
		if( d != 0 )
			return d;
		return switch( a.tag )
			{
				case Constant.tagClass -> extraConstantComparator( a.asClassConstant().nameConstant(), b.asClassConstant().nameConstant() );
				case Constant.tagMutf8 -> a.asMutf8Constant().stringValue().compareTo( b.asMutf8Constant().stringValue() );
				default -> throw new AssertionError( a.tag ); //I have not witnessed any other kind of extra constants.
			};
	}

	private static Twig twigFromFields( Collection<ByteCodeField> fields, ByteCodeType byteCodeType, Labeler labeler )
	{
		List<ByteCodeField> list = fields.stream().toList();
		return Twig.array( list.stream().map( byteCodeField -> twigFromByteCodeField( byteCodeField, byteCodeType, labeler ) ).toList() );
	}

	private static Twig twigFromMethods( Collection<ByteCodeMethod> methods, ByteCodeType byteCodeType, Labeler labeler )
	{
		List<ByteCodeMethod> list = methods.stream().toList();
		return Twig.array( list.stream().map( byteCodeMethod -> twigFromByteCodeMethod( byteCodeMethod, byteCodeType, labeler ) ).toList() );
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
		if( exceptionInfo.catchTypeConstant.isPresent() )
		{
			builder.append( ", catchType = " );
			summarizeClassConstant( exceptionInfo.catchTypeConstant.get(), builder );
		}
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
		builder.append( ", name = " ).append( localVariableTableEntry.name() );
		builder.append( ", descriptor = " );
		appendClassDescriptor( builder, localVariableTableEntry.descriptor() );
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
		return Twig.leaf( methodParameter.getClass().getSimpleName() + " accessFlags = " + stringFromFlags( methodParameter.modifierSet ) + ", name = \"" + methodParameter.name() + "\"" );
	}

	private static Twig twigFromParameterAnnotationSet( ParameterAnnotationSet parameterAnnotationSet )
	{
		return Twig.group( parameterAnnotationSet.getClass().getSimpleName(), //
			Map.entry( "annotations", //
				Twig.array( parameterAnnotationSet.annotations.stream().map( a -> twigFromAnnotation( a ) ).toList() ) ) );
	}

	private static void appendRelativeInstructionReference( Instruction instruction, StringBuilder builder, Labeler labeler )
	{
		String label = labeler.getLabel( Optional.of( instruction ) );
		builder.append( label );
	}

	private static Twig twigFromClassConstant( ClassConstant classConstant )
	{
		var builder = new StringBuilder();
		builder.append( classConstant.getClass().getSimpleName() );
		builder.append( " name = " );
		mutf8ConstantToStringBuilder( classConstant.nameConstant(), builder );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static void summarizeClassConstant( ClassConstant classConstant, StringBuilder builder )
	{
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( classConstant.classDescriptor() ) );
	}

	private static Twig twigFromValueConstant( ValueConstant<?> valueConstant )
	{
		var builder = new StringBuilder();
		valueConstantToStringBuilder( valueConstant, builder );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static void summarizeInvokeDynamicConstant( InvokeDynamicConstant invokeDynamicConstant, StringBuilder builder, ByteCodeType byteCodeType )
	{
		builder.append( "bootstrapMethodIndex = " ).append( invokeDynamicConstant.bootstrapMethodIndex() ); //TODO: inline the bootstrap method here, omit in bootstrap methods attribute.
		DynamicCallSiteDesc invokeDynamicDescriptor = invokeDynamicConstant.descriptor( byteCodeType );
		builder.append( ", name = " ).append( invokeDynamicDescriptor.invocationName() );
		builder.append( ", descriptor = " );
		appendMethodDescriptor( builder, invokeDynamicDescriptor.invocationType() );
		//The arguments belong to the bootstrap method.
		//		builder.append( ", arguments = " );
		//		boolean first = true;
		//		for( ConstantDesc argumentDescriptor : invokeDynamicDescriptor.bootstrapArgs() )
		//		{
		//			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
		//			builder.append( argumentDescriptor.toString() );
		//		}
	}

	private static void summarizeMethodHandleConstant( MethodHandleConstant methodHandleConstant, StringBuilder builder )
	{
		builder.append( methodHandleConstant.getClass().getSimpleName() );
		builder.append( " referenceKind = " ).append( methodHandleConstant.referenceKind().name() );
		builder.append( ", referenceConstant = " );
		ReferenceConstant referenceConstant = methodHandleConstant.referenceConstant();
		switch( referenceConstant.tag )
		{
			case Constant.tagFieldReference -> //
				summarizeFieldReferenceConstant( referenceConstant.asFieldReferenceConstant(), builder );
			case Constant.tagInterfaceMethodReference -> //
				summarizeInterfaceMethodReferenceConstant( referenceConstant.asInterfaceMethodReferenceConstant(), builder );
			case Constant.tagMethodReference -> //
				summarizePlainMethodReferenceConstant( referenceConstant.asPlainMethodReferenceConstant(), builder );
			default -> throw new AssertionError( referenceConstant );
		}
	}

	private static void summarizeFieldReferenceConstant( FieldReferenceConstant fieldReferenceConstant, StringBuilder builder )
	{
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( fieldReferenceConstant.fieldTypeDescriptor() ) );
		builder.append( ' ' );
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( fieldReferenceConstant.owningClassDescriptor() ) );
		builder.append( '.' );
		builder.append( fieldReferenceConstant.fieldName() );
	}

	private static void summarizeInterfaceMethodReferenceConstant( InterfaceMethodReferenceConstant interfaceMethodReferenceConstant, StringBuilder builder )
	{
		//
		builder.append( "type = " );
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( interfaceMethodReferenceConstant.owningClassDescriptor() ) );
		builder.append( ", name = " );
		builder.append( interfaceMethodReferenceConstant.methodName() );
		builder.append( ", descriptor = " );
		appendMethodDescriptor( builder, interfaceMethodReferenceConstant.methodDescriptor() );
	}

	private static void summarizePlainMethodReferenceConstant( PlainMethodReferenceConstant plainMethodReferenceConstant, StringBuilder builder )
	{
		//
		builder.append( "type = " );
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( plainMethodReferenceConstant.owningClassDescriptor() ) );
		builder.append( ", name = " );
		builder.append( plainMethodReferenceConstant.methodName() );
		builder.append( ", descriptor = " );
		appendMethodDescriptor( builder, plainMethodReferenceConstant.methodDescriptor() );
	}

	private static void methodTypeConstantToStringBuilder( MethodTypeConstant methodTypeConstant, StringBuilder builder )
	{
		builder.append( MethodTypeConstant.class.getSimpleName() );
		builder.append( " " );
		mutf8ConstantToStringBuilder( methodTypeConstant.descriptorConstant, builder );
	}

	private static Twig twigFromExtraConstant( Constant constant )
	{
		return switch( constant.tag )
			{
				case Constant.tagClass -> //
					{
						ClassConstant classConstant = constant.asClassConstant();
						yield twigFromClassConstant( classConstant );
					}
				case Constant.tagDouble, Constant.tagFloat, Constant.tagInteger, Constant.tagLong, Constant.tagString, Constant.tagMutf8 -> //
					{
						ValueConstant<?> valueConstant = constant.asValueConstant();
						yield twigFromValueConstant( valueConstant );
					}
				default -> throw new AssertionError( constant );
			};
	}

	private static Twig twigFromAnnotationValue( AnnotationValue annotationValue )
	{
		Twig childTwig = switch( annotationValue.tag )
			{
				case AnnotationValue.tagByte, AnnotationValue.tagBoolean, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger, AnnotationValue.tagLong, //
					AnnotationValue.tagShort, AnnotationValue.tagString -> twigFromConstAnnotationValue( annotationValue.asConstAnnotationValue(), annotationValue.tag );
				case AnnotationValue.tagClass -> twigFromClassAnnotationValue( annotationValue.asClassAnnotationValue() );
				case AnnotationValue.tagEnum -> twigFromEnumAnnotationValue( annotationValue.asEnumAnnotationValue() );
				case AnnotationValue.tagAnnotation -> twigFromAnnotation( annotationValue.asAnnotationAnnotationValue().annotation );
				case AnnotationValue.tagArray -> twigFromArrayAnnotationValue( annotationValue.asArrayAnnotationValue() );
				default -> throw new AssertionError( annotationValue );
			};
		return Twig.group( AnnotationValue.class.getSimpleName(), //
			Map.entry( "tag", Twig.leaf( AnnotationValue.tagName( annotationValue.tag ) ) ), //
			Map.entry( "value", childTwig ) );
	}

	private static Twig twigFromConstAnnotationValue( ConstAnnotationValue constAnnotationValue, int annotationValueTag )
	{
		return Twig.group( constAnnotationValue.getClass().getSimpleName(), //
			Map.entry( "value", twigFromValueConstant( constAnnotationValue.valueConstant, annotationValueTag ) ) );
	}

	private static Twig twigFromValueConstant( ValueConstant<?> valueConstant, int annotationValueTag )
	{
		StringBuilder builder = new StringBuilder();
		switch( annotationValueTag )
		{
			case AnnotationValue.tagByte -> integerConstantToStringBuilder( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Byte );
			case AnnotationValue.tagBoolean -> integerConstantToStringBuilder( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Boolean );
			case AnnotationValue.tagCharacter -> integerConstantToStringBuilder( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Character );
			case AnnotationValue.tagDouble -> doubleConstantToStringBuilder( valueConstant.asDoubleConstant(), builder );
			case AnnotationValue.tagFloat -> floatConstantToStringBuilder( valueConstant.asFloatConstant(), builder );
			case AnnotationValue.tagInteger -> integerConstantToStringBuilder( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Integer );
			case AnnotationValue.tagLong -> longConstantToStringBuilder( valueConstant.asLongConstant(), builder );
			case AnnotationValue.tagShort -> integerConstantToStringBuilder( valueConstant.asIntegerConstant(), builder, IntegerValueHint.Short );
			case AnnotationValue.tagString -> mutf8ConstantToStringBuilder( valueConstant.asMutf8Constant(), builder );
			default -> throw new AssertionError( annotationValueTag );
		}
		return Twig.leaf( builder.toString() );
	}

	private static Twig twigFromClassAnnotationValue( ClassAnnotationValue classAnnotationValue )
	{
		return Twig.group( classAnnotationValue.getClass().getSimpleName(), //
			Map.entry( "name", Twig.leaf( ByteCodeHelpers.typeNameFromClassDesc( classAnnotationValue.classDescriptor() ) ) ) );
	}

	private static Twig twigFromEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue )
	{
		return Twig.group( enumAnnotationValue.getClass().getSimpleName(), //
			Map.entry( "type", Twig.leaf( ByteCodeHelpers.typeNameFromClassDesc( enumAnnotationValue.typeDescriptor() ) ) ), //
			Map.entry( "value", Twig.leaf( enumAnnotationValue.valueName() ) ) );
	}

	private static Twig twigFromArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue )
	{
		return Twig.group( arrayAnnotationValue.getClass().getSimpleName(), //
			Map.entry( "values", //
				Twig.array( arrayAnnotationValue.annotationValues.stream().map( a -> twigFromAnnotationValue( a ) ).toList() ) ) );
	}

	private static Twig twigFromTypeAnnotation( TypeAnnotation typeAnnotation )
	{
		return Twig.group( TypeAnnotation.class.getSimpleName(), //
			Map.entry( "typePath", twigFromTypePath( typeAnnotation.typePath ) ), Map.entry( "typeIndex", Twig.leaf( "" + typeAnnotation.typeIndex ) ), Map.entry( "target", twigFromTarget( typeAnnotation.target ) ), //
			Map.entry( "parameters", twigFromAnnotationParameters( typeAnnotation.parameters ) ) );
	}

	private static Twig twigFromTypePath( TypePath typePath )
	{
		List<TypePathEntry> typePathEntrys = typePath.entries();
		return Twig.group( typePath.getClass().getSimpleName(), //
			Map.entry( "entries", //
				Twig.array( typePathEntrys.stream().map( e -> twigFromTypePathEntry( e ) ).toList() ) ) );
	}

	private static Twig twigFromTypePathEntry( TypePathEntry entry )
	{
		return Twig.leaf( TypePathEntry.class.getSimpleName() + " pathKind = " + entry.pathKind() + ", " + "argumentIndex = " + entry.argumentIndex() );
	}

	private static Twig twigFromAnnotationParameters( Collection<AnnotationParameter> annotationParameters )
	{
		return Twig.array( annotationParameters.stream().map( a -> twigFromAnnotationParameter( a ) ).toList() );
	}

	private static Twig twigFromTarget( Target target )
	{
		return switch( target.tag )
			{
				case Target.tagTypeParameterDeclarationOfGenericClassOrInterface, //
					Target.tagTypeParameterDeclarationOfGenericMethodOrConstructor -> //
					twigFromTypeParameterTarget( target.asTypeParameterTarget() );
				case Target.tagTypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration -> //
					twigFromSupertypeTarget( target.asSupertypeTarget() );
				case Target.tagTypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface, //
					Target.tagTypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor -> //
					twigFromTypeParameterBoundTarget( target.asTypeParameterBoundTarget() );
				case Target.tagTypeInFieldDeclaration, //
					Target.tagReturnTypeOfMethodOrTypeOfNewlyConstructedObject, //
					Target.tagReceiverTypeOfMethodOrConstructor -> //
					twigFromEmptyTarget( target.asEmptyTarget() );
				case Target.tagTypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression -> //
					twigFromFormalParameterTarget( target.asFormalParameterTarget() );
				case Target.tagTypeInThrowsClauseOfMethodOrConstructor -> //
					twigFromThrowsTarget( target.asThrowsTarget() );
				case Target.tagTypeInLocalVariableDeclaration, //
					Target.tagTypeInResourceVariableDeclaration -> //
					twigFromLocalVariableTarget( target.asLocalVariableTarget() );
				case Target.tagTypeInExceptionParameterDeclaration -> //
					twigFromCatchTarget( target.asCatchTarget() );
				case Target.tagTypeInInstanceofExpression, //
					Target.tagTypeInNewExpression, //
					Target.tagTypeInMethodReferenceExpressionUsingNew, //
					Target.tagTypeInMethodReferenceExpressionUsingIdentifier -> //
					twigFromOffsetTarget( target.asOffsetTarget() );
				case Target.tagTypeInCastExpression, //
					Target.tagTypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement, //
					Target.tagTypeArgumentForGenericMethodInMethodInvocationExpression, //
					Target.tagTypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew, //
					Target.tagTypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier -> //
					twigFromTypeArgumentTarget( target.asTypeArgumentTarget() );
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
		var builder = new StringBuilder();
		builder.append( Target.class.getSimpleName() ).append( " tag = " ).append( Target.tagName( localVariableTarget.tag ) );
		String header = builder.toString();
		return Twig.group( header, //
			Map.entry( "entries", //
				Twig.array( localVariableTarget.entries.stream().map( e -> twigFromLocalVariableTargetEntry( e ) ).toList() ) ) );
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

	private static Twig twigFromBranchInstruction( String prefix, Optional<String> suffix, BranchInstruction branchInstruction, Labeler labeler )
	{
		var builder = new StringBuilder();
		appendRelativeInstructionReference( branchInstruction.getTargetInstruction(), builder, labeler );
		return Twig.leaf( buildInstructionHeader( prefix, suffix, branchInstruction.opCode, builder.toString() ) );
	}

	private static Twig twigFromConditionalBranchInstruction( String prefix, Optional<String> suffix, ConditionalBranchInstruction conditionalBranchInstruction, Labeler labeler )
	{
		return Twig.leaf( buildInstructionHeader( prefix, suffix, conditionalBranchInstruction.getOpCode(), labeler.getLabel( conditionalBranchInstruction.getTargetInstruction() ) ) );
	}

	private static Twig twigFromConstantReferencingInstruction( String prefix, Optional<String> suffix, ConstantReferencingInstruction constantReferencingInstruction )
	{
		var builder = new StringBuilder();
		Constant constant = constantReferencingInstruction.constant;
		switch( constant.tag )
		{
			case Constant.tagFieldReference -> summarizeFieldReferenceConstant( constant.asFieldReferenceConstant(), builder );
			case Constant.tagInterfaceMethodReference -> summarizeInterfaceMethodReferenceConstant( constant.asInterfaceMethodReferenceConstant(), builder );
			case Constant.tagMethodReference -> summarizePlainMethodReferenceConstant( constant.asPlainMethodReferenceConstant(), builder );
			case Constant.tagClass -> summarizeClassConstant( constant.asClassConstant(), builder );
			default -> throw new AssertionError( constant );
		}
		return Twig.leaf( buildInstructionHeader( prefix, suffix, constantReferencingInstruction.opCode, builder.toString() ) );
	}

	private static Twig twigFromIIncInstruction( String prefix, Optional<String> suffix, IIncInstruction iIncInstruction )
	{
		return Twig.leaf( buildInstructionHeader( prefix, suffix, OpCode.IINC, "index = " + iIncInstruction.index, "delta = " + iIncInstruction.delta ) );
	}

	private static Twig twigFromImmediateLoadConstantInstruction( String prefix, Optional<String> suffix, ImmediateLoadConstantInstruction immediateLoadConstantInstruction )
	{
		return Twig.leaf( buildInstructionHeader( prefix, suffix, immediateLoadConstantInstruction.opCode, "immediateValue = " + immediateLoadConstantInstruction.immediateValue ) );
	}

	private static Twig twigFromIndirectLoadConstantInstruction( String prefix, Optional<String> suffix, IndirectLoadConstantInstruction indirectLoadConstantInstruction )
	{
		var builder = new StringBuilder();
		Constant constant = indirectLoadConstantInstruction.constant;
		switch( constant.tag )
		{
			case Constant.tagInteger, Constant.tagLong, Constant.tagFloat, Constant.tagDouble, Constant.tagString -> valueConstantToStringBuilder( constant.asValueConstant(), builder );
			case Constant.tagClass -> summarizeClassConstant( constant.asClassConstant(), builder );
			default -> throw new AssertionError( constant );
		}
		return Twig.leaf( buildInstructionHeader( prefix, suffix, indirectLoadConstantInstruction.opCode, "constant = " + builder.toString() ) );
	}

	private static Twig twigFromInvokeDynamicInstruction( String prefix, Optional<String> suffix, InvokeDynamicInstruction invokeDynamicInstruction, ByteCodeType byteCodeType )
	{
		var builder = new StringBuilder();
		summarizeInvokeDynamicConstant( invokeDynamicInstruction.invokeDynamicConstant, builder, byteCodeType );
		return Twig.leaf( buildInstructionHeader( prefix, suffix, OpCode.INVOKEDYNAMIC, "invokeDynamicConstant = " + builder.toString() ) );
	}

	private static Twig twigFromInvokeInterfaceInstruction( String prefix, Optional<String> suffix, InvokeInterfaceInstruction invokeInterfaceInstruction )
	{
		var builder = new StringBuilder();
		summarizeInterfaceMethodReferenceConstant( invokeInterfaceInstruction.interfaceMethodReferenceConstant, builder );
		return Twig.leaf( buildInstructionHeader( prefix, suffix, OpCode.INVOKEINTERFACE, "interfaceMethodReferenceConstant = " + builder.toString(), invokeInterfaceInstruction.argumentCount + " arguments" ) );
	}

	private static Twig twigFromLocalVariableInstruction( String prefix, Optional<String> suffix, LocalVariableInstruction localVariableInstruction )
	{
		String[] arguments = localVariableInstruction.index <= 3 && localVariableInstruction.getOpCode() != OpCode.RET ? new String[] { "" + localVariableInstruction.index } : Kit.ARRAY_OF_ZERO_STRINGS;
		return Twig.leaf( buildInstructionHeader( prefix, suffix, localVariableInstruction.getOpCode(), arguments ) );
	}

	private static Twig twigFromLookupSwitchInstruction( String prefix, Optional<String> suffix, LookupSwitchInstruction lookupSwitchInstruction, Labeler labeler )
	{
		return Twig.group( buildInstructionHeader( prefix, suffix, OpCode.LOOKUPSWITCH ), Map.entry( "default", Twig.leaf( labeler.getLabel( lookupSwitchInstruction.getDefaultInstruction() ) ) ), Map.entry( "entries", Twig.array( lookupSwitchInstruction.entries.stream().map( e -> twigFromLookupSwitchEntry( e, labeler ) ).toList() ) ) );
	}

	private static Twig twigFromLookupSwitchEntry( LookupSwitchEntry lookupSwitchEntry, Labeler labeler )
	{
		return Twig.leaf( lookupSwitchEntry.getClass().getSimpleName() + " value = " + lookupSwitchEntry.value() + ", target = " + labeler.getLabel( lookupSwitchEntry.getTargetInstruction() ) );
	}

	private static Twig twigFromMultiANewArrayInstruction( String prefix, Optional<String> suffix, MultiANewArrayInstruction multiANewArrayInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "class = " );
		summarizeClassConstant( multiANewArrayInstruction.classConstant, builder );
		return Twig.leaf( buildInstructionHeader( prefix, suffix, OpCode.MULTIANEWARRAY, builder.toString(), multiANewArrayInstruction.dimensionCount + " dimensions" ) );
	}

	private static Twig twigFromNewPrimitiveArrayInstruction( String prefix, Optional<String> suffix, NewPrimitiveArrayInstruction newPrimitiveArrayInstruction )
	{
		return Twig.leaf( buildInstructionHeader( prefix, suffix, OpCode.NEWARRAY, "type = " + NewPrimitiveArrayInstruction.Type.fromNumber( newPrimitiveArrayInstruction.type ).name() ) );
	}

	private static Twig twigFromOperandlessInstruction( String prefix, Optional<String> suffix, OperandlessInstruction operandlessInstruction )
	{
		return Twig.leaf( buildInstructionHeader( prefix, suffix, operandlessInstruction.opCode ) );
	}

	private static Twig twigFromOperandlessLoadConstantInstruction( String prefix, Optional<String> suffix, OperandlessLoadConstantInstruction operandlessLoadConstantInstruction )
	{
		return Twig.leaf( buildInstructionHeader( prefix, suffix, operandlessLoadConstantInstruction.opCode ) );
	}

	private static Twig twigFromRelativeInstructionReference( Instruction instruction, Labeler labeler )
	{
		String label = labeler.getLabel( instruction );
		return Twig.leaf( label );
	}

	private static String buildInstructionHeader( String prefix, Optional<String> suffix, int opCode, String... parts )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( prefix );
		stringBuilder.append( OpCode.getOpCodeName( opCode ) );
		boolean first = true;
		for( String part : parts )
		{
			stringBuilder.append( first ? " " : ", " );
			first = false;
			stringBuilder.append( part );
		}
		suffix.ifPresent( s -> stringBuilder.append( " // " ).append( s ) );
		return stringBuilder.toString();
	}

	private static Twig twigFromTableSwitchInstruction( String prefix, Optional<String> suffix, TableSwitchInstruction tableSwitchInstruction, Labeler labeler )
	{
		return Twig.group( buildInstructionHeader( prefix, suffix, OpCode.TABLESWITCH, "lowValue = " + tableSwitchInstruction.lowValue ), //
			Map.entry( "default", Twig.leaf( labeler.getLabel( tableSwitchInstruction.getDefaultInstruction() ) ) ), //
			Map.entry( "offsets", //
				Twig.array( tableSwitchInstruction.targetInstructions().stream().map( i -> twigFromRelativeInstructionReference( i, labeler ) ).toList() ) ) );
	}

	private static Twig twigFromInstructionList( InstructionList instructionList, ByteCodeType byteCodeType, Labeler labeler )
	{
		Collection<Instruction> instructions = instructionList.all();
		return Twig.array( instructions.stream().map( i -> twigFromInstruction( i, byteCodeType, labeler ) ).toList() );
	}

	private static final String indentation = "        ";

	private static Twig twigFromInstruction( Instruction instruction, ByteCodeType byteCodeType, Labeler labeler )
	{
		Optional<LabelInfo> labelInfo = labeler.tryGetLabelInfo( instruction );
		String prefix = labelInfo.map( l -> l.label + ":" + indentation.substring( l.label.length() + 1 ) ).orElse( indentation );
		Optional<String> suffix = labelInfo.flatMap( l -> l.source );
		Twig twig = switch( instruction.group )
			{
				case TableSwitch -> twigFromTableSwitchInstruction( prefix, suffix, instruction.asTableSwitchInstruction(), labeler );
				case ConditionalBranch -> twigFromConditionalBranchInstruction( prefix, suffix, instruction.asConditionalBranchInstruction(), labeler );
				case ConstantReferencing -> twigFromConstantReferencingInstruction( prefix, suffix, instruction.asConstantReferencingInstruction() );
				case IInc -> twigFromIIncInstruction( prefix, suffix, instruction.asIIncInstruction() );
				case ImmediateLoadConstant -> twigFromImmediateLoadConstantInstruction( prefix, suffix, instruction.asImmediateLoadConstantInstruction() );
				case IndirectLoadConstant -> twigFromIndirectLoadConstantInstruction( prefix, suffix, instruction.asIndirectLoadConstantInstruction() );
				case InvokeDynamic -> twigFromInvokeDynamicInstruction( prefix, suffix, instruction.asInvokeDynamicInstruction(), byteCodeType );
				case InvokeInterface -> twigFromInvokeInterfaceInstruction( prefix, suffix, instruction.asInvokeInterfaceInstruction() );
				case LocalVariable -> twigFromLocalVariableInstruction( prefix, suffix, instruction.asLocalVariableInstruction() );
				case LookupSwitch -> twigFromLookupSwitchInstruction( prefix, suffix, instruction.asLookupSwitchInstruction(), labeler );
				case MultiANewArray -> twigFromMultiANewArrayInstruction( prefix, suffix, instruction.asMultiANewArrayInstruction() );
				case NewPrimitiveArray -> twigFromNewPrimitiveArrayInstruction( prefix, suffix, instruction.asNewPrimitiveArrayInstruction() );
				case Operandless -> twigFromOperandlessInstruction( prefix, suffix, instruction.asOperandlessInstruction() );
				case OperandlessLoadConstant -> twigFromOperandlessLoadConstantInstruction( prefix, suffix, instruction.asOperandlessLoadConstantInstruction() );
				case Branch -> twigFromBranchInstruction( prefix, suffix, instruction.asBranchInstruction(), labeler );
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
		switch( verificationType.tag )
		{
			case VerificationType.tagTop, VerificationType.tagInteger, VerificationType.tagFloat, VerificationType.tagDouble, VerificationType.tagLong, //
				VerificationType.tagNull, VerificationType.tagUninitializedThis -> //
				{
					SimpleVerificationType simpleVerificationType = verificationType.asSimpleVerificationType();
					return twigFromSimpleVerificationType( simpleVerificationType );
				}
			case VerificationType.tagObject -> //
				{
					ObjectVerificationType objectVerificationType = verificationType.asObjectVerificationType();
					return twigFromObjectVerificationType( objectVerificationType );
				}
			case VerificationType.tagUninitialized -> //
				{
					UninitializedVerificationType uninitializedVerificationType = verificationType.asUninitializedVerificationType();
					return twigFromUninitializedVerificationType( uninitializedVerificationType, labeler );
				}
			default -> throw new AssertionError( verificationType );
		}
	}

	private static Twig twigFromObjectVerificationType( ObjectVerificationType objectVerificationType )
	{
		var builder = new StringBuilder();
		builder.append( "name = " );
		builder.append( VerificationType.tagName( objectVerificationType.tag ) );
		builder.append( ", type = " );
		summarizeClassConstant( objectVerificationType.classConstant(), builder );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromSimpleVerificationType( SimpleVerificationType simpleVerificationType )
	{
		var builder = new StringBuilder();
		String name = VerificationType.tagName( simpleVerificationType.tag );
		builder.append( name );
		String header = builder.toString();
		return Twig.leaf( header );
	}

	private static Twig twigFromUninitializedVerificationType( UninitializedVerificationType uninitializedVerificationType, Labeler labeler )
	{
		var builder = new StringBuilder();
		String name = VerificationType.tagName( uninitializedVerificationType.tag );
		builder.append( name );
		builder.append( ' ' );
		summarizeAbsoluteInstruction( uninitializedVerificationType.instruction, builder, labeler );
		String header = builder.toString();
		return Twig.leaf( header );
	}
}
