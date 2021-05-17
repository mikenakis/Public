package mikenakis.testana.kit.structured.writing;

import java.util.Optional;
import java.util.function.Consumer;

public interface StructuredWriter
{
	void writeValue( String value );

	void writeOptionalValue( Optional<String> value );

	void writeObject( Consumer<ObjectWriter> objectWriterConsumer );

	void writeArray( String elementName, Consumer<ArrayWriter> arrayWriterConsumer );
}
