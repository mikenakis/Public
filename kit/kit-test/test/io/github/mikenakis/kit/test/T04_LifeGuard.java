package io.github.mikenakis.kit.test;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.implementation.ThreadLocalCoherence;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.lifetime.AbstractMortalCoherent;
import io.github.mikenakis.lifetime.Mortal;
import io.github.mikenakis.lifetime.guard.DevelopmentLifeGuard;
import io.github.mikenakis.lifetime.guard.LifeGuard;
import io.github.mikenakis.lifetime.guard.MustBeAliveException;
import io.github.mikenakis.testkit.TestKit;
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

	private static final class TestClass extends AbstractMortalCoherent
	{
		private final LifeGuard lifeGuard = LifeGuard.of( this );
		@Override protected LifeGuard lifeGuard() { return lifeGuard; }

		TestClass( Coherence coherence )
		{
			super( coherence );
		}
	}

	private final Coherence coherence = ThreadLocalCoherence.instance();

	@Test public void object_is_not_alive_after_being_closed()
	{
		TestClass testObject = new TestClass( coherence );
		assert testObject.mustBeAliveAssertion();
		testObject.close();
		var exception = TestKit.expect( MustBeAliveException.class, () -> testObject.mustBeAliveAssertion() );
		assert exception.mortalClass == TestClass.class;
	}

	@Test public void failure_to_close_object_is_detected()
	{
		Kit.runGarbageCollection();
		Collection<Class<? extends Mortal>> mortalClasses = new ConcurrentLinkedQueue<>();
		DevelopmentLifeGuard.setLifetimeErrorHandler( ( Class<? extends Mortal> mortalClass, Optional<StackWalker.StackFrame[]> stackTrace ) -> //
		{
			/* PEARL: if an exception is thrown here, the JVM will silently swallow it! */
			mortalClasses.add( mortalClass );
		} );
		WeakReference<TestClass> weakReference = createAndForget();
		while( weakReference.get() != null )
			Kit.runGarbageCollection();
		assert mortalClasses.stream().toList().contains( TestClass.class );
	}

	private WeakReference<TestClass> createAndForget()
	{
		TestClass testObject = new TestClass( coherence );
		assert testObject.mustBeAliveAssertion();
		return new WeakReference<>( testObject );
	}
}
