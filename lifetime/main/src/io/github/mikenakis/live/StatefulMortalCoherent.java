package io.github.mikenakis.live;

import io.github.mikenakis.coherence.Coherent;

/**
 * A {@link Coherent} object which has a 'closed' state.
 * <p>
 * @author michael.gr
 */
public interface StatefulMortalCoherent extends Coherent
{
	boolean isClosed();

	interface Defaults extends StatefulMortalCoherent, Coherent.Defaults
	{
	}
}
