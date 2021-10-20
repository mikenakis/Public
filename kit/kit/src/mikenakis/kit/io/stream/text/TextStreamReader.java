package mikenakis.kit.io.stream.text;

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
