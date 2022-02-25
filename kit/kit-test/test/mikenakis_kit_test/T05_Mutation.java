package mikenakis_kit_test;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.guard.EndOfLifeException;
import mikenakis.kit.mutation.FreezableMutationContext;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.MutationDisallowedException;
import mikenakis.kit.mutation.OutOfMutationContextException;
import mikenakis.kit.mutation.ReadingDisallowedException;
import mikenakis.kit.mutation.SingleThreadedMutationContext;
import mikenakis.kit.mutation.TemporaryMutationContext;
import mikenakis.testkit.TestKit;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.UnmodifiableList;
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
		protected TestClass( MutationContext mutationContext )
		{
			super( mutationContext );
		}

		public void readingOperation()
		{
			assert mutationContext().canReadAssertion();
		}

		public void mutationOperation()
		{
			assert mutationContext().canMutateAssertion();
		}
	}

	private static class TestMutationContext implements MutationContext
	{
		boolean inContext;
		boolean isFrozen;

		@Override public boolean isInContextAssertion()
		{
			assert inContext : new OutOfMutationContextException( this );
			return true;
		}

		@Override public boolean isFrozen()
		{
			return isFrozen;
		}
	}

	@Test public void object_can_read_and_mutate_when_both_allowed()
	{
		TestMutationContext testMutationContext = new TestMutationContext();
		TestClass testObject = new TestClass( testMutationContext );
		testMutationContext.inContext = true;
		testMutationContext.isFrozen = false;
		testObject.readingOperation();
		testObject.mutationOperation();
	}

	@Test public void object_can_read_and_mutate_when_both_allowed2()
	{
		TestMutationContext testMutationContext = new TestMutationContext();
		MutableList<String> testObject = MutableCollections.of( testMutationContext ).newArrayList();
		testMutationContext.inContext = true;
		testMutationContext.isFrozen = false;
		testObject.size();
		testObject.add( "a" );
	}

	@Test public void object_can_read_but_not_mutate_when_frozen()
	{
		TestMutationContext testMutationContext = new TestMutationContext();
		TestClass testObject = new TestClass( testMutationContext );
		testMutationContext.inContext = true;
		testMutationContext.isFrozen = true;
		testObject.readingOperation();
		var exception = TestKit.expect( MutationDisallowedException.class, () -> testObject.mutationOperation() );
		assert exception.mutationContext == testMutationContext;
	}

	@Test public void object_can_neither_read_nor_mutate_when_not_in_context()
	{
		TestMutationContext testMutationContext = new TestMutationContext();
		TestClass testObject = new TestClass( testMutationContext );
		testMutationContext.inContext = false;
		testMutationContext.isFrozen = false;
		var exception1 = TestKit.expect( OutOfMutationContextException.class, () -> testObject.readingOperation() );
		assert exception1.mutationContext == testMutationContext;
		var exception2 = TestKit.expect( OutOfMutationContextException.class, () -> testObject.mutationOperation() );
		assert exception2.mutationContext == testMutationContext;
	}

	@Test public void test_TemporaryMutationContext()
	{
		TestClass testObject = Kit.tryGetWith( TemporaryMutationContext.of(), mutationContext -> //
		{
			TestClass t = new TestClass( mutationContext );
			t.readingOperation();
			t.mutationOperation();
			return t;
		} );
		var exception1 = TestKit.expect( EndOfLifeException.class, () -> testObject.readingOperation() );
		assert exception1.closeableClass == TemporaryMutationContext.class;
		var exception2 = TestKit.expect( EndOfLifeException.class, () -> testObject.mutationOperation() );
		assert exception2.closeableClass == TemporaryMutationContext.class;

		MutableList<String> testList = Kit.tryGetWith( TemporaryMutationContext.of(), mutationContext -> //
		{
			MutableList<String> t = MutableCollections.of( mutationContext ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		TestKit.expect( EndOfLifeException.class, () -> testList.size() );
		TestKit.expect( EndOfLifeException.class, () -> testList.add( "b" ) );

		UnmodifiableList<String> testList2 = Kit.tryGetWith( TemporaryMutationContext.of(), mutationContext -> //
		{
			MutableList<String> t = MutableCollections.of( mutationContext ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		TestKit.expect( EndOfLifeException.class, () -> testList2.size() );
		TestKit.expect( EndOfLifeException.class, () -> testList.add( "b" ) );
	}

	@Test public void test_FreezableMutationContext()
	{
		MutationContext parentMutationContext = SingleThreadedMutationContext.instance();
		TestClass testObject = Kit.tryGetWith( FreezableMutationContext.of( parentMutationContext ), mutationContext -> //
		{
			TestClass t = new TestClass( mutationContext );
			t.readingOperation();
			t.mutationOperation();
			return t;
		} );
		testObject.readingOperation();
		var exception2 = TestKit.expect( MutationDisallowedException.class, () -> testObject.mutationOperation() );
		assert exception2.mutationContext.getClass() == FreezableMutationContext.class;

		MutableList<String> testList = Kit.tryGetWith( FreezableMutationContext.of( parentMutationContext ), mutationContext -> //
		{
			MutableList<String> t = MutableCollections.of( mutationContext ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		testList.size();
		var exception4 = TestKit.expect( MutationDisallowedException.class, () -> testList.add( "b" ) );
		assert exception4.mutationContext.getClass() == FreezableMutationContext.class;

		MutableList<String> testList2 = Kit.tryGetWith( FreezableMutationContext.of( parentMutationContext ), mutationContext -> //
		{
			MutableList<String> t = MutableCollections.of( mutationContext ).newArrayList();
			t.size();
			t.add( "a" );
			return t;
		} );
		testList2.size();
		var exception5 = TestKit.expect( MutationDisallowedException.class, () -> testList2.add( "b" ) );
		assert exception5.mutationContext.getClass() == FreezableMutationContext.class;
	}
}
