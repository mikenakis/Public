package mikenakis.kit.spooling2.s0.format.binary.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;
import mikenakis.kit.spooling2.s0.enspooling.ArrayEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.GroupEnspoolStream;
import mikenakis.kit.spooling2.codec.Codec;

public class BinaryEntryEnspoolStream implements EntryEnspoolStream.Defaults
{
	public static void enspool( BinaryStreamWriter binaryStreamWriter, Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		BinaryEntryEnspoolStream enspoolStream = new BinaryEntryEnspoolStream( binaryStreamWriter );
		entryEnspooler.invoke( enspoolStream );
		assert enspoolStream.done; //the enspooler did not enspool anything.
	}

	private final BinaryStreamWriter binaryStreamWriter;
	private boolean done;

	BinaryEntryEnspoolStream( BinaryStreamWriter binaryStreamWriter )
	{
		this.binaryStreamWriter = binaryStreamWriter;
	}

	@Override public <T> void enspoolValue( Codec<T> codec, T value, T defaultValue )
	{
		assert !done;
		codec.instanceIntoBinary( value, binaryStreamWriter );
		done = true;
	}

	@Override public void enspoolGroup( Procedure1<GroupEnspoolStream> groupEnspooler )
	{
		assert !done;
		GroupEnspoolStream groupEnspoolStream = new BinaryGroupEnspoolStream( binaryStreamWriter );
		groupEnspooler.invoke( groupEnspoolStream );
		done = true;
	}

	@Override public void enspoolArray( String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler )
	{
		assert !done;
		BinaryArrayEnspoolStream arrayEnspoolStream = new BinaryArrayEnspoolStream( binaryStreamWriter );
		arrayEnspooler.invoke( arrayEnspoolStream );
		arrayEnspoolStream.close();
		done = true;
	}
}
