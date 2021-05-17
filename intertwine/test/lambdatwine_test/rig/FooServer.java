package lambdatwine_test.rig;

import mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class FooServer implements FooInterface
{
	private final Map<Integer,Alpha> map = new HashMap<>();

	@Override public Alpha theMethod( int index, Alpha alpha )
	{
		if( index < 0 )
			throw new NoSuchElementException();
		Alpha result = Kit.map.tryGet( map, index );
		Kit.map.addOrReplace( map, index, alpha );
		return result;
	}
}
