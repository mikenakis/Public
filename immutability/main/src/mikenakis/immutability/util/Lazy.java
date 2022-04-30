package mikenakis.immutability.util;

import mikenakis.immutability.internal.mykit.functional.Function0;
import mikenakis.immutability.type.field.annotations.InvariableField;

import java.util.Optional;

/**
 * Implements a lazily created value.
 *
 * @author michael.gr
 */
public final class Lazy<T>
{
	public static <T> Lazy<T> of( Function0<T> factory )
	{
		return new Lazy<>( factory );
	}

	@InvariableField private Function0<T> factory; //nullable
	@InvariableField private T instance = null; //nullable

	private Lazy( Function0<T> factory )
	{
		assert factory != null;
		this.factory = factory;
	}

	public T get()
	{
		if( instance == null )
		{
			assert factory != null;
			instance = factory.invoke();
			factory = null; //we are not going to be needing the factory anymore, so forget it.
		}
		return instance;
	}

	public Optional<T> tryGet()
	{
		return Optional.ofNullable( instance );
	}
}
