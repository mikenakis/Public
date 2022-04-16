package mikenakis.kit.lifetime;

import mikenakis.kit.mutation.Coherent;

/**
 * An object which is aware of its own lifetime.
 * See https://blog.michael.gr/2021/01/object-lifetime-awareness.html
 * In other words, this is an {@link AutoCloseable} which does not throw checked exceptions, and is capable of asserting its own 'alive' state.
 * Normally this should be called 'LifetimeAware', but that's too long.
 *
 * @author michael.gr
 */
public interface Mortal extends AutoCloseable, Coherent
{
	boolean mustBeAliveAssertion();

	@Override void close(); //overriding the close() method without any checked exceptions.

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults extends Mortal
	{
	}
}
