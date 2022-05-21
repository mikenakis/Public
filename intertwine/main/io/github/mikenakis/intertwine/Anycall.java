package io.github.mikenakis.intertwine;

import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.bytecode.model.descriptors.MethodReference;
import io.github.mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import io.github.mikenakis.java_type_model.MethodDescriptor;

/**
 * Represents any interface.
 *
 * @param <T> the type of the interface being represented.
 *
 * @author michael.gr
 */
public interface Anycall<T>
{
	static MethodPrototype methodPrototype()
	{
		return MethodPrototype.of( "anycall", MethodDescriptor.of( Object.class, MethodKey.class, Object[].class ) );
	}

	static MethodReference methodReference()
	{
		return MethodReference.of( MethodReferenceKind.Interface, Anycall.class, methodPrototype() );
	}

	/**
	 * Invokes a method.
	 *
	 * @param key       identifies the method being invoked.
	 * @param arguments the arguments passed to the method.
	 *
	 * @return the return value of the method; {@code null} if the method is of {@code void} return type.
	 */
	Object anycall( MethodKey<T> key, Object[] arguments );
}
