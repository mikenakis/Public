package mikenakis.tyraki.mutable;

import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.kit.EqualityComparator;

/**
 * Abstract {@link MutableCollection}.
 *
 * @author michael.gr
 */
abstract class AbstractMutableCollection<E> extends AbstractMutableEnumerable<E> implements MutableCollection.Defaults<E>
{
	final EqualityComparator<? super E> equalityComparator;

	protected AbstractMutableCollection( MutableCollections mutableCollections, EqualityComparator<? super E> equalityComparator )
	{
		super( mutableCollections );
		this.equalityComparator = equalityComparator;
	}

	@Override public final EqualityComparator<? super E> getEqualityComparator()
	{
		return equalityComparator;
	}

	@Override public final boolean equals( Object other )
	{
		if( other == this )
			return true;
		if( other == null )
			return false;
		if( other instanceof UnmodifiableCollection )
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableCollection<E> otherAsUnmodifiableCollection = (UnmodifiableCollection<E>)other;
			return equalsUnmodifiableCollection( otherAsUnmodifiableCollection );
		}
		assert false;
		return false;
	}
}
