package mikenakis_kit_test;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.DevelopmentLifeGuard;
import mikenakis.kit.lifetime.guard.EndOfLifeException;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.testkit.TestKit;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Test.
 */
public class T04_LifeGuard
{
	public T04_LifeGuard()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static class TestClass implements Closeable.Defaults
	{
		private final LifeGuard lifeGuard = LifeGuard.of( this );

		@Override public boolean isAliveAssertion()
		{
			assert lifeGuard.isAliveAssertion();
			return true;
		}

		@Override public void close()
		{
			lifeGuard.close();
		}
	}

	@Test public void object_is_not_alive_after_being_closed()
	{
		TestClass testObject = new TestClass();
		assert testObject.isAliveAssertion();
		testObject.close();
		var exception = TestKit.expect( EndOfLifeException.class, () -> testObject.isAliveAssertion() );
		assert exception.closeableClass == TestClass.class;
	}

	@Test public void failure_to_close_object_is_detected()
	{
		Kit.runGarbageCollection();
		Collection<Class<? extends Closeable>> closeableClasses = new ConcurrentLinkedQueue<>();
		DevelopmentLifeGuard.setLifetimeErrorHandler( ( Class<? extends Closeable> closeableClass, Optional<StackWalker.StackFrame[]> stackTrace ) -> //
		{
			/* PEARL: if an exception is thrown here, the JVM will silently swallow it! */
			closeableClasses.add( closeableClass );
		} );
		WeakReference<TestClass> weakReference = createAndForget();
		while( weakReference.get() != null )
			Kit.runGarbageCollection();
		assert closeableClasses.stream().toList().contains( TestClass.class );
	}

	private static WeakReference<TestClass> createAndForget()
	{
		TestClass testObject = new TestClass();
		assert testObject.isAliveAssertion();
		return new WeakReference<>( testObject );
	}
}
