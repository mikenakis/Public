package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.UnmodifiableList;

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
