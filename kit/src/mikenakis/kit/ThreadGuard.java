package mikenakis.kit;

public class ThreadGuard
{
	public static ThreadGuard create()
	{
		return new ThreadGuard( Thread.currentThread() );
	}

	public final Thread thread;

	private ThreadGuard( Thread thread )
	{
		this.thread = thread;
	}

	/**
	 * Asserts that the current thread is this {@link ThreadGuard}'s thread.
	 *
	 * @return always true
	 */
	public boolean inThreadAssertion()
	{
		assert Thread.currentThread().equals( thread ) : Thread.currentThread().getName() + " (" + Integer.toHexString( System.identityHashCode( Thread.currentThread() ) ) + ") " + thread.getName() + " (" + Integer.toHexString( System.identityHashCode( thread ) ) + ")";
		return true;
	}

	/**
	 * Asserts that the current thread is not this {@link ThreadGuard}'s thread.
	 *
	 * @return always true
	 */
	public boolean outOfThreadAssertion()
	{
		assert Thread.currentThread() != thread;
		return true;
	}
}
