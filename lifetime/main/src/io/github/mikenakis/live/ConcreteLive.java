package io.github.mikenakis.live;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.live.guard.LifeGuard;

public final class ConcreteLive<T> extends AbstractMortalCoherent implements Live.Defaults<T>
{
	private final LifeGuard lifeGuard = LifeGuard.of( this, true );
	private final T target;
	private final Procedure0 close;

	ConcreteLive( Coherence coherence, T target, Procedure0 close )
	{
		super( coherence );
		this.target = target;
		this.close = close;
	}

	@Override public T target()
	{
		return target;
	}

	@Override protected LifeGuard lifeGuard()
	{
		return lifeGuard;
	}

	@Override protected void onClose()
	{
		assert mustBeAliveAssertion();
		close.invoke();
		super.onClose();
	}
}
