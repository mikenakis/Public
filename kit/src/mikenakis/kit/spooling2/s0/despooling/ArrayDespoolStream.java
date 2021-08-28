package mikenakis.kit.spooling2.s0.despooling;

import mikenakis.kit.functional.Procedure1;

public interface ArrayDespoolStream
{
	void despoolAllElements( Procedure1<EntryDespoolStream> entryDespooler );
}
