package mikenakis.testana.kit.structured.json.writing;

import mikenakis.testana.kit.structured.json.JsonWriter;
import mikenakis.testana.kit.structured.writing.ObjectWriter;
import mikenakis.testana.kit.structured.writing.StructuredWriter;

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
