package mikenakis.kit.spooling2.s0.format.json.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.s0.enspooling.ArrayEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;

class JsonArrayEnspoolStream implements ArrayEnspoolStream.Defaults
{
	public static void enspoolArray( JsonEmitter jsonEmitter, Procedure1<ArrayEnspoolStream> arrayEnspooler )
	{
		ArrayEnspoolStream enspoolStream = new JsonArrayEnspoolStream( jsonEmitter );
		arrayEnspooler.invoke( enspoolStream );
	}

	private final JsonEmitter jsonEmitter;
	private int nesting;
	private boolean first = true;

	private JsonArrayEnspoolStream( JsonEmitter jsonEmitter )
	{
		this.jsonEmitter = jsonEmitter;
	}

	@Override public void enspoolElement( Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		assert nesting == 0;
		nesting++;
		if( first )
			first = false;
		else
			jsonEmitter.emitComma();
		EntryEnspoolStream enspoolStream = new JsonEntryEnspoolStream( jsonEmitter, true );
		entryEnspooler.invoke( enspoolStream );
		nesting--;
	}
}
