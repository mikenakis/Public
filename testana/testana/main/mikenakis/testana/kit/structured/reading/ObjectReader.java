package mikenakis.testana.kit.structured.reading;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ObjectReader
{
	@SuppressWarnings( "overloads" ) <T> T readMember( String memberName, Function<StructuredReader,T> structuredReaderConsumer );

	@SuppressWarnings( "overloads" ) default void readMember( String memberName, Consumer<StructuredReader> structuredReaderConsumer )
	{
		Object result = readMember( memberName, structuredReader -> {
			structuredReaderConsumer.accept( structuredReader );
			return null;
		} );
		assert result == null;
	}
}
