package mikenakis.kit.coherence;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.MutationContext;

/**
 * Coherence.
 *
 * Words considered: Reality, Continuity, Cohesion, Coherence, Continuum, Continuance, Congruity, Consistency, Consonance, Integrity, Concord, Accord
 *
 * We have a multiply instantiated software subsystem which has several entry points. Any entry point, on any instance, may be entered at any time, by an arbitrary thread.
 *
 * We want to guarantee that each instance of the subsystem is never re-entered.  (Never entered by more than one thread at the same time.)
 *
 * One way of guaranteeing this is by means of locking. That may be easy, but it might also be expensive. It is a good choice for a prototype, or even as a configurable option.
 *
 * Another way to guarantee this is to designate a specific thread to each instance, and to equip the thread with an incoming request queue.
 * Unfortunately, that would mean that we would need a thread per instance, and we certainly do not want that.
 *
 * A way to overcome the thread-per-instance problem is to have the instance return from its thread when its queue becomes empty, and to assign to an instance a thread from a
 * thread pool when its queue becomes non-empty.
 *
 * TODO: need to rename this.
 * Coherence is the state of being coherent, like this:
 * try( Coherence coherence = coherenceProvider.newCoherence() ) { ... do something in a state of coherence ... }
 * So, we need a new name for the coherence provider.
 * Possibilities: coherencer, coherer, coheror, coherentor, coherator, coheritor, coherand, coherizer, coherenciator, coherentist, coherender
 *
 * @author michael.gr
 */
public interface Coherence
{
	MutationContext mutationContext();

	void cohere( Procedure0 procedure );

	<R> R cohere( Function0<R> function );

	boolean assertCoherence();

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults extends Coherence
	{
	}
}
