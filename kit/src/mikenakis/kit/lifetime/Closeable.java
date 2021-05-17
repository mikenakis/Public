package mikenakis.kit.lifetime;

/**
 * An object which is aware of its own lifetime.
 * See https://blog.michael.gr/2021/01/object-lifetime-awareness.html
 * In other words, this is an {@link AutoCloseable} which does not throw checked exceptions, and is capable of asserting its own 'alive' state.
 *
 * Normally this should be called 'LifetimeAware', but I have (at least for the time being) opted to call it "Closeable" since Java already has
 * the 'AutoCloseable' interface, which essentially serves the same purpose.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Closeable extends AutoCloseable
{
	boolean lifeStateAssertion( boolean value );

	@Override void close(); //overriding the close() method without any checked exceptions.

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	boolean isAliveAssertion();

	boolean isNotAliveAssertion();

	interface Defaults extends Closeable
	{
		@Override default boolean isAliveAssertion()
		{
			assert lifeStateAssertion( true );
			return true;
		}

		@Override default boolean isNotAliveAssertion()
		{
			assert lifeStateAssertion( false );
			return true;
		}
	}
}
