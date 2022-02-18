package mikenakis.io.test.t01_sync;

import mikenakis.io.sync.binary.stream.reading.helpers.CloseableMemoryBinaryStreamReader;
import mikenakis.kit.functional.Procedure0;
import mikenakis.io.sync.binary.stream.reading.CloseableBinaryStreamReader;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;

import java.nio.charset.StandardCharsets;

/**
 * Test.
 *
 * @author michael.gr
 */
public class T001_ReaderOnBuffer extends BinaryStreamReaderTest
{
	private final MutationContext mutationContext = SingleThreadedMutationContext.instance();

	public T001_ReaderOnBuffer()
	{
	}

	@Override protected CloseableBinaryStreamReader newReader( String content )
	{
		Buffer bytes = Buffer.of( content, StandardCharsets.UTF_8 );
		return CloseableMemoryBinaryStreamReader.of( mutationContext, bytes, Procedure0.noOp );
	}
}
