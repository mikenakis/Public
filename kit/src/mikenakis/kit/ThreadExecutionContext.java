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
		assert Thread.currentThread().equals( thread ) : toString( Thread.currentThread() ) + " " + toString( thread );
		return true;
	}

	@Override public boolean outOfContextAssertion()
	{
		assert Thread.currentThread() != thread : toString( Thread.currentThread() ) + " " + toString( thread );
		return true;
	}

	private static String toString( Thread thread )
	{
		return thread.getName() + " (" + Integer.toHexString( System.identityHashCode( thread ) ) + ")";
	}
}
