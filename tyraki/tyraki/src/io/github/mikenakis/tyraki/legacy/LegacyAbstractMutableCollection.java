package io.github.mikenakis.tyraki.legacy;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.NullaryCoherence;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.UnmodifiableCollection;

/**
 * Abstract {@link MutableCollection}.
 *
 * @author michael.gr
 */
abstract class LegacyAbstractMutableCollection<E> implements MutableCollection.Defaults<E>
{
	private final EqualityComparator<? super E> equalityComparator;

	LegacyAbstractMutableCollection( EqualityComparator<? super E> equalityComparator )
	{
		assert equalityComparator != null;
		this.equalityComparator = equalityComparator;
	}

	@Override public Coherence coherence()
	{
		return NullaryCoherence.instance;
	}

	@Override public final EqualityComparator<? super E> getEqualityComparator()
	{
		return equalityComparator;
	}

	@Override public boolean equals( Object other )
	{
		if( other == null )
			return false;
		if( other instanceof UnmodifiableCollection )
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableCollection<E> otherAsUnmodifiableCollection = (UnmodifiableCollection<E>)other;
			return equalsCollection( otherAsUnmodifiableCollection );
		}
		assert false;
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
