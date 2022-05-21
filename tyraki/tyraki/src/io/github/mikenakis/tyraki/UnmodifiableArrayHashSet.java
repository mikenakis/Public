package io.github.mikenakis.tyraki;

import io.github.mikenakis.coherence.implementation.ConcreteFreezableCoherence;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.lifetime.Mortal;
import io.github.mikenakis.tyraki.immutable.ImmutableCollections;
import io.github.mikenakis.tyraki.mutable.MutableCollections;

/**
 * Unmodifiable Array Hash Set.
 *
 * @author michael.gr
 */
public interface UnmodifiableArrayHashSet<E> extends UnmodifiableArraySet<E>, UnmodifiableHashSet<E>
{
	/**
	 * Creates a new {@link UnmodifiableArrayHashSet}.
	 *
	 * @param items the items that the {@link UnmodifiableArrayHashSet} is to contain.
	 *
	 * @return a new {@link UnmodifiableArrayHashSet}.
	 */
	static <E> UnmodifiableArrayHashSet<E> from( UnmodifiableCollection<E> items, float fillFactor, Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		if( items.isEmpty() )
			return of();
		return Mortal.tryGetWith( ConcreteFreezableCoherence.create(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableArrayHashSet<E> mutableSet = mutableCollections.newArrayHashSet( items.size(), fillFactor, hasher, equalityComparator );
			mutableSet.addAll( items );
			return mutableSet;
		} );
	}

	/**
	 * Creates a new {@link UnmodifiableArrayHashSet}.
	 *
	 * @param items the items that the {@link UnmodifiableArrayHashSet} is to contain.
	 *
	 * @return a new {@link UnmodifiableArrayHashSet}.
	 */
	static <T> UnmodifiableArrayHashSet<T> from( UnmodifiableCollection<T> items )
	{
		Hasher<? super T> hasher = IdentityHasher.getInstance();
		EqualityComparator<? super T> equalityComparator = items.getEqualityComparator();
		return from( items, ImmutableCollections.DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableArrayHashSet}.
	 *
	 * @param items the items that the {@link UnmodifiableArrayHashSet} is to contain.
	 *
	 * @return a new {@link UnmodifiableArrayHashSet}.
	 */
	static <T> UnmodifiableArraySet<T> from( UnmodifiableHashSet<T> items )
	{
		Hasher<? super T> hasher = items.getElementHasher();
		EqualityComparator<? super T> equalityComparator = items.getEqualityComparator();
		return from( items, ImmutableCollections.DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	/**
	 * Gets the empty {@link UnmodifiableArrayHashSet}.
	 *
	 * @param <E> the type of the items of the {@link UnmodifiableArraySet}.
	 *
	 * @return the empty {@link UnmodifiableArraySet}.
	 */
	static <E> UnmodifiableArrayHashSet<E> of()
	{
		return ImmutableCollections.emptyArrayHashSet();
	}

	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	static <E> UnmodifiableArrayHashSet<E> of( E... arrayOfElements )
	{
		UnmodifiableCollection<E> elements = UnmodifiableList.onArray( arrayOfElements );
		return from( elements );
	}

	/**
	 * Gets an {@link UnmodifiableArrayHashSet} in which each key is cast to a given type.
	 */
	<TT> UnmodifiableArrayHashSet<TT> castArrayHashSet();

	interface Defaults<E> extends UnmodifiableArrayHashSet<E>, UnmodifiableArraySet.Defaults<E>, UnmodifiableHashSet.Defaults<E>
	{
		@Override default <TT> UnmodifiableArrayHashSet<TT> castArrayHashSet()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableArrayHashSet<TT> result = (UnmodifiableArrayHashSet<TT>)this;
			return result;
		}
	}
}
