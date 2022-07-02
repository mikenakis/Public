package io.github.mikenakis.kit.exceptions;

public class LengthMustBeNonNegativeException extends UncheckedException
{
	public final int length;

	public LengthMustBeNonNegativeException( int length )
	{
		this.length = length;
	}
}
