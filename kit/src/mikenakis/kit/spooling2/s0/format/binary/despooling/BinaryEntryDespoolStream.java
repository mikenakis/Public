package mikenakis.kit.spooling2.s0.format.binary.despooling;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.spooling2.s0.despooling.ArrayDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.GroupDespoolStream;
import mikenakis.kit.spooling2.codec.Codec;

public class BinaryEntryDespoolStream implements EntryDespoolStream.Defaults
{
	public static <T> T despool( BinaryStreamReader binaryStreamReader, Function1<T,EntryDespoolStream> entryDespooler )
	{
		EntryDespoolStream entryDespoolStream = new BinaryEntryDespoolStream( binaryStreamReader );
		T result = entryDespooler.invoke( entryDespoolStream );
		return result;
	}

	private final BinaryStreamReader binaryStreamReader;
	private boolean done;

	BinaryEntryDespoolStream( BinaryStreamReader binaryStreamReader )
	{
		this.binaryStreamReader = binaryStreamReader;
	}

	@Override public <T> T despoolValue( Codec<T> codec, T defaultValue )
	{
		assert !done;
		T result = codec.instanceFromBinary( binaryStreamReader );
		done = true;
		return result;
	}

	@Override public <T> T despoolGroup( Function1<T,GroupDespoolStream> groupDespooler )
	{
		assert !done;
		GroupDespoolStream groupDespoolStream = new BinaryGroupDespoolStream( binaryStreamReader );
		T result = groupDespooler.invoke( groupDespoolStream );
		done = true;
		return result;
	}

	@Override public <T> T despoolArray( String arrayElementName, Function1<T,ArrayDespoolStream> arrayDespooler )
	{
		assert !done;
		ArrayDespoolStream arrayDespoolStream = new BinaryArrayDespoolStream( binaryStreamReader );
		T result = arrayDespooler.invoke( arrayDespoolStream );
		done = true;
		return result;
	}
}
