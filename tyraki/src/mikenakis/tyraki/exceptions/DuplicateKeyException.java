package mikenakis.tyraki.exceptions;

import mikenakis.kit.UncheckedException;

/**
 * 'Duplicate Key' {@link UncheckedException}.
 *
 * @author michael.gr
 */
public class DuplicateKeyException extends UncheckedException
{
	public final Object key;
	public final Object oldValue;
	public final Object newValue;

	public DuplicateKeyException( Object key, Object newValue, Object oldValue )
	{
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
}
