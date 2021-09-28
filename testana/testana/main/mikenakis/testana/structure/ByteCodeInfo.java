package mikenakis.testana.structure;

import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.TypeAnnotation;
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
import mikenakis.bytecode.model.attributes.EnclosingMethodAttribute;
import mikenakis.bytecode.model.attributes.ExceptionInfo;
import mikenakis.bytecode.model.attributes.ExceptionsAttribute;
import mikenakis.bytecode.model.attributes.InnerClass;
import mikenakis.bytecode.model.attributes.InnerClassesAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTableEntry;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableEntry;
import mikenakis.bytecode.model.attributes.NestHostAttribute;
import mikenakis.bytecode.model.attributes.NestMembersAttribute;
import mikenakis.bytecode.model.attributes.ParameterAnnotationSet;
import mikenakis.bytecode.model.attributes.ParameterAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.instructions.ClassConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.FieldConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MethodConstantReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

final class ByteCodeInfo
{
	private static final boolean includeDescriptors = Kit.get( false ); //I believe that descriptor-only dependencies are unnecessary.

	private final ByteCodeType byteCodeType;

	ByteCodeInfo( ByteCodeType byteCodeType )
	{
		this.byteCodeType = byteCodeType;
	}

	Collection<String> getDependencyNames()
	{
		ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() ); //TODO this is only here for testing, must be removed!
		Collection<String> mutableDependencyNames = new HashSet<>();
		addDependencyTypeNames( byteCodeType, mutableDependencyNames );
		mutableDependencyNames.remove( byteCodeType.typeName() );
		return mutableDependencyNames;
	}

	String getClassSourceLocation()
	{
		return ByteCodeHelpers.getClassSourceLocation( byteCodeType );
	}

	String getMethodSourceLocation( String methodName, MethodTypeDesc methodDescriptor, Function<String,ByteCodeType> byteCodeTypeByName )
	{
		ByteCodeMethod byteCodeMethod = byteCodeType.getMethodByNameAndDescriptor( methodName, methodDescriptor, byteCodeTypeByName ).orElseThrow();
		return ByteCodeHelpers.getMethodSourceLocation( byteCodeType, byteCodeMethod );
	}

	public int getDeclaredMethodIndex( String methodName, MethodTypeDesc methodDescriptor )
	{
		return byteCodeType.findDeclaredMethodByNameAndDescriptor( methodName, methodDescriptor );
	}

	private static void addDependencyTypeNames( ByteCodeType byteCodeType, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( byteCodeType.typeName(), mutableDependencyNames );
		byteCodeType.superTypeName().ifPresent( c -> addDependencyTypeName( c, mutableDependencyNames ) );
		for( TerminalTypeDescriptor interfaceDescriptor : byteCodeType.interfaceClassDescriptors() )
			addDependencyTypeName( interfaceDescriptor.name(), mutableDependencyNames );
		for( ByteCodeField byteCodeField : byteCodeType.fields )
		{
			addDependencyTypeNameFromClassDescriptor( byteCodeField.descriptor(), mutableDependencyNames );
			for( KnownAttribute knownAttribute : byteCodeField.attributeSet.knownAttributes() )
			{
				switch( knownAttribute.tag )
				{
					case KnownAttribute.tag_ConstantValue -> addDependencyTypeNamesFromConstantValueAttribute( knownAttribute.asConstantValueAttribute() );
					case KnownAttribute.tag_RuntimeVisibleAnnotations -> addDependencyTypeNamesFromAnnotationsAttribute( knownAttribute.asRuntimeVisibleAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeInvisibleAnnotations -> addDependencyTypeNamesFromAnnotationsAttribute( knownAttribute.asRuntimeInvisibleAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> addDependencyTypeNamesFromTypeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> addDependencyTypeNamesFromTypeAnnotationsAttribute( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_Signature -> addDependencyTypeNamesFromFieldTypeSignatureString( knownAttribute.asSignatureAttribute().signatureString(), mutableDependencyNames );
					case KnownAttribute.tag_Deprecated, KnownAttribute.tag_Synthetic -> { /* nothing to do */ }
					default -> throw new AssertionError( knownAttribute );
				}
			}
		}
		for( ByteCodeMethod byteCodeMethod : byteCodeType.methods )
		{
			addDependencyTypeNamesFromMethodDescriptor( byteCodeMethod.descriptor(), mutableDependencyNames );
			for( KnownAttribute knownAttribute : byteCodeMethod.attributeSet.knownAttributes() )
			{
				switch( knownAttribute.tag )
				{
					case KnownAttribute.tag_AnnotationDefault -> addDependencyTypeNamesFromAnnotationDefaultAttribute( knownAttribute.asAnnotationDefaultAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_Code -> addDependencyTypeNamesFromCodeAttribute( knownAttribute.asCodeAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_Exceptions -> addDependencyTypeNamesFromExceptionsAttribute( knownAttribute.asExceptionsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeVisibleAnnotations -> addDependencyTypeNamesFromAnnotationsAttribute( knownAttribute.asRuntimeVisibleAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeInvisibleAnnotations -> addDependencyTypeNamesFromAnnotationsAttribute( knownAttribute.asRuntimeInvisibleAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeInvisibleParameterAnnotations -> addDependencyTypeNamesFromParameterAnnotationsAttribute( knownAttribute.asRuntimeInvisibleParameterAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeVisibleParameterAnnotations -> addDependencyTypeNamesFromParameterAnnotationsAttribute( knownAttribute.asRuntimeVisibleParameterAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> addDependencyTypeNamesFromTypeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> addDependencyTypeNamesFromTypeAnnotationsAttribute( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute(), mutableDependencyNames );
					case KnownAttribute.tag_Signature -> addDependencyTypeNamesFromMethodTypeSignatureString( knownAttribute.asSignatureAttribute().signatureString(), mutableDependencyNames );
					case KnownAttribute.tag_Deprecated, KnownAttribute.tag_Synthetic, KnownAttribute.tag_MethodParameters -> { /* nothing to do */ }
					default -> throw new AssertionError( knownAttribute );
				}
			}
		}
		for( KnownAttribute knownAttribute : byteCodeType.attributeSet.knownAttributes() )
		{
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tag_BootstrapMethods -> addDependencyTypeNamesFromBootstrapMethodsAttribute( knownAttribute.asBootstrapMethodsAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_EnclosingMethod -> addDependencyTypeNamesFromEnclosingMethodAttribute( knownAttribute.asEnclosingMethodAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_InnerClasses -> addDependencyTypeNamesFromInnerClassesAttribute( knownAttribute.asInnerClassesAttribute(), mutableDependencyNames );
				//case ModuleAttribute.name:
				//case ModulePackageAttribute.name:
				//case ModuleMainClassAttribute.name:
				case KnownAttribute.tag_NestHost -> addDependencyTypeNamesFromNestHostAttribute( knownAttribute.asNestHostAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_NestMembers -> addDependencyTypeNamesFromNestMembersAttribute( knownAttribute.asNestMembersAttribute(), mutableDependencyNames );
				//case RecordAttribute.name:
				case KnownAttribute.tag_RuntimeInvisibleAnnotations -> addDependencyTypeNamesFromAnnotationsAttribute( knownAttribute.asRuntimeInvisibleAnnotationsAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> addDependencyTypeNamesFromTypeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_RuntimeVisibleAnnotations -> addDependencyTypeNamesFromAnnotationsAttribute( knownAttribute.asRuntimeVisibleAnnotationsAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> addDependencyTypeNamesFromTypeAnnotationsAttribute( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_Signature -> addDependencyTypeNamesFromClassTypeSignatureString( knownAttribute.asSignatureAttribute().signatureString(), mutableDependencyNames );
				//SourceDebugExtension
				case KnownAttribute.tag_Deprecated, KnownAttribute.tag_SourceFile, KnownAttribute.tag_Synthetic -> { /* nothing to do */ }
				default -> throw new AssertionError( knownAttribute );
			}
		}
		for( Constant constant : byteCodeType.extraConstants )
			addDependencyTypeNamesFromConstant( constant, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromNestMembersAttribute( NestMembersAttribute nestMembersAttribute, Collection<String> mutableDependencyNames )
	{
		for( String memberTypeName : nestMembersAttribute.memberTypeNames() )
			addDependencyTypeName( memberTypeName, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromNestHostAttribute( NestHostAttribute nestHostAttribute, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( nestHostAttribute.hostClassTypeName(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromAnnotationDefaultAttribute( AnnotationDefaultAttribute attribute, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromElementValue( attribute.annotationValue(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromBootstrapMethodsAttribute( BootstrapMethodsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( BootstrapMethod bootstrapMethod : attribute.bootstrapMethods )
			addDependencyTypeNamesFromBootstrapMethod( bootstrapMethod, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromCodeAttribute( CodeAttribute codeAttribute, Collection<String> mutableDependencyNames )
	{
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos )
			addDependencyTypeNamesFromExceptionInfo( mutableDependencyNames, exceptionInfo );
		for( Instruction instruction : codeAttribute.instructions.all() )
		{
			switch( instruction.groupTag )
			{
				case Instruction.groupTag_Branch, Instruction.groupTag_ConditionalBranch, Instruction.groupTag_IInc, Instruction.groupTag_ImmediateLoadConstant, Instruction.groupTag_LocalVariable, Instruction.groupTag_LookupSwitch, //
					Instruction.groupTag_NewPrimitiveArray, Instruction.groupTag_Operandless, Instruction.groupTag_OperandlessLoadConstant, Instruction.groupTag_TableSwitch -> { /* nothing to do */ }
				case Instruction.groupTag_ClassConstantReferencing -> addDependencyTypeNamesFromClassConstantReferencingInstruction( instruction.asClassConstantReferencingInstruction(), mutableDependencyNames );
				case Instruction.groupTag_FieldConstantReferencing -> addDependencyTypeNamesFromFieldConstantReferencingInstruction( instruction.asFieldConstantReferencingInstruction(), mutableDependencyNames );
				case Instruction.groupTag_MethodConstantReferencing -> addDependencyTypeNamesFromMethodConstantReferencingInstruction( instruction.asMethodConstantReferencingInstruction(), mutableDependencyNames );
				case Instruction.groupTag_IndirectLoadConstant -> addDependencyTypeNamesFromIndirectLoadConstantInstruction( instruction.asIndirectLoadConstantInstruction(), mutableDependencyNames );
				case Instruction.groupTag_InvokeDynamic -> addDependencyTypeNamesFromInvokeDynamicInstruction( instruction.asInvokeDynamicInstruction(), mutableDependencyNames );
				case Instruction.groupTag_InvokeInterface -> addDependencyTypeNamesFromInvokeInterfaceInstruction( instruction.asInvokeInterfaceInstruction(), mutableDependencyNames );
				case Instruction.groupTag_MultiANewArray -> addDependencyTypeNamesFromMultiANewArrayInstruction( instruction.asMultiANewArrayInstruction(), mutableDependencyNames );
				default -> throw new AssertionError( instruction );
			}
		}
		for( KnownAttribute knownAttribute : codeAttribute.attributeSet.knownAttributes() )
		{
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tag_LocalVariableTable -> addDependencyTypeNamesFromLocalVariableTableAttribute( knownAttribute.asLocalVariableTableAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_LocalVariableTypeTable -> addDependencyTypeNamesFromLocalVariableTypeTableAttribute( knownAttribute.asLocalVariableTypeTableAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> addDependencyTypeNamesFromTypeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> addDependencyTypeNamesFromTypeAnnotationsAttribute( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute(), mutableDependencyNames );
				case KnownAttribute.tag_LineNumberTable, KnownAttribute.tag_StackMapTable -> { /* nothing to do */ }
				default -> throw new AssertionError( knownAttribute );
			}
		}
	}

	private static void addDependencyTypeNamesFromExceptionInfo( Collection<String> mutableDependencyNames, ExceptionInfo exceptionInfo )
	{
		exceptionInfo.catchTypeName().ifPresent( s -> addDependencyTypeName( s, mutableDependencyNames ) );
	}

	private static void addDependencyTypeNamesFromMultiANewArrayInstruction( MultiANewArrayInstruction multiANewArrayInstruction, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( multiANewArrayInstruction.targetTypeName(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInvokeInterfaceInstruction( InvokeInterfaceInstruction invokeInterfaceInstruction, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromInterfaceMethodReferenceConstant( invokeInterfaceInstruction.interfaceMethodReferenceConstant, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInvokeDynamicInstruction( InvokeDynamicInstruction invokeDynamicInstruction, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromInvokeDynamicConstant( invokeDynamicInstruction.invokeDynamicConstant, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromIndirectLoadConstantInstruction( IndirectLoadConstantInstruction indirectLoadConstantInstruction, Collection<String> mutableDependencyNames )
	{
		switch( indirectLoadConstantInstruction.constant.tag )
		{
			case Constant.tag_Float, Constant.tag_Long, Constant.tag_Integer, Constant.tag_Double, Constant.tag_String -> { /* nothing to do */ }
			case Constant.tag_Class -> addDependencyTypeName( indirectLoadConstantInstruction.constant.asClassConstant().typeName(), mutableDependencyNames );
			default -> throw new AssertionError( indirectLoadConstantInstruction.constant );
		}
	}

	private static void addDependencyTypeNamesFromClassConstantReferencingInstruction( ClassConstantReferencingInstruction classConstantReferencingInstruction, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( classConstantReferencingInstruction.targetTypeName(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromFieldConstantReferencingInstruction( FieldConstantReferencingInstruction fieldConstantReferencingInstruction, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( fieldConstantReferencingInstruction.fieldDeclaringTypeName(), mutableDependencyNames );
		addDependencyTypeNameFromClassDescriptor( fieldConstantReferencingInstruction.fieldReferenceConstant.fieldTypeDescriptor(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromMethodConstantReferencingInstruction( MethodConstantReferencingInstruction methodConstantReferencingInstruction, Collection<String> mutableDependencyNames )
	{
		MethodReferenceConstant methodReferenceConstant = methodConstantReferencingInstruction.methodReferenceConstant;
		switch( methodReferenceConstant.tag )
		{
			case Constant.tag_InterfaceMethodReference -> addDependencyTypeNamesFromInterfaceMethodReferenceConstant( methodReferenceConstant.asInterfaceMethodReferenceConstant(), mutableDependencyNames );
			case Constant.tag_MethodReference -> addDependencyTypeNamesFromPlainMethodReferenceConstant( methodReferenceConstant.asPlainMethodReferenceConstant(), mutableDependencyNames );
			default -> throw new AssertionError( methodReferenceConstant );
		}
	}

	private static void addDependencyTypeNamesFromConstantValueAttribute( ConstantValueAttribute constantValueAttribute )
	{
		switch( constantValueAttribute.valueConstant.tag )
		{
			case Constant.tag_Mutf8, Constant.tag_Integer, Constant.tag_Float, Constant.tag_Long, Constant.tag_Double, Constant.tag_String -> { /* nothing to do */ }
			default -> throw new AssertionError( constantValueAttribute.valueConstant );
		}
	}

	private static void addDependencyTypeNamesFromEnclosingMethodAttribute( EnclosingMethodAttribute attribute, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( attribute.enclosingClassTypeName(), mutableDependencyNames );
		attribute.enclosingMethodNameAndDescriptorConstant.ifPresent( nameAndDescriptorConstant -> //
		{
			MethodTypeDesc methodDescriptor = MethodTypeDesc.ofDescriptor( nameAndDescriptorConstant.getDescriptorConstant().stringValue() );
			addDependencyTypeNamesFromMethodDescriptor( methodDescriptor, mutableDependencyNames );
		} );
	}

	private static void addDependencyTypeNamesFromExceptionsAttribute( ExceptionsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( String exceptionTypeName : attribute.exceptionTypeNames() )
			addDependencyTypeName( exceptionTypeName, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInnerClassesAttribute( InnerClassesAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( InnerClass innerClass : attribute.innerClasses )
		{
			addDependencyTypeName( innerClass.innerTypeName(), mutableDependencyNames );
			innerClass.outerTypeName().ifPresent( s -> addDependencyTypeName( s, mutableDependencyNames ) );
		}
	}

	private static void addDependencyTypeNamesFromLocalVariableTableAttribute( LocalVariableTableAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( LocalVariableTableEntry localVariable : attribute.entrys )
			addDependencyTypeNamesFromTypeNameConstant( localVariable.descriptorConstant, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromLocalVariableTypeTableAttribute( LocalVariableTypeTableAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( LocalVariableTypeTableEntry localVariableType : attribute.localVariableTypes )
			addDependencyTypeNamesFromFieldTypeSignatureString( localVariableType.signatureConstant.stringValue(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromAnnotationsAttribute( AnnotationsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( Annotation byteCodeAnnotation : attribute.annotations )
			addDependencyTypeNamesFromAnnotation( byteCodeAnnotation, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromParameterAnnotationsAttribute( ParameterAnnotationsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( ParameterAnnotationSet parameterAnnotationSet : attribute.parameterAnnotationSets )
			for( Annotation byteCodeAnnotation : parameterAnnotationSet.annotations )
				addDependencyTypeNamesFromAnnotation( byteCodeAnnotation, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromTypeAnnotationsAttribute( TypeAnnotationsAttribute attribute, Collection<String> mutableDependencyNames )
	{
		for( TypeAnnotation typeAnnotation : attribute.typeAnnotations )
			for( AnnotationParameter annotationParameter : typeAnnotation.parameters )
				addDependencyTypeNamesFromElementValue( annotationParameter.value, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromElementValue( AnnotationValue annotationValue, Collection<String> mutableDependencyNames )
	{
		switch( annotationValue.tag )
		{
			case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger,  //
				AnnotationValue.tagLong, AnnotationValue.tagShort, AnnotationValue.tagString -> { /* nothing to do */ }
			case AnnotationValue.tagClass -> addDependencyTypeNamesFromClassAnnotationValue( annotationValue.asClassAnnotationValue(), mutableDependencyNames );
			case AnnotationValue.tagEnum -> addDependencyTypeNamesFromEnumAnnotationValue( annotationValue.asEnumAnnotationValue(), mutableDependencyNames );
			case AnnotationValue.tagAnnotation -> addDependencyTypeNamesFromAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue(), mutableDependencyNames );
			case AnnotationValue.tagArray -> addDependencyTypeNamesFromArrayAnnotationValue( annotationValue.asArrayAnnotationValue(), mutableDependencyNames );
			default -> throw new AssertionError( annotationValue );
		}
	}

	private static void addDependencyTypeNamesFromArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue, Collection<String> mutableDependencyNames )
	{
		for( AnnotationValue value : arrayAnnotationValue.annotationValues )
			addDependencyTypeNamesFromElementValue( value, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromAnnotationAnnotationValue( AnnotationAnnotationValue annotationAnnotationValue, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNamesFromAnnotation( annotationAnnotationValue.annotation, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassDescriptor( enumAnnotationValue.typeDescriptor(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromClassAnnotationValue( ClassAnnotationValue classAnnotationValue, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassDescriptor( classAnnotationValue.classDescriptor(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromAnnotation( Annotation byteCodeAnnotation, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeNameFromClassDescriptor( byteCodeAnnotation.typeDescriptor(), mutableDependencyNames );
		for( AnnotationParameter annotationParameter : byteCodeAnnotation.parameters )
			addDependencyTypeNamesFromElementValue( annotationParameter.value, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromFieldTypeSignatureString( String signature, Collection<String> mutableDependencyNames )
	{
		ObjectSignature parsedSignature = SignatureParser.make().parseFieldSig( signature ); // parseFieldTypeSignature() {
		if( parsedSignature instanceof ClassSignature classSignature )
			addDependencyTypeNamesFromClassSignature( classSignature, mutableDependencyNames );
		else if( parsedSignature instanceof TypeVariableSignature )
			Kit.nop();
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
			Kit.nop();
		else
			assert false;
	}

	private static void addDependencyTypeNamesFromSignaturePath( Collection<SimpleClassTypeSignature> signaturePath, Collection<String> mutableDependencyNames )
	{
		String fullTypeName = signaturePath.stream().map( s -> s.getName() ).collect( Collectors.joining( "." ) );
		addDependencyTypeName( fullTypeName, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromMethodTypeSignatureString( String signature, Collection<String> mutableDependencyNames )
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
			Kit.nop();
		else if( typeTree instanceof ByteSignature )
			Kit.nop();
		else if( typeTree instanceof BooleanSignature )
			Kit.nop();
		else if( typeTree instanceof CharSignature )
			Kit.nop();
		else if( typeTree instanceof ShortSignature )
			Kit.nop();
		else if( typeTree instanceof IntSignature )
			Kit.nop();
		else if( typeTree instanceof FloatSignature )
			Kit.nop();
		else if( typeTree instanceof LongSignature )
			Kit.nop();
		else if( typeTree instanceof DoubleSignature )
			Kit.nop();
		else if( typeTree instanceof TypeVariableSignature )
			Kit.nop();
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
		assert ByteCodeHelpers.isValidTerminalTypeName( dependencyName );
		Kit.collection.addOrReplace( mutableDependencyNames, dependencyName );
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
			case Constant.tag_Mutf8, Constant.tag_Integer, Constant.tag_Float, Constant.tag_Long, Constant.tag_Double, Constant.tag_String -> { /* nothing to do */ }
			case Constant.tag_Class -> addDependencyTypeName( constant.asClassConstant().typeName(), mutableDependencyNames );
			default -> throw new AssertionError( constant );
		}
	}

	private static void addDependencyTypeNamesFromTypeNameConstant( Mutf8Constant typeNameConstant, Collection<String> mutableDependencyNames )
	{
		ClassDesc classDescriptor = ClassDesc.ofDescriptor( typeNameConstant.stringValue() );
		addDependencyTypeNameFromClassDescriptor( classDescriptor, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromPlainMethodReferenceConstant( PlainMethodReferenceConstant plainMethodReferenceConstant, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( plainMethodReferenceConstant.declaringTypeName(), mutableDependencyNames );
		addDependencyTypeNamesFromMethodDescriptor( plainMethodReferenceConstant.methodDescriptor(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInterfaceMethodReferenceConstant( InterfaceMethodReferenceConstant interfaceMethodReferenceConstant, Collection<String> mutableDependencyNames )
	{
		addDependencyTypeName( interfaceMethodReferenceConstant.declaringTypeName(), mutableDependencyNames );
		addDependencyTypeNamesFromMethodDescriptor( interfaceMethodReferenceConstant.methodDescriptor(), mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromBootstrapMethod( BootstrapMethod bootstrapMethod, Collection<String> mutableDependencyNames )
	{
		DynamicConstantDesc<?> dynamicConstantDesc = bootstrapMethod.constantDescriptor();
		addDependencyTypeNamesFromMethodDescriptor( dynamicConstantDesc.bootstrapMethod().invocationType(), mutableDependencyNames );
		addDependencyTypeNameFromClassDescriptor( dynamicConstantDesc.bootstrapMethod().owner(), mutableDependencyNames );
		for( ConstantDesc bootstrapArgumentDescriptor : dynamicConstantDesc.bootstrapArgs() )
			addDependencyTypeNamesFromConstantDescriptor( bootstrapArgumentDescriptor, mutableDependencyNames );
	}

	private static void addDependencyTypeNamesFromInvokeDynamicConstant( InvokeDynamicConstant invokeDynamicConstant, Collection<String> mutableDependencyNames )
	{
		DynamicCallSiteDesc invokeDynamicDescriptor = invokeDynamicConstant.descriptor();
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
			Kit.nop();
		else if( constantDescriptor instanceof ClassDesc classDescriptor )
			addDependencyTypeNameFromClassDescriptor( classDescriptor, mutableDependencyNames );
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

	private static void addDependencyTypeNameFromClassDescriptor( ClassDesc classDesc, Collection<String> mutableDependencyNames )
	{
		if( !includeDescriptors )
			return;
		String typeName = ByteCodeHelpers.typeNameFromClassDesc( classDesc );
		addDependencyTypeName( typeName, mutableDependencyNames );
	}
}
