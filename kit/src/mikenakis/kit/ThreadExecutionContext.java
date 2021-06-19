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

	@Override public boolean entered()
	{
		return Thread.currentThread().equals( thread );
	}

	@Override public String toString()
	{
		return thread.getName() + " (" + Integer.toHexString( System.identityHashCode( thread ) ) + ")";
	}
}
