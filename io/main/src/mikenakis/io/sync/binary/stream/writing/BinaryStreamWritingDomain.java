package mikenakis.io.sync.binary.stream.writing;

import mikenakis.kit.Kit;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure0;
import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.kit.buffers.BufferKey;
import mikenakis.kit.lifetime.CloseableWrapper;

import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Synchronous Binary Stream Domain.
 *
 * @author michael.gr
 */
public interface BinaryStreamWritingDomain
{
	BufferKey binaryCopierBufferKey = new BufferKey( BinaryStreamWritingDomain.class.getName() + ".binaryCopy" );

	CloseableWrapper<BinaryStreamWriter> newWriterOnOutputStream( OutputStream outputStream, Procedure0 onClose );

	CloseableWrapper<BinaryStreamWriter> newWriterOnPath( Path path );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	long copy( BinaryStreamReader reader, BinaryStreamWriter writer );

	long copyToPath( BinaryStreamReader reader, Path path );

	void writeAllToPath( Path path, Buffer buffer );

	interface Defaults extends BinaryStreamWritingDomain
	{
		@Override default long copyToPath( BinaryStreamReader reader, Path path )
		{
			return Kit.tryGetWithWrapper( newWriterOnPath( path ), writer -> copy( reader, writer ) );
		}

		@Override default void writeAllToPath( Path path, Buffer buffer )
		{
			Kit.tryWithWrapper( newWriterOnPath( path ), writer -> writer.writeBytes( buffer.getBytes() ) );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Decorator extends Defaults
	{
		BinaryStreamWritingDomain getDecoratedBinaryStreamWritingDomain();

		@Override default CloseableWrapper<BinaryStreamWriter> newWriterOnOutputStream( OutputStream outputStream, Procedure0 onClose )
		{
			BinaryStreamWritingDomain decoree = getDecoratedBinaryStreamWritingDomain();
			return decoree.newWriterOnOutputStream( outputStream, onClose );
		}

		@Override default CloseableWrapper<BinaryStreamWriter> newWriterOnPath( Path path )
		{
			BinaryStreamWritingDomain decoree = getDecoratedBinaryStreamWritingDomain();
			return decoree.newWriterOnPath( path );
		}
	}
}
