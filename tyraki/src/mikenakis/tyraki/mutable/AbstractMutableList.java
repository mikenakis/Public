package mikenakis.tyraki.mutable;

import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.UnmodifiableList;
import mikenakis.kit.EqualityComparator;

/**
 * Abstract {@link UnmodifiableList}.
 *
 * @author michael.gr
 */
abstract class AbstractMutableList<E> extends AbstractMutableCollection<E> implements MutableList.Defaults<E>
{
	protected AbstractMutableList( MutableCollections mutableCollections, EqualityComparator<? super E> equalityComparator )
	{
		super( mutableCollections, equalityComparator );
	}
}
