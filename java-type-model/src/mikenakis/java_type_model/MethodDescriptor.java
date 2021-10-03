package mikenakis.java_type_model;

import mikenakis.kit.Kit;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public final class MethodDescriptor
{
	public static MethodDescriptor of( Method method )
	{
		return of( method.getReturnType(), method.getParameterTypes() );
	}

	public static MethodDescriptor of( Class<?> returnType, Class<?>... parameterTypes )
	{
		return of( returnType, List.of( parameterTypes ) );
	}

	public static MethodDescriptor of( Class<?> returnType, Iterable<Class<?>> parameterTypes )
	{
		return of( TypeDescriptor.of( returnType ), Kit.collection.stream.fromIterable( parameterTypes ).map( c -> TypeDescriptor.of( c ) ).toList() );
	}

	public static MethodDescriptor of( TypeDescriptor returnTypeDescriptor, TypeDescriptor... parameterTypeDescriptors )
	{
		return of( returnTypeDescriptor, List.of( parameterTypeDescriptors ) );
	}

	public static MethodDescriptor of( TypeDescriptor returnTypeDescriptor, Iterable<TypeDescriptor> parameterTypeDescriptors )
	{
		return new MethodDescriptor( returnTypeDescriptor, Kit.collection.stream.fromIterable( parameterTypeDescriptors ).toList() );
	}

	public static MethodDescriptor of( TypeDescriptor returnTypeDescriptor, List<TypeDescriptor> parameterTypeDescriptors )
	{
		return new MethodDescriptor( returnTypeDescriptor, parameterTypeDescriptors );
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

	public void appendParameters( StringBuilder stringBuilder )
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

	@Deprecated @Override public boolean equals( Object other ) { return other instanceof MethodDescriptor kin && equals( kin ); }
	public boolean equals( MethodDescriptor other ) { return returnTypeDescriptor.equals( other.returnTypeDescriptor ) && parameterTypeDescriptors.equals( other.parameterTypeDescriptors ); }
	@Override public int hashCode() { return Objects.hash( returnTypeDescriptor, parameterTypeDescriptors ); }
	@Deprecated @Override public String toString() { return asString(); }
}
