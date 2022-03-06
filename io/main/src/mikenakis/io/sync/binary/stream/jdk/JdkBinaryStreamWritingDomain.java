package mikenakis.io.sync.binary.stream.jdk;

import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.io.sync.binary.stream.writing.BinaryStreamWriter;
import mikenakis.io.sync.binary.stream.writing.BinaryStreamWritingDomain;
import mikenakis.kit.Kit;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Java File System {@link BinaryStreamWritingDomain}.
 *
 * @author michael.gr
 */
public final class JdkBinaryStreamWritingDomain extends Mutable implements BinaryStreamWritingDomain.Defaults
{
	public JdkBinaryStreamWritingDomain( MutationContext mutationContext )
	{
		super( mutationContext );
	}

	@Override public CloseableWrapper<BinaryStreamWriter> newWriterOnOutputStream( OutputStream outputStream, Procedure0 onClose )
	{
		return new BinaryStreamWriterOnOutputStream( mutationContext, outputStream, onClose );
	}

	@Override public CloseableWrapper<BinaryStreamWriter> newWriterOnPath( Path path )
	{
		assert Kit.path.isAbsoluteNormalized( path );
		Kit.unchecked( () -> Files.createDirectories( path.getParent() ) );
		OutputStream outputStream = Kit.unchecked( () -> Files.newOutputStream( path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING ) );
		return newWriterOnOutputStream( outputStream, () -> Kit.unchecked( outputStream::close ) );
	}

	@Override public long copy( BinaryStreamReader reader, BinaryStreamWriter writer )
	{
		return Kit.tryGetWith( BufferAllocator.instance().newBufferAllocation( binaryCopierBufferKey ), bufferAllocation -> {
			long totalCount = 0;
			for( ; ; )
			{
				int count = reader.readBuffer( bufferAllocation.bytes );
				if( count <= 0 )
					break;
				writer.writeBytes( bufferAllocation.bytes, 0, count );
				totalCount += count;
			}
			return totalCount;
		} );
	}
}
