package io.github.mikenakis.testana.kit.structured.json.writing;

import io.github.mikenakis.testana.kit.structured.writing.ObjectWriter;
import io.github.mikenakis.testana.kit.structured.writing.StructuredWriter;
import io.github.mikenakis.testana.kit.structured.json.JsonWriter;

import java.util.function.Consumer;

public class JsonObjectWriter implements ObjectWriter
{
	private boolean atLeastOneMemberEmitted;
	private final JsonWriter jsonWriter;

	JsonObjectWriter( JsonWriter jsonWriter )
	{
		this.jsonWriter = jsonWriter;
	}

	@Override public void writeMember( String memberName, Consumer<StructuredWriter> structuredWriterConsumer )
	{
		if( !atLeastOneMemberEmitted )
			atLeastOneMemberEmitted = true;
		else
			jsonWriter.emitComma();
		jsonWriter.emitIdentifier( memberName );
		jsonWriter.emitColon();
		StructuredWriter objectMemberWriter = new JsonStructuredWriter( jsonWriter, JsonWriter.Mode.Object );
		structuredWriterConsumer.accept( objectMemberWriter );
	}
}
