package mikenakis.io.sync.binary.stream.reading;

import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure0;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Synchronous Binary Stream Domain.
 *
 * @author michael.gr
 */
public interface BinaryStreamReadingDomain
{
	CloseableBinaryStreamReader newReaderOnInputStream( InputStream inputStream, Procedure0 onClose );

	CloseableBinaryStreamReader newReaderOnPath( Path path );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	boolean compare( BinaryStreamReader reader1, BinaryStreamReader reader2 );

	boolean compareToPath( BinaryStreamReader reader, Path path );

	Buffer readAllFromPath( Path path );

	interface Defaults extends BinaryStreamReadingDomain
	{
		@Override default boolean compareToPath( BinaryStreamReader reader, Path path )
		{
			try( CloseableBinaryStreamReader reader2 = newReaderOnPath( path ) )
			{
				return compare( reader, reader2 );
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Decorator extends Defaults
	{
		BinaryStreamReadingDomain getDecoratedBinaryStreamReadingDomain();

		@Override default CloseableBinaryStreamReader newReaderOnInputStream( InputStream inputStream, Procedure0 onClose )
		{
			BinaryStreamReadingDomain decoree = getDecoratedBinaryStreamReadingDomain();
			return decoree.newReaderOnInputStream( inputStream, onClose );
		}

		@Override default CloseableBinaryStreamReader newReaderOnPath( Path path )
		{
			BinaryStreamReadingDomain decoree = getDecoratedBinaryStreamReadingDomain();
			return decoree.newReaderOnPath( path );
		}
	}
}
