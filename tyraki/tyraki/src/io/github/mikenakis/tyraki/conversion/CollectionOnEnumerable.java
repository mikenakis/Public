package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerable;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

class CollectionOnEnumerable<K> implements UnmodifiableCollection.Defaults<K>
{
	private final UnmodifiableEnumerable<K> enumerable;
	private final EqualityComparator<K> equalityComparator;

	CollectionOnEnumerable( UnmodifiableEnumerable<K> enumerable, EqualityComparator<K> equalityComparator )
	{
		this.enumerable = enumerable;
		this.equalityComparator = equalityComparator;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return enumerable.mustBeImmutableAssertion();
	}

	@Override public int size()
	{
		return enumerable.countElements();
	}

	@Override public Optional<K> tryGet( K item )
	{
		assert item != null;
		for( K element : enumerable )
			if( equalityComparator.equals( item, element ) )
				return Optional.of( element );
		return Optional.empty();
	}

	@Override public int getModificationCount()
	{
		return 0;
	}

	@Override public UnmodifiableEnumerator<K> newUnmodifiableEnumerator()
	{
		return enumerable.newUnmodifiableEnumerator();
	}

	@Override public final EqualityComparator<? super K> getEqualityComparator()
	{
		return equalityComparator;
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
		//noinspection NonFinalFieldReferencedInHashCode
		return enumerable.hashCode();
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		var builder = new StringBuilder();
		builder.append( size() ).append( " elements" );
		return builder.toString();
	}

	@Override public Coherence coherence()
	{
		return enumerable.coherence();
	}
}
