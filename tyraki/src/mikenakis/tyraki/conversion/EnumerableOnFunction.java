package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * An {@link UnmodifiableEnumerable} which invokes a {@link Function0} to obtain elements until it returns {@link Optional#empty()}.
 *
 * @param <E> the type of the elements.
 *
 * @author michael.gr
 */
final class EnumerableOnFunction<E> implements UnmodifiableEnumerable.Defaults<E>
{
	private final E firstElement;
	private final Function1<Optional<E>,E> nextElementProducer;

	EnumerableOnFunction( E firstElement, Function1<Optional<E>,E> nextElementProducer )
	{
		this.firstElement = firstElement;
		this.nextElementProducer = nextElementProducer;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return true; //XXX I am not sure whether this should be true or false.
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return new EnumeratorOnFunction<>( firstElement, nextElementProducer );
	}

	@Override public int getModificationCount()
	{
		return 0;
	}
}
