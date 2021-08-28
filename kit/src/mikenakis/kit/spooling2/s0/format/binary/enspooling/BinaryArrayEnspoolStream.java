package mikenakis.kit.spooling2.s0.format.binary.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;
import mikenakis.kit.spooling2.s0.enspooling.ArrayEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.codec.Codecs;

class BinaryArrayEnspoolStream implements ArrayEnspoolStream.Defaults
{
	private final BinaryStreamWriter binaryStreamWriter;
	private int nesting;

	BinaryArrayEnspoolStream( BinaryStreamWriter binaryStreamWriter )
	{
		this.binaryStreamWriter = binaryStreamWriter;
	}

	@Override public void enspoolElement( Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		assert nesting == 0;
		nesting++;
		Codecs.ByteCodec.instanceIntoBinary( (byte)1, binaryStreamWriter );
		EntryEnspoolStream entryEnspoolStream = new BinaryEntryEnspoolStream( binaryStreamWriter );
		entryEnspooler.invoke( entryEnspoolStream );
		nesting--;
	}

	void close()
	{
		Codecs.ByteCodec.instanceIntoBinary( (byte)0, binaryStreamWriter );
	}
}
