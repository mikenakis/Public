package mikenakis.io.sync.binary.stream.jdk;

import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.io.sync.binary.stream.reading.BinaryStreamReadingDomain;
import mikenakis.kit.Kit;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.buffers.BufferAllocation;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.buffers.BufferKey;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Java File System {@link BinaryStreamReadingDomain}.
 *
 * @author michael.gr
 */
public final class JdkBinaryStreamReadingDomain extends Mutable implements BinaryStreamReadingDomain.Defaults
{
	public static final BufferKey binaryComparatorBufferKey = new BufferKey( JdkBinaryStreamReadingDomain.class.getName() + ".binaryComparison" );
	public static final BufferKey readUntilEndBufferKey = new BufferKey( JdkBinaryStreamReadingDomain.class.getName() + ".readUntilEnd()" );

	private final BufferAllocator bufferAllocator;

	public JdkBinaryStreamReadingDomain( MutationContext mutationContext, BufferAllocator bufferAllocator )
	{
		super( mutationContext );
		this.bufferAllocator = bufferAllocator;
	}

	@Override public CloseableWrapper<BinaryStreamReader> newReaderOnInputStream( InputStream inputStream, Procedure0 onClose )
	{
		return new BinaryStreamReaderOnInputStream( mutationContext, inputStream, onClose );
	}

	@Override public CloseableWrapper<BinaryStreamReader> newReaderOnPath( Path path )
	{
		assert Kit.path.isAbsoluteNormalized( path );
		InputStream inputStream = Kit.unchecked( () -> Files.newInputStream( path, StandardOpenOption.READ ) );
		return new BinaryStreamReaderOnInputStream( mutationContext, inputStream, () -> Kit.unchecked( inputStream::close ) );
	}

	@Override public boolean compare( BinaryStreamReader reader1, BinaryStreamReader reader2 )
	{
		try( BufferAllocation bufferAllocation1 = bufferAllocator.newBufferAllocation( binaryComparatorBufferKey );
		     BufferAllocation bufferAllocation2 = bufferAllocator.newBufferAllocation( binaryComparatorBufferKey ) )
		{
			for( ; ; )
			{
				int count1 = reader1.readBuffer( bufferAllocation1.bytes );
				int count2 = reader2.readBuffer( bufferAllocation2.bytes );
				if( count1 != count2 )
					return false;
				if( count1 <= 0 )
					break;
				if( Kit.bytes.compare( bufferAllocation1.bytes, 0, bufferAllocation2.bytes, 0, count1 ) != 0 )
					return false;
			}
			return true;
		}
	}

	@Override public Buffer readAllFromPath( Path path )
	{
		return Kit.tryGetWithWrapper( newReaderOnPath( path ), reader ->
		{
			int bufferSize = bufferAllocator.getBufferSize( readUntilEndBufferKey );
			byte[] buffer = new byte[bufferSize];
			byte[] bytes = reader.readUntilEnd( buffer );
			return Buffer.of( bytes );
		} );
	}
}
