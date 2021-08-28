package mikenakis.kit.spooling2.s0.format.json.despooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.io.stream.text.TextStreamReader;
import mikenakis.kit.spooling2.s0.despooling.ArrayDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.GroupDespoolStream;
import mikenakis.kit.spooling2.codec.Codecs;
import mikenakis.kit.spooling2.codec.Codec;

import java.util.Optional;

public class JsonEntryDespoolStream implements EntryDespoolStream.Defaults
{
	public static <T> T despool( TextStreamReader textStreamReader, boolean unquotedIdentifiers, Function1<T,EntryDespoolStream> entryDespooler )
	{
		return Kit.tryGetWithResources( new JsonParser( textStreamReader, unquotedIdentifiers ), jsonParser -> //
		{
			assert !jsonParser.isFinished();
			EntryDespoolStream entryDespoolStream = new JsonEntryDespoolStream( jsonParser );
			T result = entryDespooler.invoke( entryDespoolStream );
			assert jsonParser.isFinished();
			return result;
		} );
	}

	private final JsonParser jsonParser;
	private boolean done;

	JsonEntryDespoolStream( JsonParser jsonParser )
	{
		this.jsonParser = jsonParser;
	}

	@Override public <T> T despoolValue( Codec<T> codec, T defaultValue )
	{
		assert !done;
		if( jsonParser.isNull() )
		{
			String content = jsonParser.skip( JsonParser.TokenType.Null );
			assert content.equals( "null" );
			assert codec.isOptional();
			return codec.defaultInstance(); //this will be Optional.empty() for optional types.
		}
		String stringValue;
		if( codec == Codecs.BooleanCodec || codec == Codecs.optionalBooleanCodec )
			stringValue = jsonParser.skip( JsonParser.TokenType.Boolean );
		else if( Codecs.isNumeric( codec ) )
			stringValue = jsonParser.skip( JsonParser.TokenType.Number );
		else
			stringValue = jsonParser.skip( JsonParser.TokenType.String );
		return codec.instanceFromString( stringValue );
	}

	@Override public <T> T despoolGroup( Function1<T,GroupDespoolStream> groupDespooler )
	{
		assert !done;
		jsonParser.skip( JsonParser.TokenType.ObjectBegin );
		int depth = jsonParser.depth();
		T result = JsonGroupDespoolStream.despoolObject( jsonParser, groupDespooler );
		assert jsonParser.depth() == depth;
		jsonParser.skip( JsonParser.TokenType.ObjectEnd );
		done = true;
		return result;
	}

	@Override public <T> T despoolArray( String arrayElementName, Function1<T,ArrayDespoolStream> arrayDespooler )
	{
		assert !done;
		jsonParser.skip( JsonParser.TokenType.ArrayBegin );
		int depth = jsonParser.depth();
		T result = JsonArrayDespoolStream.despoolArray( jsonParser, arrayDespooler );
		assert jsonParser.depth() == depth;
		jsonParser.skip( JsonParser.TokenType.ArrayEnd );
		done = true;
		return result;
	}
}
