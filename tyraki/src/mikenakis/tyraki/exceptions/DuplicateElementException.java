package mikenakis.tyraki.exceptions;

import mikenakis.kit.UncheckedException;

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
