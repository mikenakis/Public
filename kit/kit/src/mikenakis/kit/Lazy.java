package mikenakis.kit;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure1;

/**
 * Lazy factory.
 *
 * @author michael.gr
 */
public final class Lazy<T>
{
	public static <T> Lazy<T> of( Function0<T> factory )
	{
		return new Lazy<>( factory );
	}

	private Function0<T> factory; //nullable
	private T instance = null; //nullable

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

	public void ifPresent( Procedure1<T> procedure )
	{
		if( instance != null )
			procedure.invoke( instance );
	}
}
