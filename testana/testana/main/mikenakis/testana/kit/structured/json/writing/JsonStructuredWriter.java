package mikenakis.testana.kit.structured.json.writing;

import mikenakis.testana.kit.structured.json.JsonEmitter;
import mikenakis.testana.kit.structured.writing.ArrayWriter;
import mikenakis.testana.kit.structured.writing.ObjectWriter;
import mikenakis.testana.kit.structured.writing.StructuredWriter;

import java.util.Optional;
import java.util.function.Consumer;

public class JsonStructuredWriter implements StructuredWriter
{
	private final JsonEmitter jsonEmitter;
	private final JsonEmitter.Mode mode;
	private boolean done;

	public JsonStructuredWriter( JsonEmitter jsonEmitter, JsonEmitter.Mode mode )
	{
		this.jsonEmitter = jsonEmitter;
		this.mode = mode;
	}

	@Override public void writeValue( String value )
	{
		assert !done;
		jsonEmitter.emitString( value, mode );
		done = true;
	}

	@Override public void writeOptionalValue( Optional<String> value )
	{
		assert !done;
		if( value.isEmpty() )
			jsonEmitter.emitNull( mode );
		else
			jsonEmitter.emitString( value.get(), mode );
		done = true;
	}

	@Override public void writeObject( Consumer<ObjectWriter> objectWriterConsumer )
	{
		assert !done;
		jsonEmitter.emitObjectBegin();
		int depth = jsonEmitter.depth();
		JsonObjectWriter objectWriter = new JsonObjectWriter( jsonEmitter );
		objectWriterConsumer.accept( objectWriter );
		assert jsonEmitter.depth() == depth;
		jsonEmitter.emitObjectEnd();
		done = true;
	}

	@Override public void writeArray( String elementName, Consumer<ArrayWriter> arrayWriterConsumer )
	{
		assert !done;
		jsonEmitter.emitArrayBegin();
		int depth = jsonEmitter.depth();
		JsonArrayWriter arrayWriter = new JsonArrayWriter( jsonEmitter );
		arrayWriterConsumer.accept( arrayWriter );
		assert jsonEmitter.depth() == depth;
		jsonEmitter.emitArrayEnd();
		done = true;
	}
}
