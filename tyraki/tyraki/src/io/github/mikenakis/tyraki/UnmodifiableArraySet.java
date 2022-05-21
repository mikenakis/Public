package io.github.mikenakis.tyraki;

/**
 * Unmodifiable Array Set.
 *
 * @author michael.gr
 */
public interface UnmodifiableArraySet<E> extends UnmodifiableList<E>
{
	/**
	 * Gets the empty {@link UnmodifiableArraySet}.
	 *
	 * @param <E> the type of the items of the {@link UnmodifiableArraySet}.
	 *
	 * @return the empty {@link UnmodifiableArraySet}.
	 */
	static <E> UnmodifiableArraySet<E> of()
	{
		return UnmodifiableArrayHashSet.of();
	}

	/**
	 * Returns an {@link UnmodifiableArraySet} of down-casted elements.
	 *
	 * @param collection the {@link UnmodifiableArraySet} to down-cast.
	 * @param <T>        the type of the ancestor.
	 *
	 * @return the same {@link UnmodifiableArraySet} cast to an ancestral type.
	 */
	static <T> UnmodifiableArraySet<T> downCast( UnmodifiableArraySet<? extends T> collection )
	{
		@SuppressWarnings( "unchecked" )
		UnmodifiableArraySet<T> result = (UnmodifiableArraySet<T>)collection;
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets an {@link UnmodifiableArraySet} in which each key is cast to a given type.
	 */
	<TT> UnmodifiableArraySet<TT> castArraySet();

	interface Defaults<T> extends UnmodifiableArraySet<T>, UnmodifiableList.Defaults<T>
	{
		@Override default <TT> UnmodifiableArraySet<TT> castArraySet()
		{
			@SuppressWarnings( "unchecked" )
			UnmodifiableArraySet<TT> result = (UnmodifiableArraySet<TT>)this;
			return result;
		}
	}
}
