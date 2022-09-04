package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

/**
 * {@link UnmodifiableMap} on {@link UnmodifiableCollection} of keys using a {@link Function1} for converting keys to values.
 *
 * @author michael.gr
 */
class MapOnKeyCollection<K, V> extends AbstractMap<K,V>
{
	private class MyBinding implements Binding<K,V>
	{
		final K key;

		MyBinding( K key )
		{
			assert key != null;
			this.key = key;
		}

		@Override public K getKey()
		{
			return key;
		}

		@Override public V getValue()
		{
			return converter.invoke( key );
		}
	}

	private final class MyEntriesCollection extends AbstractMapEntriesCollection<K,V>
	{
		MyEntriesCollection( EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( MapOnKeyCollection.this, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public boolean mustBeImmutableAssertion()
		{
			return MapOnKeyCollection.this.mustBeImmutableAssertion();
		}

		@Override public int getModificationCount()
		{
			return collection.getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return collection.newUnmodifiableEnumerator().map( key -> new MyBinding( key ) );
		}

		@Override public Coherence coherence()
		{
			return collection.coherence();
		}
	}

	private final UnmodifiableCollection<K> collection;
	private final Function1<? extends V,? super K> converter;
	private final MyEntriesCollection entries;

	MapOnKeyCollection( UnmodifiableCollection<K> collection, Function1<? extends V,? super K> converter )
	{
		super( collection.coherence() );
		assert collection != null;
		assert converter != null;
		assert !collection.containsDuplicates();
		EqualityComparator<? super K> keyEqualityComparator = collection.getEqualityComparator();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		entries = new MyEntriesCollection( keyEqualityComparator, valueEqualityComparator );
		this.collection = collection;
		this.converter = converter;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return collection.mustBeImmutableAssertion();
	}

	@Override public UnmodifiableCollection<Binding<K,V>> entries()
	{
		return entries;
	}

	@Override public int size()
	{
		return collection.size();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		Optional<K> existingKey = collection.tryGet( key );
		if( existingKey.isEmpty() )
			return Optional.empty();
		V value = converter.invoke( existingKey.get() );
		return Optional.of( MapEntry.of( existingKey.get(), value ) );
	}

	@Override public UnmodifiableCollection<K> keys()
	{
		return collection;
	}

	@Override public UnmodifiableCollection<V> values()
	{
		return collection.map( converter );
	}
}
