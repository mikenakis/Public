package mikenakis.test.intertwine.handwritten;

import mikenakis.intertwine.AnyCall;
import mikenakis.intertwine.Intertwine;
import mikenakis.test.intertwine.rig.Alpha;
import mikenakis.test.intertwine.rig.FooInterface;

class HandwrittenUntwiner
{
	private final FooInterface exitPoint;

	final AnyCall<FooInterface> anycall = new AnyCall<>()
	{
		@Override public Object anyCall( Intertwine.Key<FooInterface> key, Object[] arguments )
		{
			HandwrittenKey handwrittenKey = (HandwrittenKey)key;
			switch( handwrittenKey.index )
			{
				case 0:
					assert arguments.length == 0;
					exitPoint.voidMethod();
					return null;
				case 1:
					assert arguments.length == 1;
					return exitPoint.getAlpha( (int) arguments[0] );
				case 2:
					assert arguments.length == 2;
					exitPoint.setAlpha( (int)arguments[0], (Alpha)arguments[1] );
					return null;
				default:
					throw new AssertionError( handwrittenKey );
			}
		}
	};

	HandwrittenUntwiner( Intertwine<FooInterface> intertwine, FooInterface exitPoint )
	{
		assert intertwine.interfaceType().isAssignableFrom( exitPoint.getClass() );
		this.exitPoint = exitPoint;
	}
}
