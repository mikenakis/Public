package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.functional.Function0;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * An {@link UnmodifiableEnumerator} which invokes a {@link Function0} to obtain elements until it returns {@link Optional#empty()}.
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

	@Override public E current()
	{
		return current.orElseThrow();
	}

	@Override public UnmodifiableEnumerator<E> moveNext()
	{
		current = nextElementProducer.invoke( current.orElseThrow() );
		return this;
	}
}
