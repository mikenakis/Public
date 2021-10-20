package mikenakis.kit.collections;

import java.util.IdentityHashMap;

public class IdentityHashSet<T> extends SetOnMap<T>
{
	public IdentityHashSet()
	{
		super( new IdentityHashMap<>() );
	}
}
