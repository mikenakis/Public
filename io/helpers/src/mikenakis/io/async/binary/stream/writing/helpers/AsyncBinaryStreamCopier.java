package mikenakis.io.async.binary.stream.writing.helpers;

import mikenakis.io.async.Async;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.mutation.Mutable;
import mikenakis.io.async.binary.stream.reading.AsyncBinaryStreamReader;
import mikenakis.io.async.binary.stream.writing.AsyncBinaryStreamWriter;
import mikenakis.kit.buffers.BufferAllocation;
import mikenakis.kit.buffers.BufferKey;
import mikenakis.kit.mutation.MutationContext;

public final class AsyncBinaryStreamCopier extends Mutable implements Async.Defaults
{
	public static final BufferKey bufferKey = new BufferKey( AsyncBinaryStreamCopier.class.getName() );

	public static Async copy( MutationContext mutationContext, BufferAllocator bufferAllocator, AsyncBinaryStreamReader reader, AsyncBinaryStreamWriter writer, Procedure1<Long> completionHandler, Procedure1<Throwable> errorHandler )
	{
		AsyncBinaryStreamCopier copier = new AsyncBinaryStreamCopier( mutationContext, bufferAllocator, reader, writer, completionHandler, errorHandler );
		assert !copier.isBusy();
		return copier.start();
	}

	private final AsyncBinaryStreamReader reader;
	private final AsyncBinaryStreamWriter writer;
	private final BufferAllocation[] bufferAllocations = new BufferAllocation[2];
	private final Procedure1<Long> completionHandler;
	private final Procedure1<Throwable> errorHandler;
	private long totalCount = 0;
	private int readCount = 0;
	private int writeCount = 0;
	private boolean eof = false;
	private int busyCount = 0;

	private AsyncBinaryStreamCopier( MutationContext mutationContext, BufferAllocator bufferAllocator, AsyncBinaryStreamReader reader, AsyncBinaryStreamWriter writer, Procedure1<Long> completionHandler, Procedure1<Throwable> errorHandler )
	{
		super( mutationContext );
		assert canMutateAssertion();
		assert !reader.isBusy();
		assert !writer.isBusy();
		this.reader = reader;
		this.writer = writer;
		bufferAllocations[0] = bufferAllocator.newBufferAllocation( bufferKey );
		bufferAllocations[1] = bufferAllocator.newBufferAllocation( bufferKey );
		this.completionHandler = completionHandler;
		this.errorHandler = errorHandler;
	}

	@Override public boolean isBusy()
	{
		return busyCount > 0;
	}

	public Async start()
	{
		assert canMutateAssertion();
		assert !isBusy();
		assert !eof;
		assert !reader.isBusy();
		assert !writer.isBusy();
		busyCount++;
		reader.read( bufferAllocations[0].bytes, this::onReadComplete, this::onError );
		return this;
	}

	private void onError( Throwable throwable )
	{
		assert canMutateAssertion();
		assert isBusy();
		stop();
		errorHandler.invoke( throwable );
		busyCount--;
	}

	private void onReadComplete( Integer count )
	{
		assert canMutateAssertion();
		assert isBusy();
		readCount = count;
		if( count <= 0 )
			eof = true;
		nextMove();
	}

	private void onWriteComplete()
	{
		assert canMutateAssertion();
		assert isBusy();
		totalCount += writeCount;
		writeCount = 0;
		nextMove();
	}

	private void nextMove()
	{
		assert isBusy();
		if( writeCount == 0 )
		{
			if( eof )
			{
				stop();
				completionHandler.invoke( totalCount );
				busyCount--;
				return;
			}
			if( readCount > 0 )
			{
				writeCount = readCount;
				readCount = 0;
				BufferAllocation temp = bufferAllocations[0];
				bufferAllocations[0] = bufferAllocations[1];
				bufferAllocations[1] = temp;
				writer.write( bufferAllocations[1].bytes, 0, writeCount, this::onWriteComplete, this::onError );
				reader.read( bufferAllocations[0].bytes, this::onReadComplete, this::onError );
			}
		}
	}

	private void stop()
	{
		assert isBusy();
		bufferAllocations[0].close();
		bufferAllocations[1].close();
	}
}
