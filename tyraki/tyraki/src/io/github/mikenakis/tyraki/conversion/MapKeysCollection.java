package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableMap;

import java.util.Optional;

class MapKeysCollection<K, V> extends AbstractCoherent implements UnmodifiableCollection.Defaults<K>
{
	private final UnmodifiableMap<K,V> map;
	private final EqualityComparator<? super K> keyEqualityComparator;

	MapKeysCollection( UnmodifiableMap<K,V> map, EqualityComparator<? super K> keyEqualityComparator )
	{
		super( map.coherence() );
		this.map = map;
		this.keyEqualityComparator = keyEqualityComparator;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return map.mustBeImmutableAssertion();
	}

	@Override public int size()
	{
		return map.size();
	}

	@Override public Optional<K> tryGet( K element )
	{
		assert element != null;
		return map.keys().tryGet( element );
	}

	@Override public int getModificationCount()
	{
		return map.entries().getModificationCount();
	}

	@Override public UnmodifiableEnumerator<K> newUnmodifiableEnumerator()
	{
		return map.entries().newUnmodifiableEnumerator().map( kvBinding -> kvBinding.getKey() );
	}

	@Override public final EqualityComparator<? super K> getEqualityComparator()
	{
		return keyEqualityComparator;
	}

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof UnmodifiableCollection )
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableCollection<K> otherAsUnmodifiableCollection = (UnmodifiableCollection<K>)other;
			return equalsCollection( otherAsUnmodifiableCollection );
		}
		return false;
	}

	@Override public int hashCode()
	{
		return calculateHashCode();
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " elements";
	}
}
