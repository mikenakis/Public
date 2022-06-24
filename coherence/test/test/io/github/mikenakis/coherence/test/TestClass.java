package io.github.mikenakis.coherence.test;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;

class TestClass extends AbstractCoherent
{
	TestClass( Coherence coherence )
	{
		super( coherence );
	}

	void readOperation()
	{
		assert mustBeReadableAssertion();
	}

	void writeOperation()
	{
		assert mustBeWritableAssertion();
	}
}
