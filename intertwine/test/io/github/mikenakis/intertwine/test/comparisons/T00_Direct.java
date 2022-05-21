package io.github.mikenakis.intertwine.test.comparisons;

import io.github.mikenakis.intertwine.test.comparisons.rig.FooInterface;
import io.github.mikenakis.intertwine.test.comparisons.rig.FooServer;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.testkit.TestKit;
import org.junit.Test;

import java.util.NoSuchElementException;

public final class T00_Direct
{
	public T00_Direct()
	{
		if( !Kit.areAssertionsEnabled() )
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
