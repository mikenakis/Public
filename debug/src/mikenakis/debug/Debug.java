package mikenakis.debug;

import java.util.function.Supplier;

public final class Debug
{
	public static boolean expectingException;

	private Debug()
	{
	}

	/**
	 * Throws a {@link RuntimeException} and immediately catches it and swallows it, allowing the debugger to stop right here.
	 *
	 * If the debugger is properly configured, then the thrown exception will be treated as unhandled, even if the caller of this method has a catch-all clause.
	 * For how to configure the debugger, see README.md
	 *
	 * @param message a message to send to the standard output right before hitting the breakpoint.
	 */
	public static void breakPoint( String message )
	{
		System.out.println( message );
		try
		{
			throw new RuntimeException( message );
		}
		catch( RuntimeException ignored )
		{
		}
	}

	/**
	 * Invokes a given {@link Runnable}, allowing the debugger to stop at the throwing statement of any exception that is thrown by the {@link Runnable} and
	 * goes unhandled by the {@link Runnable}.
	 *
	 * If the debugger is properly configured, then any unhandled exceptions thrown by the {@link Runnable} will be treated as unhandled, even if the caller
	 * of this method has a catch-all clause. For how to configure the debugger, see README.md
	 *
	 * @param procedure0 the {@link Runnable} to invoke.
	 */
	public static void boundary( Runnable procedure0 )
	{
		if( expectingException )
		{
			procedure0.run();
			return;
		}
		//noinspection CaughtExceptionImmediatelyRethrown
		try
		{
			procedure0.run();
		}
		catch( Throwable throwable )
		{
			throw throwable;
		}
	}

	/**
	 * Invokes a given {@link Supplier} and returns the result, allowing the debugger to stop at the throwing statement of any exception that is thrown by
	 * the {@link Supplier} and goes unhandled by the {@link Supplier}.
	 *
	 * If the debugger is properly configured, then any unhandled exceptions thrown by the {@link Supplier} will be treated as unhandled, even if the caller
	 * of this method has a catch-all clause. For how to configure the debugger, see README.md
	 *
	 * @param function0 the {@link Supplier} to invoke.
	 * @param <T>       the type of the result returned by the {@link Supplier}.
	 *
	 * @return whatever the {@link Supplier} returned.
	 */
	public static <T> T boundary( Supplier<T> function0 )
	{
		if( expectingException )
			return function0.get();
		//noinspection CaughtExceptionImmediatelyRethrown
		try
		{
			return function0.get();
		}
		catch( Throwable throwable )
		{
			throw throwable;
		}
	}
}
