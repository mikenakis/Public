package mikenakis.kit.lazy;

import mikenakis.immutability.annotations.InvariableField;
import mikenakis.kit.functional.Function0;

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
	@InvariableField private T instance; //nullable

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
