package mikenakis.tyraki;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.ObjectHasher;
import mikenakis.kit.coherence.FreezableCoherence;
import mikenakis.kit.lifetime.Mortal;
import mikenakis.tyraki.immutable.ImmutableCollections;
import mikenakis.tyraki.mutable.MutableCollections;

/**
 * Unmodifiable Hash Set.
 *
 * @author michael.gr
 */
public interface UnmodifiableHashSet<E> extends UnmodifiableCollection<E>
{
	/**
	 * Creates a new immutable {@link UnmodifiableHashSet}.
	 *
	 * @param hasher             the {@link Hasher} to use for hashing items.
	 * @param equalityComparator the {@link EqualityComparator} to use for comparing items.
	 * @param items              the items that the {@link UnmodifiableCollection} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashSet}.
	 */
	static <E> UnmodifiableHashSet<E> from( UnmodifiableCollection<E> items, float fillFactor, Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		if( items.isEmpty() )
			return of();
		return Mortal.tryGetWith( FreezableCoherence.of(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableHashSet<E> mutableSet = mutableCollections.newArrayHashSet( items.size(), fillFactor, hasher, equalityComparator );
			mutableSet.addAll( items );
			return mutableSet;
		} );
	}

	/**
	 * Creates a new {@link UnmodifiableHashSet}.
	 *
	 * @param hasher             the {@link Hasher} to use for hashing items.
	 * @param equalityComparator the {@link EqualityComparator} to use for comparing items.
	 * @param arrayOfItems       the items that the {@link UnmodifiableHashSet} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashSet}.
	 */
	static <E> UnmodifiableHashSet<E> from( Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator, E[] arrayOfItems )
	{
		UnmodifiableCollection<E> items = UnmodifiableList.onArray( arrayOfItems );
		return from( items, ImmutableCollections.DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashSet}.
	 *
	 * @param items the items that the {@link UnmodifiableCollection} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashSet}.
	 */
	static <E> UnmodifiableHashSet<E> from( UnmodifiableEnumerable<E> items )
	{
		return from( items.toList() );
	}

	/**
	 * Creates a new {@link UnmodifiableHashSet}.
	 *
	 * @param set the items that the {@link UnmodifiableHashSet} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashSet}.
	 */
	static <E> UnmodifiableHashSet<E> from( UnmodifiableHashSet<E> set )
	{
		Hasher<? super E> hasher = set.getElementHasher();
		EqualityComparator<? super E> equalityComparator = set.getEqualityComparator();
		return from( set, ImmutableCollections.DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	/**
	 * Creates a new hash set {@link UnmodifiableHashSet}.
	 *
	 * @param items the items that the {@link UnmodifiableHashSet} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashSet}.
	 */
	static <E> UnmodifiableHashSet<E> from( UnmodifiableCollection<E> items )
	{
		Hasher<? super E> hasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super E> equalityComparator = items.getEqualityComparator();
		return from( items, ImmutableCollections.DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	/**
	 * Creates a new hash set {@link UnmodifiableHashSet}.
	 *
	 * @param items the items that the {@link UnmodifiableHashSet} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashSet}.
	 */
	static <E> UnmodifiableCollection<E> from( UnmodifiableCollection<E> items, Hasher<? super E> hasher )
	{
		EqualityComparator<? super E> equalityComparator = items.getEqualityComparator();
		return from( items, ImmutableCollections.DEFAULT_FILL_FACTOR, hasher, equalityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableHashSet}.
	 *
	 * @param arrayOfElements the items that the {@link UnmodifiableHashSet} is to contain.
	 * @param <E>             the type of the items.
	 *
	 * @return a new {@link UnmodifiableHashSet}.
	 */
	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	static <E> UnmodifiableHashSet<E> of( E... arrayOfElements )
	{
		UnmodifiableCollection<E> elements = UnmodifiableList.onArray( arrayOfElements );
		return from( elements );
	}

	static <E> UnmodifiableHashSet<E> of( UnmodifiableCollection<E> elements )
	{
		return from( elements );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	Hasher<? super E> getElementHasher();

	/**
	 * Gets an {@link UnmodifiableArraySet} in which each key is cast to a given type.
	 */
	<TT> UnmodifiableHashSet<TT> castHashSet();

	interface Defaults<T> extends UnmodifiableHashSet<T>, UnmodifiableCollection.Defaults<T>
	{
		@Override default <TT> UnmodifiableHashSet<TT> castHashSet()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableHashSet<TT> result = (UnmodifiableHashSet<TT>)this;
			return result;
		}
	}
}
