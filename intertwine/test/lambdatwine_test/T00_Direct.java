package lambdatwine_test;

import lambdatwine_test.rig.FooInterface;
import lambdatwine_test.rig.FooServer;
import mikenakis.kit.Kit;
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
		Kit.testing.expectException( NoSuchElementException.class, () -> fooServer.theMethod( -1, null ) );
	}
}
