package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;
import java.util.function.Predicate;

final class FilteringMap<K, V> extends AbstractMap<K,V>
{
	private final class MyEntriesCollection extends AbstractMapEntriesCollection<K,V>
	{
		MyEntriesCollection( EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( FilteringMap.this, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public boolean mustBeImmutableAssertion()
		{
			return map.mustBeImmutableAssertion();
		}

		@Override public int getModificationCount()
		{
			return map.entries().getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return new FilteringEnumerator<>( map.entries().newUnmodifiableEnumerator(), predicate );
		}

		@Override public Coherence coherence()
		{
			return FilteringMap.this.coherence();
		}
	}

	private final UnmodifiableMap<K,V> map;
	private final Predicate<Binding<K,V>> predicate;
	private final MyEntriesCollection entries;
	private final UnmodifiableCollection<K> keys;
	private final UnmodifiableCollection<V> values;
	private int lastModificationCount = -1;
	private int lastLength = 0;

	FilteringMap( UnmodifiableMap<K,V> map, Predicate<Binding<K,V>> predicate )
	{
		super( map.coherence() );
		this.map = map;
		this.predicate = predicate;
		entries = new MyEntriesCollection( map.keys().getEqualityComparator(), map.values().getEqualityComparator() );
		keys = entries.map( kvBinding1 -> kvBinding1.getKey(), map.keys().getEqualityComparator() );
		values = entries.map( kvBinding -> kvBinding.getValue() );
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return map.mustBeImmutableAssertion();
	}

	@Override public UnmodifiableCollection<Binding<K,V>> entries()
	{
		return entries;
	}

	@Override public int size()
	{
		int modificationCount = map.keys().getModificationCount();
		if( modificationCount != lastModificationCount )
		{
			lastModificationCount = modificationCount;
			lastLength = keys.countElements();
		}
		else
			assert keys.countElements() == lastLength;
		return lastLength;
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K k )
	{
		assert k != null;
		Optional<V> existingValueOption = map.tryGet( k );
		return existingValueOption.map( existingValue -> {
			Binding<K,V> binding = MapEntry.of( k, existingValue ); //TODO this looks wrong.
			if( !predicate.test( binding ) )
				return null;
			return binding;
		} );
	}

	@Override public UnmodifiableCollection<K> keys()
	{
		return keys;
	}

	@Override public UnmodifiableCollection<V> values()
	{
		return values;
	}
}
