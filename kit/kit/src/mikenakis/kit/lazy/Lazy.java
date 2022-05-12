package mikenakis.kit.lazy;

import mikenakis.assessment.annotations.Invariable;
import mikenakis.kit.functional.Function0;

import java.util.Optional;

/**
 * Implements a lazily created value.
 *
 * TODO: add equals() and hashCode()
 *
 * @author michael.gr
 */
public final class Lazy<T>
{
	public static <T> Lazy<T> of( Function0<T> factory )
	{
		return new Lazy<>( factory );
	}

	@Invariable private Function0<T> factory; //nullable
	@Invariable private T instance; //nullable

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
