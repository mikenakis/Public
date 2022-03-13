package mikenakis.kit.debug;

import mikenakis.kit.Kit;
import mikenakis.kit.UncheckedException;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.logging.Log;

public class Debug
{
	public static class BreakpointException extends UncheckedException
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
		Log.debug( message );
		try
		{
			throw new BreakpointException( message );
		}
		catch( BreakpointException breakpointException )
		{
			Kit.get( true ); // <-- place breakpoint here!
		}
	}

	public static void boundary( Procedure0 procedure0 )
	{
		if( Kit.areAssertionsEnabled() )
			DebugThreadPool.instance().post( procedure0 );
		else
			procedure0.invoke();
	}

	public static <T> T boundary( Function0<T> function0 )
	{
		if( Kit.areAssertionsEnabled() )
			return DebugThreadPool.instance().call( function0 );
		else
			return function0.invoke();
	}

	public static void boundary2( Procedure0 procedure0 )
	{
		try
		{
			procedure0.invoke();
		}
		catch( RuntimeException e )
		{
			Log.error( e );
		}
//		catch( Throwable throwable )
//		{
//			Log.error( throwable );
//		}
	}

	public static <T> T boundary2( Function0<T> function0 )
	{
		try
		{
			return function0.invoke();
		}
		catch( Throwable throwable )
		{
			Log.error( throwable );
			return null;
		}
	}
}
