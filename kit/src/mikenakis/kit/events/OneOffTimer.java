package mikenakis.kit.events;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

public abstract class OneOffTimer implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.create( this );
	//private final OverridableGuard    onDisposeOverridableGuard = new OverridableGuard( nameof(OnDispose) );
	private Procedure0 handler;

	protected OneOffTimer()
	{ }

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( value );
		return true;
	}

	public void setHandler( Procedure0 handler )
	{
		assert handler != null;
		assert this.handler == null;
		this.handler = handler;
	}

	protected void onTick()
	{
		assert lifeGuard.isAliveAssertion();
		assert handler != null; //the handler must be set immediately after construction!
		handler.invoke();
	}

	@Override public void close()
	{
		assert lifeGuard.isAliveAssertion();
		//using( onDisposeOverridableGuard.NewInvocation() )
		onClose();
		lifeGuard.close();
	}

	protected void onClose()
	{
		//onDisposeOverridableGuard.InvocationComplete();
	}
}
