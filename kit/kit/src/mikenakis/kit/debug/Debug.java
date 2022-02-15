package mikenakis.kit.debug;

import mikenakis.kit.BreakpointException;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.logging.Log;

public class Debug
{
	public static void breakPoint()
	{
		try
		{
			throw new BreakpointException();
		}
		catch( BreakpointException breakpointException )
		{
			Log.error( breakpointException ); // <-- place breakpoint here!
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
}
