package io.github.mikenakis.kit.exceptions;

public class OffsetMustBeNonNegativeException extends UncheckedException
{
	public final int offset;

	public OffsetMustBeNonNegativeException( int offset )
	{
		this.offset = offset;
	}
}
