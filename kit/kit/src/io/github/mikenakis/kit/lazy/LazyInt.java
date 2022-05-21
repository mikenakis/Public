package io.github.mikenakis.kit.lazy;

import io.github.mikenakis.bathyscaphe.annotations.Invariable;

/**
 * Implements a lazily computed int.
 *
 * TODO: add equals() and hashCode()
 *
 * @author michael.gr
 */
public final class LazyInt
{
	interface IntFunction0
	{
		int invoke();
	}

	public static LazyInt of( IntFunction0 factory )
	{
		return new LazyInt( factory );
	}

	@Invariable private IntFunction0 factory; //nullable
	@Invariable private int instance; //nullable

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
