package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.tyraki.UnmodifiableEnumerable;

/**
 * Base class in this package for classes implementing {@link UnmodifiableEnumerable}.
 *
 * @param <E> the type of the elements.
 *
 * @author michael.gr
 */
abstract class AbstractUnmodifiableEnumerable<E> implements UnmodifiableEnumerable.Defaults<E>
{
	protected AbstractUnmodifiableEnumerable()
	{
	}

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof UnmodifiableEnumerable )
			return equalsEnumerable( Kit.upCast( other ) );
		throw new AssertionError(); //does this ever happen?
	}

	@Override public int hashCode()
	{
		return calculateHashCode();
	}
}
