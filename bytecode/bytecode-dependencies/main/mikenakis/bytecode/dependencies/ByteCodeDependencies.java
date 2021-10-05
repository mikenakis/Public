package mikenakis.bytecode.dependencies;

import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.ByteCodeField;
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
import mikenakis.bytecode.model.attributes.code.instructions.ClassReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.FieldReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MethodReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.descriptors.MethodReference;
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
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.Kit;

import java.lang.constant.DirectMethodHandleDesc;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class ByteCodeDependencies
{
	public static Collection<TerminalTypeDescriptor> getDependencies( ByteCodeType byteCodeType, boolean includeDescriptorOnlyDependencies, boolean includeSignatureDependencies )
	{
		ByteCodeDependencies byteCodeDependencies = new ByteCodeDependencies( includeDescriptorOnlyDependencies, includeSignatureDependencies );
		byteCodeDependencies.visitByteCodeType( byteCodeType );
		byteCodeDependencies.mutableDependencies.remove( byteCodeType.typeDescriptor() );
		return byteCodeDependencies.mutableDependencies;
	}

	private final boolean includeDescriptorOnlyDependencies;
	private final boolean includeSignatureDependencies;
	private final Collection<TerminalTypeDescriptor> mutableDependencies = new HashSet<>();

	private ByteCodeDependencies( boolean includeDescriptorOnlyDependencies, boolean includeSignatureDependencies )
	{
		this.includeDescriptorOnlyDependencies = includeDescriptorOnlyDependencies;
		this.includeSignatureDependencies = includeSignatureDependencies;
	}

	private void visitByteCodeType( ByteCodeType byteCodeType )
	{
		visitTerminalTypeDescriptor( byteCodeType.typeDescriptor() );
		byteCodeType.superTypeDescriptor().ifPresent( t -> visitTerminalTypeDescriptor( t ) );
		for( TerminalTypeDescriptor interfaceDescriptor : byteCodeType.interfaces() )
			visitTerminalTypeDescriptor( interfaceDescriptor );
		for( ByteCodeField byteCodeField : byteCodeType.fields )
		{
			visitDescriptorTypeDescriptor( byteCodeField.descriptor().typeDescriptor );
			for( KnownAttribute knownAttribute : byteCodeField.attributeSet.knownAttributes() )
			{
				switch( knownAttribute.tag )
				{
					case KnownAttribute.tag_ConstantValue -> visitConstantValueAttribute( knownAttribute.asConstantValueAttribute() );
					case KnownAttribute.tag_RuntimeVisibleAnnotations -> visitAnnotationsAttribute( knownAttribute.asRuntimeVisibleAnnotationsAttribute() );
					case KnownAttribute.tag_RuntimeInvisibleAnnotations -> visitAnnotationsAttribute( knownAttribute.asRuntimeInvisibleAnnotationsAttribute() );
					case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> visitTypeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute() );
					case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> visitTypeAnnotationsAttribute( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute() );
					case KnownAttribute.tag_Signature -> visitFieldTypeSignatureString( knownAttribute.asSignatureAttribute().signatureString() );
					case KnownAttribute.tag_Deprecated, KnownAttribute.tag_Synthetic -> { /* nothing to do */ }
					default -> throw new AssertionError( knownAttribute );
				}
			}
		}
		for( ByteCodeMethod byteCodeMethod : byteCodeType.methods )
		{
			visitMethodDescriptor( byteCodeMethod.descriptor() );
			for( KnownAttribute knownAttribute : byteCodeMethod.attributeSet.knownAttributes() )
			{
				switch( knownAttribute.tag )
				{
					case KnownAttribute.tag_AnnotationDefault -> visitAnnotationDefaultAttribute( knownAttribute.asAnnotationDefaultAttribute() );
					case KnownAttribute.tag_Code -> visitCodeAttribute( knownAttribute.asCodeAttribute() );
					case KnownAttribute.tag_Exceptions -> visitExceptionsAttribute( knownAttribute.asExceptionsAttribute() );
					case KnownAttribute.tag_RuntimeVisibleAnnotations -> visitAnnotationsAttribute( knownAttribute.asRuntimeVisibleAnnotationsAttribute() );
					case KnownAttribute.tag_RuntimeInvisibleAnnotations -> visitAnnotationsAttribute( knownAttribute.asRuntimeInvisibleAnnotationsAttribute() );
					case KnownAttribute.tag_RuntimeInvisibleParameterAnnotations -> visitParameterAnnotationsAttribute( knownAttribute.asRuntimeInvisibleParameterAnnotationsAttribute() );
					case KnownAttribute.tag_RuntimeVisibleParameterAnnotations -> visitParameterAnnotationsAttribute( knownAttribute.asRuntimeVisibleParameterAnnotationsAttribute() );
					case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> visitTypeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute() );
					case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> visitTypeAnnotationsAttribute( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute() );
					case KnownAttribute.tag_Signature -> visitMethodTypeSignatureString( knownAttribute.asSignatureAttribute().signatureString() );
					case KnownAttribute.tag_Deprecated, KnownAttribute.tag_Synthetic, KnownAttribute.tag_MethodParameters -> { /* nothing to do */ }
					default -> throw new AssertionError( knownAttribute );
				}
			}
		}
		for( KnownAttribute knownAttribute : byteCodeType.attributeSet.knownAttributes() )
		{
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tag_BootstrapMethods -> visitBootstrapMethodsAttribute( knownAttribute.asBootstrapMethodsAttribute() );
				case KnownAttribute.tag_EnclosingMethod -> visitEnclosingMethodAttribute( knownAttribute.asEnclosingMethodAttribute() );
				case KnownAttribute.tag_InnerClasses -> visitInnerClassesAttribute( knownAttribute.asInnerClassesAttribute() );
				//case ModuleAttribute.name:
				//case ModulePackageAttribute.name:
				//case ModuleMainClassAttribute.name:
				case KnownAttribute.tag_NestHost -> visitNestHostAttribute( knownAttribute.asNestHostAttribute() );
				case KnownAttribute.tag_NestMembers -> visitNestMembersAttribute( knownAttribute.asNestMembersAttribute() );
				//case RecordAttribute.name:
				case KnownAttribute.tag_RuntimeInvisibleAnnotations -> visitAnnotationsAttribute( knownAttribute.asRuntimeInvisibleAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> visitTypeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleAnnotations -> visitAnnotationsAttribute( knownAttribute.asRuntimeVisibleAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> visitTypeAnnotationsAttribute( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute() );
				case KnownAttribute.tag_Signature -> visitClassTypeSignatureString( knownAttribute.asSignatureAttribute().signatureString() );
				//SourceDebugExtension
				case KnownAttribute.tag_Deprecated, KnownAttribute.tag_SourceFile, KnownAttribute.tag_Synthetic -> { /* nothing to do */ }
				default -> throw new AssertionError( knownAttribute );
			}
		}
		for( TerminalTypeDescriptor extraType : byteCodeType.extraTypes() )
			visitTerminalTypeDescriptor( extraType );
	}

	private void visitNestMembersAttribute( NestMembersAttribute nestMembersAttribute )
	{
		for( TypeDescriptor memberType : nestMembersAttribute.members() )
			visitTypeDescriptor( memberType );
	}

	private void visitNestHostAttribute( NestHostAttribute nestHostAttribute )
	{
		visitTerminalTypeDescriptor( nestHostAttribute.hostClass() );
	}

	private void visitAnnotationDefaultAttribute( AnnotationDefaultAttribute attribute )
	{
		visitElementValue( attribute.annotationValue );
	}

	private void visitBootstrapMethodsAttribute( BootstrapMethodsAttribute attribute )
	{
		for( BootstrapMethod bootstrapMethod : attribute.bootstrapMethods )
			visitBootstrapMethod( bootstrapMethod );
	}

	private void visitCodeAttribute( CodeAttribute codeAttribute )
	{
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos )
			visitExceptionInfo( exceptionInfo );
		for( Instruction instruction : codeAttribute.instructions.all() )
		{
			switch( instruction.groupTag )
			{
				case Instruction.groupTag_Branch, Instruction.groupTag_ConditionalBranch, Instruction.groupTag_IInc, Instruction.groupTag_LocalVariable, //
					Instruction.groupTag_LookupSwitch, Instruction.groupTag_NewPrimitiveArray, Instruction.groupTag_Operandless,
					Instruction.groupTag_TableSwitch -> { /* nothing to do */ }
				case Instruction.groupTag_ClassConstantReferencing -> visitClassReferencingInstruction( instruction.asClassReferencingInstruction() );
				case Instruction.groupTag_FieldConstantReferencing -> visitFieldReferencingInstruction( instruction.asFieldReferencingInstruction() );
				case Instruction.groupTag_MethodConstantReferencing -> visitMethodReferencingInstruction( instruction.asMethodReferencingInstruction() );
				case Instruction.groupTag_LoadConstant -> visitLoadConstantInstruction( instruction.asLoadConstantInstruction() );
				case Instruction.groupTag_InvokeDynamic -> visitInvokeDynamicInstruction( instruction.asInvokeDynamicInstruction() );
				case Instruction.groupTag_InvokeInterface -> visitInvokeInterfaceInstruction( instruction.asInvokeInterfaceInstruction() );
				case Instruction.groupTag_MultiANewArray -> visitMultiANewArrayInstruction( instruction.asMultiANewArrayInstruction() );
				default -> throw new AssertionError( instruction );
			}
		}
		for( KnownAttribute knownAttribute : codeAttribute.attributeSet.knownAttributes() )
		{
			switch( knownAttribute.tag )
			{
				case KnownAttribute.tag_LocalVariableTable -> visitLocalVariableTableAttribute( knownAttribute.asLocalVariableTableAttribute() );
				case KnownAttribute.tag_LocalVariableTypeTable -> visitLocalVariableTypeTableAttribute( knownAttribute.asLocalVariableTypeTableAttribute() );
				case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> visitTypeAnnotationsAttribute( knownAttribute.asRuntimeInvisibleTypeAnnotationsAttribute() );
				case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> visitTypeAnnotationsAttribute( knownAttribute.asRuntimeVisibleTypeAnnotationsAttribute() );
				case KnownAttribute.tag_LineNumberTable, KnownAttribute.tag_StackMapTable -> { /* nothing to do */ }
				default -> throw new AssertionError( knownAttribute );
			}
		}
	}

	private void visitExceptionInfo( ExceptionInfo exceptionInfo )
	{
		exceptionInfo.catchType().ifPresent( t -> visitTerminalTypeDescriptor( t ) );
	}

	private void visitMultiANewArrayInstruction( MultiANewArrayInstruction multiANewArrayInstruction )
	{
		visitTypeDescriptor( multiANewArrayInstruction.targetType() );
	}

	private void visitInvokeInterfaceInstruction( InvokeInterfaceInstruction invokeInterfaceInstruction )
	{
		visitMethodReference( invokeInterfaceInstruction.methodReference() );
	}

	private void visitInvokeDynamicInstruction( InvokeDynamicInstruction invokeDynamicInstruction )
	{
		visitBootstrapMethod( invokeDynamicInstruction.bootstrapMethod() );
		visitMethodDescriptor( invokeDynamicInstruction.methodPrototype().descriptor );
	}

	private void visitLoadConstantInstruction( LoadConstantInstruction loadConstantInstruction )
	{
		if( loadConstantInstruction.isType() )
			visitTypeDescriptor( loadConstantInstruction.getTypeDescriptor() );
	}

	private void visitClassReferencingInstruction( ClassReferencingInstruction classReferencingInstruction )
	{
		visitTypeDescriptor( classReferencingInstruction.target() );
	}

	private void visitFieldReferencingInstruction( FieldReferencingInstruction fieldReferencingInstruction )
	{
		visitTypeDescriptor( fieldReferencingInstruction.fieldDeclaringType() );
		visitDescriptorTypeDescriptor( fieldReferencingInstruction.fieldDescriptor().typeDescriptor );
	}

	private void visitMethodReferencingInstruction( MethodReferencingInstruction methodReferencingInstruction )
	{
		visitMethodReference( methodReferencingInstruction.methodReference() );
	}

	private static void visitConstantValueAttribute( ConstantValueAttribute constantValueAttribute )
	{
		switch( constantValueAttribute.valueConstant.tag )
		{
			case Constant.tag_Mutf8, Constant.tag_Integer, Constant.tag_Float, Constant.tag_Long, Constant.tag_Double, Constant.tag_String -> { /* nothing to do */ }
			default -> throw new AssertionError( constantValueAttribute.valueConstant );
		}
	}

	private void visitEnclosingMethodAttribute( EnclosingMethodAttribute attribute )
	{
		visitTerminalTypeDescriptor( attribute.enclosingClassTypeDescriptor() );
		attribute.enclosingMethodPrototype().ifPresent( p -> visitMethodDescriptor( p.descriptor ) );
	}

	private void visitExceptionsAttribute( ExceptionsAttribute attribute )
	{
		for( TerminalTypeDescriptor exception : attribute.exceptions() )
			visitTerminalTypeDescriptor( exception );
	}

	private void visitInnerClassesAttribute( InnerClassesAttribute attribute )
	{
		for( InnerClass innerClass : attribute.innerClasses )
		{
			visitTerminalTypeDescriptor( innerClass.innerType() );
			innerClass.outerType().ifPresent( t -> visitTerminalTypeDescriptor( t ) );
		}
	}

	private void visitLocalVariableTableAttribute( LocalVariableTableAttribute attribute )
	{
		for( LocalVariableTableEntry localVariable : attribute.localVariableTableEntries )
			visitDescriptorTypeDescriptor( localVariable.prototype().descriptor.typeDescriptor );
	}

	private void visitLocalVariableTypeTableAttribute( LocalVariableTypeTableAttribute attribute )
	{
		for( LocalVariableTypeTableEntry localVariableType : attribute.localVariableTypeTableEntries )
			visitFieldTypeSignatureString( localVariableType.signatureString() );
	}

	private void visitAnnotationsAttribute( AnnotationsAttribute attribute )
	{
		for( Annotation byteCodeAnnotation : attribute.annotations )
			visitAnnotation( byteCodeAnnotation );
	}

	private void visitParameterAnnotationsAttribute( ParameterAnnotationsAttribute attribute )
	{
		for( ParameterAnnotationSet parameterAnnotationSet : attribute.parameterAnnotationSets )
			for( Annotation byteCodeAnnotation : parameterAnnotationSet.annotations )
				visitAnnotation( byteCodeAnnotation );
	}

	private void visitTypeAnnotationsAttribute( TypeAnnotationsAttribute attribute )
	{
		for( TypeAnnotation typeAnnotation : attribute.typeAnnotations )
			for( AnnotationParameter annotationParameter : typeAnnotation.parameters )
				visitElementValue( annotationParameter.annotationValue );
	}

	private void visitElementValue( AnnotationValue annotationValue )
	{
		switch( annotationValue.tag )
		{
			case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, AnnotationValue.tagFloat, AnnotationValue.tagInteger,  //
				AnnotationValue.tagLong, AnnotationValue.tagShort, AnnotationValue.tagString -> { /* nothing to do */ }
			case AnnotationValue.tagClass -> visitClassAnnotationValue( annotationValue.asClassAnnotationValue() );
			case AnnotationValue.tagEnum -> visitEnumAnnotationValue( annotationValue.asEnumAnnotationValue() );
			case AnnotationValue.tagAnnotation -> visitAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue() );
			case AnnotationValue.tagArray -> visitArrayAnnotationValue( annotationValue.asArrayAnnotationValue() );
			default -> throw new AssertionError( annotationValue );
		}
	}

	private void visitArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue )
	{
		for( AnnotationValue value : arrayAnnotationValue.annotationValues )
			visitElementValue( value );
	}

	private void visitAnnotationAnnotationValue( AnnotationAnnotationValue annotationAnnotationValue )
	{
		visitAnnotation( annotationAnnotationValue.annotation );
	}

	private void visitEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue )
	{
		visitDescriptorTypeDescriptor( enumAnnotationValue.typeDescriptor() );
	}

	private void visitClassAnnotationValue( ClassAnnotationValue classAnnotationValue )
	{
		visitDescriptorTypeDescriptor( classAnnotationValue.typeDescriptor() );
	}

	private void visitAnnotation( Annotation byteCodeAnnotation )
	{
		visitDescriptorTypeDescriptor( byteCodeAnnotation.typeDescriptor() );
		for( AnnotationParameter annotationParameter : byteCodeAnnotation.parameters )
			visitElementValue( annotationParameter.annotationValue );
	}

	private void visitFieldTypeSignatureString( String signature )
	{
		if( !includeSignatureDependencies )
			return;
		ObjectSignature parsedSignature = SignatureParser.make().parseFieldSig( signature ); // parseFieldTypeSignature() {
		if( parsedSignature instanceof ClassSignature classSignature )
			visitClassSignature( classSignature );
		else if( parsedSignature instanceof TypeVariableSignature )
			Kit.nop();
		else if( parsedSignature instanceof ArrayTypeSignature arrayTypeSignature )
			visitArrayTypeSignature( arrayTypeSignature );
		else if( parsedSignature instanceof ClassTypeSignature classTypeSignature )
			visitClassTypeSignature( classTypeSignature );
		else
			assert false;
	}

	private void visitClassTypeSignatureString( String signature )
	{
		if( !includeSignatureDependencies )
			return;
		ClassSignature parsedSignature = SignatureParser.make().parseClassSig( signature );
		visitClassSignature( parsedSignature );
	}

	private void visitClassSignature( ClassSignature parsedSignature )
	{
		assert includeSignatureDependencies;
		visitSignaturePath( parsedSignature.getSuperclass().getPath() );
		for( var superInterface : parsedSignature.getSuperInterfaces() )
			visitSignaturePath( superInterface.getPath() );
	}

	private void visitArrayTypeSignature( ArrayTypeSignature arrayTypeSignature )
	{
		assert includeSignatureDependencies;
		visitTypeSignature( arrayTypeSignature.getComponentType() );
	}

	private void visitTypeSignature( TypeSignature typeSignature )
	{
		assert includeSignatureDependencies;
		if( typeSignature instanceof ArrayTypeSignature arrayTypeSignature )
			visitArrayTypeSignature( arrayTypeSignature );
		else if( typeSignature instanceof ClassTypeSignature classTypeSignature )
			visitClassTypeSignature( classTypeSignature );
		else if( typeSignature instanceof TypeVariableSignature )
			Kit.nop();
		else
			assert false;
	}

	private void visitSignaturePath( Collection<SimpleClassTypeSignature> signaturePath )
	{
		assert includeSignatureDependencies;
		String fullTypeName = signaturePath.stream().map( s -> s.getName() ).collect( Collectors.joining( "." ) );
		visitSignatureTypeName( fullTypeName );
	}

	private void visitMethodTypeSignatureString( String signature )
	{
		if( !includeSignatureDependencies )
			return;
		MethodTypeSignature parsedSignature = SignatureParser.make().parseMethodSig( signature );
		ReturnType returnType = parsedSignature.getReturnType();
		visitSignatureTypeTree( returnType );
		for( TypeSignature parameterType : parsedSignature.getParameterTypes() )
			visitSignatureTypeTree( parameterType );
		for( var exceptionType : parsedSignature.getExceptionTypes() )
			visitSignatureTypeTree( exceptionType );
	}

	private void visitSignatureTypeTree( TypeTree typeTree )
	{
		assert includeSignatureDependencies;
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
			visitClassTypeSignature( classTypeSignature );
		else if( typeTree instanceof ArrayTypeSignature arrayTypeSignature )
			visitSignatureTypeTree( arrayTypeSignature.getComponentType() );
		else
			assert false;
	}

	private void visitClassTypeSignature( ClassTypeSignature classTypeSignature )
	{
		assert includeSignatureDependencies;
		List<SimpleClassTypeSignature> path = classTypeSignature.getPath();
		for( SimpleClassTypeSignature element : path )
			visitSignatureTypeName( element.getName() );
	}

	private void visitSignatureTypeName( String dependencyName )
	{
		assert dependencyName != null;
		dependencyName = stripArrayNotation( dependencyName );
		TypeDescriptor typeDescriptor = TerminalTypeDescriptor.of( dependencyName );
		visitTypeDescriptor( typeDescriptor );
	}

	private void visitTypeDescriptor( TypeDescriptor typeDescriptor )
	{
		if( typeDescriptor.isArray() )
		{
			visitTypeDescriptor( typeDescriptor.asArrayTypeDescriptor().componentTypeDescriptor );
			return;
		}
		if( typeDescriptor.isPrimitive() )
			return;
		assert typeDescriptor.isTerminal();
		visitTerminalTypeDescriptor( typeDescriptor.asTerminalTypeDescriptor() );
	}

	private void visitTerminalTypeDescriptor( TerminalTypeDescriptor terminalTypeDescriptor )
	{
		Kit.collection.addOrReplace( mutableDependencies, terminalTypeDescriptor );
	}

	private static String stripArrayNotation( String typeName )
	{
		while( typeName.endsWith( "[]" ) )
			typeName = typeName.substring( 0, typeName.length() - 2 );
		return typeName;
	}

	private void visitMethodReference( MethodReference methodReference )
	{
		visitTypeDescriptor( methodReference.declaringTypeDescriptor );
		visitMethodDescriptor( methodReference.methodPrototype.descriptor );
	}

	private void visitBootstrapMethod( BootstrapMethod bootstrapMethod )
	{
		visitDirectMethodHandleDesc( bootstrapMethod.directMethodHandleDesc() );
		for( Constant bootstrapArgumentDescriptor : bootstrapMethod.argumentConstants )
			visitBootstrapArgument( bootstrapArgumentDescriptor );
	}

	private void visitDirectMethodHandleDesc( DirectMethodHandleDesc directMethodHandleDesc )
	{
		// TODO
	}

	private void visitMethodDescriptor( MethodDescriptor methodDescriptor )
	{
		visitDescriptorTypeDescriptor( methodDescriptor.returnTypeDescriptor );
		for( TypeDescriptor parameterTypeDescriptor : methodDescriptor.parameterTypeDescriptors )
			visitDescriptorTypeDescriptor( parameterTypeDescriptor );
	}

	private void visitBootstrapArgument( Constant constant )
	{
		switch( constant.tag )
			{
				case Constant.tag_Class -> visitTypeDescriptor( constant.asClassConstant().typeDescriptor() );
				case Constant.tag_MethodType -> visitMethodDescriptor( constant.asMethodTypeConstant().methodDescriptor() );
				case Constant.tag_String -> { /* nothing to do */ }
				case Constant.tag_MethodHandle -> //
					{
						DirectMethodHandleDesc directMethodHandleDesc = constant.asMethodHandleConstant().directMethodHandleDesc();
						visitDirectMethodHandleDesc( directMethodHandleDesc );
					}
				default -> throw new AssertionError( constant );
			}
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

	private void visitDescriptorTypeDescriptor( TypeDescriptor typeDescriptor )
	{
		if( !includeDescriptorOnlyDependencies )
			return;
		visitTypeDescriptor( typeDescriptor );
	}
}
