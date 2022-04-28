package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

final class MapValuesCollection<K, V> extends AbstractUnmodifiableCollection<V>
{
	private final UnmodifiableMap<K,V> map;

	MapValuesCollection( UnmodifiableMap<K,V> map, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( valueEqualityComparator );
		this.map = map;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return map.mustBeImmutableAssertion();
	}

	@Override public int size()
	{
		return map.size();
	}

	@Override public Optional<V> tryGet( V element )
	{
		return map.values().tryGet( element );
	}

	@Override public int getModificationCount()
	{
		return map.entries().getModificationCount();
	}

	@Override public UnmodifiableEnumerator<V> newUnmodifiableEnumerator()
	{
		return map.entries().newUnmodifiableEnumerator().map( kvBinding -> kvBinding.getValue() );
	}
}
