package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * An {@link UnmodifiableEnumerable} which invokes a {@link Function0} to obtain elements until it returns {@code null}.
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

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return new EnumeratorOnFunction<>( firstElement, nextElementProducer );
	}

	@Override public int getModificationCount()
	{
		return 0;
	}

	@Override public boolean isFrozen()
	{
		return true; //NOTE: perhaps this should be a parameter, because we cannot guarantee that the function producing next elements will not do anything funky.
	}
}
