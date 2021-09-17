package mikenakis.testana.structure;

import mikenakis.bytecode.exceptions.UnknownValueException;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.Descriptor;
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
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariable;
import mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableType;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
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
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
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
import mikenakis.bytecode.model.oracle.ArrayTypeSignature;
import mikenakis.bytecode.model.oracle.BooleanSignature;
import mikenakis.bytecode.model.oracle.ByteSignature;
import mikenakis.bytecode.model.oracle.CharSignature;
import mikenakis.bytecode.model.oracle.ClassSignature;
import mikenakis.bytecode.model.oracle.ClassTypeSignature;
import mikenakis.bytecode.model.oracle.DoubleSignature;
import mikenakis.bytecode.model.oracle.FloatSignature;
import mikenakis.bytecode.model.oracle.IntSignature;
import mikenakis.bytecode.model.oracle.LongSignature;
import mikenakis.bytecode.model.oracle.MethodTypeSignature;
import mikenakis.bytecode.model.oracle.ObjectSignature;
import mikenakis.bytecode.model.oracle.ReturnType;
import mikenakis.bytecode.model.oracle.ShortSignature;
import mikenakis.bytecode.model.oracle.SignatureParser;
import mikenakis.bytecode.model.oracle.SimpleClassTypeSignature;
import mikenakis.bytecode.model.oracle.TypeSignature;
import mikenakis.bytecode.model.oracle.TypeTree;
import mikenakis.bytecode.model.oracle.TypeVariableSignature;
import mikenakis.bytecode.model.oracle.VoidDescriptor;
import mikenakis.kit.Kit;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.StringConcatFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class ByteCodeInfo
{
	private final ByteCodeType byteCodeType;

	ByteCodeInfo( ByteCodeType byteCodeType )
	{
		this.byteCodeType = byteCodeType;
	}

	Collection<String> getDependencyNames()
	{
		if( byteCodeType.thisClassConstant.getClassName().equals( "bytecode_tests.model.Class6WithAnnotations" ) )
			Kit.get( true ); //FIXME XXX TODO when we modify the parameters of the @Target annotation of RuntimeVisibleAnnotation1, testana does not detect that Class6WithAnnotations is out of date!
		Collection<String> mutableDependencyNames = new HashSet<>();
		addDependencyTypeNames( byteCodeType, mutableDependencyNames );
		for( Constant constant : byteCodeType.extraConstants() )
			addDependencyTypeNamesFromConstant( byteCodeType, constant, mutableDependencyNames );
		mutableDependencyNames.remove( byteCodeType.thisClassConstant.getClassName() );
		//trim( mutableDependencyNames );
		return mutableDependencyNames;
	}

	//TODO: get rid of?
	private static final Collection<String> dependencyNamesToRemove = Stream.of( //
		void.class, byte.class, boolean.class, char.class, short.class, int.class, //
		float.class, long.class, double.class, //
		Void.class, Byte.class, Boolean.class, Character.class, Short.class, Integer.class, //
		Float.class, Long.class, Double.class, //
		Object.class, Class.class, String.class, Optional.class, //
		Collection.class, Collections.class, Iterable.class, Iterator.class, //
		Set.class, HashSet.class, LinkedHashSet.class, //
		List.class, ArrayList.class, //
		Map.class, HashMap.class, TreeMap.class, LinkedHashMap.class, //
		Stream.class, Collectors.class, //
		Runnable.class, Consumer.class, Supplier.class, //
		Thread.class, Throwable.class, RuntimeException.class, AssertionError.class, InterruptedException.class, //
		CallSite.class, LambdaMetafactory.class, MethodHandle.class, MethodHandles.class, //
		MethodHandles.Lookup.class, MethodType.class, StringConcatFactory.class //
	).map( c -> c.getName() ).collect( Collectors.toUnmodifiableSet() );

	private static void trim( Collection<String> mutableDependencyNames )
	{
		for( String s : dependencyNamesToRemove )
			mutableDependencyNames.remove( s );
	}

	String getClassSourceLocation()
	{
		return ByteCodeHelpers.getClassSourceLocation( byteCodeType );
	}

	String getMethodSourceLocation( String methodName, String methodDescriptor, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		ByteCodeMethod byteCodeMethod = byteCodeType.getMethodByNameAndDescriptor( methodName, methodDescriptor, byteCodeTypeByName );
		return ByteCodeHelpers.getMethodSourceLocation( byteCodeType, byteCodeMethod );
	}

	public int getDeclaredMethodIndex( String methodName, String methodDescriptor )
	{
		return byteCodeType.findDeclaredMethodByNameAndDescriptor( methodName, methodDescriptor );
	}

	private static void addDependencyTypeNames( ByteCodeType byteCodeType, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromConstant( byteCodeType, byteCodeType.thisClassConstant, mutableDependencyNames );
		byteCodeType.superClassConstant.ifPresent( c -> addDependencyTypeNameFromClassConstant( c, mutableDependencyNames ) );
		for( ClassConstant classConstant : byteCodeType.interfaceClassConstants() )
			addDependencyTypeNameFromClassConstant( classConstant, mutableDependencyNames );
		for( ByteCodeField byteCodeField : byteCodeType.fields() )
		{
			addDependencyTypeNamesFromDescriptorString( byteCodeField.descriptorConstant.value() /* TODO: avoid conversion! */, mutableDependencyNames );
			for( Attribute attribute : byteCodeField.attributeSet )
			{
				switch( attribute.kind.name )
				{
					case ConstantValueAttribute.name:
						addDependencyTypeNamesFromConstantValueAttribute( byteCodeType, attribute.asConstantValueAttribute(), mutableDependencyNames );
						break;
					case DeprecatedAttribute.name:
						Kit.get( 1 );
						break;
					case RuntimeVisibleAnnotationsAttribute.name:
						addDependencyTypeNamesFromAnnotationsAttribute( byteCodeType, attribute.asRuntimeVisibleAnnotationsAttribute(), mutableDependencyNames );
						break;
					case RuntimeInvisibleAnnotationsAttribute.name:
						addDependencyTypeNamesFromAnnotationsAttribute( byteCodeType, attribute.asRuntimeInvisibleAnnotationsAttribute(), mutableDependencyNames );
						break;
					case RuntimeInvisibleTypeAnnotationsAttribute.name:
						addDependencyTypeNamesFromTypeAnnotationsAttribute( byteCodeType, attribute.asRuntimeInvisibleTypeAnnotationsAttribute(), mutableDependencyNames );
						break;
					case RuntimeVisibleTypeAnnotationsAttribute.name:
						addDependencyTypeNamesFromTypeAnnotationsAttribute( byteCodeType, attribute.asRuntimeVisibleTypeAnnotationsAttribute(), mutableDependencyNames );
						break;
					case SignatureAttribute.name:
						addDependencyTypeNamesFromFieldTypeSignature( attribute.asSignatureAttribute().signatureConstant().value(), mutableDependencyNames );
						break;
					case SyntheticAttribute.name:
						Kit.get( 2 );
						break;
					default:
						assert false;
						break;
				}
			}
		}
		for( ByteCodeMethod byteCodeMethod : byteCodeType.methods() )
		{
			addDependencyTypeNamesFromDescriptorString( byteCodeMethod.descriptorConstant.value() /* TODO: avoid conversion! */, mutableDependencyNames );
			for( Attribute attribute : byteCodeMethod.attributeSet )
			{
				switch( attribute.kind.name )
				{
					case AnnotationDefaultAttribute.name:
						addDependencyTypeNamesFromAnnotationDefaultAttribute( byteCodeType, attribute.asAnnotationDefaultAttribute(), mutableDependencyNames );
						break;
					case CodeAttribute.name:
						addDependencyTypeNamesFromCodeAttribute( byteCodeType, attribute.asCodeAttribute(), mutableDependencyNames );
						break;
					case DeprecatedAttribute.name:
						Kit.get( 1 );
						break;
					case ExceptionsAttribute.name:
						addDependencyTypeNamesFromExceptionsAttribute( byteCodeType, attribute.asExceptionsAttribute(), mutableDependencyNames );
						break;
					case MethodParametersAttribute.name:
						Kit.get( 2 );
						break;
					case RuntimeVisibleAnnotationsAttribute.name:
						addDependencyTypeNamesFromAnnotationsAttribute( byteCodeType, attribute.asRuntimeVisibleAnnotationsAttribute(), mutableDependencyNames );
						break;
					case RuntimeInvisibleAnnotationsAttribute.name:
						addDependencyTypeNamesFromAnnotationsAttribute( byteCodeType, attribute.asRuntimeInvisibleAnnotationsAttribute(), mutableDependencyNames );
						break;
					case RuntimeInvisibleParameterAnnotationsAttribute.name:
						addDependencyTypeNamesFromParameterAnnotationsAttribute( byteCodeType, attribute.asRuntimeInvisibleParameterAnnotationsAttribute(), mutableDependencyNames );
						break;
					case RuntimeVisibleParameterAnnotationsAttribute.name:
						addDependencyTypeNamesFromParameterAnnotationsAttribute( byteCodeType, attribute.asRuntimeVisibleParameterAnnotationsAttribute(), mutableDependencyNames );
						break;
					case RuntimeInvisibleTypeAnnotationsAttribute.name:
						addDependencyTypeNamesFromTypeAnnotationsAttribute( byteCodeType, attribute.asRuntimeInvisibleTypeAnnotationsAttribute(), mutableDependencyNames );
						break;
					case RuntimeVisibleTypeAnnotationsAttribute.name:
						addDependencyTypeNamesFromTypeAnnotationsAttribute( byteCodeType, attribute.asRuntimeVisibleTypeAnnotationsAttribute(), mutableDependencyNames );
						break;
					case SignatureAttribute.name:
						addDependencyTypeNamesFromMethodTypeSignatureAttribute( attribute.asSignatureAttribute().signatureConstant().value(), mutableDependencyNames );
						break;
					case SyntheticAttribute.name:
						Kit.get( 3 );
						break;
					default:
						assert false;
						break;
				}
			}
		}
		for( Attribute attribute : byteCodeType.attributeSet() )
		{
			switch( attribute.kind.name )
			{
				case BootstrapMethodsAttribute.name:
					addDependencyTypeNamesFromBootstrapMethodsAttribute( byteCodeType, attribute.asBootstrapMethodsAttribute(), mutableDependencyNames );
					break;
				case DeprecatedAttribute.name:
					Kit.get( 1 );
					break;
				case EnclosingMethodAttribute.name:
					addDependencyTypeNamesFromEnclosingMethodAttribute( byteCodeType, attribute.asEnclosingMethodAttribute(), mutableDependencyNames );
					break;
				case InnerClassesAttribute.name:
					addDependencyTypeNamesFromInnerClassesAttribute( byteCodeType, attribute.asInnerClassesAttribute(), mutableDependencyNames );
					break;
				//				case ModuleAttribute.name:
				//				case ModulePackageAttribute.name:
				//				case ModuleMainClassAttribute.name:
				case NestHostAttribute.name:
				{
					NestHostAttribute nestHostAttribute = attribute.asNestHostAttribute();
					addDependencyTypeNameFromClassConstant( nestHostAttribute.hostClassConstant, mutableDependencyNames );
					break;
				}
				case NestMembersAttribute.name:
				{
					NestMembersAttribute nestMembersAttribute = attribute.asNestMembersAttribute();
					for( ClassConstant memberClassConstant : nestMembersAttribute.memberClassConstants() )
						addDependencyTypeNameFromClassConstant( memberClassConstant, mutableDependencyNames );
					break;
				}
				//              case RecordAttribute.name:
				case RuntimeInvisibleAnnotationsAttribute.name:
					addDependencyTypeNamesFromAnnotationsAttribute( byteCodeType, attribute.asRuntimeInvisibleAnnotationsAttribute(), mutableDependencyNames );
					break;
				case RuntimeInvisibleTypeAnnotationsAttribute.name:
					addDependencyTypeNamesFromTypeAnnotationsAttribute( byteCodeType, attribute.asRuntimeInvisibleTypeAnnotationsAttribute(), mutableDependencyNames );
					break;
				case RuntimeVisibleAnnotationsAttribute.name:
					addDependencyTypeNamesFromAnnotationsAttribute( byteCodeType, attribute.asRuntimeVisibleAnnotationsAttribute(), mutableDependencyNames );
					break;
				case RuntimeVisibleTypeAnnotationsAttribute.name:
					addDependencyTypeNamesFromTypeAnnotationsAttribute( byteCodeType, attribute.asRuntimeVisibleTypeAnnotationsAttribute(), mutableDependencyNames );
					break;
				case SignatureAttribute.name:
					addDependencyTypeNamesFromClassTypeSignatureString( attribute.asSignatureAttribute().signatureConstant().value(), mutableDependencyNames );
					break;
				//SourceDebugExtension
				case SourceFileAttribute.name:
					Kit.get( 2 );
					break;
				case SyntheticAttribute.name:
					Kit.get( 3 );
					break;
				default:
					assert attribute instanceof UnknownAttribute;
					break;
			}
		}
	}

	private static void addDependencyTypeNamesFromAnnotationDefaultAttribute( ByteCodeType byteCodeType, AnnotationDefaultAttribute attribute, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromAnnotationValue( byteCodeType, attribute.annotationValue(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromBootstrapMethodsAttribute( ByteCodeType byteCodeType, BootstrapMethodsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( BootstrapMethod bootstrapMethod : attribute.bootstrapMethods() )
		{
			addDependencyTypeNamesFromConstant( byteCodeType, bootstrapMethod.methodHandleConstant().referenceConstant().typeConstant(), mutableDependencyNames );
			addDependencyTypeNamesFromConstant( byteCodeType, bootstrapMethod.methodHandleConstant().referenceConstant().nameAndTypeConstant(), mutableDependencyNames );
			for( var constant : bootstrapMethod.argumentConstants() )
				addDependencyTypeNamesFromConstant( byteCodeType, constant, mutableDependencyNames );
		}
	}

	private static void addDependencyTypeNamesFromCodeAttribute( ByteCodeType byteCodeType, CodeAttribute codeAttribute, Collection<String> mutableDependencyNames )
	{
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos() )
			exceptionInfo.catchTypeConstant.ifPresent( c -> addDependencyTypeNamesFromConstant( byteCodeType, c, mutableDependencyNames ) );
		for( Instruction instruction : codeAttribute.instructions().all() )
		{
			if( byteCodeType.thisClassConstant.getClassName().equals( "bytecode_tests.T003_Writing" ) && instruction.getOpCode() == OpCode.INVOKESTATIC )
				Kit.get( false );
			switch( instruction.group )
			{
				case Branch:
					//instruction.asBranchInstruction();
					break;
				case ConditionalBranch:
					//instruction.asConditionalBranchInstruction();
					break;
				case ConstantReferencing:
				{
					ConstantReferencingInstruction constantReferencingInstruction = instruction.asConstantReferencingInstruction();
					addDependencyTypeNamesFromConstant( byteCodeType, constantReferencingInstruction.constant, mutableDependencyNames );
					break;
				}
				case IInc:
					//instruction.asIIncInstruction();
					break;
				case ImmediateLoadConstant:
					//instruction.asImmediateLoadConstantInstruction();
					break;
				case IndirectLoadConstant:
				{
					IndirectLoadConstantInstruction indirectLoadConstantInstruction = instruction.asIndirectLoadConstantInstruction();
					addDependencyTypeNamesFromConstant( byteCodeType, indirectLoadConstantInstruction.constant, mutableDependencyNames );
					break;
				}
				case InvokeDynamic:
				{
					InvokeDynamicInstruction invokeDynamicInstruction = instruction.asInvokeDynamicInstruction();
					addDependencyTypeNamesFromInvokeDynamicConstant( byteCodeType, invokeDynamicInstruction.invokeDynamicConstant, mutableDependencyNames );
					break;
				}
				case InvokeInterface:
				{
					InvokeInterfaceInstruction invokeInterfaceInstruction = instruction.asInvokeInterfaceInstruction();
					addDependencyTypeNamesFromReferenceConstant( invokeInterfaceInstruction.interfaceMethodReferenceConstant, mutableDependencyNames );
					break;
				}
				case LocalVariable:
					//instruction.asLocalVariableInstruction();
					break;
				case LookupSwitch:
					//instruction.asLookupSwitchInstruction();
					break;
				case MultiANewArray:
				{
					MultiANewArrayInstruction multiANewArrayInstruction = instruction.asMultiANewArrayInstruction();
					addDependencyTypeNameFromClassConstant( multiANewArrayInstruction.classConstant, mutableDependencyNames );
					break;
				}
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
					//instruction.asTableSwitchInstruction();
					break;
				default:
					assert false;
			}
		}
		for( Attribute attribute : codeAttribute.attributeSet() )
		{
			switch( attribute.kind.name )
			{
				case LineNumberTableAttribute.name:
					Kit.get( 1 );
					break;
				case LocalVariableTableAttribute.name:
					addDependencyTypeNamesFromLocalVariableTableAttribute( byteCodeType, attribute.asLocalVariableTableAttribute(), mutableDependencyNames );
					break;
				case LocalVariableTypeTableAttribute.name:
					addDependencyTypeNamesFromLocalVariableTypeTableAttribute( attribute.asLocalVariableTypeTableAttribute(), mutableDependencyNames );
					break;
				case RuntimeInvisibleTypeAnnotationsAttribute.name:
					addDependencyTypeNamesFromTypeAnnotationsAttribute( byteCodeType, attribute.asRuntimeInvisibleTypeAnnotationsAttribute(), mutableDependencyNames );
					break;
				case RuntimeVisibleTypeAnnotationsAttribute.name:
					addDependencyTypeNamesFromTypeAnnotationsAttribute( byteCodeType, attribute.asRuntimeVisibleTypeAnnotationsAttribute(), mutableDependencyNames );
					break;
				case StackMapTableAttribute.name:
					Kit.get( 2 );
					break;
				default:
					assert false;
					break;
			}
		}
	}

	private static void addDependencyTypeNamesFromConstantValueAttribute( ByteCodeType byteCodeType, ConstantValueAttribute attribute, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromConstant( byteCodeType, attribute.valueConstant(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromEnclosingMethodAttribute( ByteCodeType byteCodeType, EnclosingMethodAttribute attribute, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromConstant( byteCodeType, attribute.classConstant(), mutableDependencyNames );
		attribute.methodNameAndTypeConstant().ifPresent( c -> addDependencyTypeNamesFromConstant( byteCodeType, c, mutableDependencyNames ) );
	}

	private static void addDependencyTypeNamesFromExceptionsAttribute( ByteCodeType byteCodeType, ExceptionsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( ClassConstant classConstant : attribute.exceptionClassConstants() )
			addDependencyTypeNamesFromConstant( byteCodeType, classConstant, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInnerClassesAttribute( ByteCodeType byteCodeType, InnerClassesAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( InnerClass innerClass : attribute.innerClasses() )
		{
			addDependencyTypeNamesFromConstant( byteCodeType, innerClass.innerClassConstant(), mutableDependencyNames );
			innerClass.outerClassConstant().ifPresent( c -> addDependencyTypeNamesFromConstant( byteCodeType, c, mutableDependencyNames ) );
		}
	}

	private static void addDependencyTypeNamesFromLocalVariableTableAttribute( ByteCodeType byteCodeType, LocalVariableTableAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( LocalVariable localVariable : attribute.localVariables() )
			addDependencyTypeNamesFromConstant( byteCodeType, localVariable.descriptorConstant, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromLocalVariableTypeTableAttribute( LocalVariableTypeTableAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( LocalVariableType localVariableType : attribute.localVariableTypes() )
			addDependencyTypeNamesFromFieldTypeSignature( localVariableType.signatureConstant.value() /* TODO: avoid conversion to string! */, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromAnnotationsAttribute( ByteCodeType byteCodeType, AnnotationsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( ByteCodeAnnotation byteCodeAnnotation : attribute.annotations() )
			addDependencyTypeNamesFromAnnotation( byteCodeType, byteCodeAnnotation, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromParameterAnnotationsAttribute( ByteCodeType byteCodeType, ParameterAnnotationsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( ParameterAnnotationSet parameterAnnotationSet : attribute.parameterAnnotationSets() )
			for( ByteCodeAnnotation byteCodeAnnotation : parameterAnnotationSet.annotations() )
				addDependencyTypeNamesFromAnnotation( byteCodeType, byteCodeAnnotation, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromTypeAnnotationsAttribute( ByteCodeType byteCodeType, TypeAnnotationsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( TypeAnnotation typeAnnotation : attribute.typeAnnotations() )
			for( TypeAnnotation.ElementValuePair elementValuePair : typeAnnotation.elementValuePairs() )
				addDependencyTypeNamesFromAnnotationValue( byteCodeType, elementValuePair.elementValue().annotationValue(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromAnnotationValue( ByteCodeType byteCodeType, AnnotationValue annotationValue, Collection<String> mutableDependencyNames )
	{
		switch( annotationValue.tag )
		{
			case 'B':
			case 's':
			case 'Z':
			case 'S':
			case 'J':
			case 'I':
			case 'F':
			case 'D':
			case 'C':
				addDependencyTypeNamesFromConstant( byteCodeType, annotationValue.asConstAnnotationValue().valueConstant(), mutableDependencyNames );
				break;
			case 'e':
				addDependencyTypeNamesFromConstant( byteCodeType, annotationValue.asEnumAnnotationValue().typeNameConstant(), mutableDependencyNames );
				break;
			case 'c':
				addDependencyTypeNamesFromConstant( byteCodeType, annotationValue.asClassAnnotationValue().utf8Constant(), mutableDependencyNames );
				break;
			case '@':
				addDependencyTypeNamesFromAnnotation( byteCodeType, annotationValue.asAnnotationAnnotationValue().annotation(), mutableDependencyNames );
				break;
			case '[':
				for( AnnotationValue value : annotationValue.asArrayAnnotationValue().annotationValues() )
					addDependencyTypeNamesFromAnnotationValue( byteCodeType, value, mutableDependencyNames );
				break;
			default:
				throw new UnknownValueException( annotationValue.tag );
		}
	}

	private static void addDependencyTypeNamesFromAnnotation( ByteCodeType byteCodeType, ByteCodeAnnotation byteCodeAnnotation, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromClassTypeSignatureString( byteCodeAnnotation.nameConstant().value(), mutableDependencyNames );
		for( AnnotationParameter annotationParameter : byteCodeAnnotation.annotationParameters() )
			addDependencyTypeNamesFromAnnotationValue( byteCodeType, annotationParameter.annotationValue(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromFieldTypeSignature( String signature, Collection<String> mutableDependencyNames )
	{
		ObjectSignature parsedSignature = SignatureParser.make().parseFieldSig( signature ); // parseFieldTypeSignature() {
		if( parsedSignature instanceof ClassSignature classSignature )
			addDependencyTypeNamesFromClassTypeSignature( classSignature, mutableDependencyNames );
		else if( parsedSignature instanceof TypeVariableSignature )
			Kit.get( 1 );
		else if( parsedSignature instanceof ArrayTypeSignature arrayTypeSignature )
			addDependencyTypeNamesFromArrayTypeSignature( arrayTypeSignature, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromClassTypeSignatureString( String signature, Collection<String> mutableDependencyNames )
	{
		ClassSignature parsedSignature = SignatureParser.make().parseClassSig( signature );
		addDependencyTypeNamesFromClassTypeSignature( parsedSignature, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromClassTypeSignature( ClassSignature parsedSignature, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromSignaturePath( parsedSignature.getSuperclass().getPath(), mutableDependencyNames );
		for( var superInterface : parsedSignature.getSuperInterfaces() )
			addDependencyTypeNamesFromSignaturePath( superInterface.getPath(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromArrayTypeSignature( ArrayTypeSignature arrayTypeSignature, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromTypeSignature( arrayTypeSignature.getComponentType(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromTypeSignature( TypeSignature typeSignature, Collection<String> mutableDependencyNames )
	{
		if( typeSignature instanceof ArrayTypeSignature arrayTypeSignature )
			addDependencyTypeNamesFromArrayTypeSignature( arrayTypeSignature, mutableDependencyNames );
		else if( typeSignature instanceof ClassTypeSignature classTypeSignature )
			addDependencyTypeNamesFromClassTypeSignature( classTypeSignature, mutableDependencyNames );
		else if( typeSignature instanceof TypeVariableSignature typeVariableSignature )
			Kit.get( 1 );
		else
			assert false;
	}

	private static void addDependencyTypeNamesFromSignaturePath( Collection<SimpleClassTypeSignature> signaturePath, Collection<String> mutableDependencyNames )
	{
		String fullTypeName = signaturePath.stream().map( s -> s.getName() ).collect( Collectors.joining( "." ) );
		addTypeDependencyName( fullTypeName, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromMethodTypeSignatureAttribute( String signature, Collection<String> mutableDependencyNames )
	{
		if( Kit.get( true ) )
			return;
		MethodTypeSignature parsedSignature = SignatureParser.make().parseMethodSig( signature );
		ReturnType returnType = parsedSignature.getReturnType();
		addDependencyTypeNamesFromSignatureTypeTree( returnType, mutableDependencyNames );
		for( TypeSignature parameterType : parsedSignature.getParameterTypes() )
			addDependencyTypeNamesFromSignatureTypeTree( parameterType, mutableDependencyNames );
		for( var exceptionType : parsedSignature.getExceptionTypes() )
			addDependencyTypeNamesFromSignatureTypeTree( exceptionType, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromSignatureTypeTree( TypeTree typeTree, Collection<String> mutableDependencyNames )
	{
		if( typeTree instanceof VoidDescriptor )
			Kit.get( true );
		else if( typeTree instanceof ByteSignature )
			Kit.get( true );
		else if( typeTree instanceof BooleanSignature )
			Kit.get( true );
		else if( typeTree instanceof CharSignature )
			Kit.get( true );
		else if( typeTree instanceof ShortSignature )
			Kit.get( true );
		else if( typeTree instanceof IntSignature )
			Kit.get( true );
		else if( typeTree instanceof FloatSignature )
			Kit.get( true );
		else if( typeTree instanceof LongSignature )
			Kit.get( true );
		else if( typeTree instanceof DoubleSignature )
			Kit.get( true );
		else if( typeTree instanceof TypeVariableSignature )
			Kit.get( true );
		else if( typeTree instanceof ClassTypeSignature classTypeSignature )
			addDependencyTypeNamesFromClassTypeSignature( classTypeSignature, mutableDependencyNames );
		else if( typeTree instanceof ArrayTypeSignature arrayTypeSignature )
			addDependencyTypeNamesFromSignatureTypeTree( arrayTypeSignature.getComponentType(), mutableDependencyNames );
		else
			assert false;
	}

	private static void addDependencyTypeNamesFromClassTypeSignature( ClassTypeSignature classTypeSignature, Collection<String> mutableDependencyNames )
	{
		List<SimpleClassTypeSignature> path = classTypeSignature.getPath();
		for( SimpleClassTypeSignature element : path )
			addTypeDependencyName( element.getName(), mutableDependencyNames );
	}

	private static void addTypeDependencyName( String dependencyName, Collection<String> mutableDependencyNames )
	{
		assert dependencyName != null;
		dependencyName = stripArrayNotation( dependencyName );
		assert ByteCodeHelpers.isJavaTypeName( dependencyName );
		Kit.collection.addOrReplace( mutableDependencyNames, dependencyName );
	}

	private static String stripArrayNotation( String typeName )
	{
		while( typeName.endsWith( "[]" ) )
			typeName = typeName.substring( 0, typeName.length() - 2 );
		return typeName;
	}

	private static void addDependencyTypeNamesFromConstant( ByteCodeType byteCodeType, Constant constant, Collection<String> mutableDependencyNames )
	{
		switch( constant.tag )
		{
			case Utf8Constant.TAG:
			case IntegerConstant.TAG:
			case FloatConstant.TAG:
			case LongConstant.TAG:
			case DoubleConstant.TAG:
			case StringConstant.TAG:
				break;
			case ClassConstant.TAG:
				addDependencyTypeNameFromClassConstant( constant.asClassConstant(), mutableDependencyNames );
				break;
			case FieldReferenceConstant.TAG:
			case PlainMethodReferenceConstant.TAG:
			case InterfaceMethodReferenceConstant.TAG:
				addDependencyTypeNamesFromReferenceConstant( constant.asReferenceConstant(), mutableDependencyNames );
				break;
			case NameAndTypeConstant.TAG:
			{
				NameAndTypeConstant nameAndTypeConstant = constant.asNameAndTypeConstant();
				String descriptor = nameAndTypeConstant.descriptorConstant().value();
				addDependencyTypeNamesFromDescriptorString( descriptor, mutableDependencyNames );
				break;
			}
			case MethodHandleConstant.TAG:
			{
				MethodHandleConstant methodHandleConstant = constant.asMethodHandleConstant();
				addDependencyTypeNamesFromReferenceConstant( methodHandleConstant.referenceConstant(), mutableDependencyNames );
				break;
			}
			case MethodTypeConstant.TAG:
			{
				MethodTypeConstant methodTypeConstant = constant.asMethodTypeConstant();
				String descriptor = methodTypeConstant.descriptorConstant.value();
				addDependencyTypeNamesFromDescriptorString( descriptor, mutableDependencyNames );
				break;
			}
			case InvokeDynamicConstant.TAG:
				addDependencyTypeNamesFromInvokeDynamicConstant( byteCodeType, constant.asInvokeDynamicConstant(), mutableDependencyNames );
				break;
			default:
				assert false;
		}
	}

	private static void addDependencyTypeNameFromClassConstant( ClassConstant classConstant, Collection<String> mutableDependencyNames )
	{
		String className = classConstant.getClassName();
		addTypeDependencyName( className, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromReferenceConstant( ReferenceConstant referenceConstant, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassConstant( referenceConstant.typeConstant(), mutableDependencyNames );
		addDependencyTypeNamesFromDescriptorString( referenceConstant.nameAndTypeConstant().descriptorConstant().value(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInvokeDynamicConstant( ByteCodeType byteCodeType, InvokeDynamicConstant invokeDynamicConstant, Collection<String> mutableDependencyNames )
	{
		BootstrapMethod bootstrapMethod = byteCodeType.getBootstrapMethodByIndex( invokeDynamicConstant.bootstrapMethodIndex() );
		addDependencyTypeNamesFromReferenceConstant( bootstrapMethod.methodHandleConstant().referenceConstant(), mutableDependencyNames );
		for( Constant argumentConstant : bootstrapMethod.argumentConstants() )
			addDependencyTypeNamesFromConstant( byteCodeType, argumentConstant, mutableDependencyNames );
		addDependencyTypeNamesFromDescriptorString( invokeDynamicConstant.nameAndTypeConstant().descriptorConstant().value(), mutableDependencyNames );
	}

	// TODO: check whether it is necessary to include types from descriptor strings.
	private static void addDependencyTypeNamesFromDescriptorString( String descriptorString, Collection<String> mutableDependencyNames )
	{
		Descriptor descriptor = Descriptor.from( descriptorString );
		addTypeDependencyName( descriptor.typeName, mutableDependencyNames );
		if( descriptor.argumentTypeNames != null )
			for( String argumentTypeName : descriptor.argumentTypeNames )
				addTypeDependencyName( argumentTypeName, mutableDependencyNames );
	}
}
