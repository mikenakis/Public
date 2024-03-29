package io.github.mikenakis.tyraki.immutable;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.ImmutabilityCoherence;
import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.ObjectHasher;
import io.github.mikenakis.tyraki.UnmodifiableArrayHashMap;
import io.github.mikenakis.tyraki.UnmodifiableArrayHashSet;
import io.github.mikenakis.tyraki.UnmodifiableArraySet;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableList;

import java.util.Optional;

/**
 * Immutable Collections.
 *
 * @author michael.gr
 */
public final class ImmutableCollections
{
	public static final float DEFAULT_FILL_FACTOR = 0.75f;

	private ImmutableCollections()
	{
	}

	private static final UnmodifiableArrayHashMap<Object,Object> emptyArrayHashMap = new UnmodifiableArrayHashMap.Defaults<>()
	{
		@Override public Coherence coherence() { return ImmutabilityCoherence.instance; }
		@Override public boolean mustBeImmutableAssertion() { return true; }
		@Override public Hasher<Object> getKeyHasher() { return ObjectHasher.INSTANCE; }
		@Override public UnmodifiableList<Binding<Object,Object>> entries() { return UnmodifiableList.of(); }
		@Override public UnmodifiableArraySet<Object> keys() { return UnmodifiableArraySet.of(); }
		@Override public UnmodifiableList<Object> values() { return UnmodifiableList.of(); }
		@Override public int size() { return 0; }
		@Override public Optional<Binding<Object,Object>> tryGetBindingByKey( Object key ) { return Optional.empty(); }
		@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "empty"; }
	};

	private static final UnmodifiableArrayHashSet<Object> emptyArrayHashSet = new UnmodifiableArrayHashSet.Defaults<>()
	{
		@Override public Coherence coherence() { return ImmutabilityCoherence.instance; }
		@Override public boolean mustBeImmutableAssertion() { return true; }
		@Override public Object get( int index ) { throw new IndexOutOfBoundsException( index ); }
		@Override public EqualityComparator<? super Object> getEqualityComparator() { return DefaultEqualityComparator.getInstance(); }
		@Override public int size() { return 0; }
		@Override public Hasher<? super Object> getElementHasher() { return ObjectHasher.INSTANCE; }
		@Override public UnmodifiableEnumerator<Object> newUnmodifiableEnumerator() { return UnmodifiableEnumerator.of(); }
		@Override public int getModificationCount() { return 0; }
	};

	private static final UnmodifiableList<Object> emptyList = new UnmodifiableList.Defaults<>()
	{
		@Override public Coherence coherence() { return ImmutabilityCoherence.instance; }
		@Override public boolean mustBeImmutableAssertion() { return true; }
		@Override public EqualityComparator<? super Object> getEqualityComparator() { return DefaultEqualityComparator.getInstance(); }
		@Override public int size() { return 0; }
		@Override public Object get( int index ) { throw new IndexOutOfBoundsException(); }
		@Override public UnmodifiableEnumerator<Object> newUnmodifiableEnumerator() { return UnmodifiableEnumerator.of(); }
		@Override public int getModificationCount() { return 0; }
		@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "empty"; }
	};

	/**
	 * Gets the empty {@link UnmodifiableList}.
	 *
	 * @param <E> the type of the elements in the {@link UnmodifiableList}.
	 *
	 * @return the empty immutable {@link UnmodifiableList}.
	 */
	public static <E> UnmodifiableList<E> emptyArrayList()
	{
		return emptyList.upCast();
	}

	/**
	 * Gets the empty {@link UnmodifiableArrayHashMap}.
	 *
	 * @param <K> the type of the keys of the {@link UnmodifiableArrayHashMap}.
	 * @param <V> the type of the values of the {@link UnmodifiableArrayHashMap}.
	 *
	 * @return the empty immutable {@link UnmodifiableArrayHashMap}.
	 */
	public static <K, V> UnmodifiableArrayHashMap<K,V> emptyArrayHashMap()
	{
		return emptyArrayHashMap.castArrayHashMap();
	}

	/**
	 * Gets the empty {@link UnmodifiableArrayHashSet}.
	 *
	 * @param <E> the type of the items of the {@link UnmodifiableArraySet}.
	 *
	 * @return the empty {@link UnmodifiableArraySet}.
	 */
	public static <E> UnmodifiableArrayHashSet<E> emptyArrayHashSet()
	{
		return emptyArrayHashSet.castArrayHashSet();
	}
}
