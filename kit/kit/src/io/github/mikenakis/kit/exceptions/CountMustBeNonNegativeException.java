package io.github.mikenakis.kit.exceptions;

public class CountMustBeNonNegativeException extends UncheckedException
{
	public final int count;

	public CountMustBeNonNegativeException( int count )
	{
		this.count = count;
	}
}
