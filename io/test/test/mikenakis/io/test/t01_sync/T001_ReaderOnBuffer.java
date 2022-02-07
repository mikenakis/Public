package mikenakis.io.test.t01_sync;

import mikenakis.kit.io.stream.binary.CloseableBinaryStreamReader;
import mikenakis.io.sync.binary.stream.reading.helpers.BinaryStreamReaderOnBuffer;
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
		return new BinaryStreamReaderOnBuffer( mutationContext, bytes );
	}
}
