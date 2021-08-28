package mikenakis.kit.spooling2.s0.format.json.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.GroupEnspoolStream;

class JsonGroupEnspoolStream implements GroupEnspoolStream.Defaults
{
	static void enspoolObject( JsonEmitter jsonEmitter, Procedure1<GroupEnspoolStream> groupEnspooler )
	{
		GroupEnspoolStream groupEnspoolStream = new JsonGroupEnspoolStream( jsonEmitter );
		groupEnspooler.invoke( groupEnspoolStream );
	}

	private final JsonEmitter jsonEmitter;
	private int nesting;
	private boolean atLeastOneMemberEmitted;

	private JsonGroupEnspoolStream( JsonEmitter jsonEmitter )
	{
		this.jsonEmitter = jsonEmitter;
	}

	@Override public void enspoolMember( String memberName, Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		assert nesting == 0;
		nesting++;
		if( !atLeastOneMemberEmitted )
			atLeastOneMemberEmitted = true;
		else
			jsonEmitter.emitComma();
		jsonEmitter.emitIdentifier( memberName );
		jsonEmitter.emitColon();
		EntryEnspoolStream entryEnspoolStream = new JsonEntryEnspoolStream( jsonEmitter, false );
		entryEnspooler.invoke( entryEnspoolStream );
		nesting--;
	}
}
