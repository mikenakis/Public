package mikenakis.testana.kit.structured.json.writing;

import mikenakis.testana.kit.structured.json.JsonWriter;
import mikenakis.testana.kit.structured.writing.ArrayWriter;
import mikenakis.testana.kit.structured.writing.ObjectWriter;
import mikenakis.testana.kit.structured.writing.StructuredWriter;

import java.util.Optional;
import java.util.function.Consumer;

public class JsonStructuredWriter implements StructuredWriter
{
	private final JsonWriter jsonWriter;
	private final JsonWriter.Mode mode;
	private boolean done;

	public JsonStructuredWriter( JsonWriter jsonWriter, JsonWriter.Mode mode )
	{
		this.jsonWriter = jsonWriter;
		this.mode = mode;
	}

	@Override public void writeValue( String value )
	{
		assert !done;
		jsonWriter.emitString( value, mode );
		done = true;
	}

	@Override public void writeOptionalValue( Optional<String> value )
	{
		assert !done;
		if( value.isEmpty() )
			jsonWriter.emitNull( mode );
		else
			jsonWriter.emitString( value.get(), mode );
		done = true;
	}

	@Override public void writeObject( Consumer<ObjectWriter> objectWriterConsumer )
	{
		assert !done;
		jsonWriter.emitObjectBegin();
		int depth = jsonWriter.depth();
		JsonObjectWriter objectWriter = new JsonObjectWriter( jsonWriter );
		objectWriterConsumer.accept( objectWriter );
		assert jsonWriter.depth() == depth;
		jsonWriter.emitObjectEnd();
		done = true;
	}

	@Override public void writeArray( String elementName, Consumer<ArrayWriter> arrayWriterConsumer )
	{
		assert !done;
		jsonWriter.emitArrayBegin();
		int depth = jsonWriter.depth();
		JsonArrayWriter arrayWriter = new JsonArrayWriter( jsonWriter );
		arrayWriterConsumer.accept( arrayWriter );
		assert jsonWriter.depth() == depth;
		jsonWriter.emitArrayEnd();
		done = true;
	}
}
