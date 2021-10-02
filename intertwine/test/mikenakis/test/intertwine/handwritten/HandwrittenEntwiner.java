package mikenakis.test.intertwine.handwritten;

import mikenakis.intertwine.AnyCall;
import mikenakis.kit.Kit;
import mikenakis.test.intertwine.rig.Alpha;
import mikenakis.test.intertwine.rig.FooInterface;

final class HandwrittenEntwiner
{
	private final HandwrittenIntertwine intertwine;
	private final AnyCall<FooInterface> exitPoint;
	final FooInterface entryPoint = new FooInterface()
	{
		@Override public void voidMethod()
		{
			exitPoint.anyCall( intertwine.keyByIndex( 0 ), Kit.ARRAY_OF_ZERO_OBJECTS );
		}

		@Override public Alpha getAlpha( int index )
		{
			return (Alpha)exitPoint.anyCall( intertwine.keyByIndex( 1 ), new Object[]{ index } );
		}

		@Override public void setAlpha( int index, Alpha alpha )
		{
			exitPoint.anyCall( intertwine.keyByIndex( 2 ), new Object[]{ index, alpha } );
		}
	};

	HandwrittenEntwiner( HandwrittenIntertwine intertwine, AnyCall<FooInterface> exitPoint )
	{
		this.intertwine = intertwine;
		assert exitPoint != null;
		this.exitPoint = exitPoint;
	}
}
