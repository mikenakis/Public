package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.kit.Kit;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class MethodDescriptor extends Descriptor
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

	@Override public String name()
	{
		return stringFrom();
	}

	public String stringFrom()
	{
		return stringFrom( Optional.empty() );
	}

	public String stringFrom( String methodName )
	{
		return stringFrom( Optional.of( methodName ) );
	}

	public String stringFrom( Optional<String> methodName )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( returnTypeDescriptor.name() );
		methodName.ifPresent( s -> stringBuilder.append( " " ).append( s ) );
		if( parameterTypeDescriptors.isEmpty() )
			stringBuilder.append( "()" );
		else
		{
			stringBuilder.append( "( " );
			boolean first = true;
			for( TypeDescriptor parameterTypeDescriptor : parameterTypeDescriptors )
			{
				first = Kit.stringBuilder.appendDelimiter( stringBuilder, first, ", " );
				stringBuilder.append( parameterTypeDescriptor.name() );
			}
			stringBuilder.append( " )" );
		}
		return stringBuilder.toString();
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
