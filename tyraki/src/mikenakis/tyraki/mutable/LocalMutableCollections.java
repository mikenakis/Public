package mikenakis.tyraki.mutable;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.MutationContext;

/**
 * Stack-local {@link MutableCollections}.
 *
 * @author michael.gr
 */
public final class LocalMutableCollections
{
	public static void tryWith( Procedure1<MutableCollections> procedure )
	{
		Kit.tryWith( new LocalMutationContext(), mutationContext -> //
		{
			MutableCollections mutableCollections = new MutableCollections( mutationContext );
			procedure.invoke( mutableCollections );
		} );
	}

	public static <T> T tryGetWith( Function1<T,MutableCollections> function )
	{
		return Kit.tryGetWith( new LocalMutationContext(), mutationContext -> //
		{
			MutableCollections mutableCollections = new MutableCollections( mutationContext );
			return function.invoke( mutableCollections );
		} );
	}

	private static class LocalMutationContext implements MutationContext, Closeable.Defaults
	{
		private final LifeGuard lifeGuard = LifeGuard.create( this );

		@Override public boolean inContextAssertion()
		{
			assert isAliveAssertion();
			return true;
		}

		@Override public boolean lifeStateAssertion( boolean value )
		{
			return lifeGuard.lifeStateAssertion( value );
		}

		@Override public void close()
		{
			lifeGuard.close();
		}
	}
}
