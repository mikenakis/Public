package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableMap;

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

		@Override public boolean isFrozenAssertion()
		{
			return mapToFilter.isFrozenAssertion();
		}

		@Override public int getModificationCount()
		{
			return mapToFilter.entries().getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return new FilteringEnumerator<>( mapToFilter.entries().newUnmodifiableEnumerator(), predicate );
		}
	}

	private final UnmodifiableMap<K,V> mapToFilter;
	private final Predicate<Binding<K,V>> predicate;
	private final MyEntriesCollection entries;
	private final UnmodifiableCollection<K> keys;
	private final UnmodifiableCollection<V> values;
	private int lastModificationCount = -1;
	private int lastLength = 0;

	FilteringMap( UnmodifiableMap<K,V> mapToFilter, Predicate<Binding<K,V>> predicate )
	{
		this.mapToFilter = mapToFilter;
		this.predicate = predicate;
		entries = new MyEntriesCollection( mapToFilter.keys().getEqualityComparator(), mapToFilter.values().getEqualityComparator() );
		keys = entries.map( kvBinding1 -> kvBinding1.getKey(), mapToFilter.keys().getEqualityComparator() );
		values = entries.map( kvBinding -> kvBinding.getValue() );
	}

	@Override public boolean isFrozenAssertion()
	{
		return mapToFilter.isFrozenAssertion();
	}

	@Override public UnmodifiableCollection<Binding<K,V>> entries()
	{
		return entries;
	}

	@Override public int size()
	{
		int modificationCount = mapToFilter.keys().getModificationCount();
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
		Optional<V> existingValueOption = mapToFilter.tryGet( k );
		return existingValueOption.map( existingValue ->
		{
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
