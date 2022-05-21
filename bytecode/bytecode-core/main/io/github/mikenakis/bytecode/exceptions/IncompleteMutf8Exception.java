package io.github.mikenakis.bytecode.exceptions;

import io.github.mikenakis.kit.UncheckedException;

/**
 * "Incomplete MUTF8" exception.
 *
 * @author michael.gr
 */
public final class IncompleteMutf8Exception extends UncheckedException
{
	public final int position;

	public IncompleteMutf8Exception( int position )
	{
		this.position = position;
	}
}
