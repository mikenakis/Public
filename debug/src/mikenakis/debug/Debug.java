package mikenakis.debug;

import java.util.function.Supplier;

public final class Debug
{
	public static boolean expectingException;

	private Debug()
	{
	}

	//PEARL: this has been observed to return false even though assertions are enabled, when invoked from a static context, e.g. main()
	public static boolean areAssertionsEnabled()
	{
		boolean b = false;
		//noinspection AssertWithSideEffects
		assert b = true;
		//noinspection ConstantConditions
		return b;
	}

	private static boolean debugging()
	{
		if( expectingException )
			return false;
		return areAssertionsEnabled();
	}

	public static class BreakpointException extends RuntimeException
	{
		public final String message;

		public BreakpointException( String message )
		{
			this.message = message;
		}
	}

	public static void breakPoint()
	{
		breakPoint( "" );
	}

	public static void breakPoint( String message )
	{
		System.out.println( message );
		try
		{
			throw new BreakpointException( message );
		}
		catch( BreakpointException ignored )
		{
		}
	}

	public static void boundary( Runnable procedure0 )
	{
		if( expectingException )
		{
			procedure0.run();
			return;
		}
		try
		{
			procedure0.run();
		}
		catch( Throwable throwable )
		{
			assert false : throwable;
		}
	}

	public static <T> T boundary( Supplier<T> function0 )
	{
		if( expectingException )
			return function0.get();
		try
		{
			return function0.get();
		}
		catch( Throwable throwable )
		{
			assert false : throwable;
			return null;
		}
	}
}
