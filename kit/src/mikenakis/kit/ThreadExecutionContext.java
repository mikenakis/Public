package mikenakis.kit;

/**
 * An {@link ExecutionContext} which considers the current thread as the current execution context.
 */
public class ThreadExecutionContext extends ExecutionContext
{
	public static ThreadExecutionContext create()
	{
		return new ThreadExecutionContext( Thread.currentThread() );
	}

	public final Thread thread;

	private ThreadExecutionContext( Thread thread )
	{
		this.thread = thread;
	}

	@Override public Thread thread()
	{
		return thread;
	}

	@Override public boolean inContextAssertion()
	{
		assert Thread.currentThread().equals( thread ) : Thread.currentThread().getName() + " (" + Integer.toHexString( System.identityHashCode( Thread.currentThread() ) ) + ") " + thread.getName() + " (" + Integer.toHexString( System.identityHashCode( thread ) ) + ")";
		return true;
	}

	@Override public boolean outOfContextAssertion()
	{
		assert Thread.currentThread() != thread;
		return true;
	}
}
