package mikenakis.io.sync.binary.stream.writing;

import mikenakis.kit.Kit;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;
import mikenakis.kit.io.stream.binary.CloseableBinaryStreamWriter;
import mikenakis.kit.buffers.BufferKey;

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

	CloseableBinaryStreamWriter newWriterOnOutputStream( OutputStream outputStream, Procedure0 onClose );

	CloseableBinaryStreamWriter newWriterOnPath( Path path );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	long copy( BinaryStreamReader reader, BinaryStreamWriter writer );

	long copyToPath( BinaryStreamReader reader, Path path );

	void writeAllToPath( Path path, Buffer buffer );

	interface Defaults extends BinaryStreamWritingDomain
	{
		@Override default long copyToPath( BinaryStreamReader reader, Path path )
		{
			return Kit.tryGetWith( newWriterOnPath( path ), writer -> copy( reader, writer ) );
		}

		@Override default void writeAllToPath( Path path, Buffer buffer )
		{
			Kit.tryWith( newWriterOnPath( path ), writer -> writer.writeBytes( buffer.getBytes() ) );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Decorator extends Defaults
	{
		BinaryStreamWritingDomain getDecoratedBinaryStreamWritingDomain();

		@Override default CloseableBinaryStreamWriter newWriterOnOutputStream( OutputStream outputStream, Procedure0 onClose )
		{
			BinaryStreamWritingDomain decoree = getDecoratedBinaryStreamWritingDomain();
			return decoree.newWriterOnOutputStream( outputStream, onClose );
		}

		@Override default CloseableBinaryStreamWriter newWriterOnPath( Path path )
		{
			BinaryStreamWritingDomain decoree = getDecoratedBinaryStreamWritingDomain();
			return decoree.newWriterOnPath( path );
		}
	}
}
