package io.github.mikenakis.tyraki;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Abstract {@link UnmodifiableEnumerator}.
 *
 * @author michael.gr
 */
public abstract class AbstractEnumerator<E> implements UnmodifiableEnumerator.Defaults<E>
{
	protected AbstractEnumerator()
	{
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		var builder = new StringBuilder();
		unmodifiableEnumeratorToStringBuilder( builder );
		return builder.toString();
	}
}
