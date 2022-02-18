package mikenakis.test.intertwine.comparisons.implementations.testing.handwritten;

import mikenakis.intertwine.Anycall;
import mikenakis.kit.Kit;
import mikenakis.test.intertwine.comparisons.rig.Alpha;
import mikenakis.test.intertwine.comparisons.rig.FooInterface;

final class HandwrittenEntwiner implements FooInterface
{
	private final HandwrittenKey[] keys;
	private final Anycall<FooInterface> exitPoint;

	public HandwrittenEntwiner( HandwrittenKey[] keys, Anycall<FooInterface> exitPoint )
	{
		this.keys = keys;
		this.exitPoint = exitPoint;
	}

	@Override public void voidMethod()
	{
		exitPoint.anycall( keys[0], Kit.ARRAY_OF_ZERO_OBJECTS );
	}

	@Override public Alpha getAlpha( int index )
	{
		return (Alpha)exitPoint.anycall( keys[1], new Object[] { index } );
	}

	@Override public void setAlpha( int index, Alpha alpha )
	{
		exitPoint.anycall( keys[2], new Object[] { index, alpha } );
	}
}
