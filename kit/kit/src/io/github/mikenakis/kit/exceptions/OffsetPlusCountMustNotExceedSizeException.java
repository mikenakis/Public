package io.github.mikenakis.kit.exceptions;

public class OffsetPlusCountMustNotExceedSizeException extends UncheckedException
{
	public final int offset;
	public final int count;
	public final int size;

	public OffsetPlusCountMustNotExceedSizeException( int offset, int count, int size )
	{
		this.offset = offset;
		this.count = count;
		this.size = size;
	}
}
