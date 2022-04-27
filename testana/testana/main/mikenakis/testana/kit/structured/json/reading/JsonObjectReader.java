package mikenakis.testana.kit.structured.json.reading;

import mikenakis.testana.kit.structured.json.JsonReader;
import mikenakis.testana.kit.structured.reading.ObjectReader;
import mikenakis.testana.kit.structured.reading.StructuredReader;

import java.util.function.Function;

public class JsonObjectReader implements ObjectReader
{
	private boolean first = true;
	private final JsonReader jsonReader;

	JsonObjectReader( JsonReader jsonReader )
	{
		this.jsonReader = jsonReader;
	}

	@Override public <T> T readMember( String memberName, Function<StructuredReader, T> structuredReaderConsumer )
	{
		if( first )
			first = false;
		else
			jsonReader.skip( JsonReader.TokenType.Comma );
		String identifier = jsonReader.skip( JsonReader.TokenType.Identifier );
		assert identifier.equals( memberName );
		jsonReader.skip( JsonReader.TokenType.Colon );
		StructuredReader objectMemberReader = new JsonStructuredReader( jsonReader );
		return structuredReaderConsumer.apply( objectMemberReader );
	}
}
