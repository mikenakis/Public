package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * An {@link UnmodifiableEnumerator} which invokes a {@link Function0} to obtain elements until it returns {@code null}.
 *
 * @param <E> the type of the elements.
 *
 * @author michael.gr
 */
final class EnumeratorOnFunction<E> extends AbstractUnmodifiableEnumerator<E>
{
	private final Function1<Optional<E>,E> nextElementProducer;
	private Optional<E> current;

	EnumeratorOnFunction( E firstElement, Function1<Optional<E>,E> nextElementProducer )
	{
		current = Optional.of( firstElement );
		this.nextElementProducer = nextElementProducer;
	}

	@Override public boolean isFinished()
	{
		return current.isEmpty();
	}

	@Override public E getCurrent()
	{
		return current.orElseThrow();
	}

	@Override public UnmodifiableEnumerator<E> moveNext()
	{
		current = nextElementProducer.invoke( current.orElseThrow() );
		return this;
	}
}
