package mikenakis.io.test.t01_sync;

import mikenakis.io.sync.binary.stream.reading.helpers.BufferingBinaryStreamReader;
import mikenakis.io.sync.binary.stream.reading.helpers.CloseableMemoryBinaryStreamReader;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.io.stream.binary.CloseableBinaryStreamReader;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Test.
 *
 * @author michael.gr
 */
public class T003_BufferingBinaryStreamReader
{
	private static final Buffer LF = Buffer.of( '\n' );

	private final MutationContext mutationContext = SingleThreadedMutationContext.instance();
	private final byte[] bytes = new byte[10];

	public T003_BufferingBinaryStreamReader()
	{
	}

	private BufferingBinaryStreamReader newBufferedReader( String content )
	{
		Buffer buffer = Buffer.of( content, StandardCharsets.UTF_8 );
		CloseableBinaryStreamReader unbufferedReader = CloseableMemoryBinaryStreamReader.create( mutationContext, buffer, Procedure0.noOp );
		return new BufferingBinaryStreamReader( mutationContext, bytes, unbufferedReader, unbufferedReader::close );
	}

	@Test
	public void Reading_From_Empty_Yields_Nothing()
	{
		try( BufferingBinaryStreamReader bufferedReader = newBufferedReader( "" ) )
		{
			Optional<Buffer> line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isEmpty();
		}
	}

	@Test
	public void Reading_From_Single_LF_Terminated_Line_Yields_The_Line()
	{
		try( BufferingBinaryStreamReader bufferedReader = newBufferedReader( "abc\n" ) )
		{
			Optional<Buffer> line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "abc" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isEmpty();
		}
	}

	@Test
	public void Reading_Two_LF_Terminated_Lines_Yields_The_Two_Lines()
	{
		try( BufferingBinaryStreamReader bufferedReader = newBufferedReader( "abc\ndef\n" ) )
		{
			Optional<Buffer> line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "abc" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "def" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isEmpty();
		}
	}

	@Test
	public void Reading_Single_Unterminated_Line_Yields_The_Line()
	{
		try( BufferingBinaryStreamReader bufferedReader = newBufferedReader( "abc" ) )
		{
			Optional<Buffer> line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "abc" ) );
		}
	}

	@Test
	public void Reading_Line_Followed_By_Unterminated_Line_Yields_Both_Lines()
	{
		try( BufferingBinaryStreamReader bufferedReader = newBufferedReader( "abc\ndef" ) )
		{
			Optional<Buffer> line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "abc" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "def" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isEmpty();
		}
	}

	@Test
	public void Reading_Multiple_Lines_Longer_Than_Buffer_Works()
	{
		try( BufferingBinaryStreamReader bufferedReader = newBufferedReader( "part1\npart2\n\npart4\npart5\n\npart7" ) )
		{
			Optional<Buffer> line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "part1" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "part2" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().isEmpty();
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "part4" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "part5" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().isEmpty();
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isPresent();
			assert line.get().equals( Buffer.of( "part7" ) );
			line = bufferedReader.tryReadUntilDelimiter( LF );
			assert line.isEmpty();
		}
	}
}
