package mikenakis.bytecode.writing;

import mikenakis.bytecode.exceptions.UnknownConstantException;
import mikenakis.bytecode.exceptions.UnknownValueException;
import mikenakis.bytecode.model.AnnotationParameter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.bytecode.model.ByteCodeMember;
import mikenakis.bytecode.model.Constant;
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
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.NameAndTypeConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.Kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the constant pool of a java class file.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 */
final class ConstantPool
{
	private final List<Constant> entries;

	ConstantPool()
	{
		entries = new ArrayList<>( 1 );
		entries.add( null ); // first entry is empty. (Ancient legacy bollocks.)
	}

	private int tryGetIndex( Constant constant )
	{
		if( constant == null )
			return 0;
		for( int i = 0; i < entries.size(); i++ )
		{
			Constant existingConstant = entries.get( i );
			if( existingConstant == null )
				continue;
			if( existingConstant.equals( constant ) )
				return i;
		}
		return -1;
	}

	public int getIndex( Constant constant )
	{
		int index = tryGetIndex( constant );
		assert index != -1;
		return index;
	}

	void internConstant( Constant constant )
	{
		switch( constant.tag )
		{
			case Utf8Constant.TAG:
			case IntegerConstant.TAG:
			case FloatConstant.TAG:
			case LongConstant.TAG:
			case DoubleConstant.TAG:
				break;
			case ClassConstant.TAG:
			{
				ClassConstant classConstant = constant.asClassConstant();
				internConstant( classConstant.nameConstant() );
				break;
			}
			case StringConstant.TAG:
			{
				StringConstant stringConstant = constant.asStringConstant();
				internConstant( stringConstant.valueUtf8Constant() );
				break;
			}
			case FieldReferenceConstant.TAG:
			{
				FieldReferenceConstant fieldReferenceConstant = constant.asFieldReferenceConstant();
				internConstant( fieldReferenceConstant.typeConstant() );
				internConstant( fieldReferenceConstant.nameAndTypeConstant() );
				break;
			}
			case PlainMethodReferenceConstant.TAG:
			{
				MethodReferenceConstant methodReferenceConstant = constant.asMethodReferenceConstant();
				internConstant( methodReferenceConstant.typeConstant() );
				internConstant( methodReferenceConstant.nameAndTypeConstant() );
				break;
			}
			case InterfaceMethodReferenceConstant.TAG:
			{
				InterfaceMethodReferenceConstant interfaceMethodReferenceConstant = constant.asInterfaceMethodReferenceConstant();
				internConstant( interfaceMethodReferenceConstant.typeConstant() );
				internConstant( interfaceMethodReferenceConstant.nameAndTypeConstant() );
				break;
			}
			case NameAndTypeConstant.TAG:
			{
				NameAndTypeConstant nameAndTypeConstant = constant.asNameAndTypeConstant();
				internConstant( nameAndTypeConstant.nameConstant() );
				internConstant( nameAndTypeConstant.descriptorConstant() );
				break;
			}
			case MethodHandleConstant.TAG:
			{
				MethodHandleConstant methodHandleConstant = constant.asMethodHandleConstant();
				internConstant( methodHandleConstant.referenceConstant() );
				break;
			}
			case MethodTypeConstant.TAG:
			{
				MethodTypeConstant methodTypeConstant = constant.asMethodTypeConstant();
				internConstant( methodTypeConstant.descriptorConstant );
				break;
			}
			case InvokeDynamicConstant.TAG:
			{
				InvokeDynamicConstant invokeDynamicConstant = constant.asInvokeDynamicConstant();
				internConstant( invokeDynamicConstant.nameAndTypeConstant() );
				break;
			}
			default:
				throw new UnknownConstantException( constant.tag );
		}
		int existingIndex = tryGetIndex( constant );
		assert existingIndex != 0;
		if( existingIndex == -1 )
		{
			assert !entries.isEmpty();
			entries.add( constant );
			if( constant.tag == LongConstant.TAG || constant.tag == DoubleConstant.TAG )
				entries.add( null ); //8-byte constants occupy two constant pool entries. (Ancient legacy bollocks.)
		}
	}

	void internMember( ByteCodeMember byteCodeMember )
	{
		internConstant( byteCodeMember.nameConstant );
		internConstant( byteCodeMember.descriptorConstant );
		internAttributeSet( byteCodeMember.attributeSet );
	}

	void internAttributeSet( AttributeSet attributeSet )
	{
		for( Attribute attribute : attributeSet )
		{
			Utf8Constant nameConstant = attribute.kind.utf8Name;
			internConstant( nameConstant );
			internAttribute( attribute );
		}
	}

	private void internAttribute( Attribute attribute )
	{
		switch( attribute.kind.name )
		{
			case AnnotationDefaultAttribute.name -> internAnnotationDefaultAttribute( attribute.asAnnotationDefaultAttribute() );
			case BootstrapMethodsAttribute.name -> internBootstrapMethodsAttribute( attribute.asBootstrapMethodsAttribute() );
			case CodeAttribute.name -> internCodeAttribute( attribute.asCodeAttribute() );
			case ConstantValueAttribute.name -> internConstantValueAttribute( attribute.asConstantValueAttribute() );
			case DeprecatedAttribute.name -> internDeprecatedAttribute( attribute.asDeprecatedAttribute() );
			case EnclosingMethodAttribute.name -> internEnclosingMethodAttribute( attribute.asEnclosingMethodAttribute() );
			case ExceptionsAttribute.name -> internExceptionsAttribute( attribute.asExceptionsAttribute() );
			case InnerClassesAttribute.name -> internInnerClassesAttribute( attribute.asInnerClassesAttribute() );
			case NestHostAttribute.name -> internNestHostAttribute( attribute.asNestHostAttribute() );
			case NestMembersAttribute.name -> internNestMembersAttribute( attribute.asNestMembersAttribute() );
			case LineNumberTableAttribute.name -> internLineNumberTableAttribute( attribute.asLineNumberTableAttribute() );
			case LocalVariableTableAttribute.name -> internLocalVariableTableAttribute( attribute.asLocalVariableTableAttribute() );
			case LocalVariableTypeTableAttribute.name -> internLocalVariableTypeTableAttribute( attribute.asLocalVariableTypeTableAttribute() );
			case MethodParametersAttribute.name -> internMethodParametersAttribute( attribute.asMethodParametersAttribute() );
			case RuntimeVisibleAnnotationsAttribute.name -> internRuntimeVisibleAnnotationsAttribute( attribute.asRuntimeVisibleAnnotationsAttribute() );
			case RuntimeInvisibleAnnotationsAttribute.name -> internRuntimeInvisibleAnnotationsAttribute( attribute.asRuntimeInvisibleAnnotationsAttribute() );
			case RuntimeInvisibleParameterAnnotationsAttribute.name -> internRuntimeInvisibleParameterAnnotationsAttribute( attribute.asRuntimeInvisibleParameterAnnotationsAttribute() );
			case RuntimeVisibleParameterAnnotationsAttribute.name -> internRuntimeVisibleParameterAnnotationsAttribute( attribute.asRuntimeVisibleParameterAnnotationsAttribute() );
			case RuntimeInvisibleTypeAnnotationsAttribute.name -> internRuntimeInvisibleTypeAnnotationsAttribute( attribute.asRuntimeInvisibleTypeAnnotationsAttribute() );
			case RuntimeVisibleTypeAnnotationsAttribute.name -> internRuntimeVisibleTypeAnnotationsAttribute( attribute.asRuntimeVisibleTypeAnnotationsAttribute() );
			case SignatureAttribute.name -> internSignatureAttribute( attribute.asSignatureAttribute() );
			case SourceFileAttribute.name -> internSourceFileAttribute( attribute.asSourceFileAttribute() );
			case StackMapTableAttribute.name -> internStackMapTableAttribute( attribute.asStackMapTableAttribute() );
			case SyntheticAttribute.name -> internSyntheticAttribute( attribute.asSyntheticAttribute() );
			default -> internUnknownAttribute( attribute.asUnknownAttribute() );
		}
	}

	private void internAnnotationDefaultAttribute( AnnotationDefaultAttribute annotationDefaultAttribute )
	{
		internAnnotationValue( annotationDefaultAttribute.annotationValue() );
	}

	private void internBootstrapMethodsAttribute( BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		for( BootstrapMethod bootstrapMethod : bootstrapMethodsAttribute.bootstrapMethods() )
		{
			internConstant( bootstrapMethod.methodHandleConstant() );
			for( Constant argumentConstant : bootstrapMethod.argumentConstants() )
				internConstant( argumentConstant );
		}
	}

	private void internCodeAttribute( CodeAttribute codeAttribute )
	{
		for( ExceptionInfo exceptionInfo : codeAttribute.exceptionInfos() )
			exceptionInfo.catchTypeConstant.ifPresent( c -> internConstant( c ) );

		internAttributeSet( codeAttribute.attributeSet() );

		for( Instruction instruction : codeAttribute.instructions().all() )
			internInstruction( instruction );

		for( Instruction instruction : codeAttribute.instructions().all() )
			internInstruction( instruction );
	}

	private void internConstantValueAttribute( ConstantValueAttribute constantValueAttribute )
	{
		internConstant( constantValueAttribute.valueConstant() );
	}

	private void internDeprecatedAttribute( DeprecatedAttribute deprecatedAttribute )
	{
		Kit.get( deprecatedAttribute ); /* nothing to do */
	}

	private void internEnclosingMethodAttribute( EnclosingMethodAttribute enclosingMethodAttribute )
	{
		internConstant( enclosingMethodAttribute.classConstant() );
		enclosingMethodAttribute.methodNameAndTypeConstant().ifPresent( c -> internConstant( c ) );
	}

	private void internExceptionsAttribute( ExceptionsAttribute exceptionsAttribute )
	{
		for( ClassConstant exceptionClassConstant : exceptionsAttribute.exceptionClassConstants() )
			internConstant( exceptionClassConstant );
	}

	private void internInnerClassesAttribute( InnerClassesAttribute innerClassesAttribute )
	{
		for( InnerClass innerClass : innerClassesAttribute.innerClasses() )
		{
			internConstant( innerClass.innerClassConstant() );
			innerClass.outerClassConstant().ifPresent( c -> internConstant( c ) );
			innerClass.innerNameConstant().ifPresent( c -> internConstant( c ) );
		}
	}

	private void internNestHostAttribute( NestHostAttribute nestHostAttribute )
	{
		internConstant( nestHostAttribute.hostClassConstant );
	}

	private void internNestMembersAttribute( NestMembersAttribute nestMembersAttribute )
	{
		for( ClassConstant memberClassConstant : nestMembersAttribute.memberClassConstants() )
			internConstant( memberClassConstant );
	}

	private void internLineNumberTableAttribute( LineNumberTableAttribute lineNumberTableAttribute )
	{
		Kit.get( lineNumberTableAttribute ); /* nothing to do */
	}

	private void internLocalVariableTableAttribute( LocalVariableTableAttribute localVariableTableAttribute )
	{
		for( LocalVariable localVariable : localVariableTableAttribute.localVariables() )
		{
			internConstant( localVariable.nameConstant );
			internConstant( localVariable.descriptorConstant );
		}
	}

	private void internLocalVariableTypeTableAttribute( LocalVariableTypeTableAttribute localVariableTypeTableAttribute )
	{
		for( LocalVariableType entry : localVariableTypeTableAttribute.localVariableTypes() )
		{
			internConstant( entry.nameConstant );
			internConstant( entry.signatureConstant );
		}
	}

	private void internMethodParametersAttribute( MethodParametersAttribute methodParametersAttribute )
	{
		for( MethodParameter entry : methodParametersAttribute.methodParameters() )
			internConstant( entry.nameConstant );
	}

	private void internRuntimeVisibleAnnotationsAttribute( RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute )
	{
		for( ByteCodeAnnotation annotation : runtimeVisibleAnnotationsAttribute.annotations() )
			internAnnotation( annotation );
	}

	private void internRuntimeInvisibleAnnotationsAttribute( RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute )
	{
		for( ByteCodeAnnotation annotation : runtimeInvisibleAnnotationsAttribute.annotations() )
			internAnnotation( annotation );
	}

	private void internRuntimeInvisibleParameterAnnotationsAttribute( RuntimeInvisibleParameterAnnotationsAttribute runtimeInvisibleParameterAnnotationsAttribute )
	{
		for( ParameterAnnotationSet entry : runtimeInvisibleParameterAnnotationsAttribute.parameterAnnotationSets() )
			for( ByteCodeAnnotation annotation : entry.annotations() )
				internAnnotation( annotation );
	}

	private void internRuntimeVisibleParameterAnnotationsAttribute( RuntimeVisibleParameterAnnotationsAttribute runtimeVisibleParameterAnnotationsAttribute )
	{
		for( ParameterAnnotationSet entry : runtimeVisibleParameterAnnotationsAttribute.parameterAnnotationSets() )
			for( ByteCodeAnnotation annotation : entry.annotations() )
				internAnnotation( annotation );
	}

	private void internRuntimeInvisibleTypeAnnotationsAttribute( RuntimeInvisibleTypeAnnotationsAttribute runtimeInvisibleTypeAnnotationsAttribute )
	{
		internTypeAnnotations( runtimeInvisibleTypeAnnotationsAttribute.typeAnnotations() );
	}

	private void internRuntimeVisibleTypeAnnotationsAttribute( RuntimeVisibleTypeAnnotationsAttribute runtimeVisibleTypeAnnotationsAttribute )
	{
		internTypeAnnotations( runtimeVisibleTypeAnnotationsAttribute.typeAnnotations() );
	}

	private void internSignatureAttribute( SignatureAttribute signatureAttribute )
	{
		internConstant( signatureAttribute.signatureConstant() );
	}

	private void internSourceFileAttribute( SourceFileAttribute sourceFileAttribute )
	{
		internConstant( sourceFileAttribute.valueConstant() );
	}

	private void internStackMapTableAttribute( StackMapTableAttribute stackMapTableAttribute )
	{
		for( StackMapFrame frame : stackMapTableAttribute.frames() )
			internStackMapFrame( frame );
	}

	private void internSyntheticAttribute( SyntheticAttribute syntheticAttribute )
	{
		Kit.get( syntheticAttribute ); /* nothing to do */
	}

	private void internUnknownAttribute( UnknownAttribute unknownAttribute )
	{
		Kit.get( unknownAttribute ); /* nothing to do */
	}

	private void internAnnotationValue( AnnotationValue annotationValue )
	{
		switch( annotationValue.tag )
		{
			case 'B', 's', 'Z', 'S', 'J', 'I', 'F', 'D', 'C' -> internConstAnnotationValue( annotationValue.asConstAnnotationValue() );
			case 'e' -> internEnumAnnotationValue( annotationValue.asEnumAnnotationValue() );
			case 'c' -> internClassAnnotationValue( annotationValue.asClassAnnotationValue() );
			case '@' -> internAnnotationAnnotationValue( annotationValue.asAnnotationAnnotationValue() );
			case '[' -> internArrayAnnotationValue( annotationValue.asArrayAnnotationValue() );
			default -> throw new UnknownValueException( annotationValue.tag );
		}
	}

	private void internConstAnnotationValue( ConstAnnotationValue constAnnotationValue )
	{
		internConstant( constAnnotationValue.valueConstant() );
	}

	private void internEnumAnnotationValue( EnumAnnotationValue enumAnnotationValue )
	{
		internConstant( enumAnnotationValue.typeNameConstant() );
		internConstant( enumAnnotationValue.valueNameConstant() );
	}

	private void internClassAnnotationValue( ClassAnnotationValue classAnnotationValue )
	{
		internConstant( classAnnotationValue.utf8Constant() );
	}

	private void internAnnotationAnnotationValue( AnnotationAnnotationValue annotationAnnotationValue )
	{
		internAnnotation( annotationAnnotationValue.annotation() );
	}

	private void internArrayAnnotationValue( ArrayAnnotationValue arrayAnnotationValue )
	{
		for( AnnotationValue annotationValue : arrayAnnotationValue.annotationValues() )
			internAnnotationValue( annotationValue );
	}

	private void internInstruction( Instruction instruction )
	{
		Optional<Constant> constant = switch( instruction.group )
			{
				case ConstantReferencing -> Optional.of( instruction.asConstantReferencingInstruction().constant );
				case IndirectLoadConstant -> Optional.of( instruction.asIndirectLoadConstantInstruction().constant );
				case InvokeDynamic -> Optional.of( instruction.asInvokeDynamicInstruction().invokeDynamicConstant );
				case InvokeInterface -> Optional.of( instruction.asInvokeInterfaceInstruction().interfaceMethodReferenceConstant );
				case MultiANewArray -> Optional.of( instruction.asMultiANewArrayInstruction().classConstant );
				default -> Optional.empty();
			};
		constant.ifPresent( c -> internConstant( c ) );
	}

	private void internAnnotation( ByteCodeAnnotation annotation )
	{
		internConstant( annotation.nameConstant() );
		for( AnnotationParameter annotationParameter : annotation.annotationParameters() )
		{
			internConstant( annotationParameter.nameConstant() );
			internAnnotationValue( annotationParameter.annotationValue() );
		}
	}

	private void internTypeAnnotations( Iterable<TypeAnnotation> typeAnnotations )
	{
		for( TypeAnnotation entry : typeAnnotations )
			for( TypeAnnotation.ElementValuePair elementValuePair : entry.elementValuePairs() )
			{
				AnnotationParameter annotationParameter = elementValuePair.elementValue();
				internConstant( annotationParameter.nameConstant() );
				internAnnotationValue( annotationParameter.annotationValue() );
			}
	}

	private void internStackMapFrame( StackMapFrame stackMapFrame )
	{
		if( stackMapFrame.isAppendStackMapFrame() )
		{
			AppendStackMapFrame appendStackMapFrame = stackMapFrame.asAppendStackMapFrame();
			for( VerificationType verificationType : appendStackMapFrame.localVerificationTypes() )
				internVerificationType( verificationType );
		}
		else if( stackMapFrame.isChopStackMapFrame() )
		{
			ChopStackMapFrame chopStackMapFrame = stackMapFrame.asChopStackMapFrame();
			Kit.get( chopStackMapFrame ); //nothing to do.
		}
		else if( stackMapFrame.isFullStackMapFrame() )
		{
			FullStackMapFrame fullStackMapFrame = stackMapFrame.asFullStackMapFrame();
			for( VerificationType verificationType : fullStackMapFrame.localVerificationTypes() )
				internVerificationType( verificationType );
			for( VerificationType verificationType : fullStackMapFrame.stackVerificationTypes() )
				internVerificationType( verificationType );
		}
		else if( stackMapFrame.isSameStackMapFrame() )
		{
			SameStackMapFrame sameStackMapFrame = stackMapFrame.asSameStackMapFrame();
			Kit.get( sameStackMapFrame ); //nothing to do.
		}
		else if( stackMapFrame.isSameLocals1StackItemStackMapFrame() )
		{
			SameLocals1StackItemStackMapFrame sameLocals1StackItemStackMapFrame = stackMapFrame.asSameLocals1StackItemStackMapFrame();
			internVerificationType( sameLocals1StackItemStackMapFrame.stackVerificationType() );
		}
		else
			assert false;
	}

	private void internVerificationType( VerificationType verificationType )
	{
		if( verificationType.isObjectVerificationType() )
		{
			ObjectVerificationType objectVerificationType = verificationType.asObjectVerificationType();
			internConstant( objectVerificationType.classConstant() );
		}
		else if( verificationType.isSimpleVerificationType() )
		{
			SimpleVerificationType simpleVerificationType = verificationType.asSimpleVerificationType();
			Kit.get( simpleVerificationType ); //nothing to do
		}
		else if( verificationType.isUninitializedVerificationType() )
		{
			UninitializedVerificationType uninitializedVerificationType = verificationType.asUninitializedVerificationType();
			Kit.get( uninitializedVerificationType ); //nothing to do
		}
		else
			assert false;
	}

	public int size()
	{
		return entries.size();
	}

	public Iterable<Constant> constants()
	{
		return Kit.iterable.filtered( entries, c -> c != null );
	}

	@Override public String toString()
	{
		return size() + " entries";
	}
}
