package mikenakis_kit_test;

import mikenakis.kit.Kit;
import mikenakis.kit.mutation.ConcreteFreezableMutationContext;
import mikenakis.kit.mutation.FreezableMutationContext;
import mikenakis.kit.mutation.MustBeFrozenException;
import mikenakis.kit.mutation.MustBeReadableException;
import mikenakis.kit.mutation.MustBeWritableException;
import mikenakis.kit.mutation.MustNotBeFrozenException;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.TemporaryMutationContext;
import mikenakis.kit.mutation.ThreadLocalMutationContext;
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

	private static class TestClass extends Mutable
	{
		TestClass( MutationContext mutationContext )
		{
			super( mutationContext );
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

	private static class TestMutationContext implements FreezableMutationContext
	{
		boolean isEntered;
		boolean isFrozen;

		TestMutationContext( boolean isEntered, boolean isFrozen )
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

		@Override public boolean mustBeFrozenAssertion()
		{
			assert isFrozen : new MustBeFrozenException( this );
			return true;
		}

		@Override public boolean mustNotBeFrozenAssertion()
		{
			assert !isFrozen : new MustNotBeFrozenException( this );
			return true;
		}

		@Override public String toString()
		{
			return "isEntered: " + isEntered + "; isFrozen: " + isFrozen;
		}
	}

	@Test public void object_can_read_and_mutate_when_both_allowed()
	{
		TestMutationContext testMutationContext = new TestMutationContext( true, false );
		TestClass testObject = new TestClass( testMutationContext );
		testMutationContext.isFrozen = false;
		testObject.readOperation();
		testObject.writeOperation();
	}

	@Test public void object_can_read_and_mutate_when_both_allowed2()
	{
		TestMutationContext testMutationContext = new TestMutationContext( true, false );
		MutableList<String> testObject = MutableCollections.of( testMutationContext ).newArrayList();
		testMutationContext.isFrozen = false;
		testObject.size();
		testObject.add( "a" );
	}

	@Test public void object_can_read_but_not_mutate_when_frozen()
	{
		TestMutationContext testMutationContext = new TestMutationContext( true, false );
		TestClass testObject = new TestClass( testMutationContext );
		testMutationContext.isFrozen = true;
		testObject.readOperation();
		var exception = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception.mutationContext == testMutationContext;
	}

	@Test public void object_can_neither_read_nor_mutate_when_not_in_context()
	{
		TestMutationContext testMutationContext = new TestMutationContext( true, false );
		TestClass testObject = new TestClass( testMutationContext );
		testMutationContext.isEntered = false;
		testMutationContext.isFrozen = false;
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testObject.readOperation() );
		assert exception1.mutationContext == testMutationContext;
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.mutationContext == testMutationContext;
	}

	@Test public void test_TemporaryMutationContext1()
	{
		TemporaryMutationContext temporaryMutationContext = TemporaryMutationContext.of();
		TestClass testObject = Kit.tryGetWith( temporaryMutationContext, mutationContext -> //
		{
			TestClass t = new TestClass( mutationContext );
			t.readOperation();
			t.writeOperation();
			return t;
		} );
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testObject.readOperation() );
		assert exception1.mutationContext == temporaryMutationContext;
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.mutationContext == temporaryMutationContext;
	}

	@Test public void test_TemporaryMutationContext2()
	{
		TemporaryMutationContext temporaryMutationContext = TemporaryMutationContext.of();
		MutableList<String> testList = Kit.tryGetWith( temporaryMutationContext, mutationContext -> //
		{
			MutableList<String> t = MutableCollections.of( mutationContext ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		var exception1 = TestKit.expect( MustBeReadableException.class, () -> testList.size() );
		assert exception1.mutationContext == temporaryMutationContext;
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testList.add( "b" ) );
		assert exception2.mutationContext == temporaryMutationContext;
	}

	@Test public void test_FreezableMutationContext()
	{
		MutationContext parentMutationContext = ThreadLocalMutationContext.instance();
		TestClass testObject = Kit.tryGetWith( FreezableMutationContext.of( parentMutationContext ), mutationContext -> //
		{
			TestClass t = new TestClass( mutationContext );
			t.readOperation();
			t.writeOperation();
			return t;
		} );
		testObject.readOperation();
		var exception2 = TestKit.expect( MustBeWritableException.class, () -> testObject.writeOperation() );
		assert exception2.mutationContext.getClass() == ConcreteFreezableMutationContext.class;

		MutableList<String> testList = Kit.tryGetWith( FreezableMutationContext.of( parentMutationContext ), mutationContext -> //
		{
			MutableList<String> t = MutableCollections.of( mutationContext ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		testList.size();
		var exception4 = TestKit.expect( MustBeWritableException.class, () -> testList.add( "b" ) );
		assert exception4.mutationContext.getClass() == ConcreteFreezableMutationContext.class;

		MutableList<String> testList2 = Kit.tryGetWith( FreezableMutationContext.of( parentMutationContext ), mutationContext -> //
		{
			MutableList<String> t = MutableCollections.of( mutationContext ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		testList2.size();
		var exception5 = TestKit.expect( MustBeWritableException.class, () -> testList2.add( "b" ) );
		assert exception5.mutationContext.getClass() == ConcreteFreezableMutationContext.class;
	}
}
