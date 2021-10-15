package mikenakis.test.intertwine.implementations.testing.handwritten;

import mikenakis.intertwine.AnyCall;
import mikenakis.kit.Kit;
import mikenakis.test.intertwine.rig.Alpha;
import mikenakis.test.intertwine.rig.FooInterface;

final class HandwrittenEntwiner implements FooInterface
{
	private final HandwrittenKey[] keys;
	private final AnyCall<FooInterface> exitPoint;

	public HandwrittenEntwiner( HandwrittenKey[] keys, AnyCall<FooInterface> exitPoint )
	{
		this.keys = keys;
		this.exitPoint = exitPoint;
	}

	@Override public void voidMethod()
	{
		exitPoint.anyCall( keys[0], Kit.ARRAY_OF_ZERO_OBJECTS );
	}

	@Override public Alpha getAlpha( int index )
	{
		return (Alpha)exitPoint.anyCall( keys[1], new Object[] { index } );
	}

	@Override public void setAlpha( int index, Alpha alpha )
	{
		exitPoint.anyCall( keys[2], new Object[] { index, alpha } );
	}
}
