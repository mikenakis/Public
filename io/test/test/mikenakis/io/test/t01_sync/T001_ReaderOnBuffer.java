package mikenakis.io.test.t01_sync;

import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.io.sync.binary.stream.reading.helpers.InMemoryBinaryStreamReader;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.ThreadLocalMutationContext;

import java.nio.charset.StandardCharsets;

/**
 * Test.
 *
 * @author michael.gr
 */
public class T001_ReaderOnBuffer extends BinaryStreamReaderTest
{
	private final MutationContext mutationContext = ThreadLocalMutationContext.instance();

	public T001_ReaderOnBuffer()
	{
	}

	@Override protected CloseableWrapper<BinaryStreamReader> newReader( String content )
	{
		Buffer bytes = Buffer.of( content, StandardCharsets.UTF_8 );
		return InMemoryBinaryStreamReader.of( mutationContext, bytes, Procedure0.noOp );
	}
}
