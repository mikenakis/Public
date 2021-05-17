package mikenakis.kit.events;

import java.io.PrintStream;
import java.io.PrintWriter;

public class DeadlockException extends RuntimeException
{
	public final Thread callingThread;
	public final Thread targetThread;
	public final StackTraceElement[] targetThreadStackTrace;

	DeadlockException( Thread callingThread, Thread targetThread, StackTraceElement[] targetThreadStackTrace )
	{
		this.callingThread = callingThread;
		this.targetThread = targetThread;
		this.targetThreadStackTrace = targetThreadStackTrace;
	}

	@Override public String getMessage()
	{
		return "Calling thread = " + callingThread + "; Target thread = " + targetThread;
	}

	@Override public void printStackTrace( PrintStream s )
	{
		assert false; //not implemented
	}

	@Override public void printStackTrace( PrintWriter s )
	{
		super.printStackTrace( s );
		Throwable throwable = new Throwable( "Target thread stack:", null, true, true ) { };
		throwable.setStackTrace( targetThreadStackTrace );
		throwable.printStackTrace( s );
	}
}
