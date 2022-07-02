package io.github.mikenakis.kit.exceptions;

public class LengthMustBePositiveException extends UncheckedException
{
	public final int length;

	public LengthMustBePositiveException( int length )
	{
		this.length = length;
	}
}
