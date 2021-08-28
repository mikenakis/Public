package mikenakis.kit.spooling2.s0.format.json.despooling;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.GroupDespoolStream;

class JsonGroupDespoolStream implements GroupDespoolStream.Defaults
{
	public static <T> T despoolObject( JsonParser jsonParser, Function1<T,GroupDespoolStream> groupDespooler )
	{
		GroupDespoolStream groupDespoolStream = new JsonGroupDespoolStream( jsonParser );
		return groupDespooler.invoke( groupDespoolStream );
	}

	private final JsonParser jsonParser;
	private int nesting;
	private boolean first = true;

	private JsonGroupDespoolStream( JsonParser jsonParser )
	{
		this.jsonParser = jsonParser;
	}

	@Override public <T> T despoolMember( String memberName, Function1<T,EntryDespoolStream> entryDespooler )
	{
		assert nesting == 0;
		nesting++;
		if( first )
			first = false;
		else
			jsonParser.skip( JsonParser.TokenType.Comma );
		String identifier = jsonParser.skip( JsonParser.TokenType.Identifier );
		assert identifier.equals( memberName );
		jsonParser.skip( JsonParser.TokenType.Colon );
		EntryDespoolStream entryDespoolStream = new JsonEntryDespoolStream( jsonParser );
		T result = entryDespooler.invoke( entryDespoolStream );
		nesting--;
		return result;
	}
}
