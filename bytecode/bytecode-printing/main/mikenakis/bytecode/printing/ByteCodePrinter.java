package mikenakis.bytecode.printing;

import mikenakis.bytecode.exceptions.UnknownConstantException;
import mikenakis.bytecode.exceptions.UnknownValueException;
import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.kit.OmniSwitch3;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMember;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.Descriptor;
import mikenakis.bytecode.model.FlagSet;
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
import mikenakis.bytecode.model.attributes.TypeAnnotation;
import mikenakis.bytecode.model.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.UnknownAttribute;
import mikenakis.bytecode.model.attributes.code.AbsoluteInstructionReference;
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
import mikenakis.bytecode.model.constants.NameAndTypeConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.Kit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
		Twig rootNode = byteCodePrinter.twigFromByteCodeType( byteCodeType, sourcePath );
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

			@Override public String toString()
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

			@Override public final String toString()
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

		@Override public String toString()
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
			Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetAttributeByName( CodeAttribute.kind.utf8Name ) //
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

	private static final OmniSwitch3<Void,Utf8Constant,Attribute,Consumer<Optional<Instruction>>> updateCodeAttributeLabelsSwitch = //
		OmniSwitch3.<Void,Utf8Constant,Attribute,Consumer<Optional<Instruction>>>newBuilder() //
			.with( BootstrapMethodsAttribute.kind.utf8Name, ByteCodePrinter::updateLineNumberTableAttributeLabels )              //
			.with( LocalVariableTableAttribute.kind.utf8Name, ByteCodePrinter::updateLocalVariableTableAttributeLabels )         //
			.with( LocalVariableTypeTableAttribute.kind.utf8Name, ByteCodePrinter::updateLocalVariableTypeTableAttributeLabels ) //
			.with( StackMapTableAttribute.kind.utf8Name, ByteCodePrinter::updateStackMapTableAttributeAttributeLabels )          //
			.with( LineNumberTableAttribute.kind.utf8Name, ByteCodePrinter::updateLineNumberTableAttributeLabels )               //
			.withDefault( ByteCodePrinter::updateUnknownAttributeLabels )                                                        //
			.build();

	private void updateLabels( CodeAttribute codeAttribute )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = getLineNumberFromInstructionMap( codeAttribute );
		Collection<Instruction> targets = new HashSet<>();

		Consumer<Optional<Instruction>> targetInstructionConsumer = instruction -> //
		{
			if( instruction.isEmpty() )
				return;
			assert codeAttribute.instructions().contains( instruction.get() );
			Kit.collection.tryAdd( targets, instruction.get() );
		};

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
				default:
					assert false;
			}
		}
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos() )
		{
			targetInstructionConsumer.accept( exceptionInfo.startInstructionReference.targetInstruction() );
			targetInstructionConsumer.accept( exceptionInfo.endInstructionReference.targetInstruction() );
			targetInstructionConsumer.accept( exceptionInfo.handlerInstructionReference.targetInstruction() );
		}
		for( Attribute attribute : codeAttribute.attributeSet() )
		{
			updateCodeAttributeLabelsSwitch.on( attribute.kind.utf8Name, attribute, targetInstructionConsumer );
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

	private static Void updateLocalVariableTableAttributeLabels( Utf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		assert attributeNameConstant == LocalVariableTableAttribute.kind.utf8Name;
		LocalVariableTableAttribute localVariableTableAttribute = attribute.asLocalVariableTableAttribute();
		for( LocalVariable localVariable : localVariableTableAttribute.localVariables() )
		{
			targetInstructionConsumer.accept( localVariable.startInstructionReference.targetInstruction() );
			targetInstructionConsumer.accept( localVariable.endInstructionReference.targetInstruction() );
		}
		return null;
	}

	private static Void updateLocalVariableTypeTableAttributeLabels( Utf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		assert attributeNameConstant == LocalVariableTypeTableAttribute.kind.utf8Name;
		LocalVariableTypeTableAttribute localVariableTypeTableAttribute = attribute.asLocalVariableTypeTableAttribute();
		for( LocalVariableType entry : localVariableTypeTableAttribute.localVariableTypes() )
			targetInstructionConsumer.accept( entry.startInstructionReference.targetInstruction() );
		return null;
	}

	private static Void updateStackMapTableAttributeAttributeLabels( Utf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		assert attributeNameConstant == StackMapTableAttribute.kind.utf8Name;
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

	private static Void updateLineNumberTableAttributeLabels( Utf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
	{
		assert attributeNameConstant == LineNumberTableAttribute.kind.utf8Name;
		return null;
	}

	private static Void updateUnknownAttributeLabels( Utf8Constant attributeNameConstant, Attribute attribute, Consumer<Optional<Instruction>> targetInstructionConsumer )
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
		if( verificationType.isUninitializedVerificationType() )
		{
			UninitializedVerificationType uninitializedVerificationType = verificationType.asUninitializedVerificationType();
			targetInstructionConsumer.accept( uninitializedVerificationType.instructionReference.targetInstruction() );
		}
	}

	private static Map<Instruction,Integer> getLineNumberFromInstructionMap( CodeAttribute codeAttribute )
	{
		Map<Instruction,Integer> lineNumberFromInstructionMap = new HashMap<>();
		codeAttribute.attributeSet().tryGetAttributeByName( LineNumberTableAttribute.kind.utf8Name ) //
			.map( a -> a.asLineNumberTableAttribute() ) //
			.ifPresent( lineNumberTableAttribute -> //
			{
				for( LineNumber entry : lineNumberTableAttribute.lineNumbers() )
				{
					Instruction instruction = entry.instructionReference().targetInstruction().orElseThrow();
					Kit.map.add( lineNumberFromInstructionMap, instruction, entry.lineNumber() );
				}
			} );
		return lineNumberFromInstructionMap;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static void appendNameAndDescriptor( StringBuilder builder, Utf8Constant nameConstant, Utf8Constant descriptorConstant )
	{
		Descriptor descriptor = Descriptor.from( descriptorConstant.value() );
		appendNameAndDescriptor( descriptor, builder, nameConstant.value() );
	}

	private static void appendNameAndTypeAndDescriptor( StringBuilder builder, Utf8Constant nameConstant, Utf8Constant descriptorConstant, ClassConstant typeConstant )
	{
		Descriptor descriptor = Descriptor.from( descriptorConstant.value() );
		appendNameAndTypeAndDescriptor( descriptor, builder, nameConstant.value(), Optional.of( typeConstant.getClassName() ) );
	}

	private static void appendNameAndSignature( StringBuilder builder, Utf8Constant nameConstant, Utf8Constant signatureConstant )
	{
		//TODO
		builder.append( nameConstant.value() );
		builder.append( ' ' );
		builder.append( signatureConstant.value() );
	}

	private static void appendNameAndDescriptor( Descriptor descriptor, StringBuilder builder, String name )
	{
		appendNameAndTypeAndDescriptor( descriptor, builder, name, Optional.empty() );
	}

	private static void appendNameAndTypeAndDescriptor( Descriptor descriptor, StringBuilder builder, String name, Optional<String> declaringTypeName )
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


	private static void annotationValueToStringBuilder( AnnotationValue annotationValue, StringBuilder builder )
	{
		switch( annotationValue.tag )
		{
			case 'B', 's', 'Z', 'S', 'J', 'I', 'F', 'D', 'C' -> constAnnotationValueToStringBuilder( annotationValue.asConstAnnotationValue(), builder );
			case 'e' -> enumAnnotationValueToStringBuilder( annotationValue.asEnumAnnotationValue(), builder );
			case 'c' -> classAnnotationValueToStringBuilder( annotationValue.asClassAnnotationValue(), builder );
			case '@' -> annotationAnnotationValueToStringBuilder( annotationValue.asAnnotationAnnotationValue(), builder );
			case '[' -> arrayAnnotationValueToStringBuilder( annotationValue.asArrayAnnotationValue(), builder );
			default -> throw new UnknownValueException( annotationValue.tag );
		}
	}

	private static void constAnnotationValueToStringBuilder( ConstAnnotationValue constAnnotationValue, StringBuilder builder )
	{
		builder.append( AnnotationValue.getNameFromTag( constAnnotationValue.tag ) );
		builder.append( " value = " );
		constantToStringBuilder( constAnnotationValue.valueConstant(), builder );
	}

	private static void enumAnnotationValueToStringBuilder( EnumAnnotationValue enumAnnotationValue, StringBuilder builder )
	{
		builder.append( "type = " ).append( enumAnnotationValue.typeNameConstant().value() );
		builder.append( ", value = " ).append( enumAnnotationValue.valueNameConstant().value() );
	}

	private static void classAnnotationValueToStringBuilder( ClassAnnotationValue classAnnotationValue, StringBuilder builder )
	{
		builder.append( "class = " ).append( classAnnotationValue.utf8Constant().value() );
	}

	private static void annotationAnnotationValueToStringBuilder( AnnotationAnnotationValue annotationAnnotationValue, StringBuilder builder )
	{
		builder.append( "annotation = { " );
		ByteCodeAnnotation annotation = annotationAnnotationValue.annotation();
		builder.append( "type = " ).append( annotation.typeConstant.value() );
		builder.append( ", " ).append( annotation.annotationParameters().size() ).append( " elements" );
		builder.append( " }" );
	}

	private static void arrayAnnotationValueToStringBuilder( ArrayAnnotationValue arrayAnnotationValue, StringBuilder builder )
	{
		builder.append( arrayAnnotationValue.annotationValues().size() ).append( " elements" );
	}

	private static void constantToStringBuilder( Constant constant, StringBuilder builder )
	{
		switch( constant.tag )
		{
			case Utf8Constant.TAG:
				utf8ConstantToStringBuilder( constant.asUtf8Constant(), builder );
				break;
			case IntegerConstant.TAG:
				integerConstantToStringBuilder( constant.asIntegerConstant(), builder );
				break;
			case FloatConstant.TAG:
				floatConstantToStringBuilder( constant.asFloatConstant(), builder );
				break;
			case LongConstant.TAG:
				longConstantToStringBuilder( constant.asLongConstant(), builder );
				break;
			case DoubleConstant.TAG:
				doubleConstantToStringBuilder( constant.asDoubleConstant(), builder );
				break;
			case ClassConstant.TAG:
				classConstantToStringBuilder( constant.asClassConstant(), builder );
				break;
			case StringConstant.TAG:
				stringConstantToStringBuilder( constant.asStringConstant(), builder );
				break;
			case FieldReferenceConstant.TAG:
				referenceConstantToStringBuilder( constant.asFieldReferenceConstant(), builder );
				break;
			case PlainMethodReferenceConstant.TAG:
				referenceConstantToStringBuilder( constant.asPlainMethodReferenceConstant(), builder );
				break;
			case InterfaceMethodReferenceConstant.TAG:
				referenceConstantToStringBuilder( constant.asInterfaceMethodReferenceConstant(), builder );
				break;
			case NameAndTypeConstant.TAG:
				nameAndTypeConstantToStringBuilder( constant.asNameAndTypeConstant(), builder );
				break;
			case MethodHandleConstant.TAG:
				methodHandleConstantToStringBuilder( constant.asMethodHandleConstant(), builder );
				break;
			case MethodTypeConstant.TAG:
				methodTypeConstantToStringBuilder( constant.asMethodTypeConstant(), builder );
				break;
			case InvokeDynamicConstant.TAG:
				invokeDynamicConstantToStringBuilder( constant.asInvokeDynamicConstant(), builder );
				break;
			default:
				throw new UnknownConstantException( constant.tag );
		}
	}

	private static void utf8ConstantToStringBuilder( Utf8Constant utf8Constant, StringBuilder builder )
	{
		Kit.stringBuilder.appendEscapedForJava( builder, utf8Constant.value(), '"' );
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

	private static void classConstantToStringBuilder( ClassConstant classConstant, StringBuilder builder )
	{
		builder.append( classConstant.getClassName() );
	}

	private static void stringConstantToStringBuilder( StringConstant stringConstant, StringBuilder builder )
	{
		utf8ConstantToStringBuilder( stringConstant.valueUtf8Constant(), builder );
	}

	private static void referenceConstantToStringBuilder( ReferenceConstant referenceConstant, StringBuilder builder )
	{
		builder.append( "type = " );
		builder.append( referenceConstant.typeConstant().getClassName() );
		builder.append( ", nameAndType = " );
		NameAndTypeConstant nameAndTypeConstant = referenceConstant.nameAndTypeConstant();
		builder.append( "name = " );
		builder.append( nameAndTypeConstant.nameConstant() );
		builder.append( ", descriptor = " );
		builder.append( nameAndTypeConstant.descriptorConstant() );
		builder.append( ' ' );
	}

	private static void nameAndTypeConstantToStringBuilder( NameAndTypeConstant nameAndTypeConstant, StringBuilder builder )
	{
		builder.append( "name = " );
		builder.append( nameAndTypeConstant.nameConstant() );
		builder.append( ", descriptor = " );
		builder.append( nameAndTypeConstant.descriptorConstant() );
		builder.append( ' ' );
	}

	private static void methodHandleConstantToStringBuilder( MethodHandleConstant methodHandleConstant, StringBuilder builder )
	{
		builder.append( "kind = " ).append( methodHandleConstant.referenceKind().name() );
		ReferenceConstant referenceConstant = methodHandleConstant.referenceConstant();
		builder.append( " type = " );
		builder.append( referenceConstant.typeConstant().getClassName() );
		builder.append( ", nameAndType = " );
		NameAndTypeConstant nameAndTypeConstant = referenceConstant.nameAndTypeConstant();
		builder.append( "name = " );
		builder.append( nameAndTypeConstant.nameConstant() );
		builder.append( ", descriptor = " );
		builder.append( nameAndTypeConstant.descriptorConstant() );
		builder.append( ' ' );
	}

	private static void methodTypeConstantToStringBuilder( MethodTypeConstant methodTypeConstant, StringBuilder builder )
	{
		builder.append( "descriptor = " );
		Kit.stringBuilder.appendEscapedForJava( builder, methodTypeConstant.descriptorConstant.value(), '"' );
	}

	private static void invokeDynamicConstantToStringBuilder( InvokeDynamicConstant invokeDynamicConstant, StringBuilder builder )
	{
		builder.append( "bootstrapMethod " ).append( invokeDynamicConstant.bootstrapMethodIndex() );
		builder.append( " name = " );
		builder.append( invokeDynamicConstant.nameAndTypeConstant().nameConstant() );
		builder.append( ", descriptor = " );
		builder.append( invokeDynamicConstant.nameAndTypeConstant().descriptorConstant() );
		builder.append( ' ' );
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

	private void summarizeAbsoluteInstructionReference( AbsoluteInstructionReference instructionReference, StringBuilder builder )
	{
		String label = getLabel( instructionReference.targetInstruction() );
		builder.append( label );
	}

	private static Twig twigFromAnnotationParameter( AnnotationParameter annotationParameter )
	{
		var builder = new StringBuilder();
		builder.append( "parameter name = " );
		summarizeValueConstant( annotationParameter.nameConstant(), builder );
		AnnotationValue annotationValue = annotationParameter.annotationValue();
		return Twig.of( builder.toString(), List.of( //
			twigFromAnnotationValue( annotationValue, AnnotationValue.getNameFromTag( annotationValue.tag ) + " value = " ) ) );
	}

	private Twig twigFromAttributeSet( AttributeSet attributeSet )
	{
		List<Twig> children = new ArrayList<>();
		for( Attribute attribute : attributeSet )
		{
			Twig twig = switch( attribute.kind.name )
				{
					case BootstrapMethodsAttribute.name -> twigFromBootstrapMethodsAttribute( attribute.asBootstrapMethodsAttribute(), attribute.kind.name );
					case MethodParametersAttribute.name -> twigFromMethodParametersAttribute( attribute.asMethodParametersAttribute(), attribute.kind.name );
					case AnnotationDefaultAttribute.name -> twigFromAnnotationDefaultAttribute( attribute.asAnnotationDefaultAttribute(), attribute.kind.name );
					case RuntimeInvisibleAnnotationsAttribute.name, //
						RuntimeVisibleAnnotationsAttribute.name -> twigFromAnnotationsAttribute( attribute.asAnnotationsAttribute(), attribute.kind.name );
					case StackMapTableAttribute.name -> twigFromStackMapTableAttribute( attribute.asStackMapTableAttribute(), attribute.kind.name );
					case LineNumberTableAttribute.name -> twigFromLineNumberTableAttribute( attribute.asLineNumberTableAttribute(), attribute.kind.name );
					case LocalVariableTableAttribute.name -> twigFromLocalVariableTableAttribute( attribute.asLocalVariableTableAttribute(), attribute.kind.name );
					case LocalVariableTypeTableAttribute.name -> twigFromLocalVariableTypeTableAttribute( attribute.asLocalVariableTypeTableAttribute(), attribute.kind.name );
					case NestHostAttribute.name -> twigFromNestHostAttribute( attribute.asNestHostAttribute(), attribute.kind.name );
					case NestMembersAttribute.name -> twigFromNestMembersAttribute( attribute.asNestMembersAttribute(), attribute.kind.name );
					case ConstantValueAttribute.name -> twigFromConstantValueAttribute( attribute.asConstantValueAttribute(), attribute.kind.name );
					case DeprecatedAttribute.name -> twigFromDeprecatedAttribute( attribute.asDeprecatedAttribute(), attribute.kind.name );
					case EnclosingMethodAttribute.name -> twigFromEnclosingMethodAttribute( attribute.asEnclosingMethodAttribute(), attribute.kind.name );
					case ExceptionsAttribute.name -> twigFromExceptionsAttribute( attribute.asExceptionsAttribute(), attribute.kind.name );
					case InnerClassesAttribute.name -> twigFromInnerClassesAttribute( attribute.asInnerClassesAttribute(), attribute.kind.name );
					case RuntimeVisibleParameterAnnotationsAttribute.name, //
						RuntimeInvisibleParameterAnnotationsAttribute.name -> twigFromParameterAnnotationsAttribute( attribute.asParameterAnnotationsAttribute(), attribute.kind.name );
					case SignatureAttribute.name -> twigFromSignatureAttribute( attribute.asSignatureAttribute(), attribute.kind.name );
					case SourceFileAttribute.name -> twigFromSourceFileAttribute( attribute.asSourceFileAttribute(), attribute.kind.name );
					case SyntheticAttribute.name -> twigFromSyntheticAttribute( attribute.asSyntheticAttribute(), attribute.kind.name );
					case CodeAttribute.name -> twigFromCodeAttribute( attribute.asCodeAttribute(), attribute.kind.name );
					case RuntimeVisibleTypeAnnotationsAttribute.name, //
						RuntimeInvisibleTypeAnnotationsAttribute.name -> twigFromTypeAnnotationsAttribute( attribute.asTypeAnnotationsAttribute(), attribute.kind.name );
					default -> twigFromUnknownAttribute( attribute.asUnknownAttribute(), attribute.kind.name );
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
		if( !argumentConstants.isEmpty() )
		{
			builder.append( " arguments: " );
			boolean first = true;
			for( Constant argumentConstant : argumentConstants )
			{
				first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
				switch( argumentConstant.tag )
				{
					case StringConstant.TAG:
						summarizeValueConstant( argumentConstant.asValueConstant(), builder );
						break;
					case MethodTypeConstant.TAG:
						summarizeMethodTypeConstant( argumentConstant.asMethodTypeConstant(), builder );
						break;
					case MethodHandleConstant.TAG:
						summarizeMethodHandleConstant( argumentConstant.asMethodHandleConstant(), builder );
						break;
					default:
						assert false;
						//RenderingContext.newConstantPrinter( argumentConstant ).appendGildedTo( renderingContext, builder );
						break;
				}
			}
		}
	}

	private static Twig twigFromByteCodeAnnotation( ByteCodeAnnotation byteCodeAnnotation, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( ' ' );
		summarizeByteCodeAnnotation( byteCodeAnnotation, builder );
		String header = builder.toString();
		return Twig.of( header, //
			byteCodeAnnotation.annotationParameters().stream().map( p -> twigFromAnnotationParameter( p ) ).toList() );
	}

	private static void summarizeByteCodeAnnotation( ByteCodeAnnotation byteCodeAnnotation, StringBuilder builder )
	{
		builder.append( "type = " );
		builder.append( ByteCodeHelpers.getJavaTypeNameFromDescriptorTypeName( byteCodeAnnotation.typeConstant.value() ) );
		builder.append( ", " ).append( byteCodeAnnotation.annotationParameters().size() ).append( " parameters" );
	}

	private Twig twigFromByteCodeMember( ByteCodeMember byteCodeMember )
	{
		var builder = new StringBuilder();
		builder.append( " accessFlags = " );
		flagsToStringBuilder( byteCodeMember.modifierSet(), builder );
		builder.append( ", name = " ).append( byteCodeMember.nameConstant );
		builder.append( ", descriptor = " ).append( byteCodeMember.descriptorConstant );
		builder.append( " (" );
		appendNameAndDescriptor( builder, byteCodeMember.nameConstant, byteCodeMember.descriptorConstant );
		builder.append( ")" );
		String header = builder.toString();
		AttributeSet attributeSet = byteCodeMember.attributeSet;
		return Twig.of( header, attributeSet.size() == 0 ? List.of() : List.of( twigFromAttributeSet( attributeSet ) ) );
	}

	private Twig twigFromByteCodeType( ByteCodeType byteCodeType, Optional<Path> sourcePath )
	{
		ByteCodePrinter renderingContext = new ByteCodePrinter( byteCodeType, sourcePath );
		String header = summarizeByteCodeType( byteCodeType );
		List<Twig> children = new ArrayList<>();
		Collection<ClassConstant> interfaces = byteCodeType.interfaceClassConstants();
		children.add( Twig.of( "interfaces (" + interfaces.size() + " entries)", //
			interfaces.stream().sorted( Comparator.comparingInt( Constant::hashCode ) ) //
				.map( constant -> twigFromConstant( renderingContext, Constant.getTagNameByTag( constant.tag ), constant ) ).toList() ) );
		Collection<Constant> extraConstants = byteCodeType.extraConstants();
		children.add( Twig.of( "extra constants (" + extraConstants.size() + " entries)", //
			extraConstants.stream().sorted( Comparator.comparingInt( Constant::hashCode ) ) //
				.map( constant -> twigFromConstant( renderingContext, Constant.getTagNameByTag( constant.tag ), constant ) ).toList() ) );
		Collection<ByteCodeField> fields = byteCodeType.fields();
		children.add( Twig.of( "fields (" + fields.size() + " entries)", //
			fields.stream().map( field -> twigFromByteCodeMember( field ) ).toList() ) );
		Collection<ByteCodeMethod> methods = byteCodeType.methods();
		children.add( Twig.of( "methods (" + methods.size() + " entries)", //
			methods.stream().map( method -> twigFromByteCodeMember( method ) ).toList() ) );
		children.add( twigFromAttributeSet( byteCodeType.attributeSet() ) );
		return Twig.of( header, children );
	}

	private static String summarizeByteCodeType( ByteCodeType byteCodeType )
	{
		StringBuilder builder = new StringBuilder( 1024 );
		builder.append( " version = " ).append( byteCodeType.majorVersion ).append( '.' ).append( byteCodeType.minorVersion );
		builder.append( ", accessFlags = " );
		flagsToStringBuilder( byteCodeType.modifierSet(), builder );
		builder.append( ", thisClass = " ).append( byteCodeType.thisClassConstant.getClassName() );
		byteCodeType.superClassConstant.ifPresent( s1 -> builder.append( ", superClass = " ).append( s1.getClassName() ) );
//		Collection<ClassConstant> interfaceClassConstants = byteCodeType.interfaceClassConstants();
//		builder.append( ", interfaces = [" );
//		boolean first = true;
//		for( ClassConstant interfaceClassConstant : interfaceClassConstants )
//		{
//			first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
//			builder.append( interfaceClassConstant.getClassName() );
//		}
//		builder.append( "]" );
		return builder.toString();
	}

	private Twig twigFromExceptionInfo( ExceptionInfo exceptionInfo )
	{
		var builder = new StringBuilder();
		builder.append( " startPc = " );
		summarizeAbsoluteInstructionReference( exceptionInfo.startInstructionReference, builder );
		builder.append( " endPc = " );
		summarizeAbsoluteInstructionReference( exceptionInfo.endInstructionReference, builder );
		builder.append( " handlerPc = " );
		summarizeAbsoluteInstructionReference( exceptionInfo.handlerInstructionReference, builder );
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
		Optional<Utf8Constant> innerNameConstant = innerClass.innerNameConstant();
		if( innerNameConstant.isPresent() )
		{
			builder.append( ", innerName = " );
			summarizeValueConstant( innerNameConstant.get(), builder );
		}
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromLineNumber( LineNumber lineNumber )
	{
		var builder = new StringBuilder();
		builder.append( " lineNumber = " ).append( lineNumber.lineNumber() );
		builder.append( ", startPc = " );
		summarizeAbsoluteInstructionReference( lineNumber.instructionReference(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromLocalVariable( LocalVariable localVariable )
	{
		var builder = new StringBuilder();
		builder.append( " index = " ).append( localVariable.index );
		builder.append( ", startPc = " );
		summarizeAbsoluteInstructionReference( localVariable.startInstructionReference, builder );
		builder.append( ", endPc = " );
		summarizeAbsoluteInstructionReference( localVariable.endInstructionReference, builder );
		builder.append( ", name = " ).append( localVariable.nameConstant );
		builder.append( ", descriptor = " ).append( localVariable.descriptorConstant );
		builder.append( " (" );
		appendNameAndDescriptor( builder, localVariable.nameConstant, localVariable.descriptorConstant );
		builder.append( ")" );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromLocalVariableType( LocalVariableType localVariableType )
	{
		var builder = new StringBuilder();
		builder.append( " index = " ).append( localVariableType.index );
		builder.append( ", startPc = " );
		summarizeAbsoluteInstructionReference( localVariableType.startInstructionReference, builder );
		builder.append( ", endPc = " );
		summarizeAbsoluteInstructionReference( localVariableType.endInstructionReference, builder );
		builder.append( ", name = " ).append( localVariableType.nameConstant );
		builder.append( ", signature = " ).append( localVariableType.signatureConstant );
		builder.append( " (" );
		appendNameAndSignature( builder, localVariableType.nameConstant, localVariableType.signatureConstant );
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
		builder.append( methodParameter.nameConstant.value() );
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
			parameterAnnotationSet.annotations().stream().map( a -> twigFromByteCodeAnnotation( a, "annotation" ) ).toList() );
	}

	private void twigFromRelativeInstructionReference( Instruction instruction, StringBuilder builder )
	{
		String label = getLabel( Optional.of( instruction ) );
		builder.append( label );
	}

	private static Twig twigFromTypeAnnotationElementValuePair( TypeAnnotation.ElementValuePair elementValuePair )
	{
		var builder = new StringBuilder();
		builder.append( " elementNameIndex = " ).append( elementValuePair.elementNameIndex() );
		builder.append( ", elementValue = " );
		AnnotationParameter annotationParameter = elementValuePair.elementValue();
		builder.append( "name = " );
		Kit.stringBuilder.appendEscapedForJava( builder, annotationParameter.nameConstant().value(), '"' );
		builder.append( ", value = " );
		annotationValueToStringBuilder( annotationParameter.annotationValue(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromLocalVariableTargetEntry( LocalVariableTarget.Entry localVariableTargetEntry, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "startPc = " ).append( localVariableTargetEntry.startPc() ).append( ", " );
		builder.append( "length = " ).append( localVariableTargetEntry.length() ).append( ", " );
		builder.append( "index = " ).append( localVariableTargetEntry.index() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromTypePathEntry( TypePath.Entry entry, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "pathKind = " ).append( entry.pathKind() ).append( ", " );
		builder.append( "argumentIndex = " ).append( entry.argumentIndex() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromClassConstant( ClassConstant classConstant, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "name = " );
		summarizeValueConstant( classConstant.nameConstant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeClassConstant( ClassConstant classConstant, StringBuilder builder )
	{
		builder.append( classConstant.getClassName() );
	}

	private static Twig twigFromValueConstant( ValueConstant<?> valueConstant, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		summarizeValueConstant( valueConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeValueConstant( ValueConstant<?> valueConstant, StringBuilder builder )
	{
		constantToStringBuilder( valueConstant, builder );
	}

	private static Twig twigFromInvokeDynamicConstant( InvokeDynamicConstant invokeDynamicConstant, ByteCodePrinter renderingContext, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " bootstrapMethod = " );
		summarizeBootstrapMethod( invokeDynamicConstant.getBootstrapMethod( renderingContext.byteCodeType ), builder );

		NameAndTypeConstant nameAndTypeConstant = invokeDynamicConstant.nameAndTypeConstant();
		builder.append( ", name = " ).append( nameAndTypeConstant.nameConstant() );
		builder.append( ", descriptor = " ).append( nameAndTypeConstant.descriptorConstant() );
		builder.append( " (" );
		appendNameAndDescriptor( builder, nameAndTypeConstant.nameConstant(), nameAndTypeConstant.descriptorConstant() );
		builder.append( ")" );
		String header = builder.toString();
		return Twig.of( header );
	}

	private void summarizeInvokeDynamicConstant( InvokeDynamicConstant invokeDynamicConstant, StringBuilder builder )
	{
		builder.append( "bootstrapMethod = {" );
		summarizeBootstrapMethod( invokeDynamicConstant.getBootstrapMethod( byteCodeType ), builder );
		NameAndTypeConstant nameAndTypeConstant = invokeDynamicConstant.nameAndTypeConstant();
		builder.append( "}" );
		builder.append( ", name = " ).append( nameAndTypeConstant.nameConstant() );
		builder.append( ", descriptor = " ).append( nameAndTypeConstant.descriptorConstant() );
		builder.append( " (" );
		appendNameAndDescriptor( builder, nameAndTypeConstant.nameConstant(), nameAndTypeConstant.descriptorConstant() );
		builder.append( ")" );
	}

	private static Twig twigFromMethodHandleConstant( MethodHandleConstant methodHandleConstant, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		summarizeMethodHandleConstant( methodHandleConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeMethodHandleConstant( MethodHandleConstant methodHandleConstant, StringBuilder builder )
	{
		builder.append( methodHandleConstant.referenceKind().name() );
		builder.append( ", referenceConstant = " );
		summarizeReferenceConstant( methodHandleConstant.referenceConstant(), builder );
	}

	private static Twig twigFromReferenceConstant( ReferenceConstant referenceConstant, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		summarizeReferenceConstant( referenceConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeReferenceConstant( ReferenceConstant referenceConstant, StringBuilder builder )
	{
		NameAndTypeConstant nameAndTypeConstant = referenceConstant.nameAndTypeConstant();
		Descriptor descriptor = Descriptor.from( nameAndTypeConstant.descriptorConstant().value() );
		appendNameAndTypeAndDescriptor( descriptor, builder, nameAndTypeConstant.nameConstant().value(), Optional.of( referenceConstant.typeConstant().getClassName() ) );
	}

	private static Twig twigFromMethodTypeConstant( MethodTypeConstant methodTypeConstant, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "descriptor = " );
		summarizeValueConstant( methodTypeConstant.descriptorConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeMethodTypeConstant( MethodTypeConstant methodTypeConstant, StringBuilder builder )
	{
		summarizeValueConstant( methodTypeConstant.descriptorConstant, builder );
	}

	private static Twig twigFromNameAndTypeConstant( NameAndTypeConstant nameAndTypeConstant, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		summarizeNameAndTypeConstant( nameAndTypeConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeNameAndTypeConstant( NameAndTypeConstant nameAndTypeConstant, StringBuilder builder )
	{
		builder.append( "name = " ).append( nameAndTypeConstant.nameConstant() );
		builder.append( "descriptor = " ).append( nameAndTypeConstant.descriptorConstant() );
		builder.append( " (" );
		appendNameAndDescriptor( builder, nameAndTypeConstant.nameConstant(), nameAndTypeConstant.descriptorConstant() );
		builder.append( ")" );
	}

	private static Twig twigFromConstant( ByteCodePrinter renderingContext, String prefix, Constant constant )
	{
		return switch( constant.tag )
			{
				case ClassConstant.TAG -> //
					twigFromClassConstant( constant.asClassConstant(), prefix );
				case MethodTypeConstant.TAG -> //
					twigFromMethodTypeConstant( constant.asMethodTypeConstant(), prefix );
				case FieldReferenceConstant.TAG, InterfaceMethodReferenceConstant.TAG, PlainMethodReferenceConstant.TAG -> //
					twigFromReferenceConstant( constant.asReferenceConstant(), prefix );
				case InvokeDynamicConstant.TAG -> //
					twigFromInvokeDynamicConstant( constant.asInvokeDynamicConstant(), renderingContext, prefix );
				case DoubleConstant.TAG, FloatConstant.TAG, IntegerConstant.TAG, LongConstant.TAG, StringConstant.TAG, Utf8Constant.TAG -> //
					twigFromValueConstant( constant.asValueConstant(), prefix );
				case NameAndTypeConstant.TAG -> //
					twigFromNameAndTypeConstant( constant.asNameAndTypeConstant(), prefix );
				case MethodHandleConstant.TAG -> //
					twigFromMethodHandleConstant( constant.asMethodHandleConstant(), prefix );
				default -> throw new AssertionError();
			};
	}

	private void summarizeConstant( StringBuilder builder, Constant constant )
	{
		switch( constant.tag )
		{
			case ClassConstant.TAG -> //
				summarizeClassConstant( constant.asClassConstant(), builder );
			case MethodTypeConstant.TAG -> //
				summarizeMethodTypeConstant( constant.asMethodTypeConstant(), builder );
			case FieldReferenceConstant.TAG, InterfaceMethodReferenceConstant.TAG, PlainMethodReferenceConstant.TAG -> //
				summarizeReferenceConstant( constant.asReferenceConstant(), builder );
			case InvokeDynamicConstant.TAG -> //
				summarizeInvokeDynamicConstant( constant.asInvokeDynamicConstant(), builder );
			case DoubleConstant.TAG, FloatConstant.TAG, IntegerConstant.TAG, LongConstant.TAG, StringConstant.TAG, Utf8Constant.TAG -> //
				summarizeValueConstant( constant.asValueConstant(), builder );
			case NameAndTypeConstant.TAG -> //
				summarizeNameAndTypeConstant( constant.asNameAndTypeConstant(), builder );
			case MethodHandleConstant.TAG -> //
				summarizeMethodHandleConstant( constant.asMethodHandleConstant(), builder );
			default -> throw new AssertionError();
		}
	}

	private static Twig twigFromAnnotationValue( AnnotationValue annotationValue, String prefix )
	{
		return switch( annotationValue.tag )
			{
				case AnnotationValue.ByteTag, AnnotationValue.CharacterTag, AnnotationValue.DoubleTag, AnnotationValue.FloatTag, AnnotationValue.IntTag, //
					AnnotationValue.LongTag, AnnotationValue.ShortTag, AnnotationValue.BooleanTag, AnnotationValue.StringTag -> //
					twigFromConstAnnotationValue( annotationValue.asConstAnnotationValue(), prefix );
				case AnnotationValue.AnnotationTag -> twigFromAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue(), prefix );
				case AnnotationValue.ArrayTag -> twigFromArrayAnnotationValue( annotationValue.asArrayAnnotationValue(), prefix );
				case AnnotationValue.ClassTag -> twigFromClassAnnotationValue( annotationValue.asClassAnnotationValue(), prefix );
				case AnnotationValue.EnumTag -> twigFromEnumAnnotationValue( annotationValue.asEnumAnnotationValue(), prefix );
				default -> throw new AssertionError( annotationValue.tag );
			};
	}

	private static void summarizeAnnotationValue( AnnotationValue annotationValue, StringBuilder builder )
	{
		switch( annotationValue.tag )
		{
			case AnnotationValue.ByteTag, AnnotationValue.CharacterTag, AnnotationValue.DoubleTag, AnnotationValue.FloatTag, AnnotationValue.IntTag, //
				AnnotationValue.LongTag, AnnotationValue.ShortTag, AnnotationValue.BooleanTag, AnnotationValue.StringTag -> //
				summarizeConstAnnotationValue( annotationValue.asConstAnnotationValue(), builder );
			case AnnotationValue.AnnotationTag -> summarizeAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue(), builder );
			case AnnotationValue.ArrayTag -> summarizeArrayAnnotationValue( annotationValue.asArrayAnnotationValue(), builder );
			case AnnotationValue.ClassTag -> summarizeClassAnnotationValue( annotationValue.asClassAnnotationValue(), builder );
			case AnnotationValue.EnumTag -> summarizeEnumAnnotationValue( annotationValue.asEnumAnnotationValue(), builder );
			default -> throw new AssertionError( annotationValue.tag );
		}
	}

	private static Twig twigFromClassAnnotationValue( ClassAnnotationValue classAnnotationValue, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		summarizeValueConstant( classAnnotationValue.utf8Constant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeClassAnnotationValue( ClassAnnotationValue classAnnotationValue, StringBuilder builder )
	{
		builder.append( "class = " ).append( classAnnotationValue.utf8Constant().value() );
	}

	private static void summarizeConstAnnotationValue( ConstAnnotationValue constAnnotationValue, StringBuilder builder )
	{
		builder.append( AnnotationValue.getNameFromTag( constAnnotationValue.tag ) );
		builder.append( " value = " );
		summarizeValueConstant( constAnnotationValue.valueConstant(), builder );
	}

	private static Twig twigFromConstAnnotationValue( ConstAnnotationValue constAnnotationValue, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		summarizeValueConstant( constAnnotationValue.valueConstant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromAnnotationAnnotationValue( AnnotationAnnotationValue annotationAnnotationValue, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		summarizeByteCodeAnnotation( annotationAnnotationValue.annotation(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeAnnotationAnnotationValue( AnnotationAnnotationValue annotationAnnotationValue, StringBuilder builder )
	{
		builder.append( "annotation = { " );
		ByteCodeAnnotation annotation = annotationAnnotationValue.annotation();
		builder.append( "type = " ).append( annotation.typeConstant.value() );
		builder.append( ", " ).append( annotation.annotationParameters().size() ).append( " elements" );
		builder.append( " }" );
	}

	private static Twig twigFromArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( arrayAnnotationValue.annotationValues().size() );
		builder.append( " elements" );
		String header = builder.toString();
		return Twig.of( header, //
			arrayAnnotationValue.annotationValues().stream().map( a -> twigFromAnnotationValue( a, "element" ) ).toList() );
	}

	private static void summarizeArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue, StringBuilder builder )
	{
		builder.append( arrayAnnotationValue.annotationValues().size() ).append( " elements" );
	}

	private static Twig twigFromEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "type = " );
		builder.append( ByteCodeHelpers.getJavaTypeNameFromDescriptorTypeName( enumAnnotationValue.typeNameConstant().value() ) );
		builder.append( ", value = " );
		builder.append( enumAnnotationValue.valueNameConstant().value() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static void summarizeEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue, StringBuilder builder )
	{
		builder.append( "type = " ).append( enumAnnotationValue.typeNameConstant().value() );
		builder.append( ", value = " ).append( enumAnnotationValue.valueNameConstant().value() );
	}

	private static Twig twigFromAnnotationDefaultAttribute( AnnotationDefaultAttribute annotationDefaultAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		String header = builder.toString();
		AnnotationValue annotationValue = annotationDefaultAttribute.annotationValue();
		return Twig.of( header, //
			List.of( twigFromAnnotationValue( annotationValue, AnnotationValue.getNameFromTag( annotationValue.tag ) + " value = " ) ) );
	}

	private static Twig twigFromAnnotationsAttribute( AnnotationsAttribute annotationsAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( annotationsAttribute.annotations().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			annotationsAttribute.annotations().stream().map( a -> twigFromByteCodeAnnotation( a, "annotation " ) ).toList() );
	}

	private static Twig twigFromBootstrapMethodsAttribute( BootstrapMethodsAttribute bootstrapMethodsAttribute, String prefix )
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

	private static Twig twigFromConstantValueAttribute( ConstantValueAttribute constantValueAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		ValueConstant<?> valueConstant = constantValueAttribute.valueConstant();
		summarizeValueConstant( valueConstant, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromDeprecatedAttribute( @SuppressWarnings( "unused" ) DeprecatedAttribute deprecatedAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromEnclosingMethodAttribute( EnclosingMethodAttribute enclosingMethodAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		Optional<NameAndTypeConstant> methodNameAndTypeConstant = enclosingMethodAttribute.methodNameAndTypeConstant();
		if( methodNameAndTypeConstant.isPresent() )
		{
			builder.append( "name+type+descriptor = " );
			appendNameAndTypeAndDescriptor( builder, methodNameAndTypeConstant.get().nameConstant(), //
				methodNameAndTypeConstant.get().descriptorConstant(), enclosingMethodAttribute.classConstant() );
		}
		else
		{
			builder.append( "class = " );
			builder.append( enclosingMethodAttribute.classConstant().getClassName() );
		}
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromExceptionsAttribute( ExceptionsAttribute exceptionsAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( exceptionsAttribute.exceptionClassConstants().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			exceptionsAttribute.exceptionClassConstants().stream().map( c -> twigFromClassConstant( c, "exception " ) ).toList() );
	}

	private static Twig twigFromInnerClassesAttribute( InnerClassesAttribute innerClassesAttribute, String prefix )
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

	private Twig twigFromLineNumberTableAttribute( LineNumberTableAttribute lineNumberTableAttribute, String prefix )
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

	private Twig twigFromLocalVariableTableAttribute( LocalVariableTableAttribute localVariableTableAttribute, String prefix )
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

	private Twig twigFromLocalVariableTypeTableAttribute( LocalVariableTypeTableAttribute localVariableTypeTableAttribute, String prefix )
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

	private static Twig twigFromMethodParametersAttribute( MethodParametersAttribute methodParametersAttribute, String prefix )
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

	private static Twig twigFromNestHostAttribute( NestHostAttribute nestHostAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "class = " );
		builder.append( nestHostAttribute.hostClassConstant.getClassName() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromNestMembersAttribute( NestMembersAttribute nestMembersAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "(" );
		builder.append( nestMembersAttribute.memberClassConstants().size() ).append( " entries" );
		builder.append( ')' );
		String header = builder.toString();
		return Twig.of( header, //
			nestMembersAttribute.memberClassConstants().stream().map( c -> Twig.of( "member = " + c.getClassName() ) ).toList() );
	}

	private static Twig twigFromParameterAnnotationsAttribute( ParameterAnnotationsAttribute parameterAnnotationsAttribute, String prefix )
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

	private static Twig twigFromSignatureAttribute( SignatureAttribute signatureAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( signatureAttribute.signatureConstant().value() );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromSourceFileAttribute( SourceFileAttribute sourceFileAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "value = " );
		summarizeValueConstant( sourceFileAttribute.valueConstant(), builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromStackMapTableAttribute( StackMapTableAttribute stackMapTableAttribute, String prefix )
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

	private static Twig twigFromSyntheticAttribute( @SuppressWarnings( "unused" ) SyntheticAttribute syntheticAttribute, String prefix )
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
		builder.append( " targetType = " ).append( String.format( "0x%02x ", typeAnnotation.targetType() ) );
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
			List.of( //
				Twig.of( "target", twig ), //
				Twig.of( "elementValuePairs", //
					typeAnnotation.elementValuePairs().stream().map( a -> twigFromTypeAnnotationElementValuePair( a ) ).toList() ) ) );
	}

	private static Twig twigFromTypeAnnotationsAttribute( TypeAnnotationsAttribute typeAnnotationsAttribute, String prefix )
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

	private static Twig twigFromUnknownAttribute( UnknownAttribute unknownAttribute, String prefix )
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

	private Twig twigFromConstantReferencingInstruction( ConstantReferencingInstruction constantReferencingInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( OpCode.getOpCodeName( constantReferencingInstruction.opCode ) );
		builder.append( ' ' );
		Constant constant = constantReferencingInstruction.constant;
		switch( constant.tag )
		{
			case FieldReferenceConstant.TAG:
			case InterfaceMethodReferenceConstant.TAG:
			case PlainMethodReferenceConstant.TAG:
				summarizeReferenceConstant( constant.asReferenceConstant(), builder );
				break;
			case ClassConstant.TAG:
				summarizeClassConstant( constant.asClassConstant(), builder );
				break;
			default:
				assert false;
				summarizeConstant( builder, constant );
				break;
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

	private Twig twigFromIndirectLoadConstantInstruction( IndirectLoadConstantInstruction indirectLoadConstantInstruction )
	{
		var builder = new StringBuilder();
		builder.append( "     " );
		builder.append( Kit.get( true ) ? "LDCx" : OpCode.getOpCodeName( indirectLoadConstantInstruction.opCode ) );
		builder.append( ' ' );
		Constant constant = indirectLoadConstantInstruction.constant;
		switch( constant.tag )
		{
			case IntegerConstant.TAG:
			case LongConstant.TAG:
			case FloatConstant.TAG:
			case DoubleConstant.TAG:
			case StringConstant.TAG:
				summarizeValueConstant( constant.asValueConstant(), builder );
				break;
			case ClassConstant.TAG:
				summarizeClassConstant( constant.asClassConstant(), builder );
				break;
			default:
				assert false;
				summarizeConstant( builder, constant );
				break;
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
		summarizeReferenceConstant( invokeInterfaceInstruction.interfaceMethodReferenceConstant, builder );
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

	private Twig twigFromCodeAttribute( CodeAttribute codeAttribute, String prefix )
	{
		var builder = new StringBuilder();
		builder.append( prefix );
		builder.append( " " );
		builder.append( "maxStack = " ).append( codeAttribute.getMaxStack() );
		builder.append( ", maxLocals = " ).append( codeAttribute.getMaxLocals() );
		String header = builder.toString();
		List<ExceptionInfo> exceptionInfos = codeAttribute.exceptionInfos();
		return Twig.of( header, List.of( //
			Twig.of( "instructions (" + codeAttribute.instructions().size() + " entries)", //
				twigFromInstructionList( codeAttribute.instructions() ) ), //
			Twig.of( "exceptionInfos (" + exceptionInfos.size() + " entries)", //
				exceptionInfos.stream().map( e -> twigFromExceptionInfo( e ) ).toList() ), //
			twigFromAttributeSet( codeAttribute.attributeSet() ) ) );
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
		List<VerificationType> localVerificationTypes = appendFrame.localVerificationTypes();
		return Twig.of( header, //
			List.of( //
				Twig.of( "localVerificationTypes (" + localVerificationTypes.size() + " entries)", //
					localVerificationTypes.stream().map( t -> twigFromVerificationType( t ) ).toList() ) ) );
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
		List<VerificationType> localVerificationTypes = fullFrame.localVerificationTypes();
		List<VerificationType> stackVerificationTypes = fullFrame.stackVerificationTypes();
		return Twig.of( header, List.of( //
			Twig.of( "localVerificationTypes (" + localVerificationTypes.size() + " entries)", //
				localVerificationTypes.stream().map( t -> twigFromVerificationType( t ) ).toList() ), //
			Twig.of( "stackVerificationTypes (" + stackVerificationTypes.size() + " entries)", //
				stackVerificationTypes.stream().map( t -> twigFromVerificationType( t ) ).toList() ) ) );
	}

	private Twig twigFromSameLocals1StackItemStackMapFrame( SameLocals1StackItemStackMapFrame sameLocals1StackItemFrame, Optional<StackMapFrame> previousFrame )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		builder.append( sameLocals1StackItemFrame.getName( previousFrame ) ).append( ' ' );
		String label = getLabel( Optional.of( sameLocals1StackItemFrame.getTargetInstruction() ) );
		builder.append( "target = " ).append( label );
		String header = builder.toString();
		return Twig.of( header, List.of( //
			Twig.of( "stackVerificationType", //
				List.of( twigFromVerificationType( sameLocals1StackItemFrame.stackVerificationType() ) ) ) ) );
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
		if( verificationType.isSimpleVerificationType() )
			return twigFromSimpleVerificationType( verificationType.asSimpleVerificationType() );
		else if( verificationType.isObjectVerificationType() )
			return twigFromObjectVerificationType( verificationType.asObjectVerificationType() );
		else if( verificationType.isUninitializedVerificationType() )
			return twigFromUninitializedVerificationType( verificationType.asUninitializedVerificationType() );
		else
			throw new AssertionError();
	}

	private static Twig twigFromObjectVerificationType( ObjectVerificationType objectVerificationType )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		String name = VerificationType.getTagNameFromTag( ObjectVerificationType.tag );
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
		String name = VerificationType.getTagNameFromTag( simpleVerificationType.tag );
		builder.append( name );
		String header = builder.toString();
		return Twig.of( header );
	}

	private Twig twigFromUninitializedVerificationType( UninitializedVerificationType uninitializedVerificationType )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		String name = VerificationType.getTagNameFromTag( UninitializedVerificationType.tag );
		builder.append( name );
		builder.append( ' ' );
		summarizeAbsoluteInstructionReference( uninitializedVerificationType.instructionReference, builder );
		String header = builder.toString();
		return Twig.of( header );
	}

	private static Twig twigFromTypeAnnotationsAttributeTarget( Target target )
	{
		var builder = new StringBuilder();
		builder.append( " " );
		if( target.isCatchTarget() )
			builder.append( "exceptionTableIndex = " ).append( target.asCatchTarget().exceptionTableIndex );
		else if( target.isEmptyTarget() )
		{
			target.asEmptyTarget();
			builder.append( "(empty)" );
		}
		else if( target.isFormalParameterTarget() )
			builder.append( "formalParameterIndex = " ).append( target.asFormalParameterTarget().formalParameterIndex );
		else if( target.isLocalVariableTarget() )
			builder.append( target.asLocalVariableTarget().entries.size() ).append( " entries" );
		else if( target.isOffsetTarget() )
			builder.append( "offset = " ).append( target.asOffsetTarget().offset );
		else if( target.isSupertypeTarget() )
			builder.append( "superTypeIndex = " ).append( target.asSupertypeTarget().supertypeIndex );
		else if( target.isThrowsTarget() )
			builder.append( "throwsTypeIndex = " ).append( target.asThrowsTarget().throwsTypeIndex );
		else if( target.isTypeArgumentTarget() )
		{
			TypeArgumentTarget typeArgumentTarget = target.asTypeArgumentTarget();
			builder.append( "offset = " ).append( typeArgumentTarget.offset );
			builder.append( ", typeArgumentIndex = " ).append( typeArgumentTarget.typeArgumentIndex );
		}
		else if( target.isTypeParameterBoundTarget() )
		{
			TypeParameterBoundTarget typeParameterBoundTarget = target.asTypeParameterBoundTarget();
			builder.append( "typeParameterIndex = " ).append( typeParameterBoundTarget.typeParameterIndex );
			builder.append( ", boundIndex = " ).append( typeParameterBoundTarget.boundIndex );
		}
		else if( target.isTypeParameterTarget() )
			builder.append( "typeParameterIndex = " ).append( target.asTypeParameterTarget().typeParameterIndex );
		else
			throw new AssertionError();
		String header = builder.toString();
		return Twig.of( header );
	}
}
