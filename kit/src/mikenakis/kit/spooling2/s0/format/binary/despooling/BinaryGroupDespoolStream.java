package mikenakis.kit.spooling2.s0.format.binary.despooling;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.GroupDespoolStream;

class BinaryGroupDespoolStream implements GroupDespoolStream.Defaults
{
	private final BinaryStreamReader binaryStreamReader;
	private int nesting;

	BinaryGroupDespoolStream( BinaryStreamReader binaryStreamReader )
	{
		this.binaryStreamReader = binaryStreamReader;
	}

	@Override public <T> T despoolMember( String memberName, Function1<T,EntryDespoolStream> entryDespooler )
	{
		assert nesting == 0;
		nesting++;
		EntryDespoolStream entryDespoolStream = new BinaryEntryDespoolStream( binaryStreamReader );
		T result = entryDespooler.invoke( entryDespoolStream );
		nesting--;
		return result;
	}
}
