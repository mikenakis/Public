package io.github.mikenakis.kit.exceptions;

public class OffsetMustNotExceedSizeException extends UncheckedException
{
	public final int offset;
	public final int size;

	public OffsetMustNotExceedSizeException( int offset, int size )
	{
		this.offset = offset;
		this.size = size;
	}
}
