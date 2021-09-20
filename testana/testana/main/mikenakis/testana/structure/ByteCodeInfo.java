package mikenakis.testana.structure;

import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.annotationvalues.AnnotationAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ArrayAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ClassAnnotationValue;
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
import mikenakis.bytecode.model.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.signature.ArrayTypeSignature;
import mikenakis.bytecode.model.signature.BooleanSignature;
import mikenakis.bytecode.model.signature.ByteSignature;
import mikenakis.bytecode.model.signature.CharSignature;
import mikenakis.bytecode.model.signature.ClassSignature;
import mikenakis.bytecode.model.signature.ClassTypeSignature;
import mikenakis.bytecode.model.signature.DoubleSignature;
import mikenakis.bytecode.model.signature.FloatSignature;
import mikenakis.bytecode.model.signature.IntSignature;
import mikenakis.bytecode.model.signature.LongSignature;
import mikenakis.bytecode.model.signature.MethodTypeSignature;
import mikenakis.bytecode.model.signature.ObjectSignature;
import mikenakis.bytecode.model.signature.ReturnType;
import mikenakis.bytecode.model.signature.ShortSignature;
import mikenakis.bytecode.model.signature.SignatureParser;
import mikenakis.bytecode.model.signature.SimpleClassTypeSignature;
import mikenakis.bytecode.model.signature.TypeSignature;
import mikenakis.bytecode.model.signature.TypeTree;
import mikenakis.bytecode.model.signature.TypeVariableSignature;
import mikenakis.bytecode.model.signature.VoidDescriptor;
import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.kit.Kit;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
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
		ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ); //TODO get rid of!
		if( byteCodeType.name().equals( "bytecode_tests.model.Class6WithAnnotations" ) )
			Kit.get( true ); //FIXME XXX TODO when we modify the parameters of the @Target annotation of RuntimeVisibleAnnotation1, testana does not detect that Class6WithAnnotations is out of date!
		Collection<String> mutableDependencyNames = new HashSet<>();
		addDependencyTypeNames( byteCodeType, mutableDependencyNames );
		for( Constant constant : byteCodeType.extraConstants() )
			addDependencyTypeNamesFromConstant( constant, mutableDependencyNames );
		mutableDependencyNames.remove( byteCodeType.name() );
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

	String getMethodSourceLocation( String methodName, MethodTypeDesc methodDescriptor, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		ByteCodeMethod byteCodeMethod = byteCodeType.getMethodByNameAndDescriptor( methodName, methodDescriptor, byteCodeTypeByName );
		return ByteCodeHelpers.getMethodSourceLocation( byteCodeType, byteCodeMethod );
	}

	public int getDeclaredMethodIndex( String methodName, MethodTypeDesc methodDescriptor )
	{
		return byteCodeType.findDeclaredMethodByNameAndDescriptor( methodName, methodDescriptor );
	}

	private static void addDependencyTypeNames( ByteCodeType byteCodeType, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( byteCodeType.name(), mutableDependencyNames );
		byteCodeType.superClassName().ifPresent( c -> addDependencyTypeName( c, mutableDependencyNames ) );
		for( ClassConstant classConstant : byteCodeType.interfaceClassConstants() )
			addDependencyTypeNameFromClassConstant( classConstant, mutableDependencyNames );
		for( ByteCodeField byteCodeField : byteCodeType.fields() )
		{
			addDependencyTypeNameFromClassDescriptor( byteCodeField.descriptor(), mutableDependencyNames );
			for( Attribute attribute : byteCodeField.attributeSet )
			{
				switch( attribute.kind.name )
				{
					case ConstantValueAttribute.name:
						addDependencyTypeNamesFromConstantValueAttribute( attribute.asConstantValueAttribute(), mutableDependencyNames );
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
						addDependencyTypeNamesFromFieldTypeSignature( attribute.asSignatureAttribute().signatureConstant().stringValue(), mutableDependencyNames );
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
			addDependencyTypeNamesFromMethodDescriptor( byteCodeMethod.descriptor(), mutableDependencyNames );
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
						addDependencyTypeNamesFromExceptionsAttribute( attribute.asExceptionsAttribute(), mutableDependencyNames );
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
						addDependencyTypeNamesFromMethodTypeSignatureAttribute( attribute.asSignatureAttribute().signatureConstant().stringValue(), mutableDependencyNames );
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
					addDependencyTypeNamesFromBootstrapMethodsAttribute( attribute.asBootstrapMethodsAttribute(), mutableDependencyNames );
					break;
				case DeprecatedAttribute.name:
					Kit.get( 1 );
					break;
				case EnclosingMethodAttribute.name:
					addDependencyTypeNamesFromEnclosingMethodAttribute( attribute.asEnclosingMethodAttribute(), mutableDependencyNames );
					break;
				case InnerClassesAttribute.name:
					addDependencyTypeNamesFromInnerClassesAttribute( attribute.asInnerClassesAttribute(), mutableDependencyNames );
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
					addDependencyTypeNamesFromClassTypeSignatureString( attribute.asSignatureAttribute().signatureConstant().stringValue(), mutableDependencyNames );
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

	private static void addDependencyTypeNamesFromBootstrapMethodsAttribute( BootstrapMethodsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( BootstrapMethod bootstrapMethod : attribute.bootstrapMethods() )
			addDependencyTypeNamesFromBootstrapMethod( bootstrapMethod, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromCodeAttribute( ByteCodeType byteCodeType, CodeAttribute codeAttribute, Collection<String> mutableDependencyNames )
	{
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos() )
			exceptionInfo.catchTypeConstant.ifPresent( classConstant -> addDependencyTypeNameFromClassConstant( classConstant, mutableDependencyNames ) );
		for( Instruction instruction : codeAttribute.instructions().all() )
		{
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
					Constant constant = constantReferencingInstruction.constant;
					switch( constant.tag )
					{
						case InterfaceMethodReference:
							addDependencyTypeNamesFromInterfaceMethodReferenceConstant( constant.asInterfaceMethodReferenceConstant(), mutableDependencyNames );
							break;
						case MethodReference:
							addDependencyTypeNamesFromPlainMethodReferenceConstant( constant.asPlainMethodReferenceConstant(), mutableDependencyNames );
							break;
						case FieldReference:
							addDependencyTypeNamesFromFieldReferenceConstant( constant.asFieldReferenceConstant(), mutableDependencyNames );
							break;
						case Class:
							addDependencyTypeNameFromClassConstant( constant.asClassConstant(), mutableDependencyNames );
							break;
						default:
							assert false;
					}
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
					switch( indirectLoadConstantInstruction.constant.tag )
					{
						case Float:
						case Long:
						case Integer:
						case Double:
						case String:
							break;
						case Class:
							addDependencyTypeNameFromClassConstant( indirectLoadConstantInstruction.constant.asClassConstant(), mutableDependencyNames );
							break;
						default:
							assert false;
					}
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
					addDependencyTypeNamesFromInterfaceMethodReferenceConstant( invokeInterfaceInstruction.interfaceMethodReferenceConstant, mutableDependencyNames );
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
					addDependencyTypeNamesFromLocalVariableTableAttribute( attribute.asLocalVariableTableAttribute(), mutableDependencyNames );
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

	private static void addDependencyTypeNamesFromConstantValueAttribute( ConstantValueAttribute constantValueAttribute, Collection<String> mutableDependencyNames )
	{
		switch( constantValueAttribute.valueConstant().tag )
		{
			case Mutf8:
			case Integer:
			case Float:
			case Long:
			case Double:
			case String:
				break;
			default:
				assert false;
		}
	}

	private static void addDependencyTypeNamesFromEnclosingMethodAttribute( EnclosingMethodAttribute attribute, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassConstant( attribute.classConstant(), mutableDependencyNames );
		attribute.methodNameAndDescriptorConstant().ifPresent( nameAndDescriptorConstant -> //
		{
			MethodTypeDesc methodDescriptor = MethodTypeDesc.ofDescriptor( nameAndDescriptorConstant.descriptorConstant().stringValue() );
			addDependencyTypeNamesFromMethodDescriptor( methodDescriptor, mutableDependencyNames );
		} );
	}

	private static void addDependencyTypeNamesFromExceptionsAttribute( ExceptionsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( ClassConstant classConstant : attribute.exceptionClassConstants() )
			addDependencyTypeNameFromClassConstant( classConstant, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInnerClassesAttribute( InnerClassesAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( InnerClass innerClass : attribute.innerClasses() )
		{
			addDependencyTypeNameFromClassConstant( innerClass.innerClassConstant(), mutableDependencyNames );
			innerClass.outerClassConstant().ifPresent( classConstant -> addDependencyTypeNameFromClassConstant( classConstant, mutableDependencyNames ) );
		}
	}

	private static void addDependencyTypeNamesFromLocalVariableTableAttribute( LocalVariableTableAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( LocalVariable localVariable : attribute.localVariables() )
			addDependencyTypeNamesFromTypeNameConstant( localVariable.descriptorConstant, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromLocalVariableTypeTableAttribute( LocalVariableTypeTableAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( LocalVariableType localVariableType : attribute.localVariableTypes() )
			addDependencyTypeNamesFromFieldTypeSignature( localVariableType.signatureConstant.stringValue(), mutableDependencyNames );
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

	private static void addDependencyTypeNamesFromAnnotationValue( ByteCodeType byteCodeType /* TODO: remove this parameter */, AnnotationValue annotationValue, Collection<String> mutableDependencyNames )
	{
		switch( annotationValue.tag )
		{
			case Byte:
			case String:
			case Boolean:
			case Short:
			case Long:
			case Integer:
			case Float:
			case Double:
			case Character:
				break;
			case Enum:
			{
				EnumAnnotationValue enumAnnotationValue = annotationValue.asEnumAnnotationValue();
				addDependencyTypeNameFromClassDescriptor( enumAnnotationValue.typeDescriptor(), mutableDependencyNames );
				break;
			}
			case Class:
			{
				ClassAnnotationValue classAnnotationValue = annotationValue.asClassAnnotationValue();
				addDependencyTypeNameFromClassDescriptor( classAnnotationValue.classDescriptor(), mutableDependencyNames );
				break;
			}
			case Annotation:
			{
				AnnotationAnnotationValue annotationAnnotationValue = annotationValue.asAnnotationAnnotationValue();
				addDependencyTypeNamesFromAnnotation( byteCodeType, annotationAnnotationValue.annotation(), mutableDependencyNames );
				break;
			}
			case Array:
			{
				ArrayAnnotationValue arrayAnnotationValue = annotationValue.asArrayAnnotationValue();
				for( AnnotationValue value : arrayAnnotationValue.annotationValues() )
					addDependencyTypeNamesFromAnnotationValue( byteCodeType, value, mutableDependencyNames );
				break;
			}
			default:
				throw new AssertionError( annotationValue );
		}
	}

	private static void addDependencyTypeNamesFromAnnotation( ByteCodeType byteCodeType, ByteCodeAnnotation byteCodeAnnotation, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassDescriptor( byteCodeAnnotation.typeDescriptor(), mutableDependencyNames );
		for( AnnotationParameter annotationParameter : byteCodeAnnotation.parameters() )
			addDependencyTypeNamesFromAnnotationValue( byteCodeType, annotationParameter.annotationValue(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromFieldTypeSignature( String signature, Collection<String> mutableDependencyNames )
	{
		ObjectSignature parsedSignature = SignatureParser.make().parseFieldSig( signature ); // parseFieldTypeSignature() {
		if( parsedSignature instanceof ClassSignature classSignature )
			addDependencyTypeNamesFromClassSignature( classSignature, mutableDependencyNames );
		else if( parsedSignature instanceof TypeVariableSignature )
			Kit.get( 1 );
		else if( parsedSignature instanceof ArrayTypeSignature arrayTypeSignature )
			addDependencyTypeNamesFromArrayTypeSignature( arrayTypeSignature, mutableDependencyNames );
		else if( parsedSignature instanceof ClassTypeSignature classTypeSignature )
			addDependencyTypeNamesFromClassTypeSignature( classTypeSignature, mutableDependencyNames );
		else
			assert false;
	}

	private static void addDependencyTypeNamesFromClassTypeSignatureString( String signature, Collection<String> mutableDependencyNames )
	{
		ClassSignature parsedSignature = SignatureParser.make().parseClassSig( signature );
		addDependencyTypeNamesFromClassSignature( parsedSignature, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromClassSignature( ClassSignature parsedSignature, Collection<String> mutableDependencyNames )
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
		else if( typeSignature instanceof TypeVariableSignature )
			Kit.get( 1 );
		else
			assert false;
	}

	private static void addDependencyTypeNamesFromSignaturePath( Collection<SimpleClassTypeSignature> signaturePath, Collection<String> mutableDependencyNames )
	{
		String fullTypeName = signaturePath.stream().map( s -> s.getName() ).collect( Collectors.joining( "." ) );
		addDependencyTypeName( fullTypeName, mutableDependencyNames );
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
			addDependencyTypeName( element.getName(), mutableDependencyNames );
	}

	private static void addDependencyTypeName( String dependencyName, Collection<String> mutableDependencyNames )
	{
		assert dependencyName != null;
		dependencyName = stripArrayNotation( dependencyName );
		assert isValidTypeName( dependencyName );
		Kit.collection.addOrReplace( mutableDependencyNames, dependencyName );
	}

	private static boolean isValidTypeName( String typeName )
	{
		if( typeName.length() <= 1 ) //NOTE: this will probably fail for a single-letter class name in the global scope (outside any package.)
			return false;
		if( typeName.startsWith( "L" ) )
			return false;
		if( typeName.startsWith( "[" ) )
			return false;
		if( typeName.endsWith( ";" ) )
			return false;
		if( typeName.contains( "/" ) )
			return false;
		if( typeName.endsWith( "[]" ) )
			return false;
		return true;
	}

	private static String stripArrayNotation( String typeName )
	{
		while( typeName.endsWith( "[]" ) )
			typeName = typeName.substring( 0, typeName.length() - 2 );
		return typeName;
	}

	private static void addDependencyTypeNamesFromConstant( Constant constant, Collection<String> mutableDependencyNames )
	{
		switch( constant.tag )
		{
			case Mutf8:
			case Integer:
			case Float:
			case Long:
			case Double:
			case String:
				break;
			case Class:
				addDependencyTypeNameFromClassConstant( constant.asClassConstant(), mutableDependencyNames );
				break;
			default:
				assert false;
		}
	}

	private static void addDependencyTypeNamesFromTypeNameConstant( Mutf8Constant typeNameConstant, Collection<String> mutableDependencyNames )
	{
		ClassDesc classDescriptor = ClassDesc.ofDescriptor( typeNameConstant.stringValue() );
		addDependencyTypeNameFromClassDescriptor( classDescriptor, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromFieldReferenceConstant( FieldReferenceConstant fieldReferenceConstant, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassConstant( fieldReferenceConstant.typeConstant(), mutableDependencyNames );
		addDependencyTypeNameFromClassDescriptor( fieldReferenceConstant.fieldTypeDescriptor(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromPlainMethodReferenceConstant( PlainMethodReferenceConstant plainMethodReferenceConstant, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassConstant( plainMethodReferenceConstant.typeConstant(), mutableDependencyNames );
		addDependencyTypeNamesFromMethodDescriptor( plainMethodReferenceConstant.methodDescriptor(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInterfaceMethodReferenceConstant( InterfaceMethodReferenceConstant interfaceMethodReferenceConstant, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassConstant( interfaceMethodReferenceConstant.typeConstant(), mutableDependencyNames );
		addDependencyTypeNamesFromMethodDescriptor( interfaceMethodReferenceConstant.methodDescriptor(), mutableDependencyNames );
	}

	private static void addDependencyTypeNameFromClassConstant( ClassConstant classConstant, Collection<String> mutableDependencyNames )
	{
		String className = ByteCodeHelpers.typeNameFromClassDesc( classConstant.classDescriptor() );
		addDependencyTypeName( className, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromBootstrapMethod( BootstrapMethod bootstrapMethod, Collection<String> mutableDependencyNames )
	{
		DynamicConstantDesc<?> dynamicConstantDesc = bootstrapMethod.constantDescriptor();
		addDependencyTypeNamesFromMethodDescriptor( dynamicConstantDesc.bootstrapMethod().invocationType(), mutableDependencyNames );
		addDependencyTypeNameFromClassDescriptor( dynamicConstantDesc.bootstrapMethod().owner(), mutableDependencyNames );
		for( ConstantDesc bootstrapArgumentDescriptor : dynamicConstantDesc.bootstrapArgs() )
			addDependencyTypeNamesFromConstantDescriptor( bootstrapArgumentDescriptor, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInvokeDynamicConstant( ByteCodeType byteCodeType, InvokeDynamicConstant invokeDynamicConstant, Collection<String> mutableDependencyNames )
	{
		DynamicCallSiteDesc invokeDynamicDescriptor = invokeDynamicConstant.descriptor( byteCodeType );
		addDependencyTypeNamesFromDynamicCallSiteDescriptor( invokeDynamicDescriptor, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromDynamicCallSiteDescriptor( DynamicCallSiteDesc dynamicCallSiteDescriptor, Collection<String> mutableDependencyNames )
	{
		MethodHandleDesc bootstrapMethod = dynamicCallSiteDescriptor.bootstrapMethod();
		addDependencyTypeNamesFromMethodDescriptor( bootstrapMethod.invocationType(), mutableDependencyNames );
		addDependencyTypeNamesFromMethodDescriptor( dynamicCallSiteDescriptor.invocationType(), mutableDependencyNames );
		for( ConstantDesc bootstrapArgument : dynamicCallSiteDescriptor.bootstrapArgs() )
			addDependencyTypeNamesFromConstantDescriptor( bootstrapArgument, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromConstantDescriptor( ConstantDesc constantDescriptor, Collection<String> mutableDependencyNames )
	{
		if( constantDescriptor instanceof MethodTypeDesc methodDescriptor )
		{
			addDependencyTypeNameFromClassDescriptor( methodDescriptor.returnType(), mutableDependencyNames );
			for( ClassDesc parameterDescriptor : methodDescriptor.parameterList() )
				addDependencyTypeNameFromClassDescriptor( parameterDescriptor, mutableDependencyNames );
		}
		else if( constantDescriptor instanceof MethodHandleDesc methodHandleDescriptor )
		{
			MethodTypeDesc invocationType = methodHandleDescriptor.invocationType();
			addDependencyTypeNamesFromMethodDescriptor( invocationType, mutableDependencyNames );
		}
		else if( constantDescriptor instanceof String )
		{
			Kit.get( constantDescriptor );
		}
		else if( constantDescriptor instanceof ClassDesc classDescriptor )
		{
			addDependencyTypeNameFromClassDescriptor( classDescriptor, mutableDependencyNames );
		}
		else
			assert false;
	}

	//DirectMethodHandleDesc java.lang.constant.ConstantDescs.ofCallsiteBootstrap( ClassDesc owner, String name, ClassDesc returnType, ClassDesc... paramTypes );
	//DirectMethodHandleDesc java.lang.constant.ConstantDescs.ofConstantBootstrap( ClassDesc owner, String name, ClassDesc returnType, ClassDesc... paramTypes );
	//DirectMethodHandleDesc java.lang.constant.MethodHandleDesc.of( DirectMethodHandleDesc.Kind kind, ClassDesc owner, String name, String lookupDescriptor );
	//DirectMethodHandleDesc java.lang.constant.MethodHandleDesc.ofMethod( DirectMethodHandleDesc.Kind kind, ClassDesc owner, String name, MethodTypeDesc lookupMethodType );
	//DirectMethodHandleDesc java.lang.constant.MethodHandleDesc.ofConstructor( ClassDesc owner, ClassDesc... paramTypes );
	//DirectMethodHandleDesc java.lang.constant.MethodHandleDesc.ofField( DirectMethodHandleDesc.Kind kind, ClassDesc owner, String fieldName, ClassDesc fieldType );
	//ClassDesc java.lang.constant.ClassDesc.ofDescriptor( String descriptor );
	//ClassDesc java.lang.constant.ClassDesc.of( String name );
	//ClassDesc java.lang.constant.ClassDesc.of( String packageName, String className );
	//MethodTypeDesc java.lang.constant.MethodTypeDesc.of( ClassDesc returnDesc, ClassDesc... paramDescs );
	//MethodTypeDesc java.lang.constant.MethodTypeDesc.ofDescriptor( String descriptor );
	//DynamicCallSiteDesc java.lang.constant.DynamicCallSiteDesc.of( DirectMethodHandleDesc bootstrapMethod, MethodTypeDesc invocationType );
	//DynamicCallSiteDesc java.lang.constant.DynamicCallSiteDesc.of( DirectMethodHandleDesc bootstrapMethod, String invocationName, MethodTypeDesc invocationType, ConstantDesc... bootstrapArgs );
	//DynamicCallSiteDesc java.lang.constant.DynamicCallSiteDesc.of( DirectMethodHandleDesc bootstrapMethod,String invocationName,MethodTypeDesc invocationType );
	//DynamicConstantDesc<T> java.lang.constant.DynamicConstantDesc.of( DirectMethodHandleDesc bootstrapMethod );
	//DynamicConstantDesc<T> java.lang.constant.DynamicConstantDesc.of( DirectMethodHandleDesc bootstrapMethod, ConstantDesc... bootstrapArgs );
	//DynamicConstantDesc<T> java.lang.constant.DynamicConstantDesc.ofNamed( DirectMethodHandleDesc bootstrapMethod, String constantName, ClassDesc constantType, ConstantDesc... bootstrapArgs );
	//DynamicConstantDesc<T> java.lang.constant.DynamicConstantDesc.ofCanonical( DirectMethodHandleDesc bootstrapMethod, String constantName, ClassDesc constantType, ConstantDesc[] bootstrapArgs );
	//java.lang.constant.Constable.
	//java.lang.constant.DirectMethodHandleDesc.
	//java.lang.constant.ConstantDesc.
	//java.lang.constant.ConstantDescs.ofConstantBootstrap( ClassDesc owner, String name, ClassDesc returnType, ClassDesc... paramTypes );
	//java.lang.constant.ConstantDescs.ofCallsiteBootstrap( ClassDesc owner, String name, ClassDesc returnType, ClassDesc... paramTypes );

	private static void addDependencyTypeNamesFromMethodDescriptor( MethodTypeDesc methodTypeDesc, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassDescriptor( methodTypeDesc.returnType(), mutableDependencyNames );
		for( ClassDesc argumentDescriptor : methodTypeDesc.parameterArray() )
			addDependencyTypeNameFromClassDescriptor( argumentDescriptor, mutableDependencyNames );
	}

	// TODO: check whether it is necessary to include types from descriptor strings.
	private static void addDependencyTypeNameFromClassDescriptor( ClassDesc classDesc, Collection<String> mutableDependencyNames )
	{
		String typeName = ByteCodeHelpers.typeNameFromClassDesc( classDesc );
		addDependencyTypeName( typeName, mutableDependencyNames );
	}
}
