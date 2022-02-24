package mikenakis.kit.lifetime;

/**
 * An object which is aware of its own lifetime.
 * See https://blog.michael.gr/2021/01/object-lifetime-awareness.html
 * In other words, this is an {@link AutoCloseable} which does not throw checked exceptions, and is capable of asserting its own 'alive' state.
 *
 * Normally this should be called 'LifetimeAware', but I have (at least for the time being) opted to call it "Closeable" since Java already has
 * the 'AutoCloseable' interface, which essentially serves the same purpose.
 *
 * @author michael.gr
 */
public interface Closeable extends AutoCloseable
{
	boolean isAliveAssertion();

	@Override void close(); //overriding the close() method without any checked exceptions.

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults extends Closeable
	{
	}
}
