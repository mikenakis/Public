package mikenakis.kit;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * Registers an object of type T with a {@link Registry} and un-registers it when closed.
 *
 * @author michael.gr
 */
public class Registration<T> extends Mutable implements Closeable.Defaults
{
	public static <T> Registration<T> of( MutationContext mutationContext, Registry<T> registry, T registrant )
	{
		return new Registration<>( mutationContext, registry, registrant );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final Registry<T> registry;
	private final T registrant;

	private Registration( MutationContext mutationContext, Registry<T> registry, T registrant )
	{
		super( mutationContext );
		this.registry = registry;
		this.registrant = registrant;
		registry.registerObserver( true, registrant );
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		registry.registerObserver( false, registrant );
		lifeGuard.close();
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}
}
