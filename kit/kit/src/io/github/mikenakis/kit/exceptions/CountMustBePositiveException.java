package io.github.mikenakis.kit.exceptions;

public class CountMustBePositiveException extends UncheckedException
{
	public final int count;

	public CountMustBePositiveException( int count )
	{
		this.count = count;
	}
}
