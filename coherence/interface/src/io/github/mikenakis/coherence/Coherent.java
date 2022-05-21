package io.github.mikenakis.coherence;

/**
 * A coherence-aware object.
 */
public interface Coherent
{
	Coherence coherence();

	interface Defaults extends Coherent
	{
	}

	interface Decorator extends Defaults
	{
		Coherent decoratedCoherent();

		@Override default Coherence coherence()
		{
			return decoratedCoherent().coherence();
		}
	}
}
