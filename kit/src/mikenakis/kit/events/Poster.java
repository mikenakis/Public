package mikenakis.kit.events;

import mikenakis.kit.functional.Procedure0;

/**
 * The bare minimum functionality exposed by an event-driven system: the ability to post a message to it.
 */
public interface Poster
{
	void post( Procedure0 procedure );
}
