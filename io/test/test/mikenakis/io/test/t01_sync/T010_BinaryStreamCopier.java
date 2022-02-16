package mikenakis.io.test.t01_sync;

import mikenakis.io.sync.binary.stream.jdk.JdkBinaryStreamReadingDomain;
import mikenakis.io.sync.binary.stream.jdk.JdkBinaryStreamWritingDomain;
import mikenakis.io.sync.binary.stream.reading.BinaryStreamReadingDomain;
import mikenakis.io.sync.binary.stream.writing.BinaryStreamWritingDomain;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.io.sync.binary.stream.reading.CloseableBinaryStreamReader;
import mikenakis.io.sync.binary.stream.writing.CloseableBinaryStreamWriter;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;
import mikenakis.testkit.TestInfo;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;

/**
 * Test.
 *
 * @author michael.gr
 */
public class T010_BinaryStreamCopier
{
	private final MutationContext mutationContext = SingleThreadedMutationContext.instance();
	private final BufferAllocator bufferAllocator = BufferAllocator.of( mutationContext, 65536 );
	private final BinaryStreamReadingDomain binaryStreamReadingDomain = new JdkBinaryStreamReadingDomain( mutationContext, bufferAllocator );
	private final BinaryStreamWritingDomain binaryStreamWritingDomain = new JdkBinaryStreamWritingDomain( mutationContext, bufferAllocator );

	public T010_BinaryStreamCopier()
	{
	}

	@Test
	public void Small_Test()
	{
		TestInfo testInfo = TestInfo.of( getClass(), TestSourceRootMarker.getTestSourceRoot() );
		bufferAllocator.setBufferSize( JdkBinaryStreamReadingDomain.binaryComparatorBufferKey, 10 );
		test( testInfo, "small", 10 );
	}

	@Ignore //takes too long
	@Test
	public void Long_Test()
	{
		TestInfo testInfo = TestInfo.of( getClass(), TestSourceRootMarker.getTestSourceRoot() );
		bufferAllocator.setBufferSize( JdkBinaryStreamReadingDomain.binaryComparatorBufferKey, 65536 );
		test( testInfo, "large", 100_000_000 );
	}

	private void test( TestInfo testInfo, String suffix, int wordCount )
	{
		Path inPath = testInfo.getTestOutputDataPath().resolve( suffix + "_1.txt" );
		Path outPath = testInfo.getTestTempPath().resolve( suffix + "_2.txt" );
		createRandomBinaryFile( inPath, wordCount );
		try( CloseableBinaryStreamReader reader = binaryStreamReadingDomain.newReaderOnPath( inPath );
		     CloseableBinaryStreamWriter writer = binaryStreamWritingDomain.newWriterOnPath( outPath ) )
		{
			binaryStreamWritingDomain.copy( reader, writer );
		}
		boolean ok = verifyRandomBinaryPath( outPath, wordCount );
		assert ok;
	}

	private void createRandomBinaryFile( Path path, int wordCount )
	{
		BinaryStreamReader reader = new GeneratingBinaryStreamReader( mutationContext, wordCount, 1 );
		binaryStreamWritingDomain.copyToPath( reader, path );
	}

	private boolean verifyRandomBinaryPath( Path path, int wordCount )
	{
		BinaryStreamReader reader = new GeneratingBinaryStreamReader( mutationContext, wordCount, 1 );
		return binaryStreamReadingDomain.compareToPath( reader, path );
	}
}
