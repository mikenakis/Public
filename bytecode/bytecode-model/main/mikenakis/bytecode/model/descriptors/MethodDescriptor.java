package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.kit.Kit;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class MethodDescriptor
{
	public static MethodDescriptor of( Method method )
	{
		return of( method.getReturnType(), method.getParameterTypes() );
	}

	public static MethodDescriptor of( Class<?> returnType, Class<?>... parameterTypes )
	{
		return of( returnType, List.of( parameterTypes ) );
	}

	public static MethodDescriptor of( Class<?> returnType, Collection<Class<?>> parameterTypes )
	{
		return of( TypeDescriptor.of( returnType ), parameterTypes.stream().map( c -> TypeDescriptor.of( c ) ).toList() );
	}

	public static MethodDescriptor of( TypeDescriptor returnTypeDescriptor, TypeDescriptor... parameterTypeDescriptors )
	{
		return of( returnTypeDescriptor, List.of( parameterTypeDescriptors ) );
	}

	public static MethodDescriptor of( TypeDescriptor returnTypeDescriptor, List<TypeDescriptor> parameterTypeDescriptors )
	{
		return new MethodDescriptor( returnTypeDescriptor, parameterTypeDescriptors );
	}

	public static MethodDescriptor ofDescriptorString( String descriptorString )
	{
		return ByteCodeHelpers.methodDescriptorFromDescriptorString( descriptorString );
	}

	public final TypeDescriptor returnTypeDescriptor;
	public final List<TypeDescriptor> parameterTypeDescriptors;

	private MethodDescriptor( TypeDescriptor returnTypeDescriptor, List<TypeDescriptor> parameterTypeDescriptors )
	{
		this.returnTypeDescriptor = returnTypeDescriptor;
		this.parameterTypeDescriptors = parameterTypeDescriptors;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( returnTypeDescriptor.typeName() );
		appendParameters( stringBuilder );
		return stringBuilder.toString();
	}

	public String descriptorString()
	{
		ClassDesc returnDesc = classDescFromTypeDescriptor( returnTypeDescriptor );
		ClassDesc[] paramDescs = new ClassDesc[parameterTypeDescriptors.size()];
		for( int i = 0;  i < paramDescs.length;  i++ )
			paramDescs[i] = ClassDesc.ofDescriptor( parameterTypeDescriptors.get( i ).descriptorString() );
		MethodTypeDesc methodTypeDesc = MethodTypeDesc.of( returnDesc, paramDescs );
		return methodTypeDesc.descriptorString();
	}

	private static ClassDesc classDescFromTypeDescriptor( TypeDescriptor typeDescriptor )
	{
		return ClassDesc.ofDescriptor( typeDescriptor.descriptorString() );
	}

	void appendParameters( StringBuilder stringBuilder )
	{
		if( parameterTypeDescriptors.isEmpty() )
			stringBuilder.append( "()" );
		else
		{
			stringBuilder.append( "( " );
			boolean first = true;
			for( TypeDescriptor parameterTypeDescriptor : parameterTypeDescriptors )
			{
				first = Kit.stringBuilder.appendDelimiter( stringBuilder, first, ", " );
				stringBuilder.append( parameterTypeDescriptor.typeName() );
			}
			stringBuilder.append( " )" );
		}
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof MethodDescriptor kin )
			return equals( kin );
		return false;
	}

	public boolean equals( MethodDescriptor other )
	{
		return returnTypeDescriptor.equals( other.returnTypeDescriptor ) && parameterTypeDescriptors.equals( other.parameterTypeDescriptors );
	}

	@Override public int hashCode()
	{
		int result = 1;
		result = 31 * result + returnTypeDescriptor.hashCode();
		for( var parameterTypeDescriptor : parameterTypeDescriptors )
			result = 31 * result + parameterTypeDescriptor.hashCode();
		return result;
	}
}
