package mikenakis.io.async.binary.stream.writing;

import mikenakis.io.async.Async;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.buffer.Buffer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * {@link AsyncBinaryStreamWriter}.
 *
 * @author michael.gr
 */
public interface AsyncBinaryStreamWriter extends Async
{
	/**
	 * Writes bytes.
	 *
	 * @param buffer the buffer containing the bytes to write.
	 * @param offset the offset within the buffer to start reading the bytes to write.
	 * @param length how many bytes to write.
	 */
	void write( byte[] buffer, int offset, int length, Procedure0 completionHandler, Procedure1<Throwable> errorHandler );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes bytes.
	 *
	 * @param buffer the buffer containing the bytes to write.
	 */
	void write( byte[] buffer, Procedure0 completionHandler, Procedure1<Throwable> errorHandler );

	/**
	 * Writes a buffer.
	 *
	 * @param buffer the text to write.
	 */
	void write( Buffer buffer, Procedure0 completionHandler, Procedure1<Throwable> errorHandler );

	/**
	 * Writes a string in UTF-8.
	 *
	 * @param string the string to write.
	 */
	void write( String string, Procedure0 completionHandler, Procedure1<Throwable> errorHandler );

	/**
	 * Writes a string.
	 *
	 * @param string the string to write.
	 */
	void write( String string, Charset charset, Procedure0 completionHandler, Procedure1<Throwable> errorHandler );

	interface Defaults extends AsyncBinaryStreamWriter, Async.Defaults
	{
		@Override default void write( byte[] buffer, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
		{
			write( buffer, 0, buffer.length, completionHandler, errorHandler );
		}

		@Override default void write( String string, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
		{
			write( string, StandardCharsets.UTF_8, completionHandler, errorHandler );
		}

		@Override default void write( String string, Charset charset, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
		{
			byte[] bytes = string.getBytes( StandardCharsets.UTF_8 );
			write( bytes, 0, bytes.length, completionHandler, errorHandler );
		}

		@Override default void write( Buffer buffer, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
		{
			byte[] bytes = buffer.getBytes();
			write( bytes, 0, bytes.length, completionHandler, errorHandler );
		}
	}
}
