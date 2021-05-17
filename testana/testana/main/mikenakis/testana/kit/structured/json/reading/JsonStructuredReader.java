package mikenakis.testana.kit.structured.json.reading;

import mikenakis.kit.functional.Function1;
import mikenakis.testana.kit.structured.json.JsonEmitter;
import mikenakis.testana.kit.structured.json.JsonParser;
import mikenakis.testana.kit.structured.reading.ArrayReader;
import mikenakis.testana.kit.structured.reading.ObjectReader;
import mikenakis.testana.kit.structured.reading.StructuredReader;

import java.util.Optional;

public class JsonStructuredReader implements StructuredReader
{
	private final JsonParser jsonParser;
	private final JsonEmitter.Mode mode;
	private boolean done;

	public JsonStructuredReader( JsonParser jsonParser, JsonEmitter.Mode mode )
	{
		this.jsonParser = jsonParser;
		this.mode = mode;
	}

	@Override public String readValue()
	{
		assert !done;
		return jsonParser.skip( JsonParser.TokenType.String );
	}

	@Override public Optional<String> readOptionalValue()
	{
		assert !done;
		if( jsonParser.isNull() )
		{
			String content = jsonParser.skip( JsonParser.TokenType.Null );
			assert content.equals( "null" );
			return Optional.empty();
		}
		return Optional.of( jsonParser.skip( JsonParser.TokenType.String ) );
	}

	@Override public <T> T readObject( Function1<T,ObjectReader> objectReaderConsumer )
	{
		assert !done;
		jsonParser.skip( JsonParser.TokenType.ObjectBegin );
		int depth = jsonParser.depth();
		ObjectReader objectReader = new JsonObjectReader( jsonParser );
		T result = objectReaderConsumer.invoke( objectReader );
		assert jsonParser.depth() == depth;
		jsonParser.skip( JsonParser.TokenType.ObjectEnd );
		done = true;
		return result;
	}

	@Override public <T> T readArray( String elementName, Function1<T,ArrayReader> arrayReaderConsumer )
	{
		assert !done;
		jsonParser.skip( JsonParser.TokenType.ArrayBegin );
		int depth = jsonParser.depth();
		ArrayReader arrayReader = new JsonArrayReader( jsonParser );
		T result = arrayReaderConsumer.invoke( arrayReader );
		assert jsonParser.depth() == depth;
		jsonParser.skip( JsonParser.TokenType.ArrayEnd );
		done = true;
		return result;
	}
}
