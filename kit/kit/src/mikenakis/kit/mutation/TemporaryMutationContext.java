package mikenakis.kit.mutation;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

public class TemporaryMutationContext extends Mutable implements MutationContext, Closeable.Defaults
{
	public static TemporaryMutationContext of()
	{
		return of( ThreadLocalMutationContext.instance() );
	}

	public static TemporaryMutationContext of( MutationContext parentMutationContext )
	{
		return new TemporaryMutationContext( parentMutationContext );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );

	private TemporaryMutationContext( MutationContext parentMutationContext )
	{
		super( parentMutationContext );
	}

	@Override public boolean mustBeAliveAssertion()
	{
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		lifeGuard.close();
	}

	@Override public boolean mustBeReadableAssertion()
	{
		return Kit.assertion( this::mustBeAliveAssertion, cause -> new MustBeReadableException( this, cause ) );
	}

	@Override public boolean mustBeWritableAssertion()
	{
		return Kit.assertion( this::mustBeAliveAssertion, cause -> new MustBeWritableException( this, cause ) );
	}

	@Override public String toString()
	{
		return lifeGuard.toString() + "; parent: " + super.toString();
	}
}
