package mikenakis.intertwine.predefined;

import mikenakis.kit.UncheckedException;

/**
 * 'Key Not Found' {@link UncheckedException}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class KeyNotFoundException extends UncheckedException
{
	public final Object key;

	public KeyNotFoundException( Object key )
	{
		this.key = key;
	}
}
