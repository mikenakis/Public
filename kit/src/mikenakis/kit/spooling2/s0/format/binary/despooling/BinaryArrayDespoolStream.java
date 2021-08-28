package mikenakis.kit.spooling2.s0.format.binary.despooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.spooling2.s0.despooling.ArrayDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;

/**
 * Binary {@link ArrayDespoolStream}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
final class BinaryArrayDespoolStream implements ArrayDespoolStream
{
	private final BinaryStreamReader binaryStreamReader;
	private boolean done;
	private int nesting;

	BinaryArrayDespoolStream( BinaryStreamReader binaryStreamReader )
	{
		this.binaryStreamReader = binaryStreamReader;
	}

	@Override public void despoolAllElements( Procedure1<EntryDespoolStream> entryDespooler )
	{
		assert !done;
		assert nesting == 0;
		nesting++;
		for( ;; )
		{
			byte value = binaryStreamReader.readByte();
			assert value == 0 || value == 1;
			if( value == 0 )
				break;
			EntryDespoolStream entryDespoolStream = new BinaryEntryDespoolStream( binaryStreamReader );
			entryDespooler.invoke( entryDespoolStream );
		}
		nesting--;
		done = true;
	}
}
