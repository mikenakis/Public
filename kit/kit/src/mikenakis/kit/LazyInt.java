package mikenakis.kit;

import mikenakis.kit.functional.IntFunction0;

/**
 * Implements a lazily computed int.
 *
 * @author michael.gr
 */
public final class LazyInt
{
	public static  LazyInt of( IntFunction0 factory )
	{
		return new LazyInt( factory );
	}

	private IntFunction0 factory; //nullable
	private int instance; //nullable

	private LazyInt( IntFunction0 factory )
	{
		assert factory != null;
		this.factory = factory;
	}

	public int get()
	{
		if( factory == null )
			return instance;
		instance = factory.invoke();
		factory = null;
		return instance;
	}
}
