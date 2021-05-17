package mikenakis.testana.kit.structured.json.writing;

import mikenakis.testana.kit.structured.json.JsonEmitter;
import mikenakis.testana.kit.structured.writing.ObjectWriter;
import mikenakis.testana.kit.structured.writing.StructuredWriter;

import java.util.function.Consumer;

public class JsonObjectWriter implements ObjectWriter
{
	private boolean atLeastOneMemberEmitted;
	private final JsonEmitter jsonEmitter;

	JsonObjectWriter( JsonEmitter jsonEmitter )
	{
		this.jsonEmitter = jsonEmitter;
	}

	@Override public void writeMember( String memberName, Consumer<StructuredWriter> structuredWriterConsumer )
	{
		if( !atLeastOneMemberEmitted )
			atLeastOneMemberEmitted = true;
		else
			jsonEmitter.emitComma();
		jsonEmitter.emitIdentifier( memberName );
		jsonEmitter.emitColon();
		JsonStructuredWriter objectMemberWriter = new JsonStructuredWriter( jsonEmitter, JsonEmitter.Mode.Object );
		structuredWriterConsumer.accept( objectMemberWriter );
	}
}
