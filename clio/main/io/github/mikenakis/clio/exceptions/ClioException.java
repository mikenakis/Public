package io.github.mikenakis.clio.exceptions;

import io.github.mikenakis.clio.Clio;

/**
 * {@link Clio} {@link Exception}.
 *
 * @author michael.gr
 */
public abstract class ClioException extends RuntimeException
{
	protected ClioException()
	{
	}

	protected ClioException( Throwable cause )
	{
		super( cause );
	}
}
