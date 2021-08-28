package mikenakis.kit.spooling2.s0.format.json.enspooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.io.stream.text.TextStreamWriter;
import mikenakis.kit.spooling2.s0.enspooling.ArrayEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.GroupEnspoolStream;
import mikenakis.kit.spooling2.codec.Codecs;
import mikenakis.kit.spooling2.codec.Codec;

public class JsonEntryEnspoolStream implements EntryEnspoolStream.Defaults
{
	public static void enspool( TextStreamWriter textStreamWriter, boolean prettyPrint, boolean unquotedIdentifiers, Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		Kit.tryWithResources( new JsonEmitter( textStreamWriter, prettyPrint, unquotedIdentifiers ), jsonEmitter -> //
		{
			JsonEntryEnspoolStream enspoolStream = new JsonEntryEnspoolStream( jsonEmitter, false );
			entryEnspooler.invoke( enspoolStream );
			assert enspoolStream.done;
		} );
	}

	private final JsonEmitter jsonEmitter;
	private final boolean arrayIndentationMode;
	private boolean done;

	JsonEntryEnspoolStream( JsonEmitter jsonEmitter, boolean arrayIndentationMode )
	{
		this.jsonEmitter = jsonEmitter;
		this.arrayIndentationMode = arrayIndentationMode;
	}

	@Override public <T> void enspoolValue( Codec<T> codec, T value, T defaultValue )
	{
		assert !done;
		if( codec.isOptional() && value.equals( codec.defaultInstance() ) )
			jsonEmitter.emitNull( arrayIndentationMode );
		else
		{
			String stringValue = codec.stringFromInstance( value );
			if( codec == Codecs.BooleanCodec || codec == Codecs.optionalBooleanCodec )
				jsonEmitter.emitLiteral( stringValue, arrayIndentationMode );
			else if( Codecs.isNumeric( codec ) )
				jsonEmitter.emitLiteral( stringValue, arrayIndentationMode );
			else
				jsonEmitter.emitString( stringValue, arrayIndentationMode );
		}
		done = true;
	}

	@Override public void enspoolGroup( Procedure1<GroupEnspoolStream> groupEnspooler )
	{
		assert !done;
		jsonEmitter.emitObjectBegin();
		int depth = jsonEmitter.depth();
		JsonGroupEnspoolStream.enspoolObject( jsonEmitter, groupEnspooler );
		assert jsonEmitter.depth() == depth;
		jsonEmitter.emitObjectEnd();
		done = true;
	}

	@Override public void enspoolArray( String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler )
	{
		assert !done;
		jsonEmitter.emitArrayBegin();
		int depth = jsonEmitter.depth();
		JsonArrayEnspoolStream.enspoolArray( jsonEmitter, arrayEnspooler );
		assert jsonEmitter.depth() == depth;
		jsonEmitter.emitArrayEnd();
		done = true;
	}
}
