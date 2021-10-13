package mikenakis.tyraki.conversion;

import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.function.Predicate;

/**
 * A decorator of {@link UnmodifiableEnumerable} which filters items using a {@link Predicate}.
 *
 * @param <E> the type of elements yielded by the {@link UnmodifiableEnumerable}
 *
 * @author michael.gr
 */
class FilteringEnumerable<E> extends AbstractUnmodifiableEnumerable<E>
{
	private final UnmodifiableEnumerable<E> enumerableToFilter;
	final Predicate<? super E> predicate;

	FilteringEnumerable( UnmodifiableEnumerable<E> enumerableToFilter, Predicate<? super E> predicate )
	{
		this.enumerableToFilter = enumerableToFilter;
		this.predicate = predicate;
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		UnmodifiableEnumerator<E> enumeratorToFilter = enumerableToFilter.newUnmodifiableEnumerator();
		return new FilteringEnumerator<>( enumeratorToFilter, predicate );
	}

	@Override public int getModificationCount()
	{
		return enumerableToFilter.getModificationCount();
	}

	@Override public boolean isFrozen()
	{
		return enumerableToFilter.isFrozen();
	}
}
