package io.github.mikenakis.buffer;

import java.io.OutputStream;

public final class BufferBuilderOutputStream extends OutputStream
{
	private final BufferBuilder bufferBuilder;

	public BufferBuilderOutputStream( BufferBuilder bufferBuilder )
	{
		this.bufferBuilder = bufferBuilder;
	}

	@Override public void write( int b )
	{
		bufferBuilder.appendByte( (byte)b );
	}

	@Override public void write( byte[] bytes, int off, int len )
	{
		bufferBuilder.appendText( bytes, off, len );
	}

	@Override public String toString()
	{
		return bufferBuilder.toString();
	}

	@Override public void close()
	{
	}
}
