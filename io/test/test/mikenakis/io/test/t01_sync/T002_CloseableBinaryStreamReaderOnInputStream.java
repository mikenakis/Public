package mikenakis.io.test.t01_sync;

import mikenakis.io.sync.binary.stream.jdk.JdkBinaryStreamReadingDomain;
import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.io.sync.binary.stream.reading.BinaryStreamReadingDomain;
import mikenakis.kit.Kit;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Test.
 *
 * @author michael.gr
 */
public class T002_CloseableBinaryStreamReaderOnInputStream extends BinaryStreamReaderTest
{
	private final MutationContext mutationContext = SingleThreadedMutationContext.instance();
	private final BufferAllocator bufferAllocator = BufferAllocator.of( mutationContext, 65536 );
	private final BinaryStreamReadingDomain streamReadingDomain = new JdkBinaryStreamReadingDomain( mutationContext, bufferAllocator );

	public T002_CloseableBinaryStreamReaderOnInputStream()
	{
	}

	@Override protected CloseableWrapper<BinaryStreamReader> newReader( String content )
	{
		byte[] bytes = content.getBytes( StandardCharsets.UTF_8 );
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( bytes );
		return streamReadingDomain.newReaderOnInputStream( byteArrayInputStream, () -> Kit.unchecked( byteArrayInputStream::close ) );
	}
}
