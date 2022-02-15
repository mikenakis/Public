package mikenakis.io.async;

import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.mutation.MutationContext;

/**
 * Represents an asynchronous worker.
 *
 * @author michael.gr
 */
public interface Async
{
	/**
	 * Checks whether this asynchronous worker is busy.  (There is a pending operation.)
	 *
	 * @return {@code true} if there is a pending operation; {@code false} otherwise.
	 */
	boolean isBusy();

	interface Defaults extends Async
	{
	}
}
