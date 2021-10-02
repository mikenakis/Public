package mikenakis.test.intertwine;

import mikenakis.test.intertwine.rig.FooInterface;
import mikenakis.test.intertwine.rig.FooServer;
import mikenakis.kit.Kit;
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
		Kit.testing.expectException( NoSuchElementException.class, () -> fooServer.getAlpha( -1 ) );
	}
}
