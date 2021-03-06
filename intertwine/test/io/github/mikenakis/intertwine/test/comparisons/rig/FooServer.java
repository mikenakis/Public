package io.github.mikenakis.intertwine.test.comparisons.rig;

import io.github.mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class FooServer implements FooInterface
{
	private final Map<Integer,Alpha> map = new HashMap<>();

	@Override public void voidMethod()
	{
	}

	@Override public Alpha getAlpha( int index )
	{
		Alpha alpha = Kit.map.tryGet( map, index );
		if( alpha == null )
			throw new NoSuchElementException();
		return alpha;
	}

	@Override public void setAlpha( int index, Alpha alpha )
	{
		assert index > 0;
		Alpha newAlpha = new Alpha( alpha );
		Kit.map.addOrReplace( map, index, newAlpha );
	}
}
