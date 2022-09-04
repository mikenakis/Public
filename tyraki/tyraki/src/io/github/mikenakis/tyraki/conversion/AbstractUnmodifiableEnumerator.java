package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

/**
 * Base class in this package for classes implementing {@link UnmodifiableEnumerator}.
 *
 * @param <E> the type of the elements.
 *
 * @author michael.gr
 */
abstract class AbstractUnmodifiableEnumerator<E> implements UnmodifiableEnumerator.Defaults<E>
{
	AbstractUnmodifiableEnumerator()
	{
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		var builder = new StringBuilder();
		unmodifiableEnumeratorToStringBuilder( builder );
		return builder.toString();
	}
}
