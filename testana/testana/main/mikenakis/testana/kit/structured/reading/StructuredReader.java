package mikenakis.testana.kit.structured.reading;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure1;

import java.util.Optional;

public interface StructuredReader
{
	String readValue();

	Optional<String> readOptionalValue();

	@SuppressWarnings( "overloads" ) <T> T readObject( Function1<T,ObjectReader> objectReaderConsumer );

	@SuppressWarnings( "overloads" ) <T> T readArray( String elementName, Function1<T,ArrayReader> arrayReaderConsumer );

	@SuppressWarnings( "overloads" ) default void readObject( Procedure1<ObjectReader> objectReaderConsumer )
	{
		Object result = readObject( arrayReader -> //
		{
			objectReaderConsumer.invoke( arrayReader );
			return null;
		} );
		assert result == null;
	}

	@SuppressWarnings( "overloads" ) default void readArray( String elementName, Procedure1<ArrayReader> arrayReaderConsumer )
	{
		Object result = readArray( elementName, arrayReader -> //
		{
			arrayReaderConsumer.invoke( arrayReader );
			return null;
		} );
		assert result == null;
	}
}
