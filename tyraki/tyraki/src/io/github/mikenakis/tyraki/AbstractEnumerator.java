package io.github.mikenakis.tyraki;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Abstract {@link UnmodifiableEnumerator}.
 *
 * @author michael.gr
 */
public abstract class AbstractEnumerator<E> extends AbstractCoherent implements UnmodifiableEnumerator.Defaults<E>
{
	protected AbstractEnumerator( Coherence coherence )
	{
		super( coherence );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return unmodifiableEnumeratorToString();
	}
}
