package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.descriptors.ArrayTypeDescriptor;
import mikenakis.bytecode.model.descriptors.MethodDescriptor;
import mikenakis.bytecode.model.descriptors.MethodHandleDescriptor;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ByteCode helpers.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeHelpers
{
	private ByteCodeHelpers()
	{
	}

	public static String getClassSourceLocation( ByteCodeType byteCodeType )
	{
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return byteCodeType.typeName() + "(" + (sourceFileName.orElse( "?" )) + ":" + 1 + ")";
	}

	public static String getMethodSourceLocation( ByteCodeType byteCodeType, ByteCodeMethod byteCodeMethod )
	{
		Optional<CodeAttribute> codeAttribute = byteCodeMethod.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_Code ) //
			.map( a -> a.asCodeAttribute() );
		Optional<LineNumberTableAttribute> lineNumberTableAttribute = codeAttribute //
			.flatMap( a -> a.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_LineNumberTable ) ) //
			.map( a -> a.asLineNumberTableAttribute() );
		int lineNumber = lineNumberTableAttribute.map( a -> a.entrys.get( 0 ).lineNumber() ).orElse( 0 );
		String methodName = byteCodeMethod.name();
		Optional<String> sourceFileName = byteCodeType.tryGetSourceFileName();
		return byteCodeType.typeName() + '.' + methodName + "(" + (sourceFileName.orElse( "?" )) + ":" + lineNumber + ")";
	}

	public static String typeNameFromClassDesc( ClassDesc classDesc )
	{
		if( classDesc.isPrimitive() )
			return classDesc.displayName();
		if( classDesc.isClassOrInterface() )
			return classDesc.packageName() + "." + classDesc.displayName();
		assert classDesc.isArray();
		return typeNameFromClassDesc( classDesc.componentType() ) + "[]";
	}

	public static boolean isValidTerminalTypeName( String typeName )
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

	public static String descriptorStringFromInternalName( String internalName )
	{
		if( internalName.startsWith( "[" ) )
			return internalName;
		if( !internalName.endsWith( ";" ) )
			return "L" + internalName + ";";
		return internalName;
	}

	public static ClassConstant classConstantFromTerminalTypeDescriptor( TerminalTypeDescriptor typeDescriptor )
	{
		ClassConstant classConstant = new ClassConstant();
		classConstant.setNameConstant( Mutf8Constant.of( typeDescriptor.descriptorString() ) );
		return classConstant;
	}

	public static ClassDesc classDescFromClassConstant( ClassConstant classConstant )
	{
		return ClassDesc.ofDescriptor( classConstant.descriptorString() );
	}

	public static DirectMethodHandleDesc directMethodHandleDescFromMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		NameAndDescriptorConstant nameAndDescriptorConstant = methodHandleConstant.getReferenceConstant().getNameAndDescriptorConstant();
		return MethodHandleDesc.of( //
			DirectMethodHandleDesc.Kind.valueOf( //
				methodHandleConstant.referenceKind().number, //
				methodHandleConstant.referenceKind() == MethodHandleConstant.ReferenceKind.InvokeInterface ), //I do not know why the isInterface parameter is needed here, but it does not work otherwise.
			classDescFromClassConstant( methodHandleConstant.getReferenceConstant().getDeclaringTypeConstant() ), //
			nameAndDescriptorConstant.getNameConstant().stringValue(), //
			nameAndDescriptorConstant.getDescriptorConstant().stringValue() );
	}

	public static String typeNameFromClassConstant( ClassConstant classConstant )
	{
		return typeNameFromClassDesc( classDescFromClassConstant( classConstant ) );
	}

	public static MethodDescriptor methodDescriptorFromDescriptorString( String descriptorString )
	{
		MethodTypeDesc methodTypeDesc = MethodTypeDesc.ofDescriptor( descriptorString );
		TypeDescriptor returnTypeDescriptor = TypeDescriptor.ofDescriptorString( methodTypeDesc.returnType().descriptorString() );
		List<ClassDesc> parameterClassDescs = methodTypeDesc.parameterList();
		List<TypeDescriptor> parameterTypeDescriptors = new ArrayList<>( parameterClassDescs.size() );
		for( ClassDesc parameterClassDesc : parameterClassDescs )
		{
			TypeDescriptor parameterTypeDescriptor = TypeDescriptor.ofDescriptorString( parameterClassDesc.descriptorString() );
			parameterTypeDescriptors.add( parameterTypeDescriptor );
		}
		return MethodDescriptor.of( returnTypeDescriptor, parameterTypeDescriptors );
	}

	public static TypeDescriptor typeDescriptorFromDescriptorString( String descriptorString )
	{
		ClassDesc classDesc = ClassDesc.ofDescriptor( descriptorString );
		if( classDesc.isArray() )
		{
			TypeDescriptor componentType = TypeDescriptor.ofDescriptorString( classDesc.componentType().descriptorString() );
			return ArrayTypeDescriptor.of( componentType );
		}
		return TerminalTypeDescriptor.ofDescriptorString( classDesc.descriptorString() );
	}

	public static boolean isArrayDescriptorString( String descriptorString )
	{
		return ClassDesc.ofDescriptor( descriptorString ).isArray();
	}

	public static String typeNameFromDescriptorString( String descriptorString )
	{
		return typeNameFromClassDesc( ClassDesc.ofDescriptor( descriptorString ) );
	}

	public static String descriptorStringFromMethodHandleConstantInvocation( MethodHandleConstant methodHandleConstant )
	{
		return directMethodHandleDescFromMethodHandleConstant( methodHandleConstant ).invocationType().descriptorString();
	}

	public static String descriptorStringFromMethodHandleConstantOwner( MethodHandleConstant methodHandleConstant )
	{
		return directMethodHandleDescFromMethodHandleConstant( methodHandleConstant ).owner().descriptorString();
	}

	public static ConstantDesc constantDescFromMethodDescriptorString( String methodDescriptorString )
	{
		return MethodTypeDesc.ofDescriptor( methodDescriptorString );
	}

	public static MethodHandleDescriptor methodHandleDescriptorFromMethodHandleConstant( MethodHandleConstant methodHandleConstant )
	{
		DirectMethodHandleDesc directMethodHandleDesc = directMethodHandleDescFromMethodHandleConstant( methodHandleConstant );
		MethodDescriptor methodDescriptor = MethodDescriptor.ofDescriptorString( directMethodHandleDesc.invocationType().descriptorString() );
		TypeDescriptor ownerTypeDescriptor = TypeDescriptor.ofDescriptorString( directMethodHandleDesc.owner().descriptorString() );
		return MethodHandleDescriptor.of( methodDescriptor, ownerTypeDescriptor );
	}
}
