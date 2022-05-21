package io.github.mikenakis.testana.kit.structured.json.reading;

import io.github.mikenakis.testana.kit.structured.json.JsonReader;
import io.github.mikenakis.testana.kit.structured.reading.ArrayReader;
import io.github.mikenakis.testana.kit.structured.reading.StructuredReader;

import java.util.function.Consumer;

public class JsonArrayReader implements ArrayReader
{
	private final JsonReader jsonReader;
	private boolean done;

	JsonArrayReader( JsonReader jsonReader )
	{
		this.jsonReader = jsonReader;
	}

	@Override public void readElements( Consumer<StructuredReader> structuredReaderConsumer )
	{
		assert !done;
		for( boolean first = true; !jsonReader.isArrayEnd(); )
		{
			if( first )
				first = false;
			else
				jsonReader.skip( JsonReader.TokenType.Comma );
			StructuredReader arrayElementReader = new JsonStructuredReader( jsonReader );
			structuredReaderConsumer.accept( arrayElementReader );
		}
		done = true;
	}
}
