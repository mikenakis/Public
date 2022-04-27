package mikenakis.kit;

/**
 * Implements a lazily computed int.
 *
 * @author michael.gr
 */
public final class LazyInt
{
	interface IntFunction0
	{
		int invoke();
	}

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
