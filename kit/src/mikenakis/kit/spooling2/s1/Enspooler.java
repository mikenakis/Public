package mikenakis.kit.spooling2.s1;

import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;

public interface Enspooler<T>
{
	void enspool( T spoolable, EntryEnspoolStream entryEnspoolStream );
}
