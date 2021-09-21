package mikenakis.bytecode.printing;

import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.kit.OmniSwitch3;
import mikenakis.bytecode.model.ElementValuePair;
import mikenakis.bytecode.model.ElementValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.FlagSet;
import mikenakis.bytecode.model.annotationvalues.AnnotationElementValue;
import mikenakis.bytecode.model.annotationvalues.ArrayElementValue;
import mikenakis.bytecode.model.annotationvalues.ClassElementValue;
import mikenakis.bytecode.model.annotationvalues.ConstElementValue;
import mikenakis.bytecode.model.annotationvalues.EnumElementValue;
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
import mikenakis.bytecode.model.attributes.ParameterAnnotationsAttribute;
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
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.stream.Stream;

/**
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodePrinter
{
	public static String printByteCodeType( ByteCodeType byteCodeType, Optional<Path> sourcePath )
	{
		ByteCodePrinter byteCodePrinter = new ByteCodePrinter( byteCodeType, sourcePath );
		Twig rootNode = byteCodePrinter.twigFromByteCodeType();
		var builder = new StringBuilder();
		printTree( rootNode, Twig::getChildren, Twig::getPayload, s -> builder.append( s ).append( '\n' ) );
		return builder.toString();
	}

	private interface Twig
	{
		String getPayload();

		Collection<Twig> getChildren();

		static Twig of( String payload )
		{
			return new LeafTwig( payload );
		}

		static Twig of( String payload, Twig... arrayOfChildren )
		{
			return of( payload, Arrays.asList( arrayOfChildren ) );
		}

		static Twig of( String payload, List<Twig> children )
		{
			return new BranchTwig( payload, children );
		}

		class LeafTwig implements Twig
		{
			final String payload;

			LeafTwig( String payload )
			{
				this.payload = payload;
			}

			@Override public String getPayload()
			{
				return payload;
			}

			@Override public Collection<Twig> getChildren()
			{
				return Collections.emptyList();
			}

			@ExcludeFromJacocoGeneratedReport @Override public String toString()
			{
				return payload;
			}
		}

		class BranchTwig extends LeafTwig
		{
			final List<Twig> children;

			BranchTwig( String payload, List<Twig> children )
			{
				super( payload );
				assert children.stream().noneMatch( t -> t == null );
				this.children = children;
			}

			@ExcludeFromJacocoGeneratedReport @Override public final String toString()
			{
				return payload + " (" + children.size() + " child nodes)";
			}

			@Override public Collection<Twig> getChildren()
			{
				return children;
			}
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

	private final ByteCodeType byteCodeType;
	private final Optional<List<String>> sourceLines;
	private final Map<Instruction,LabelInfo> labelInfoFromInstructionMap = new HashMap<>();

	private ByteCodePrinter( ByteCodeType byteCodeType, Optional<Path> sourcePath )
	{
		this.byteCodeType = byteCodeType;
		Optional<Path> sourcePathName = sourcePath.map( p -> p.resolve( byteCodeType.tryGetSourceFileName().orElse( "" ) ) );
		sourceLines = sourcePathName.map( p -> Kit.unchecked( () -> Files.readAllLines( p ) ) );
		for( ByteCodeMethod byteCodeMethod : byteCodeType.methods() )
		{
			Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetAttributeByName( CodeAttribute.kind.mutf8Name ) //
				.map( a -> a.asCodeAttribute() );
			if( codeAttribute.isEmpty() )
				continue;
			updateLabels( codeAttribute.get() );
		}
	}

	private Optional<LabelInfo> tryGetLabelInfo( Instruction instruction )
	{
		return Kit.map.getOptional( labelInfoFromInstructionMap, instruction );
	}

	private String getLabel( Optional<Instruction> instruction )
	{
		if( instruction.isEmpty() )
			return CodeAttribute.END_LABEL;
		LabelInfo data = tryGetLabelInfo( instruction.get() ).orElseThrow();
		return data.label;
	}

	private static final OmniSwitch3<Void,Mutf8Constant,Attribute,Consumer<Optional<Instruction>>> updateCodeAttributeLabelsSwitch = //
		OmniSwitch3.<Void,Mutf8Constant,Attribute,Consumer<Optional<Instruction>>>newBuilder() //
			.with( BootstrapMethodsAttribute.kind.mutf8Name, ByteCodePrinter::updateLineNumberTableAttributeLabels )              //
			.with( LocalVariableTableAttribute.kind.mutf8Name, ByteCodePrinter::updateLocalVariableTableAttributeLabels )         //
			.with( LocalVariableTypeTableAttribute.kind.mutf8Name, ByteCodePrinter::updateLocalVariableTypeTableAttributeLabels ) //
			.with( StackMapTableAttribute.kind.mutf8Name, ByteCodePrinter::updateStackMapTableAttributeAttributeLabels )          //
			.with( LineNumberTableAttribute.kind.mutf8Name, ByteCodePrinter::updateLineNumberTableAttributeLabels )               //
			.withDefault( ByteCodePrinter::updateUnknownAttributeLabels )                                                        //
			.build();

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
					//ConstantReferencingInstruction constantReferencingInstruction = instruction.asConstantReferencingInstruction();
					break;
				case IInc:
					//instruction.asIIncInstruction();
					break;
				case ImmediateLoadConstant:
					//instruction.asImmediateLoadConstantInstruction();
					break;
				case IndirectLoadConstant:
					//IndirectLoadConstantInstruction indirectLoadConstantInstruction = instruction.asIndirectLoadConstantInstruction();
					break;
				case InvokeDynamic:
					//InvokeDynamicInstruction invokeDynamicInstruction = instruction.asInvokeDynamicInstruction();
					break;
				case InvokeInterface:
					//InvokeInterfaceInstruction invokeInterfaceInstruction = instruction.asInvokeInterfaceInstruction();
					break;
				case LocalVariable:
					//instruction.asLocalVariableInstruction();
					break;
				case LookupSwitch:
				{
					LookupSwitchInstruction lookupSwitchInstruction = instruction.asLookupSwitchInstruction();
					targetInstructionConsumer.accept( Optional.of( lookupSwitchInstruction.getDefaultInstruction() ) );
					for( LookupSwitchEntry entry : lookupSwitchInstruction.entries )
						targetInstructionConsumer.accept( Optional.of( entry.getTargetInstruction() ) );
					break;
				}
				case MultiANewArray:
					//MultiANewArrayInstruction multiANewArrayInstruction = instruction.asMultiANewArrayInstruction();
					break;
				case NewPrimitiveArray:
					//instruction.asNewPrimitiveArrayInstruction();
					break;
				case Operandless:
					//instruction.asOperandlessInstruction();
					break;
				case OperandlessLoadConstant:
					//instruction.asOperandlessLoadConstantInstruction();
					break;
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
		for( Attribute attribute : codeAttribute.attributeSet() )
		{
			updateCodeAttributeLabelsSwitch.on( attribute.kind.mutf8Name, attribute, targetInstructionConsumer );
		}

		int targetInstructionLabelNumberSeed = 1;
		for( Instruction instruction : codeAttribute.instructions().all() )
		{
			Optional<Integer> lineNumber = Kit.map.getOptional( lineNumberFromInstructionMap, instruction );
			if( lineNumber.isPresent() )
			{
				String label = "L" + lineNumber.get();
				LabelInfo data = new LabelInfo( label, sourceLines.map( s -> s.get( lineNumber.get() - 1 ).trim() ) );
				Kit.map.add( labelInfoFromInstructionMap, instruction, data );
			}
			else if( Kit.collection.contains( targets, instruction ) )
			{
				String label = "T" + targetInstructionLabelNumberSeed++; //'T' stands for 'Target'
				LabelInfo data = new LabelInfo( label, Optional.empty() );
				Kit.map.add( labelInfoFromInstructionMap, instruction, data );
			}
		}
	}

	private static Void updateLocalVariableTableAttributeLabels( Mutf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		assert attributeNameConstant == LocalVariableTableAttribute.kind.mutf8Name;
		LocalVariableTableAttribute localVariableTableAttribute = attribute.asLocalVariableTableAttribute();
		for( LocalVariable localVariable : localVariableTableAttribute.localVariables() )
		{
			targetInstructionConsumer.accept( Optional.of( localVariable.startInstruction ) );
			targetInstructionConsumer.accept( localVariable.endInstruction );
		}
		return null;
	}

	private static Void updateLocalVariableTypeTableAttributeLabels( Mutf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		assert attributeNameConstant == LocalVariableTypeTableAttribute.kind.mutf8Name;
		LocalVariableTypeTableAttribute localVariableTypeTableAttribute = attribute.asLocalVariableTypeTableAttribute();
		for( LocalVariableType entry : localVariableTypeTableAttribute.localVariableTypes() )
		{
			targetInstructionConsumer.accept( Optional.of( entry.startInstruction ) );
			//FIXME how come we are not feeding the endInstruction?
		}
		return null;
	}

	private static Void updateStackMapTableAttributeAttributeLabels( Mutf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		assert attributeNameConstant == StackMapTableAttribute.kind.mutf8Name;
		StackMapTableAttribute stackMapTableAttribute = attribute.asStackMapTableAttribute();
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
		return null;
	}

	private static Void updateLineNumberTableAttributeLabels( Mutf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		assert attributeNameConstant == LineNumberTableAttribute.kind.mutf8Name;
		return null;
	}

	private static Void updateUnknownAttributeLabels( Mutf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		return null;
	}

	private static void collectTargetInstructionsFromVerificationTypes( Consumer<Optional<Instruction>> targetInstructionConsumer, Iterable<VerificationType> verificationTypes )
	{
		for( VerificationType verificationType : verificationTypes )
			collectTargetInstructionsFromVerificationType( targetInstructionConsumer, verificationType );
	}

	private static void collectTargetInstructionsFromVerificationType( Consumer<Optional<Instruction>> targetInstructionConsumer, VerificationType verificationType )
	{
		verificationType.visit( new VerificationType.Visitor<Void>()
		{
			@Override public Void visit( SimpleVerificationType simpleVerificationType )
			{
				return null; //nothing to do
			}
			@Override public Void visit( ObjectVerificationType objectVerificationType )
			{
				return null; //nothing to do
			}
			@Override public Void visit( UninitializedVerificationType uninitializedVerificationType )
			{
				targetInstructionConsumer.accept( Optional.of( uninitializedVerificationType.instruction ) );
				return null;
			}
		} );
	}

	private static Map<Instruction,Integer> getLineNumberFromInstructionMap( CodeAttribute codeAttribute )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = new HashMap<>();
		codeAttribute.attributeSet().tryGetAttributeByName( LineNumberTableAttribute.kind.mutf8Name ) //
			.map( a -> a.asLineNumberTableAttribute() ) //
			.ifPresent( lineNumberTableAttribute -> //
			{
				for( LineNumberEntry entry : lineNumberTableAttribute.lineNumbers() )
					Kit.map.add( lineNumberFromInstructionMap, entry.instruction(), entry.lineNumber() );
			} );
		return lineNumberFromInstructionMap;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

	private static void valueConstantToStringBuilder( ValueConstant<?> constant, StringBuilder builder )
	{
		switch( constant.tag )
		{
			case Mutf8 -> mutf8ConstantToStringBuilder( constant.asMutf8Constant(), builder );
			case Integer -> integerConstantToStringBuilder( constant.asIntegerConstant(), builder );
			case Float -> floatConstantToStringBuilder( constant.asFloatConstant(), builder );
			case Long -> longConstantToStringBuilder( constant.asLongConstant(), builder );
			case Double -> doubleConstantToStringBuilder( constant.asDoubleConstant(), builder );
			case String -> stringConstantToStringBuilder( constant.asStringConstant(), builder );
			default -> throw new AssertionError( constant );
		}
	}

	private static void mutf8ConstantToStringBuilder( Mutf8Constant mutf8Constant, StringBuilder builder )
	{
		Kit.stringBuilder.appendEscapedForJava( builder, mutf8Constant.stringValue(), '"' );
	}

	private static void integerConstantToStringBuilder( IntegerConstant integerConstant, StringBuilder builder )
	{
		builder.append( integerConstant.value );
	}

	private static void floatConstantToStringBuilder( FloatConstant floatConstant, StringBuilder builder )
	{
		builder.append( floatConstant.value ).append( 'f' );
	}

	private static void longConstantToStringBuilder( LongConstant longConstant, StringBuilder builder )
	{
		builder.append( longConstant.value ).append( 'L' );
	}

	private static void doubleConstantToStringBuilder( DoubleConstant doubleConstant, StringBuilder builder )
	{
		builder.append( doubleConstant.value );
	}

	private static void stringConstantToStringBuilder( StringConstant stringConstant, StringBuilder builder )
	{
		mutf8ConstantToStringBuilder( stringConstant.valueConstant(), builder );
	}

	private static <E extends Enum<E>> void flagsToStringBuilder( FlagSet<E> flagSet, StringBuilder stringBuilder )
	{
		stringBuilder.append( "[" );
		boolean first = true;
		for( E value : flagSet.flags() )
		{
			first = Kit.stringBuilder.appendDelimiter( stringBuilder, first, ", " );
			stringBuilder.append( value.toString().toLowerCase( Locale.ROOT ) );
		}
		stringBuilder.append( "]" );
	}

	private void summarizeAbsoluteInstruction( Optional<Instruction> instruction, StringBuilder builder )
	{
		String label = getLabel( instruction );
		builder.append( label );
	}

	private void summarizeAbsoluteInstruction( Instruction instruction, StringBuilder builder )
	{
		summarizeAbsoluteInstruction( Optional.of( instruction ), builder );
	}

	private static Twig twigFromElementValuePair( ElementValuePair elementValuePair )
	{
		var builder = new StringBuilder();
		builder.append( "parameter name = " );
		valueConstantToStringBuilder( elementValuePair.nameConstant(), builder );
		ElementValue elementValue = elementValuePair.elementValue();
		return Twig.of( builder.toString(), //
			twigFromElementValue( elementValue.tag.name() + " value = ", elementValue ) );
	}

	private Twig twigFromAttributeSet( AttributeSet attributeSet )
	{
		List<Twig> children = new ArrayList<>();
		for( Attribute attribute : attributeSet )
		{
			Twig twig = switch( attribute.kind.name )
				{
					case BootstrapMethodsAttribute.name -> twigFromBootstrapMethodsAttribute( attribute.kind.name, attribute.asBootstrapMethodsAttribute() );
					case MethodParametersAttribute.name -> twigFromMethodParametersAttribute( attribute.kind.name, attribute.asMethodParametersAttribute() );
					case AnnotationDefaultAttribute.name -> twigFromAnnotationDefaultAttribute( attribute.kind.name, attribute.asAnnotationDefaultAttribute() );
					case RuntimeInvisibleAnnotationsAttribute.name, //
						RuntimeVisibleAnnotationsAttribute.name -> twigFromAnnotationsAttribute( attribute.kind.name, attribute.asAnnotationsAttribute() );
					case StackMapTableAttribute.name -> twigFromStackMapTableAttribute( attribute.kind.name, attribute.asStackMapTableAttribute() );
					case LineNumberTableAttribute.name -> twigFromLineNumberTableAttribute( attribute.kind.name, attribute.asLineNumberTableAttribute() );
					case LocalVariableTableAttribute.name -> twigFromLocalVariableTableAttribute( attribute.kind.name, attribute.asLocalVariableTableAttribute() );
					case LocalVariableTypeTableAttribute.name -> twigFromLocalVariableTypeTableAttribute( attribute.kind.name, attribute.asLocalVariableTypeTableAttribute() );
					case NestHostAttribute.name -> twigFromNestHostAttribute( attribute.kind.name, attribute.asNestHostAttribute() );
					case NestMembersAttribute.name -> twigFromNestMembersAttribute( attribute.kind.name, attribute.asNestMembersAttribute() );
					case ConstantValueAttribute.name -> twigFromConstantValueAttribute( attribute.kind.name, attribute.asConstantValueAttribute() );
					case DeprecatedAttribute.name -> twigFromDeprecatedAttribute( attribute.kind.name, attribute.asDeprecatedAttribute() );
					case EnclosingMethodAttribute.name -> twigFromEnclosingMethodAttribute( attribute.kind.name, attribute.asEnclosingMethodAttribute() );
					case ExceptionsAttribute.name -> twigFromExceptionsAttribute( attribute.kind.name, attribute.asExceptionsAttribute() );
					case InnerClassesAttribute.name -> twigFromInnerClassesAttribute( attribute.kind.name, attribute.asInnerClassesAttribute() );
					case RuntimeVisibleParameterAnnotationsAttribute.name, //
						RuntimeInvisibleParameterAnnotationsAttribute.name -> twigFromParameterAnnotationsAttribute( attribute.kind.name, attribute.asParameterAnnotationsAttribute() );
					case SignatureAttribute.name -> twigFromSignatureAttribute( attribute.kind.name, attribute.asSignatureAttribute() );
					case SourceFileAttribute.name -> twigFromSourceFileAttribute( attribute.kind.name, attribute.asSourceFileAttribute() );
					case SyntheticAttribute.name -> twigFromSyntheticAttribute( attribute.kind.name, attribute.asSyntheticAttribute() );
					case CodeAttribute.name -> twigFromCodeAttribute( attribute.kind.name, attribute.asCodeAttribute() );
					case RuntimeVisibleTypeAnnotationsAttribute.name, //
						RuntimeInvisibleTypeAnnotationsAttribute.name -> twigFromTypeAnnotationsAttribute( attribute.kind.name, attribute.asTypeAnnotationsAttribute() );
					default -> twigFromUnknownAttribute( attribute.kind.name, attribute.asUnknownAttribute() );
				};
			children.add( twig );
		}
		return Twig.of( "attributes (" + attributeSet.size() + " entries)", children );
	}

	private static Twig twigFromBootstrapMethod( BootstrapMethod bootstrapMethod )
	{
		var builder = new StringBuilder();
		summarizeBootstrapMethod( bootstrapMethod, builder );
		return Twig.of( builder.toString() );
	}

	private static void summarizeBootstrapMethod( BootstrapMethod bootstrapMethod, StringBuilder builder )
	{
		builder.append( "methodHandle = " );
		summarizeMethodHandleConstant( bootstrapMethod.methodHandleConstant(), builder );
		List<Constant> argumentConstants = bootstrapMethod.argumentConstants();
		builder.append( " arguments: " );
		boolean first = true;
		for( Constant argumentConstant : argumentConstants )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			switch( argumentConstant.tag )
			{
				case String -> valueConstantToStringBuilder( argumentConstant.asValueConstant(), builder );
				case MethodType -> summarizeMethodTypeConstant( argumentConstant.asMethodTypeConstant(), builder );
				case MethodHandle -> summarizeMethodHandleConstant( argumentConstant.asMethodHandleConstant(), builder );
				case Class -> summarizeClassConstant( argumentConstant.asClassConstant(), builder );
				default -> throw new AssertionError( argumentConstant );
			}
		}
	}

	private static Twig twigFromByteCodeAnnotation( String prefix, Annotation byteCodeAnnotation )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( ' ' );
		summarizeByteCodeAnnotation( byteCodeAnnotation, builder );
		String header = builder.toString();
		return Twig.of( header, //
			byteCodeAnnotation.elementValuePairs().stream().map( p -> twigFromElementValuePair( p ) ).toList() );
	}

	private static void summarizeByteCodeAnnotation( Annotation byteCodeAnnotation, StringBuilder builder )
	{
		builder.append( "type = " );
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( byteCodeAnnotation.typeDescriptor() ) );
		builder.append( ", " ).append( byteCodeAnnotation.elementValuePairs().size() ).append( " parameters" );
	}

	private Twig twigFromByteCodeField( ByteCodeField byteCodeField )
	{
		var builder = new StringBuilder();
		builder.append( "field accessFlags = " );
		flagsToStringBuilder( byteCodeField.modifierSet, builder );
		builder.append( ", name = " ).append( byteCodeField.name() );
		builder.append( ", descriptor = " );
		appendClassDescriptor( builder, byteCodeField.descriptor() );
		String header = builder.toString();
		AttributeSet attributeSet = byteCodeField.attributeSet;
		return Twig.of( header, attributeSet.size() == 0 ? List.of() : List.of( twigFromAttributeSet( attributeSet ) ) );
	}

	private Twig twigFromByteCodeMethod( ByteCodeMethod byteCodeMethod )
	{
		var builder = new StringBuilder();
		builder.append( "method accessFlags = " );
		flagsToStringBuilder( byteCodeMethod.modifierSet, builder );
		builder.append( ", name = " ).append( byteCodeMethod.name() );
		builder.append( ", descriptor = " );
		appendMethodDescriptor( builder, byteCodeMethod.descriptor() );
		String header = builder.toString();
		AttributeSet attributeSet = byteCodeMethod.attributeSet;
		return Twig.of( header, attributeSet.size() == 0 ? List.of() : List.of( twigFromAttributeSet( attributeSet ) ) );
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

	private Twig twigFromByteCodeType()
	{
		String header = summarizeByteCodeType( byteCodeType );
		List<Twig> children = new ArrayList<>();
		Collection<ClassConstant> interfaces = byteCodeType.interfaceClassConstants();
		children.add( Twig.of( "interfaces (" + interfaces.size() + " entries)", //
			interfaces.stream().map( constant -> twigFromClassConstant( constant.tag.name(), constant ) ).toList() ) );
		Collection<Constant> extraConstants = byteCodeType.extraConstants();
		children.add( Twig.of( "extra constants (" + extraConstants.size() + " entries)", //
			extraConstants.stream().sorted().map( constant -> twigFromExtraConstant( constant.tag.name(), constant ) ).toList() ) );
		Collection<ByteCodeField> fields = byteCodeType.fields();
		children.add( Twig.of( "fields (" + fields.size() + " entries)", //
			fields.stream().map( field -> twigFromByteCodeField( field ) ).toList() ) );
		Collection<ByteCodeMethod> methods = byteCodeType.methods();
		children.add( Twig.of( "methods (" + methods.size() + " entries)", //
			methods.stream().map( method -> twigFromByteCodeMethod( method ) ).toList() ) );
		children.add( twigFromAttributeSet( byteCodeType.attributeSet() ) );
		return Twig.of( header, children );
	}

	private static String summarizeByteCodeType( ByteCodeType byteCodeType )
	{
		StringBuilder builder = new StringBuilder( 1024 );
		builder.append( " version = " ).append( byteCodeType.majorVersion ).append( '.' ).append( byteCodeType.minorVersion );
		builder.append( ", accessFlags = " );
		flagsToStringBuilder( byteCodeType.modifierSet(), builder );
		builder.append( ", thisClass = " ).append( ByteCodeHelpers.typeNameFromClassDesc( byteCodeType.descriptor() ) );
		byteCodeType.superClassConstant.ifPresent( s1 -> builder.append( ", superClass = " ).append( ByteCodeHelpers.typeNameFromClassDesc( s1.classDescriptor() ) ) );
		return builder.toString();
	}

	private Twig twigFromExceptionInfo( ExceptionInfo exceptionInfo )
	{
		var builder = new StringBuilder();
		builder.append( " start = " );
		summarizeAbsoluteInstruction( exceptionInfo.startInstruction, builder );
		builder.append( " end = " );
		summarizeAbsoluteInstruction( exceptionInfo.endInstruction, builder );
		builder.append( " handler = " );
		summarizeAbsoluteInstruction( exceptionInfo.handlerInstruction, builder );
		if( exceptionInfo.catchTypeConstant.isPresent() )
		{
			builder.append( " catchType = " );
			summarizeClassConstant( exceptionInfo.catchTypeConstant.get(), builder );
		}
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromInnerClass( InnerClass innerClass )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		Optional<ClassConstant> outerClassConstant = innerClass.outerClassConstant();
		if( outerClassConstant.isPresent() )
		{
			builder.append( "outerClass = " );
			summarizeClassConstant( outerClassConstant.get(), builder );
			builder.append( ", " );
		}
		builder.append( ", accessFlags = " );
		flagsToStringBuilder( innerClass.modifierSet(), builder );
		builder.append( ", innerClass=" );
		summarizeClassConstant( innerClass.innerClassConstant(), builder );
		Optional<Mutf8Constant> innerNameConstant = innerClass.innerNameConstant();
		if( innerNameConstant.isPresent() )
		{
			builder.append( ", innerName = " );
			valueConstantToStringBuilder( innerNameConstant.get(), builder );
		}
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromLineNumber( LineNumberEntry lineNumber )
	{
		var builder = new StringBuilder();
		builder.append( " lineNumber = " ).append( lineNumber.lineNumber() );
		builder.append( ", start = " );
		summarizeAbsoluteInstruction( lineNumber.instruction(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromLocalVariable( LocalVariable localVariable )
	{
		var builder = new StringBuilder();
		builder.append( " index = " ).append( localVariable.index );
		builder.append( ", start = " );
		summarizeAbsoluteInstruction( localVariable.startInstruction, builder );
		builder.append( ", end = " );
		summarizeAbsoluteInstruction( localVariable.endInstruction, builder );
		builder.append( ", name = " ).append( localVariable.name() );
		builder.append( ", descriptor = " );
		appendClassDescriptor( builder, localVariable.descriptor() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromLocalVariableType( LocalVariableType localVariableType )
	{
		var builder = new StringBuilder();
		builder.append( " index = " ).append( localVariableType.index );
		builder.append( ", start = " );
		summarizeAbsoluteInstruction( localVariableType.startInstruction, builder );
		builder.append( ", end = " );
		summarizeAbsoluteInstruction( localVariableType.endInstruction, builder );
		builder.append( ", name = " ).append( localVariableType.nameConstant );
		builder.append( ", signature = " ).append( localVariableType.signatureConstant );
		builder.append( " (" );
		builder.append( localVariableType.nameConstant.stringValue() );
		builder.append( ' ' );
		builder.append( localVariableType.signatureConstant.stringValue() );
		builder.append( ")" );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromMethodParameter( MethodParameter methodParameter )
	{
		var builder = new StringBuilder();
		builder.append( " accessFlags = " );
		flagsToStringBuilder( methodParameter.modifierSet, builder );
		builder.append( ", name = " );
		builder.append( methodParameter.nameConstant.stringValue() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromParameterAnnotationSet( ParameterAnnotationSet parameterAnnotationSet )
	{
		var builder = new StringBuilder();
		builder.append( " ParameterAnnotations " );
		builder.append( parameterAnnotationSet.annotations().size() ).append( " entries" );
		String header = builder.toString();
		return Twig.of( header, //
			parameterAnnotationSet.annotations().stream().map( a -> twigFromByteCodeAnnotation( "annotation", a ) ).toList() );
	}

	private void twigFromRelativeInstructionReference( Instruction instruction, StringBuilder builder )
	{
		String label = getLabel( Optional.of( instruction ) );
		builder.append( label );
	}

//	private static Twig twigFromTypeAnnotationElementValuePair( ElementValuePair elementValuePair )
//	{
//		var builder = new StringBuilder();
//		builder.append( " elementName = " ).append( elementValuePair.nameConstant() );
//		builder.append( ", elementValue = " );
//		ElementValue elementValue = elementValuePair.elementValue();
//		builder.append( "name = " );
//		switch( elementValue.tag )
//		{
//			case Byte, Boolean, Short, Long, Integer, Float, Double, Character -> //
//				{
//					ConstElementValue constAnnotationValue = elementValue.asConstAnnotationValue();
//					builder.append( constAnnotationValue.tag.name() );
//					builder.append( " value = " );
//					valueConstantToStringBuilder( constAnnotationValue.valueConstant(), builder );
//				}
//			case Enum -> //
//				{
//					EnumElementValue enumAnnotationValue = elementValue.asEnumAnnotationValue();
//					builder.append( "type = " ).append( ByteCodeHelpers.typeNameFromClassDesc( enumAnnotationValue.typeDescriptor() ) );
//					builder.append( ", value = " ).append( enumAnnotationValue.valueName() );
//				}
//			case Class -> //
//				{
//					ClassElementValue classAnnotationValue = elementValue.asClassAnnotationValue();
//					builder.append( "class = " ).append( classAnnotationValue.nameConstant().stringValue() );
//				}
//			case Annotation -> //
//				{
//					AnnotationElementValue annotationAnnotationValue = elementValue.asAnnotationAnnotationValue();
//					builder.append( "annotation = { " );
//					Annotation annotation = annotationAnnotationValue.annotation();
//					builder.append( "type = " ).append( ByteCodeHelpers.typeNameFromClassDesc( annotation.typeDescriptor() ) );
//					builder.append( ", " ).append( annotation.parameters().size() ).append( " elements" );
//					builder.append( " }" );
//				}
//			case Array -> //
//				{
//					ArrayElementValue arrayAnnotationValue = elementValue.asArrayAnnotationValue();
//					builder.append( arrayAnnotationValue.annotationValues().size() ).append( " elements" );
//				}
//			default -> throw new AssertionError( elementValue );
//		}
//		String header = builder.toString();
//		return Twig.of( header );
//	}

	private static Twig twigFromLocalVariableTargetEntry( String prefix, LocalVariableTarget.Entry localVariableTargetEntry )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "start = " ).append( localVariableTargetEntry.startPc() ).append( ", " );
		builder.append( "length = " ).append( localVariableTargetEntry.length() ).append( ", " );
		builder.append( "index = " ).append( localVariableTargetEntry.index() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromTypePathEntry( String prefix, TypePath.Entry entry )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "pathKind = " ).append( entry.pathKind() ).append( ", " );
		builder.append( "argumentIndex = " ).append( entry.argumentIndex() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromClassConstant( String prefix, ClassConstant classConstant )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "name = " );
		valueConstantToStringBuilder( classConstant.nameConstant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeClassConstant( ClassConstant classConstant, StringBuilder builder )
	{
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( classConstant.classDescriptor() ) );
	}

	private static Twig twigFromValueConstant( String prefix, ValueConstant<?> valueConstant )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		valueConstantToStringBuilder( valueConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private void summarizeInvokeDynamicConstant( InvokeDynamicConstant invokeDynamicConstant, StringBuilder builder )
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
		builder.append( methodHandleConstant.referenceKind().name() );
		builder.append( ", referenceConstant = " );
		ReferenceConstant referenceConstant = methodHandleConstant.referenceConstant();
		switch( referenceConstant.tag )
		{
			case FieldReference -> //
				summarizeFieldReferenceConstant( referenceConstant.asFieldReferenceConstant(), builder );
			case InterfaceMethodReference -> //
				summarizeInterfaceMethodReferenceConstant( referenceConstant.asInterfaceMethodReferenceConstant(), builder );
			case MethodReference -> //
				summarizePlainMethodReferenceConstant( referenceConstant.asPlainMethodReferenceConstant(), builder );
			default -> throw new AssertionError();
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

	private static void summarizeMethodTypeConstant( MethodTypeConstant methodTypeConstant, StringBuilder builder )
	{
		//TODO
		valueConstantToStringBuilder( methodTypeConstant.descriptorConstant, builder );
	}

	private static Twig twigFromExtraConstant( String prefix, Constant constant )
	{
		return switch( constant.tag )
			{
				case Class -> //
					twigFromClassConstant( prefix, constant.asClassConstant() );
				case Double, Float, Integer, Long, String, Mutf8 -> //
					twigFromValueConstant( prefix, constant.asValueConstant() );
				default -> throw new AssertionError();
			};
	}

	private static Twig twigFromElementValue( String prefix, ElementValue annotationValue )
	{
		return switch( annotationValue.tag )
			{
				case Byte, Character, Double, Float, Integer, Long, Short, Boolean, String -> //
					twigFromConstAnnotationValue( prefix, annotationValue.asConstAnnotationValue() );
				case Annotation -> twigFromAnnotationAnnotationValue( prefix, annotationValue.asAnnotationAnnotationValue() );
				case Array -> twigFromArrayAnnotationValue( prefix, annotationValue.asArrayAnnotationValue() );
				case Class -> twigFromClassAnnotationValue( prefix, annotationValue.asClassAnnotationValue() );
				case Enum -> twigFromEnumAnnotationValue( prefix, annotationValue.asEnumAnnotationValue() );
			};
	}

	private static Twig twigFromClassAnnotationValue( String prefix, ClassElementValue classAnnotationValue )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		valueConstantToStringBuilder( classAnnotationValue.nameConstant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromConstAnnotationValue( String prefix, ConstElementValue constAnnotationValue )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		valueConstantToStringBuilder( constAnnotationValue.valueConstant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromAnnotationAnnotationValue( String prefix, AnnotationElementValue annotationAnnotationValue )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		summarizeByteCodeAnnotation( annotationAnnotationValue.annotation(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromArrayAnnotationValue( String prefix, ArrayElementValue arrayAnnotationValue )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( arrayAnnotationValue.annotationValues().size() );
		builder.append( " elements" );
		String header = builder.toString();
		return Twig.of( header, //
			arrayAnnotationValue.annotationValues().stream().map( a -> twigFromElementValue( "element", a ) ).toList() );
	}

	private static Twig twigFromEnumAnnotationValue( String prefix, EnumElementValue enumAnnotationValue )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "type = " );
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( enumAnnotationValue.typeDescriptor() ) );
		builder.append( ", value = " );
		builder.append( enumAnnotationValue.valueName() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromAnnotationDefaultAttribute( String prefix, AnnotationDefaultAttribute annotationDefaultAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		String header = builder.toString();
		ElementValue annotationValue = annotationDefaultAttribute.annotationValue();
		return Twig.of( header, //
			twigFromElementValue( annotationValue.tag.name() + " value = ", annotationValue ) );
	}

	private static Twig twigFromAnnotationsAttribute( String prefix, AnnotationsAttribute annotationsAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( annotationsAttribute.annotations().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			annotationsAttribute.annotations().stream().map( a -> twigFromByteCodeAnnotation( "annotation ", a ) ).toList() );
	}

	private static Twig twigFromBootstrapMethodsAttribute( String prefix, BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( bootstrapMethodsAttribute.bootstrapMethods().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			bootstrapMethodsAttribute.bootstrapMethods().stream().map( m -> twigFromBootstrapMethod( m ) ).toList() );
	}

	private static Twig twigFromConstantValueAttribute( String prefix, ConstantValueAttribute constantValueAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		ValueConstant<?> valueConstant = constantValueAttribute.valueConstant();
		valueConstantToStringBuilder( valueConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromDeprecatedAttribute( String prefix, @SuppressWarnings( "unused" ) DeprecatedAttribute deprecatedAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromEnclosingMethodAttribute( String prefix, EnclosingMethodAttribute enclosingMethodAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		if( enclosingMethodAttribute.hasMethod() )
		{
			builder.append( "name+type+descriptor = " );
			//
			builder.append( "type = " );
			builder.append( ByteCodeHelpers.typeNameFromClassDesc( enclosingMethodAttribute.classDescriptor() ) );
			builder.append( ", name = " );
			builder.append( enclosingMethodAttribute.methodName() );
			builder.append( ", descriptor = " );
			appendMethodDescriptor( builder, enclosingMethodAttribute.methodDescriptor() );
		}
		else
		{
			builder.append( "class = " );
			builder.append( ByteCodeHelpers.typeNameFromClassDesc( enclosingMethodAttribute.classDescriptor() ) );
		}
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromExceptionsAttribute( String prefix, ExceptionsAttribute exceptionsAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( exceptionsAttribute.exceptionClassConstants().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			exceptionsAttribute.exceptionClassConstants().stream().map( c -> twigFromClassConstant( "exception ", c ) ).toList() );
	}

	private static Twig twigFromInnerClassesAttribute( String prefix, InnerClassesAttribute innerClassesAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( innerClassesAttribute.innerClasses().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			innerClassesAttribute.innerClasses().stream().map( c -> twigFromInnerClass( c ) ).toList() );
	}

	private Twig twigFromLineNumberTableAttribute( String prefix, LineNumberTableAttribute lineNumberTableAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( lineNumberTableAttribute.lineNumbers().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			lineNumberTableAttribute.lineNumbers().stream().map( n -> twigFromLineNumber( n ) ).toList() );
	}

	private Twig twigFromLocalVariableTableAttribute( String prefix, LocalVariableTableAttribute localVariableTableAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( localVariableTableAttribute.localVariables().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			localVariableTableAttribute.localVariables().stream().map( e -> twigFromLocalVariable( e ) ).toList() );
	}

	private Twig twigFromLocalVariableTypeTableAttribute( String prefix, LocalVariableTypeTableAttribute localVariableTypeTableAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( localVariableTypeTableAttribute.localVariableTypes().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			localVariableTypeTableAttribute.localVariableTypes().stream().map( e -> twigFromLocalVariableType( e ) ).toList() );
	}

	private static Twig twigFromMethodParametersAttribute( String prefix, MethodParametersAttribute methodParametersAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( methodParametersAttribute.methodParameters().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			methodParametersAttribute.methodParameters().stream().map( p -> twigFromMethodParameter( p ) ).toList() );
	}

	private static Twig twigFromNestHostAttribute( String prefix, NestHostAttribute nestHostAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "class = " );
		builder.append( ByteCodeHelpers.typeNameFromClassDesc( nestHostAttribute.hostClassConstant.classDescriptor() ) );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromNestMembersAttribute( String prefix, NestMembersAttribute nestMembersAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( nestMembersAttribute.memberClassConstants().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			nestMembersAttribute.memberClassConstants().stream().map( c -> Twig.of( "member = " + ByteCodeHelpers.typeNameFromClassDesc( c.classDescriptor() ) ) ).toList() );
	}

	private static Twig twigFromParameterAnnotationsAttribute( String prefix, ParameterAnnotationsAttribute parameterAnnotationsAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( parameterAnnotationsAttribute.kind.name ).append( ' ' ).append( parameterAnnotationsAttribute.parameterAnnotationSets().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			parameterAnnotationsAttribute.parameterAnnotationSets().stream().map( a -> twigFromParameterAnnotationSet( a ) ).toList() );
	}

	private static Twig twigFromSignatureAttribute( String prefix, SignatureAttribute signatureAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( signatureAttribute.signatureConstant().stringValue() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromSourceFileAttribute( String prefix, SourceFileAttribute sourceFileAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "value = " );
		valueConstantToStringBuilder( sourceFileAttribute.valueConstant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromStackMapTableAttribute( String prefix, StackMapTableAttribute stackMapTableAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( stackMapTableAttribute.frames().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		List<Twig> twigs = new ArrayList<>();
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( StackMapFrame frame : stackMapTableAttribute.frames() )
		{
			Twig result;
			if( frame instanceof ChopStackMapFrame )
				result = twigFromChopStackMapFrame( frame.asChopStackMapFrame(), previousFrame );
			else if( frame instanceof FullStackMapFrame )
				result = twigFromFullStackMapFrame( frame.asFullStackMapFrame(), previousFrame );
			else if( frame instanceof AppendStackMapFrame )
				result = twigFromAppendStackMapFrame( frame.asAppendStackMapFrame(), previousFrame );
			else if( frame instanceof SameStackMapFrame )
				result = twigFromSameStackMapFrame( frame.asSameStackMapFrame(), previousFrame );
			else if( frame instanceof SameLocals1StackItemStackMapFrame )
				result = twigFromSameLocals1StackItemStackMapFrame( frame.asSameLocals1StackItemStackMapFrame(), previousFrame );
			else
				throw new AssertionError();
			twigs.add( result );
			previousFrame = Optional.of( frame );
		}
		return Twig.of( header, twigs );
	}

	private static Twig twigFromSyntheticAttribute( String prefix, @SuppressWarnings( "unused" ) SyntheticAttribute syntheticAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromTypeAnnotation( TypeAnnotation typeAnnotation )
	{
		var builder = new StringBuilder();
		builder.append( " targetType = " ).append( String.format( "0x%02x ", typeAnnotation.target().type.number ) );
		builder.append( "targetPath = " );
		TypePath targetPath = typeAnnotation.typePath();
		builder.append( targetPath.entries().size() ).append( " entries" );
		builder.append( "typeIndex = " ).append( typeAnnotation.typeIndex() );
		builder.append( ", " ).append( typeAnnotation.elementValuePairs().size() ).append( " elementValuePairs" );
		String header = builder.toString();
		Target target = typeAnnotation.target();
		Twig twig;
		if( target instanceof CatchTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof EmptyTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof FormalParameterTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof LocalVariableTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof OffsetTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof SupertypeTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof ThrowsTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof TypeArgumentTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof TypeParameterBoundTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else if( target instanceof TypeParameterTarget )
			twig = twigFromTypeAnnotationsAttributeTarget( target );
		else
			throw new AssertionError();
		return Twig.of( header, //
			Twig.of( "target", twig ), //
			Twig.of( "elementValuePairs", //
				typeAnnotation.elementValuePairs().stream().map( a -> twigFromElementValuePair( a ) ).toList() ) );
	}

	private static Twig twigFromTypeAnnotationsAttribute( String prefix, TypeAnnotationsAttribute typeAnnotationsAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( typeAnnotationsAttribute.kind.name ).append( ' ' );
		builder.append( typeAnnotationsAttribute.typeAnnotations().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			typeAnnotationsAttribute.typeAnnotations().stream().map( a -> twigFromTypeAnnotation( a ) ).toList() );
	}

	private static Twig twigFromUnknownAttribute( String prefix, UnknownAttribute unknownAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( unknownAttribute.buffer().length() ).append( " bytes" );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromBranchInstruction( BranchInstruction branchInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( branchInstruction.getOpCode() ) );
		builder.append( ' ' );
		twigFromRelativeInstructionReference( branchInstruction.getTargetInstruction(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromConditionalBranchInstruction( ConditionalBranchInstruction conditionalBranchInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( conditionalBranchInstruction.getOpCode() ) );
		builder.append( ' ' );
		twigFromRelativeInstructionReference( conditionalBranchInstruction.getTargetInstruction(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromConstantReferencingInstruction( ConstantReferencingInstruction constantReferencingInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( constantReferencingInstruction.opCode ) );
		builder.append( ' ' );
		Constant constant = constantReferencingInstruction.constant;
		switch( constant.tag )
		{
			case FieldReference -> summarizeFieldReferenceConstant( constant.asFieldReferenceConstant(), builder );
			case InterfaceMethodReference -> summarizeInterfaceMethodReferenceConstant( constant.asInterfaceMethodReferenceConstant(), builder );
			case MethodReference -> summarizePlainMethodReferenceConstant( constant.asPlainMethodReferenceConstant(), builder );
			case Class -> summarizeClassConstant( constant.asClassConstant(), builder );
			default -> throw new AssertionError( constant );
		}
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromIIncInstruction( IIncInstruction iIncInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		if( iIncInstruction.isWide() )
			builder.append( "wide " );
		builder.append( OpCode.getOpCodeName( OpCode.IINC ) );
		builder.append( ' ' ).append( iIncInstruction.index );
		builder.append( ' ' ).append( iIncInstruction.delta );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromImmediateLoadConstantInstruction( ImmediateLoadConstantInstruction immediateLoadConstantInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( immediateLoadConstantInstruction.opCode ) );
		builder.append( ' ' );
		builder.append( immediateLoadConstantInstruction.immediateValue );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromIndirectLoadConstantInstruction( IndirectLoadConstantInstruction indirectLoadConstantInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( Kit.get( true ) ? "LDCx" : OpCode.getOpCodeName( indirectLoadConstantInstruction.opCode ) );
		builder.append( ' ' );
		Constant constant = indirectLoadConstantInstruction.constant;
		switch( constant.tag )
		{
			case Integer, Long, Float, Double, String -> valueConstantToStringBuilder( constant.asValueConstant(), builder );
			case Class -> summarizeClassConstant( constant.asClassConstant(), builder );
			default -> throw new AssertionError( constant );
		}
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromInvokeDynamicInstruction( InvokeDynamicInstruction invokeDynamicInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( OpCode.INVOKEDYNAMIC ) );
		builder.append( ' ' );
		summarizeInvokeDynamicConstant( invokeDynamicInstruction.invokeDynamicConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromInvokeInterfaceInstruction( InvokeInterfaceInstruction invokeInterfaceInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( OpCode.INVOKEINTERFACE ) );
		builder.append( ' ' );
		summarizeInterfaceMethodReferenceConstant( invokeInterfaceInstruction.interfaceMethodReferenceConstant, builder );
		builder.append( ' ' ).append( invokeInterfaceInstruction.argumentCount ).append( " arguments" );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromLocalVariableInstruction( LocalVariableInstruction localVariableInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		int flavor = localVariableInstruction.index <= 3 && localVariableInstruction.getOpCode() != OpCode.RET ? localVariableInstruction.index : -1;
		boolean wide = !Helpers.isUnsignedByte( localVariableInstruction.index );
		if( wide )
			builder.append( "wide " );
		builder.append( OpCode.getOpCodeName( localVariableInstruction.getOpCode() ) );
		if( flavor == -1 )
			builder.append( ' ' ).append( localVariableInstruction.index );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromLookupSwitchInstruction( LookupSwitchInstruction lookupSwitchInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( OpCode.LOOKUPSWITCH ) );
		builder.append( " default: " );
		twigFromRelativeInstructionReference( lookupSwitchInstruction.getDefaultInstruction(), builder );
		builder.append( " value-offset-pairs: [" );
		boolean first = true;
		for( LookupSwitchEntry entry : lookupSwitchInstruction.entries )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			builder.append( entry.value() ).append( ":" );
			twigFromRelativeInstructionReference( entry.getTargetInstruction(), builder );
		}
		builder.append( ']' );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromMultiANewArrayInstruction( MultiANewArrayInstruction multiANewArrayInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( OpCode.MULTIANEWARRAY ) );
		builder.append( ' ' );
		summarizeClassConstant( multiANewArrayInstruction.classConstant, builder );
		builder.append( ' ' ).append( multiANewArrayInstruction.dimensionCount ).append( " dimensions" );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromNewPrimitiveArrayInstruction( NewPrimitiveArrayInstruction newPrimitiveArrayInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( OpCode.NEWARRAY ) );
		builder.append( ' ' ).append( NewPrimitiveArrayInstruction.Type.fromNumber( newPrimitiveArrayInstruction.type ).name() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromOperandlessInstruction( OperandlessInstruction operandlessInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( operandlessInstruction.opCode ) );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromOperandlessLoadConstantInstruction( OperandlessLoadConstantInstruction operandlessLoadConstantInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( operandlessLoadConstantInstruction.opCode ) );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromTableSwitchInstruction( TableSwitchInstruction tableSwitchInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( OpCode.TABLESWITCH ) );
		builder.append( " default: " );
		twigFromRelativeInstructionReference( tableSwitchInstruction.getDefaultInstruction(), builder );
		int lowValue = tableSwitchInstruction.lowValue;
		builder.append( " range: " ).append( lowValue );
		builder.append( " - " ).append( lowValue + tableSwitchInstruction.getTargetInstructionCount() - 1 );
		builder.append( " offsets: [" );
		boolean first = true;
		for( Instruction targetInstruction : tableSwitchInstruction.targetInstructions() )
		{
			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
			twigFromRelativeInstructionReference( targetInstruction, builder );
		}
		builder.append( ']' );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromCodeAttribute( String prefix, CodeAttribute codeAttribute )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "maxStack = " ).append( codeAttribute.getMaxStack() );
		builder.append( ", maxLocals = " ).append( codeAttribute.getMaxLocals() );
		String header = builder.toString();
		List<ExceptionInfo> exceptionInfos = codeAttribute.exceptionInfos();
		return Twig.of( header, //
			Twig.of( "instructions (" + codeAttribute.instructions().size() + " entries)", //
				twigFromInstructionList( codeAttribute.instructions() ) ), //
			Twig.of( "exceptionInfos (" + exceptionInfos.size() + " entries)", //
				exceptionInfos.stream().map( e -> twigFromExceptionInfo( e ) ).toList() ), //
			twigFromAttributeSet( codeAttribute.attributeSet() ) );
	}

	private List<Twig> twigFromInstructionList( InstructionList instructionList )
	{
		return instructionList.all().stream().flatMap( i -> //
			Stream.concat( //
				tryGetLabelInfo( i ).map( labelInfo -> twigFromLabelInfo( labelInfo ) ).stream(), //
				Stream.of( twigFromInstruction( i ) ) //
			) ).toList();
	}

	private static Twig twigFromLabelInfo( LabelInfo labelInfo )
	{
		var builder = new StringBuilder();
		builder.append( labelInfo.label );
		builder.append( ':' );
		labelInfo.source.ifPresent( s -> builder.append( " // " ).append( s ) );
		return Twig.of( builder.toString() );
	}

	private Twig twigFromInstruction( Instruction instruction )
	{
		Twig twig = switch( instruction.group )
			{
				case TableSwitch -> twigFromTableSwitchInstruction( instruction.asTableSwitchInstruction() );
				case ConditionalBranch -> twigFromConditionalBranchInstruction( instruction.asConditionalBranchInstruction() );
				case ConstantReferencing -> twigFromConstantReferencingInstruction( instruction.asConstantReferencingInstruction() );
				case IInc -> twigFromIIncInstruction( instruction.asIIncInstruction() );
				case ImmediateLoadConstant -> twigFromImmediateLoadConstantInstruction( instruction.asImmediateLoadConstantInstruction() );
				case IndirectLoadConstant -> twigFromIndirectLoadConstantInstruction( instruction.asIndirectLoadConstantInstruction() );
				case InvokeDynamic -> twigFromInvokeDynamicInstruction( instruction.asInvokeDynamicInstruction() );
				case InvokeInterface -> twigFromInvokeInterfaceInstruction( instruction.asInvokeInterfaceInstruction() );
				case LocalVariable -> twigFromLocalVariableInstruction( instruction.asLocalVariableInstruction() );
				case LookupSwitch -> twigFromLookupSwitchInstruction( instruction.asLookupSwitchInstruction() );
				case MultiANewArray -> twigFromMultiANewArrayInstruction( instruction.asMultiANewArrayInstruction() );
				case NewPrimitiveArray -> twigFromNewPrimitiveArrayInstruction( instruction.asNewPrimitiveArrayInstruction() );
				case Operandless -> twigFromOperandlessInstruction( instruction.asOperandlessInstruction() );
				case OperandlessLoadConstant -> twigFromOperandlessLoadConstantInstruction( instruction.asOperandlessLoadConstantInstruction() );
				case Branch -> twigFromBranchInstruction( instruction.asBranchInstruction() );
			};
		return twig;
	}

	private Twig twigFromAppendStackMapFrame( AppendStackMapFrame appendFrame, Optional<StackMapFrame> previousFrame )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		builder.append( appendFrame.getName( previousFrame ) ).append( ' ' );
		String label = getLabel( Optional.of( appendFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.of( header, //
			twigFromVerificationTypes( "localVerificationTypes", appendFrame.localVerificationTypes() ) );
	}

	private Twig twigFromChopStackMapFrame( ChopStackMapFrame chopFrame, Optional<StackMapFrame> previousFrame )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		builder.append( chopFrame.getName( previousFrame ) ).append( ' ' );
		String label = getLabel( Optional.of( chopFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		builder.append( ' ' );
		builder.append( "count = " ).append( chopFrame.count() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromFullStackMapFrame( FullStackMapFrame fullFrame, Optional<StackMapFrame> previousFrame )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		builder.append( fullFrame.getName( previousFrame ) ).append( ' ' );
		String label = getLabel( Optional.of( fullFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.of( header, //
			twigFromVerificationTypes( "localVerificationTypes", fullFrame.localVerificationTypes() ), //
			twigFromVerificationTypes( "stackVerificationTypes", fullFrame.stackVerificationTypes() ) );
	}

	private Twig twigFromVerificationTypes( String prefix, List<VerificationType> verificationTypes )
	{
		return Twig.of( prefix + " (" + verificationTypes.size() + " entries)", //
			verificationTypes.stream().map( t -> twigFromVerificationType( t ) ).toList() );
	}

	private Twig twigFromSameLocals1StackItemStackMapFrame( SameLocals1StackItemStackMapFrame sameLocals1StackItemFrame, Optional<StackMapFrame> previousFrame )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		builder.append( sameLocals1StackItemFrame.getName( previousFrame ) ).append( ' ' );
		String label = getLabel( Optional.of( sameLocals1StackItemFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.of( header, //
			Twig.of( "stackVerificationType", //
				twigFromVerificationType( sameLocals1StackItemFrame.stackVerificationType() ) ) );
	}

	private Twig twigFromSameStackMapFrame( SameStackMapFrame sameFrame, Optional<StackMapFrame> previousFrame )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		builder.append( sameFrame.getName( previousFrame ) ).append( ' ' );
		String label = getLabel( Optional.of( sameFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromVerificationType( VerificationType verificationType )
	{
		return verificationType.visit( new VerificationType.Visitor<>()
		{
			@Override public Twig visit( SimpleVerificationType simpleVerificationType )
			{
				return twigFromSimpleVerificationType( simpleVerificationType );
			}
			@Override public Twig visit( ObjectVerificationType objectVerificationType )
			{
				return twigFromObjectVerificationType( objectVerificationType );
			}
			@Override public Twig visit( UninitializedVerificationType uninitializedVerificationType )
			{
				return twigFromUninitializedVerificationType( uninitializedVerificationType );
			}
		} );
	}

	private static Twig twigFromObjectVerificationType( ObjectVerificationType objectVerificationType )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		String name = objectVerificationType.tag.name();
		builder.append( name );
		builder.append( ' ' );
		summarizeClassConstant( objectVerificationType.classConstant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromSimpleVerificationType( SimpleVerificationType simpleVerificationType )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		String name = simpleVerificationType.tag.name();
		builder.append( name );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromUninitializedVerificationType( UninitializedVerificationType uninitializedVerificationType )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		String name = uninitializedVerificationType.tag.name();
		builder.append( name );
		builder.append( ' ' );
		summarizeAbsoluteInstruction( uninitializedVerificationType.instruction, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromTypeAnnotationsAttributeTarget( Target target )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		target.visit( new Target.Visitor<Void>()
		{
			@Override public Void visit( CatchTarget catchTarget )
			{
				builder.append( "exceptionTableIndex = " ).append( catchTarget.exceptionTableIndex );
				return null;
			}
			@Override public Void visit( EmptyTarget emptyTarget )
			{
				builder.append( "(empty)" );
				return null;
			}
			@Override public Void visit( FormalParameterTarget formalParameterTarget )
			{
				builder.append( "formalParameterIndex = " ).append( formalParameterTarget.formalParameterIndex );
				return null;
			}
			@Override public Void visit( LocalVariableTarget localVariableTarget )
			{
				builder.append( localVariableTarget.entries.size() ).append( " entries" );
				return null;
			}
			@Override public Void visit( OffsetTarget offsetTarget )
			{
				builder.append( "offset = " ).append( offsetTarget.offset );
				return null;
			}
			@Override public Void visit( SupertypeTarget supertypeTarget )
			{
				builder.append( "superTypeIndex = " ).append( supertypeTarget.supertypeIndex );
				return null;
			}
			@Override public Void visit( ThrowsTarget throwsTarget )
			{
				builder.append( "throwsTypeIndex = " ).append( throwsTarget.throwsTypeIndex );
				return null;
			}
			@Override public Void visit( TypeArgumentTarget typeArgumentTarget )
			{
				builder.append( "offset = " ).append( typeArgumentTarget.offset );
				builder.append( ", typeArgumentIndex = " ).append( typeArgumentTarget.typeArgumentIndex );
				return null;
			}
			@Override public Void visit( TypeParameterBoundTarget typeParameterBoundTarget )
			{
				builder.append( "typeParameterIndex = " ).append( typeParameterBoundTarget.typeParameterIndex );
				builder.append( ", boundIndex = " ).append( typeParameterBoundTarget.boundIndex );
				return null;
			}
			@Override public Void visit( TypeParameterTarget typeParameterTarget )
			{
				builder.append( "typeParameterIndex = " ).append( typeParameterTarget.typeParameterIndex );
				return null;
			}
		} );
		String header = builder.toString();
		return Twig.of( header );
	}
}
