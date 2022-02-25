package mikenakis.io.async.binary.stream.reading;

import mikenakis.io.async.Async;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.buffers.BufferKey;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.util.Arrays;
import java.util.Optional;

/**
 * {@link AsyncBinaryStreamReader}.
 *
 * @author michael.gr
 */
public interface AsyncBinaryStreamReader extends Async
{
	/**
	 * Reads bytes.
	 *
	 * @param bytes  the buffer to read into.
	 * @param offset the offset within the buffer to start storing bytes.
	 * @param length the number of bytes to read.
	 */
	void read( byte[] bytes, int offset, int length, Procedure1<Integer> completionHandler, Procedure1<Throwable> errorHandler );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reads bytes.
	 *
	 * @param bytes the buffer to read into.
	 */
	void read( byte[] bytes, Procedure1<Integer> completionHandler, Procedure1<Throwable> errorHandler );

	void read( int length, Procedure1<Optional<Buffer>> completionHandler, Procedure1<Throwable> errorHandler );

	void readUntilEnd( BufferAllocator bufferAllocator, Procedure1<Buffer> completionHandler, Procedure1<Throwable> errorHandler );

	void readFull( BufferAllocator bufferAllocator, byte[] bytes, Procedure0 completionHandler, Procedure1<Throwable> errorHandler );

	void readFull( BufferAllocator bufferAllocator, int length, Procedure1<Buffer> completionHandler, Procedure1<Throwable> errorHandler );

	interface Defaults extends AsyncBinaryStreamReader, Async.Defaults
	{
		MutationContext mutationContext();

		@Override default void read( byte[] bytes, Procedure1<Integer> completionHandler, Procedure1<Throwable> errorHandler )
		{
			read( bytes, 0, bytes.length, completionHandler, errorHandler );
		}

		@Override default void read( int length, Procedure1<Optional<Buffer>> completionHandler, Procedure1<Throwable> errorHandler )
		{
			byte[] bytes = new byte[length];
			read( bytes, 0, length, bytesRead ->
			{
				assert mutationContext().canMutateAssertion();
				Optional<Buffer> buffer = bytesRead < 0 ? Optional.empty() : Optional.of( Buffer.of( bytes, 0, bytesRead ) );
				completionHandler.invoke( buffer );
			}, errorHandler );
		}

		BufferKey readUntilEndBufferKey = new BufferKey( AsyncBinaryStreamReader.Defaults.class.getName() + ".readUntilEnd()" );

		final class AsyncReader extends Mutable
		{
			final AsyncBinaryStreamReader reader;
			final int bufferSize; // = getLogicDomain().getBufferAllocator().getBufferSize( readUntilEndBufferKey );
			byte[] bytes;
			final boolean untilTheEnd;
			final Procedure1<byte[]> completionHandler;
			final Procedure1<Throwable> errorHandler;
			int offset = 0;

			AsyncReader( MutationContext mutationContext, AsyncBinaryStreamReader reader, Optional<byte[]> bytes, BufferAllocator bufferAllocator, Procedure1<byte[]> completionHandler,	Procedure1<Throwable> errorHandler )
			{
				super( mutationContext );
				assert canMutateAssertion();
				this.reader = reader;
				this.bufferSize = bufferAllocator.getBufferSize( readUntilEndBufferKey );
				this.completionHandler = completionHandler;
				this.errorHandler = errorHandler;
				untilTheEnd = bytes.isEmpty();
				this.bytes = untilTheEnd ? new byte[bufferSize] : bytes.get();
			}

			void start()
			{
				assert canMutateAssertion();
				int count = bytes.length - offset;
				reader.read( bytes, offset, count, this::onReadCompleted, this::onError );
			}

			private void onReadCompleted( int bytesRead )
			{
				assert canMutateAssertion();
				if( bytesRead <= 0 )
				{
					if( untilTheEnd )
					{
						bytes = Arrays.copyOf( bytes, offset );
						completionHandler.invoke( bytes );
					}
					else
					{
						Exception endOfStreamException = new RuntimeException( "premature end of stream" ); //TODO
						errorHandler.invoke( endOfStreamException );
					}
					return;
				}
				offset += bytesRead;
				if( offset >= bytes.length )
				{
					if( untilTheEnd )
					{
						bytes = Arrays.copyOf( bytes, bytes.length + bufferSize );
						start();
					}
					else
					{
						completionHandler.invoke( bytes );
					}
				}
			}

			private void onError( Throwable throwable )
			{
				assert canMutateAssertion();
				errorHandler.invoke( throwable );
			}
		}

		@Override default void readUntilEnd( BufferAllocator bufferAllocator, Procedure1<Buffer> completionHandler, Procedure1<Throwable> errorHandler )
		{
			assert mutationContext().canMutateAssertion();
			AsyncReader asyncReader = new AsyncReader( mutationContext(), this, Optional.empty(), bufferAllocator, bytes ->
			{
				assert mutationContext().canMutateAssertion();
				Buffer buffer = Buffer.of( bytes );
				completionHandler.invoke( buffer );
			}, errorHandler );
			asyncReader.start();
		}

		@Override default void readFull( BufferAllocator bufferAllocator, byte[] bytes, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
		{
			assert mutationContext().canMutateAssertion();
			AsyncReader asyncReader = new AsyncReader( mutationContext(), this, Optional.of( bytes ), bufferAllocator, bytes2 ->
			{
				assert mutationContext().canMutateAssertion();
				//noinspection ArrayEquality
				assert bytes2 == bytes;
				completionHandler.invoke();
			}, errorHandler );
			asyncReader.start();
		}

		@Override default void readFull( BufferAllocator bufferAllocator, int length, Procedure1<Buffer> completionHandler, Procedure1<Throwable> errorHandler )
		{
			assert mutationContext().canMutateAssertion();
			byte[] bytes = new byte[length];
			AsyncReader asyncReader = new AsyncReader( mutationContext(), this, Optional.of( bytes ), bufferAllocator, bytes2 ->
			{
				assert mutationContext().canMutateAssertion();
				//noinspection ArrayEquality
				assert bytes2 == bytes;
				Buffer buffer = Buffer.of( bytes );
				completionHandler.invoke( buffer );
			}, errorHandler );
			asyncReader.start();
		}
	}
}
