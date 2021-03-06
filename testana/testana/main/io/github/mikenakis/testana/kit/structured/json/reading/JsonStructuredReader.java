package io.github.mikenakis.testana.kit.structured.json.reading;

import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.testana.kit.structured.json.JsonReader;
import io.github.mikenakis.testana.kit.structured.reading.ArrayReader;
import io.github.mikenakis.testana.kit.structured.reading.ObjectReader;
import io.github.mikenakis.testana.kit.structured.reading.StructuredReader;

import java.util.Optional;

public class JsonStructuredReader implements StructuredReader
{
	private final JsonReader jsonReader;
	private boolean done;

	public JsonStructuredReader( JsonReader jsonReader )
	{
		this.jsonReader = jsonReader;
	}

	@Override public String readValue()
	{
		assert !done;
		return jsonReader.skip( JsonReader.TokenType.String );
	}

	@Override public Optional<String> readOptionalValue()
	{
		assert !done;
		if( jsonReader.isNull() )
		{
			String content = jsonReader.skip( JsonReader.TokenType.Null );
			assert content.equals( "null" );
			return Optional.empty();
		}
		return Optional.of( jsonReader.skip( JsonReader.TokenType.String ) );
	}

	@Override public <T> T readObject( Function1<T,ObjectReader> objectReaderConsumer )
	{
		assert !done;
		jsonReader.skip( JsonReader.TokenType.ObjectBegin );
		int depth = jsonReader.depth();
		ObjectReader objectReader = new JsonObjectReader( jsonReader );
		T result = objectReaderConsumer.invoke( objectReader );
		assert jsonReader.depth() == depth;
		jsonReader.skip( JsonReader.TokenType.ObjectEnd );
		done = true;
		return result;
	}

	@Override public <T> T readArray( String elementName, Function1<T,ArrayReader> arrayReaderConsumer )
	{
		assert !done;
		jsonReader.skip( JsonReader.TokenType.ArrayBegin );
		int depth = jsonReader.depth();
		ArrayReader arrayReader = new JsonArrayReader( jsonReader );
		T result = arrayReaderConsumer.invoke( arrayReader );
		assert jsonReader.depth() == depth;
		jsonReader.skip( JsonReader.TokenType.ArrayEnd );
		done = true;
		return result;
	}
}
