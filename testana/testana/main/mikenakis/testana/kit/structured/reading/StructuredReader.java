package mikenakis.testana.kit.structured.reading;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface StructuredReader
{
	String readValue();

	Optional<String> readOptionalValue();

	<T> T readObject( Function<ObjectReader,T> objectReaderConsumer );

	<T> T readArray( String elementName, Function<ArrayReader,T> arrayReaderConsumer );

	default void readObject( Consumer<ObjectReader> objectReaderConsumer )
	{
		Object result = readObject( arrayReader -> { objectReaderConsumer.accept( arrayReader ); return null; } );
		assert result == null;
	}

	default void readArray( String elementName, Consumer<ArrayReader> arrayReaderConsumer )
	{
		Object result = readArray( elementName, arrayReader -> { arrayReaderConsumer.accept( arrayReader ); return null; } );
		assert result == null;
	}
}
