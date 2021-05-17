package mikenakis.testana.kit.structured.json.writing;

import mikenakis.testana.kit.structured.json.JsonEmitter;
import mikenakis.testana.kit.structured.writing.ArrayWriter;
import mikenakis.testana.kit.structured.writing.StructuredWriter;

import java.util.function.Consumer;

public class JsonArrayWriter implements ArrayWriter
{
	private boolean first = true;
	private final JsonEmitter jsonEmitter;

	JsonArrayWriter( JsonEmitter jsonEmitter )
	{
		this.jsonEmitter = jsonEmitter;
	}

	@Override public void writeElement( Consumer<StructuredWriter> structuredWriterConsumer )
	{
		if( first )
			first = false;
		else
			jsonEmitter.emitComma();
		JsonStructuredWriter arrayElementWriter = new JsonStructuredWriter( jsonEmitter, JsonEmitter.Mode.Array );
		structuredWriterConsumer.accept( arrayElementWriter );
	}
}
