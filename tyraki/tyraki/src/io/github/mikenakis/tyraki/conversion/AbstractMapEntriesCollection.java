package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.BindingEqualityComparator;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

abstract class AbstractMapEntriesCollection<K, V> extends AbstractUnmodifiableCollection<Binding<K,V>>
{
	private final UnmodifiableMap<K,V> map;

	AbstractMapEntriesCollection( UnmodifiableMap<K,V> map, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( new BindingEqualityComparator<>( keyEqualityComparator, valueEqualityComparator ) );
		this.map = map;
	}

	@Override public final int size()
	{
		return map.size();
	}

	@Override public final Optional<Binding<K,V>> tryGet( Binding<K,V> element )
	{
		Optional<V> value = map.tryGet( element.getKey() );
		if( value.isEmpty() )
			return Optional.empty();
		if( !map.values().getEqualityComparator().equals( value.get(), element.getValue() ) )
			return Optional.empty();
		return Optional.of( element );
	}
}
