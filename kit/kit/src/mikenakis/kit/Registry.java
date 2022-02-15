package mikenakis.kit;

/**
 * Registry.
 *
 * @author michael.gr
 */
public interface Registry<T>
{
	/**
	 * Registers or un-registers an observer.
	 *
	 * @param register {@code true} to register the given observer, false to un-register it.
	 * @param observer the observer to register or un-register.
	 *
	 * @return {@code true} if the membership of the observer was changed; {@code false} if the membership of the observer was same as the one requested.
	 */
	boolean tryRegisterObserver( boolean register, T observer );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds or removes an observer.  Fails if the membership state of the observer is already same as requested.
	 *
	 * @param register {@code true} to add the given observer, false to remover it.
	 * @param observer the observer to add or remove.
	 */
	void registerObserver( boolean register, T observer );

	/**
	 * Adds an observer.
	 *
	 * @param observer the observer to add.
	 */
	void addObserver( T observer );

	/**
	 * Removes an observer.
	 *
	 * @param observer the observer to remove.
	 */
	void removeObserver( T observer );

	interface Defaults<T> extends Registry<T>//, MutableCollection.Defaults<T>
	{
		@Override default void registerObserver( boolean register, T observer )
		{
			boolean ok = tryRegisterObserver( register, observer );
			assert ok : new IllegalArgumentException();
		}

		@Override default void addObserver( T observer )
		{
			registerObserver( true, observer );
		}

		@Override default void removeObserver( T observer )
		{
			registerObserver( false, observer );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Decorator<T> extends Defaults<T>
	{
		Registry<T> getDecoratedRegistry();

		@Override default boolean tryRegisterObserver( boolean register, T observer )
		{
			Registry<T> decoree = getDecoratedRegistry();
			return decoree.tryRegisterObserver( register, observer );
		}
	}
}
