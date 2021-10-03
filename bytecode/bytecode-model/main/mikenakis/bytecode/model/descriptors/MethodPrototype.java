package mikenakis.bytecode.model.descriptors;

import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.TypeDescriptor;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

public final class MethodPrototype
{
	public static MethodPrototype of( Method method )
	{
		return of( method.getName(), TypeDescriptor.of( method.getReturnType() ), Stream.of( method.getParameterTypes() ).map( t -> TypeDescriptor.of( t ) ).toList() );
	}

	public static MethodPrototype of( String methodName, MethodDescriptor descriptor )
	{
		return new MethodPrototype( methodName, descriptor );
	}

	public static MethodPrototype of( String methodName, TypeDescriptor returnType, TypeDescriptor... parameterTypes )
	{
		return new MethodPrototype( methodName, MethodDescriptor.of( returnType, parameterTypes ) );
	}

	public static MethodPrototype of( String methodName, TypeDescriptor returnType, Iterable<TypeDescriptor> parameterTypes )
	{
		return new MethodPrototype( methodName, MethodDescriptor.of( returnType, parameterTypes ) );
	}

	public final String methodName;
	public final MethodDescriptor descriptor;

	private MethodPrototype( String methodName, MethodDescriptor descriptor )
	{
		this.methodName = methodName;
		this.descriptor = descriptor;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( descriptor.returnTypeDescriptor.typeName() );
		stringBuilder.append( " " ).append( methodName );
		descriptor.appendParameters( stringBuilder );
		return stringBuilder.toString();
	}

	@Deprecated @Override public String toString() { return asString(); }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof MethodPrototype kin && equals( kin ); }
	public boolean equals( MethodPrototype other ) { return methodName.equals( other.methodName ) && descriptor.equals( other.descriptor ); }
	@Override public int hashCode() { return Objects.hash( methodName, descriptor ); }
}
