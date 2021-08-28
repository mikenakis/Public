package mikenakis.kit.spooling2.s0.format.binary.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.GroupEnspoolStream;

class BinaryGroupEnspoolStream implements GroupEnspoolStream.Defaults
{
	private final BinaryStreamWriter binaryStreamWriter;
	private int nesting;

	BinaryGroupEnspoolStream( BinaryStreamWriter binaryStreamWriter )
	{
		this.binaryStreamWriter = binaryStreamWriter;
	}

	@Override public void enspoolMember( String memberName, Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		assert nesting == 0;
		nesting++;
		EntryEnspoolStream entryEnspoolStream = new BinaryEntryEnspoolStream( binaryStreamWriter );
		entryEnspooler.invoke( entryEnspoolStream );
		nesting--;
	}
}
