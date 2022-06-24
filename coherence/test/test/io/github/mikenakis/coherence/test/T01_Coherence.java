package io.github.mikenakis.coherence.test;

import io.github.mikenakis.coherence.FreezableCoherence;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeReadableException;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeWritableException;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.testkit.TestKit;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.mutable.MutableCollections;
import org.junit.Test;

/**
 * Test.
 */
public class T01_Coherence
{
	public T01_Coherence()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static class TestCoherence implements FreezableCoherence
	{
		boolean isEntered;
		boolean isFrozen;

		TestCoherence( boolean isEntered, boolean isFrozen )
		{
			this.isEntered = isEntered;
			this.isFrozen = isFrozen;
		}

		@Override public boolean mustBeReadableAssertion()
		{
			assert isFrozen || isEntered : new MustBeReadableException( this );
			return true;
		}

		@Override public boolean mustBeWritableAssertion()
		{
			assert !isFrozen && isEntered : new MustBeWritableException( this );
			return true;
		}

		@Override public boolean isFrozen()
		{
			return isFrozen;
		}

		@Override public String toString()
		{
			return "isEntered: " + isEntered + "; isFrozen: " + isFrozen;
		}
	}

	@Test public void can_query_and_mutate_when_not_frozen()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		TestClass testObject = new TestClass( testCoherence );
		testCoherence.isFrozen = false;
		testObject.readOperation();
		testObject.writeOperation();
	}

	@Test public void can_query_and_mutate_when_not_frozen2()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		MutableList<String> testObject = MutableCollections.of( testCoherence ).newArrayList();
		testCoherence.isFrozen = false;
		testObject.size();
		testObject.add( "a" );
	}

	@Test public void can_query_when_frozen()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		TestClass testObject = new TestClass( testCoherence );
		testCoherence.isFrozen = true;
		testObject.readOperation();
	}

	@Test public void cannot_mutate_when_frozen()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		TestClass testObject = new TestClass( testCoherence );
		testCoherence.isFrozen = true;
		var exception = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception.coherence == testCoherence;
	}

	@Test public void cannot_read_when_not_in_context()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		TestClass testObject = new TestClass( testCoherence );
		testCoherence.isEntered = false;
		testCoherence.isFrozen = false;
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testObject.readOperation() );
		assert exception1.coherence == testCoherence;
	}

	@Test public void cannot_mutate_when_not_in_context()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		TestClass testObject = new TestClass( testCoherence );
		testCoherence.isEntered = false;
		testCoherence.isFrozen = false;
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.coherence == testCoherence;
	}
}
