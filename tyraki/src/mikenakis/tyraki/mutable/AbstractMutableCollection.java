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
		return other instanceof UnmodifiableCollection<?> kin && equals( kin );
	}

	public final boolean equals( UnmodifiableCollection<?> other )
	{
		@SuppressWarnings( "unchecked" ) UnmodifiableCollection<E> otherAsUnmodifiableCollection = (UnmodifiableCollection<E>)other;
		return equalsCollection( otherAsUnmodifiableCollection );
	}
}
