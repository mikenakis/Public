package mikenakis.testana.kit.structured.json.reading;

import mikenakis.testana.kit.structured.json.JsonEmitter;
import mikenakis.testana.kit.structured.json.JsonParser;
import mikenakis.testana.kit.structured.reading.ObjectReader;
import mikenakis.testana.kit.structured.reading.StructuredReader;

import java.util.function.Function;

public class JsonObjectReader implements ObjectReader
{
	private boolean first = true;
	private final JsonParser jsonParser;

	JsonObjectReader( JsonParser jsonParser )
	{
		this.jsonParser = jsonParser;
	}

	@Override public <T> T readMember( String memberName, Function<StructuredReader, T> structuredReaderConsumer )
	{
		if( first )
			first = false;
		else
			jsonParser.skip( JsonParser.TokenType.Comma );
		String identifier = jsonParser.skip( JsonParser.TokenType.Identifier );
		assert identifier.equals( memberName );
		jsonParser.skip( JsonParser.TokenType.Colon );
		JsonStructuredReader objectMemberReader = new JsonStructuredReader( jsonParser, JsonEmitter.Mode.Object );
		return structuredReaderConsumer.apply( objectMemberReader );
	}
}
