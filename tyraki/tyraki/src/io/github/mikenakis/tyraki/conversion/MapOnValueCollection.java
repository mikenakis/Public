package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

/**
 * {@link UnmodifiableMap} on {@link UnmodifiableCollection}.
 *
 * @author michael.gr
 */
class MapOnValueCollection<K, V> extends AbstractMap<K,V>
{
	private class MyBinding implements Binding<K,V>
	{
		final V value;

		MyBinding( V value )
		{
			this.value = value;
		}

		@Override public K getKey()
		{
			return converter.invoke( value );
		}

		@Override public V getValue()
		{
			return value;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return getKey() + " -> " + value;
		}
	}

	private final class MyEntriesCollection extends AbstractMapEntriesCollection<K,V>
	{
		MyEntriesCollection( EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( MapOnValueCollection.this, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public boolean mustBeImmutableAssertion()
		{
			return MapOnValueCollection.this.mustBeImmutableAssertion();
		}

		@Override public int getModificationCount()
		{
			return collection.getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return collection.newUnmodifiableEnumerator().map( value -> new MyBinding( value ) );
		}
	}

	private final UnmodifiableCollection<V> collection;
	private final Function1<? extends K,? super V> converter;
	private final MyEntriesCollection entries;

	MapOnValueCollection( UnmodifiableCollection<V> collection, Function1<? extends K,? super V> converter )
	{
		assert !collection.containsDuplicates();
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		entries = new MyEntriesCollection( keyEqualityComparator, valueEqualityComparator );
		this.collection = collection;
		this.converter = converter;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return collection.mustBeImmutableAssertion();
	}

	@Override public int size()
	{
		return collection.size();
	}

	@Override public UnmodifiableCollection<Binding<K,V>> entries()
	{
		return entries;
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		for( V value : collection )
		{
			K existingKey = converter.invoke( value );
			if( existingKey.equals( key ) )
				return Optional.of( MapEntry.of( existingKey, value ) );
		}
		return Optional.empty();
	}

	@Override public UnmodifiableCollection<K> keys()
	{
		return collection.map( converter );
	}

	@Override public UnmodifiableCollection<V> values()
	{
		return collection;
	}
}
