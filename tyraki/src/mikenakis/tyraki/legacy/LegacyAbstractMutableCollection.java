package mikenakis.tyraki.legacy;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.UnknownMutationContext;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.kit.EqualityComparator;

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

	@Override public MutationContext mutationContext()
	{
		return UnknownMutationContext.instance;
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
