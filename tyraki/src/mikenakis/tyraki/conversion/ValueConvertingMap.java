package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.PartialConverter;
import mikenakis.tyraki.PartiallyConvertingEqualityComparator;
import mikenakis.tyraki.TotalConverter;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableMap;
import mikenakis.kit.EqualityComparator;

import java.util.Optional;

/**
 * Converts keys and values using {@link Function1}s.
 *
 * @param <K>  the key type.
 * @param <TV> the value type to convert to.
 * @param <SV> the value type to convert from.
 *
 * @author michael.gr
 */
class ValueConvertingMap<K, TV, SV> extends AbstractMap<K,TV>
{
	private class MyBinding implements Binding<K,TV>
	{
		final Binding<K,SV> sourceBinding;

		MyBinding( Binding<K,SV> sourceBinding )
		{
			this.sourceBinding = sourceBinding;
		}

		@Override public K getKey()
		{
			return sourceBinding.getKey();
		}

		@Override public TV getValue()
		{
			SV sourceValue = sourceBinding.getValue();
			TV targetValue = converter.invoke( sourceValue );
			assert targetValue != null;
			return targetValue;
		}
	}

	private final class MyEntriesCollection extends AbstractMapEntriesCollection<K,TV>
	{
		MyEntriesCollection( EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super TV> valueEqualityComparator )
		{
			super( ValueConvertingMap.this, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public int getModificationCount()
		{
			return mapToConvert.entries().getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<K,TV>> newUnmodifiableEnumerator()
		{
			return mapToConvert.entries().newUnmodifiableEnumerator().converted( sourceBinding -> new MyBinding( sourceBinding ) );
		}

		@Override public boolean isFrozen()
		{
			return mapToConvert.isFrozen();
		}
	}

	private final UnmodifiableMap<K,SV> mapToConvert;
	private final TotalConverter<? extends TV,? super SV> converter;
	private final PartialConverter<? extends SV,? super TV> reverter;
	private final MyEntriesCollection entries;

	ValueConvertingMap( UnmodifiableMap<K,SV> mapToConvert, TotalConverter<? extends TV,? super SV> converter, PartialConverter<? extends SV,? super TV> reverter )
	{
		assert mapToConvert != null;
		assert converter != null;
		assert reverter != null;
		this.mapToConvert = mapToConvert;
		entries = new MyEntriesCollection( mapToConvert.keys().getEqualityComparator(), new PartiallyConvertingEqualityComparator<>( reverter ) );
		this.converter = converter;
		this.reverter = reverter;
	}

	@Override public boolean isFrozen()
	{
		return mapToConvert.isFrozen();
	}

	@Override public final int size()
	{
		return mapToConvert.size();
	}

	@Override public UnmodifiableCollection<Binding<K,TV>> entries()
	{
		return entries;
	}

	@Override public final boolean containsKey( K key )
	{
		assert key != null;
		return mapToConvert.containsKey( key );
	}

	@Override public final Optional<Binding<K,TV>> tryGetBindingByKey( K key )
	{
		assert key != null;
		Optional<Binding<K,SV>> sourceBinding = mapToConvert.tryGetBindingByKey( key );
		return sourceBinding.map( b ->
		{
			TV tValue = converter.invoke( b.getValue() );
			return MapEntry.of( b.getKey(), tValue );
		} );
	}

	@Override public final UnmodifiableCollection<K> keys()
	{
		return mapToConvert.keys();
	}

	@Override public final UnmodifiableCollection<TV> values()
	{
		return mapToConvert.values().converted( converter, reverter );
	}
}
