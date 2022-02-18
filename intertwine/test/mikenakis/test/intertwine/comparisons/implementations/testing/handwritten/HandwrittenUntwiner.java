package mikenakis.test.intertwine.comparisons.implementations.testing.handwritten;

import mikenakis.intertwine.Anycall;
import mikenakis.intertwine.MethodKey;
import mikenakis.test.intertwine.comparisons.rig.Alpha;
import mikenakis.test.intertwine.comparisons.rig.FooInterface;

class HandwrittenUntwiner implements Anycall<FooInterface>
{
	private final FooInterface exitPoint;

	HandwrittenUntwiner( FooInterface exitPoint )
	{
		this.exitPoint = exitPoint;
	}

	@Override public Object anycall( MethodKey<FooInterface> key, Object[] arguments )
	{
		HandwrittenKey handwrittenKey = (HandwrittenKey)key;
		switch( handwrittenKey.index )
		{
			case 0:
				//assert arguments.length == 0;
				exitPoint.voidMethod();
				return null;
			case 1:
				//assert arguments.length == 1;
				return exitPoint.getAlpha( (int) arguments[0] );
			case 2:
				//assert arguments.length == 2;
				exitPoint.setAlpha( (int)arguments[0], (Alpha)arguments[1] );
				return null;
			default:
				throw new AssertionError( handwrittenKey );
		}
	}
}
