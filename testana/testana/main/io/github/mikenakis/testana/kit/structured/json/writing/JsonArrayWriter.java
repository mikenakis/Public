package io.github.mikenakis.testana.kit.structured.json.writing;

import io.github.mikenakis.testana.kit.structured.writing.ArrayWriter;
import io.github.mikenakis.testana.kit.structured.writing.StructuredWriter;
import io.github.mikenakis.testana.kit.structured.json.JsonWriter;

import java.util.function.Consumer;

public class JsonArrayWriter implements ArrayWriter
{
	private boolean first = true;
	private final JsonWriter jsonWriter;

	JsonArrayWriter( JsonWriter jsonWriter )
	{
		this.jsonWriter = jsonWriter;
	}

	@Override public void writeElement( Consumer<StructuredWriter> structuredWriterConsumer )
	{
		if( first )
			first = false;
		else
			jsonWriter.emitComma();
		StructuredWriter arrayElementWriter = new JsonStructuredWriter( jsonWriter, JsonWriter.Mode.Array );
		structuredWriterConsumer.accept( arrayElementWriter );
	}
}
