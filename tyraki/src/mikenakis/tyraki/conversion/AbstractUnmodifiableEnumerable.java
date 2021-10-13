package mikenakis.tyraki.conversion;

import mikenakis.kit.Kit;
import mikenakis.tyraki.UnmodifiableEnumerable;

/**
 * Base class in this package for classes implementing {@link UnmodifiableEnumerable}.
 *
 * @param <E> the type of the elements.
 *
 * @author michael.gr
 */
abstract class AbstractUnmodifiableEnumerable<E> implements UnmodifiableEnumerable.Defaults<E>
{
	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof UnmodifiableEnumerable )
			return equalsUnmodifiableEnumerable( Kit.upCast( other ) );
		throw new AssertionError(); //does this ever happen?
	}

	@Override public int hashCode()
	{
		return calculateHashCode();
	}
}
