package mikenakis_kit_test;

import mikenakis.kit.Kit;
import mikenakis.coherence.AbstractCoherent;
import mikenakis.coherence.Coherence;
import mikenakis.coherence.FreezableCoherence;
import mikenakis.coherence.implementation.ConcreteFreezableCoherence;
import mikenakis.coherence.implementation.exceptions.MustBeReadableException;
import mikenakis.coherence.implementation.exceptions.MustBeWritableException;
import mikenakis.coherence.implementation.TemporaryCoherence;
import mikenakis.coherence.implementation.ThreadLocalCoherence;
import mikenakis.lifetime.Mortal;
import mikenakis.testkit.TestKit;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.mutable.MutableCollections;
import org.junit.Test;

/**
 * Test.
 */
public class T05_Mutation
{
	public T05_Mutation()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static class TestClass extends AbstractCoherent
	{
		TestClass( Coherence coherence )
		{
			super( coherence );
		}

		void readOperation()
		{
			assert mustBeReadableAssertion();
		}

		void writeOperation()
		{
			assert mustBeWritableAssertion();
		}
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

	@Test public void object_can_read_and_mutate_when_both_allowed()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		TestClass testObject = new TestClass( testCoherence );
		testCoherence.isFrozen = false;
		testObject.readOperation();
		testObject.writeOperation();
	}

	@Test public void object_can_read_and_mutate_when_both_allowed2()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		MutableList<String> testObject = MutableCollections.of( testCoherence ).newArrayList();
		testCoherence.isFrozen = false;
		testObject.size();
		testObject.add( "a" );
	}

	@Test public void object_can_read_but_not_mutate_when_frozen()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		TestClass testObject = new TestClass( testCoherence );
		testCoherence.isFrozen = true;
		testObject.readOperation();
		var exception = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception.coherence == testCoherence;
	}

	@Test public void object_can_neither_read_nor_mutate_when_not_in_context()
	{
		TestCoherence testCoherence = new TestCoherence( true, false );
		TestClass testObject = new TestClass( testCoherence );
		testCoherence.isEntered = false;
		testCoherence.isFrozen = false;
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testObject.readOperation() );
		assert exception1.coherence == testCoherence;
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.coherence == testCoherence;
	}

	@Test public void test_TemporaryCoherence1()
	{
		TemporaryCoherence temporaryCoherence = TemporaryCoherence.of();
		TestClass testObject = Mortal.tryGetWith( temporaryCoherence, coherence -> //
		{
			TestClass t = new TestClass( coherence );
			t.readOperation();
			t.writeOperation();
			return t;
		} );
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testObject.readOperation() );
		assert exception1.coherence == temporaryCoherence;
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.coherence == temporaryCoherence;
	}

	@Test public void test_TemporaryCoherence2()
	{
		TemporaryCoherence temporaryCoherence = TemporaryCoherence.of();
		MutableList<String> testList = Mortal.tryGetWith( temporaryCoherence, coherence -> //
		{
			MutableList<String> t = MutableCollections.of( coherence ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testList.size() );
		assert exception1.coherence == temporaryCoherence;
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testList.add( "b" ) );
		assert exception2.coherence == temporaryCoherence;
	}

	@Test public void test_FreezableCoherence()
	{
		Coherence parentCoherence = ThreadLocalCoherence.instance();
		TestClass testObject = Mortal.tryGetWith( ConcreteFreezableCoherence.create( parentCoherence ), coherence -> //
		{
			TestClass t = new TestClass( coherence );
			t.readOperation();
			t.writeOperation();
			return t;
		} );
		testObject.readOperation();
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.coherence.getClass() == ConcreteFreezableCoherence.class;

		MutableList<String> testList = Mortal.tryGetWith( ConcreteFreezableCoherence.create( parentCoherence ), coherence -> //
		{
			MutableList<String> t = MutableCollections.of( coherence ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		testList.size();
		var exception4 = TestKit.expect( MustBeWritableException.class, () -> testList.add( "b" ) );
		assert exception4.coherence.getClass() == ConcreteFreezableCoherence.class;

		MutableList<String> testList2 = Mortal.tryGetWith( ConcreteFreezableCoherence.create( parentCoherence ), coherence -> //
		{
			MutableList<String> t = MutableCollections.of( coherence ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		testList2.size();
		var exception5 = TestKit.expect( MustBeWritableException.class, () -> testList2.add( "b" ) );
		assert exception5.coherence.getClass() == ConcreteFreezableCoherence.class;
	}
}
