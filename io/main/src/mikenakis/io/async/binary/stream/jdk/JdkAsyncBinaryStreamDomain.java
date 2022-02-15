package mikenakis.io.async.binary.stream.jdk;

import mikenakis.io.async.binary.stream.AsyncBinaryStreamDomain;
import mikenakis.io.async.binary.stream.reading.CloseableAsyncBinaryStreamReader;
import mikenakis.io.async.binary.stream.writing.CloseableAsyncBinaryStreamWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.Coherent;

import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * Java File System {@link AsyncBinaryStreamDomain}.
 *
 * @author michael.gr
 */
public final class JdkAsyncBinaryStreamDomain extends Coherent implements AsyncBinaryStreamDomain.Defaults
{
	private static final Set<StandardOpenOption> READER_OPTIONS = EnumSet.of( StandardOpenOption.READ );
	private static final Set<StandardOpenOption> WRITER_OPTIONS = EnumSet.of( StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING );

	private final BufferAllocator bufferAllocator;

	public JdkAsyncBinaryStreamDomain( Coherence coherence, BufferAllocator bufferAllocator )
	{
		super( coherence );
		this.bufferAllocator = bufferAllocator;
	}

	@Override public BufferAllocator getBufferAllocator()
	{
		return bufferAllocator;
	}

	@Override public CloseableAsyncBinaryStreamReader newReaderOnAsynchronousByteChannel( AsynchronousByteChannel asynchronousByteChannel, boolean handOff )
	{
		return new AsyncBinaryStreamReaderOnAsynchronousByteChannel( getCoherence(), asynchronousByteChannel, handOff );
	}

	@Override public CloseableAsyncBinaryStreamWriter newWriterOnAsynchronousByteChannel( AsynchronousByteChannel asynchronousFileChannel, boolean handOff )
	{
		return new AsyncBinaryStreamWriterOnAsynchronousByteChannel( getCoherence(), asynchronousFileChannel, handOff );
	}

	private ExecutorService executorService()
	{
		return ForkJoinPool.commonPool(); //according to https://stackoverflow.com/a/52277821/773113
//		ThreadPool threadPool = MachineDomain.instance().getThreadingDomain().getMainThreadPool();
//		return ((JvmThreadPool)threadPool).executorService;
	}

	@Override public CloseableAsyncBinaryStreamReader newReaderOnPath( Path path )
	{
		assert Kit.path.isAbsoluteNormalized( path );
		ExecutorService executorService = executorService();
		AsynchronousFileChannel asynchronousFileChannel = Kit.unchecked( () -> AsynchronousFileChannel.open( path, READER_OPTIONS, executorService ) );
		return newReaderOnAsynchronousFileChannel( asynchronousFileChannel );
	}

	@Override public CloseableAsyncBinaryStreamWriter newWriterOnPath( Path path )
	{
		assert Kit.path.isAbsoluteNormalized( path );
		Kit.unchecked( () -> Files.createDirectories( path.getParent() ) ); //Note: ideally, the AsynchronousFileChannel.open() method would be doing this, but it has no provision for doing such a thing.
		ExecutorService executorService = executorService();
		AsynchronousFileChannel asynchronousFileChannel = Kit.unchecked( () -> AsynchronousFileChannel.open( path, WRITER_OPTIONS, executorService ) );
		return newWriterOnAsynchronousFileChannel( asynchronousFileChannel );
	}

	private CloseableAsyncBinaryStreamReader newReaderOnAsynchronousFileChannel( AsynchronousFileChannel asynchronousFileChannel )
	{
		AsynchronousByteChannel asynchronousByteChannel = new AsynchronousByteChannelOnAsynchronousFileChannel( mutationContext, asynchronousFileChannel, true, 0L );
		return new AsyncBinaryStreamReaderOnAsynchronousByteChannel( getCoherence(), asynchronousByteChannel, true );
	}

	private CloseableAsyncBinaryStreamWriter newWriterOnAsynchronousFileChannel( AsynchronousFileChannel asynchronousFileChannel )
	{
		AsynchronousByteChannel asynchronousByteChannel = new AsynchronousByteChannelOnAsynchronousFileChannel( mutationContext, asynchronousFileChannel, true, 0L );
		return new AsyncBinaryStreamWriterOnAsynchronousByteChannel( getCoherence(), asynchronousByteChannel, true );
	}
}
