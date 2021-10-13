package mikenakis.tyraki.conversion;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.TotalConverter;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableMap;
import mikenakis.kit.EqualityComparator;

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

		@Override public int getModificationCount()
		{
			return collection.getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return collection.newUnmodifiableEnumerator().converted( value -> new MyBinding( value ) );
		}

		@Override public boolean isFrozen()
		{
			return MapOnValueCollection.this.isFrozen();
		}
	}

	private final UnmodifiableCollection<V> collection;
	private final TotalConverter<? extends K,? super V> converter;
	private final MyEntriesCollection entries;

	MapOnValueCollection( UnmodifiableCollection<V> collection, TotalConverter<? extends K,? super V> converter )
	{
		assert !collection.containsDuplicates();
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		entries = new MyEntriesCollection( keyEqualityComparator, valueEqualityComparator );
		this.collection = collection;
		this.converter = converter;
	}

	@Override public boolean isFrozen()
	{
		return collection.isFrozen();
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
		return collection.converted( converter );
	}

	@Override public UnmodifiableCollection<V> values()
	{
		return collection;
	}
}
