package mikenakis.testana.kit.structured.json.reading;

import mikenakis.testana.kit.structured.json.JsonEmitter;
import mikenakis.testana.kit.structured.json.JsonParser;
import mikenakis.testana.kit.structured.reading.ArrayReader;
import mikenakis.testana.kit.structured.reading.StructuredReader;

import java.util.function.Consumer;

public class JsonArrayReader implements ArrayReader
{
	private final JsonParser jsonParser;
	private boolean done;

	JsonArrayReader( JsonParser jsonParser )
	{
		this.jsonParser = jsonParser;
	}

	@Override public void readElements( Consumer<StructuredReader> structuredReaderConsumer )
	{
		assert !done;
		for( boolean first = true; !jsonParser.isArrayEnd(); )
		{
			if( first )
				first = false;
			else
				jsonParser.skip( JsonParser.TokenType.Comma );
			JsonStructuredReader arrayElementReader = new JsonStructuredReader( jsonParser, JsonEmitter.Mode.Array );
			structuredReaderConsumer.accept( arrayElementReader );
		}
		done = true;
	}
}
