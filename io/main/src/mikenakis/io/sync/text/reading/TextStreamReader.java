package mikenakis.io.sync.text.reading;

import java.util.Optional;

public interface TextStreamReader
{
	Optional<String> tryRead( int length );

	Optional<String> tryReadLine();

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults extends TextStreamReader
	{
	}
}
