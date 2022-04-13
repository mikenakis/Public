package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.PartiallyConvertingEqualityComparator;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableMap;

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

		@Override public boolean isImmutableAssertion()
		{
			return ValueConvertingMap.this.isImmutableAssertion();
		}

		@Override public int getModificationCount()
		{
			return map.entries().getModificationCount();
		}

		@Override public UnmodifiableEnumerator<Binding<K,TV>> newUnmodifiableEnumerator()
		{
			return map.entries().newUnmodifiableEnumerator().map( sourceBinding -> new MyBinding( sourceBinding ) );
		}
	}

	private final UnmodifiableMap<K,SV> map;
	private final Function1<? extends TV,? super SV> converter;
	private final Function1<Optional<? extends SV>,? super TV> reverter;
	private final MyEntriesCollection entries;

	ValueConvertingMap( UnmodifiableMap<K,SV> map, Function1<? extends TV,? super SV> converter, Function1<Optional<? extends SV>,? super TV> reverter )
	{
		assert converter != null;
		assert reverter != null;
		this.map = map;
		entries = new MyEntriesCollection( map.keys().getEqualityComparator(), new PartiallyConvertingEqualityComparator<>( reverter ) );
		this.converter = converter;
		this.reverter = reverter;
	}

	@Override public boolean isImmutableAssertion()
	{
		return map.isImmutableAssertion();
	}

	@Override public final int size()
	{
		return map.size();
	}

	@Override public UnmodifiableCollection<Binding<K,TV>> entries()
	{
		return entries;
	}

	@Override public final boolean containsKey( K key )
	{
		assert key != null;
		return map.containsKey( key );
	}

	@Override public final Optional<Binding<K,TV>> tryGetBindingByKey( K key )
	{
		assert key != null;
		Optional<Binding<K,SV>> sourceBinding = map.tryGetBindingByKey( key );
		return sourceBinding.map( b ->
		{
			TV tValue = converter.invoke( b.getValue() );
			return MapEntry.of( b.getKey(), tValue );
		} );
	}

	@Override public final UnmodifiableCollection<K> keys()
	{
		return map.keys();
	}

	@Override public final UnmodifiableCollection<TV> values()
	{
		return map.values().map( converter, reverter );
	}
}
