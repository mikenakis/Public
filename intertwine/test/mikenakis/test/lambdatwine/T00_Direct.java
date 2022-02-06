package mikenakis.test.lambdatwine;

import mikenakis.test.lambdatwine.rig.FooInterface;
import mikenakis.test.lambdatwine.rig.FooServer;
import mikenakis.kit.Kit;
import mikenakis.testkit.TestKit;
import org.junit.Test;

import java.util.NoSuchElementException;

/**
 * A few preliminary tests to ensure that direct invocations between caller and callee work,
 * before we move on to other tests that introduce lambdatwine between them.
 */
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
		TestKit.expect( NoSuchElementException.class, () -> fooServer.theMethod( -1, null ) );
	}
}
