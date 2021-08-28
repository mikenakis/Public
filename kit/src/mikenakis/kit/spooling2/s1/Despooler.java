package mikenakis.kit.spooling2.s1;

import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;

public interface Despooler<T>
{
	T despool( EntryDespoolStream entryDespoolStream );
}
