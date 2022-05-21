package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.kit.UncheckedException;

/**
 * "Malformed MUTF8" exception.
 *
 * @author michael.gr
 */
public final class MalformedMutf8Exception extends UncheckedException
{
	public final int position;

	public MalformedMutf8Exception( int position )
	{
		this.position = position;
	}
}
