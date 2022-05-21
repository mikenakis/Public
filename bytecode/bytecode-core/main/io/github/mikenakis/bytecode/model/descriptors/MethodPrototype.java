package io.github.mikenakis.bytecode.model.descriptors;

import io.github.mikenakis.java_type_model.MethodDescriptor;

import java.lang.reflect.Method;
import java.util.Objects;

public final class MethodPrototype
{
	public static MethodPrototype of( Method method )
	{
		return of( method.getName(), MethodDescriptor.of( method ) );
	}

	public static MethodPrototype of( String methodName, MethodDescriptor descriptor )
	{
		return new MethodPrototype( methodName, descriptor );
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

	public int parameterCount() { return descriptor.parameterCount(); }
	@Deprecated @Override public String toString() { return asString(); }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof MethodPrototype kin && equals( kin ); }
	public boolean equals( MethodPrototype other ) { return methodName.equals( other.methodName ) && descriptor.equals( other.descriptor ); }
	@Override public int hashCode() { return Objects.hash( methodName, descriptor ); }
}
