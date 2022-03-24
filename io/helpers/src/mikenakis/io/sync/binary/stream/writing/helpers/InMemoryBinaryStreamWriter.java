package mikenakis.io.sync.binary.stream.writing.helpers;

import mikenakis.io.sync.binary.stream.writing.BinaryStreamWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.buffer.BufferBuilder;

/**
 * A {@link BinaryStreamWriter} which accumulates bytes in a supplied {@link BufferBuilder}.
 *
 * @author michael.gr
 */
public final class InMemoryBinaryStreamWriter implements BinaryStreamWriter.Defaults
{
	public static BinaryStreamWriter of( BufferBuilder bufferBuilder )
	{
		return new InMemoryBinaryStreamWriter( bufferBuilder );
	}

	private final BufferBuilder bufferBuilder;

	private InMemoryBinaryStreamWriter( BufferBuilder bufferBuilder )
	{
		assert bufferBuilder != null;
		this.bufferBuilder = bufferBuilder;
	}

	@Override public void writeBytes( byte[] bytes, int index, int count )
	{
		assert Kit.bytes.validArgumentsAssertion( bytes, index, count );
		bufferBuilder.append( bytes, index, count );
	}

	@Override public String toString()
	{
		return bufferBuilder.toString();
	}
}
