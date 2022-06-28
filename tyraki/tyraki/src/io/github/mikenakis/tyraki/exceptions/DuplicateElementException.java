package io.github.mikenakis.tyraki.exceptions;

import io.github.mikenakis.kit.exceptions.UncheckedException;

/**
 * Duplicate Element {@link UncheckedException}.
 *
 * @author michael.gr
 */
public class DuplicateElementException extends UncheckedException
{
	public final Object element;

	public DuplicateElementException( Object element )
	{
		this.element = element;
	}
}
