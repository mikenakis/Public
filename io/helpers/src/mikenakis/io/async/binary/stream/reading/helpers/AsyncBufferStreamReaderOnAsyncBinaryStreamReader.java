package mikenakis.io.async.binary.stream.reading.helpers;

import mikenakis.io.async.binary.stream.reading.AsyncBinaryStreamReader;
import mikenakis.kit.Kit;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

import java.util.Optional;

public class AsyncBufferStreamReaderOnAsyncBinaryStreamReader implements AsyncBufferStreamReader, Closeable.Defaults
{
	public static AsyncBufferStreamReaderOnAsyncBinaryStreamReader of( AsyncBinaryStreamReader binaryStreamReader, //
		Procedure1<Throwable> errorHandler )
	{
		return new AsyncBufferStreamReaderOnAsyncBinaryStreamReader( binaryStreamReader, errorHandler );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final AsyncBinaryStreamReader binaryStreamReader;
	private final Procedure1<Throwable> errorHandler;

	private AsyncBufferStreamReaderOnAsyncBinaryStreamReader( AsyncBinaryStreamReader binaryStreamReader, //
		Procedure1<Throwable> errorHandler )
	{
		this.binaryStreamReader = binaryStreamReader;
		this.errorHandler = errorHandler;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		lifeGuard.close();
	}

	@Override public boolean isBusy()
	{
		return binaryStreamReader.isBusy();
	}

	@Override public void readBuffer( Procedure1<Optional<Buffer>> receiver )
	{
		binaryStreamReader.read( 4, optionalLengthBuffer ->
		{
			if( optionalLengthBuffer.isEmpty() )
			{
				receiver.invoke( Optional.empty() );
				return;
			}
			int length = intFromBuffer( optionalLengthBuffer.get() );
			binaryStreamReader.read( length, receiver, errorHandler );
		}, errorHandler );
	}

	private static int intFromBuffer( Buffer buffer )
	{
		return Kit.bytes.intFromBytes( buffer.getBytes() );
	}
}
