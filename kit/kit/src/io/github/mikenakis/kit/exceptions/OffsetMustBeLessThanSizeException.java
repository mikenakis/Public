package io.github.mikenakis.kit.exceptions;

public class OffsetMustBeLessThanSizeException extends UncheckedException
{
	public final int offset;
	public final int size;

	public OffsetMustBeLessThanSizeException( int offset, int size )
	{
		this.offset = offset;
		this.size = size;
	}
}
