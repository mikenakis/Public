package io.github.mikenakis.tyraki.exceptions;

import io.github.mikenakis.kit.exceptions.UncheckedException;

/**
 * 'Key Not Found' {@link UncheckedException}.
 *
 * @author michael.gr
 */
public class KeyNotFoundException extends UncheckedException
{
	public final Object key;

	public KeyNotFoundException( Object key )
	{
		this.key = key;
	}
}
