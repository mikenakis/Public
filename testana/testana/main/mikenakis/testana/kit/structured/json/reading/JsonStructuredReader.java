package mikenakis.testana.kit.structured.json.reading;

import mikenakis.kit.functional.Function1;
import mikenakis.testana.kit.structured.json.JsonWriter;
import mikenakis.testana.kit.structured.json.JsonReader;
import mikenakis.testana.kit.structured.reading.ArrayReader;
import mikenakis.testana.kit.structured.reading.ObjectReader;
import mikenakis.testana.kit.structured.reading.StructuredReader;

import java.util.Optional;

public class JsonStructuredReader implements StructuredReader
{
	private final JsonReader jsonReader;
	private final JsonWriter.Mode mode;
	private boolean done;

	public JsonStructuredReader( JsonReader jsonReader, JsonWriter.Mode mode )
	{
		this.jsonReader = jsonReader;
		this.mode = mode;
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
