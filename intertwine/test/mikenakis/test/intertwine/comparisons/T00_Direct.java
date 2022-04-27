package mikenakis.test.intertwine.comparisons;

import mikenakis.test.intertwine.comparisons.rig.FooInterface;
import mikenakis.test.intertwine.comparisons.rig.FooServer;
import mikenakis.debug.Debug;
import mikenakis.testkit.TestKit;
import org.junit.Test;

import java.util.NoSuchElementException;

public final class T00_Direct
{
	public T00_Direct()
	{
		if( !Debug.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void Happy_Path_via_Direct_Call_Works()
	{
		FooInterface fooServer = new FooServer();
		ClientHelpers.runHappyPath( fooServer );
	}

	@Test
	public void Failure_via_Direct_Call_Works()
	{
		FooInterface fooServer = new FooServer();
		TestKit.expect( NoSuchElementException.class, () -> fooServer.getAlpha( -1 ) );
	}
}
