package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.tyraki.DebugView;
import io.github.mikenakis.tyraki.UnmodifiableEnumerable;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

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
	@SuppressWarnings( { "unused", "FieldNamingConvention" } )
	private final DebugView _debugView = DebugView.create( this::toList );
	private final UnmodifiableEnumerable<E> enumerable;
	final Predicate<? super E> predicate;

	FilteringEnumerable( UnmodifiableEnumerable<E> enumerable, Predicate<? super E> predicate )
	{
		this.enumerable = enumerable;
		this.predicate = predicate;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return enumerable.mustBeImmutableAssertion();
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		UnmodifiableEnumerator<E> enumeratorToFilter = enumerable.newUnmodifiableEnumerator();
		return new FilteringEnumerator<>( enumeratorToFilter, predicate );
	}

	@Override public int getModificationCount()
	{
		return enumerable.getModificationCount();
	}

	@Override public Coherence coherence()
	{
		return enumerable.coherence();
	}
}
