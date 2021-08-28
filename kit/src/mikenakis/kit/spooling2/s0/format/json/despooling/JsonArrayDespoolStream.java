package mikenakis.kit.spooling2.s0.format.json.despooling;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.s0.despooling.ArrayDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;

class JsonArrayDespoolStream implements ArrayDespoolStream
{
	public static <T> T despoolArray( JsonParser jsonParser, Function1<T,ArrayDespoolStream> arrayDespooler )
	{
		ArrayDespoolStream arrayDespoolStream = new JsonArrayDespoolStream( jsonParser );
		return arrayDespooler.invoke( arrayDespoolStream );
	}

	private final JsonParser jsonParser;
	private int nesting;
	private boolean done;

	private JsonArrayDespoolStream( JsonParser jsonParser )
	{
		this.jsonParser = jsonParser;
	}

	@Override public void despoolAllElements( Procedure1<EntryDespoolStream> entryDespooler )
	{
		assert !done;
		assert nesting == 0;
		nesting++;
		for( boolean first = true; !jsonParser.isArrayEnd(); )
		{
			if( first )
				first = false;
			else
				jsonParser.skip( JsonParser.TokenType.Comma );
			EntryDespoolStream entryDespoolStream = new JsonEntryDespoolStream( jsonParser );
			entryDespooler.invoke( entryDespoolStream );
		}
		nesting--;
		done = true;
	}
}
