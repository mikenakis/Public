package io.github.mikenakis.coherence.test;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.implementation.TemporaryCoherence;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeReadableException;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeWritableException;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.live.Live;
import io.github.mikenakis.testkit.TestKit;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.mutable.MutableCollections;
import org.junit.Test;

/**
 * Test.
 */
public class T02_TemporaryCoherence
{
	public T02_TemporaryCoherence()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void test_TemporaryCoherence1()
	{
		Live<Coherence> temporaryCoherence = TemporaryCoherence.of();
		TestClass testObject = Live.tryGetWith( temporaryCoherence, coherence -> //
		{
			TestClass t = new TestClass( coherence );
			t.readOperation();
			t.writeOperation();
			return t;
		} );
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testObject.readOperation() );
		assert exception1.coherence == temporaryCoherence.target();
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.coherence == temporaryCoherence.target();
	}

	@Test public void test_TemporaryCoherence2()
	{
		Live<Coherence> temporaryCoherence = TemporaryCoherence.of();
		MutableList<String> testList = Live.tryGetWith( temporaryCoherence, coherence -> //
		{
			MutableList<String> t = MutableCollections.of( coherence ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testList.size() );
		assert exception1.coherence == temporaryCoherence.target();
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testList.add( "b" ) );
		assert exception2.coherence == temporaryCoherence.target();
	}
}
