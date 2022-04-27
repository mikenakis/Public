package mikenakis.tyraki.test.t02_mutable;

import mikenakis.debug.Debug;
import mikenakis.kit.Kit;
import mikenakis.testkit.TestKit;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Test.
 *
 * @author michael.gr
 */
public abstract class MutableCollectionTest<T>
{
	protected MutableCollectionTest()
	{
		if( !Debug.areAssertionsEnabled() )
			throw new AssertionError();
	}

	protected abstract MutableCollection<T> newCollection();

	protected abstract T newElement();

	protected abstract boolean isOrdered();

	@Test
	public void IsEmpty_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		assert collection.isEmpty();
		collection.add( a );
		assert collection.nonEmpty();
		collection.remove( a );
		assert collection.isEmpty();
	}

	@Test
	public void GetLength_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		assert collection.isEmpty();
		assert collection.isEmpty();
		collection.add( a );
		assert collection.size() == 1;
		collection.add( b );
		assert collection.size() == 2;
		collection.remove( a );
		assert collection.size() == 1;
		collection.remove( b );
		assert collection.isEmpty();
	}

	@Test
	public void Add_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert collection.isEmpty();
		collection.add( a );
		assert collection.equalsCollection( UnmodifiableCollection.of( a ) );
		collection.add( b );
		assert collection.equalsCollection( UnmodifiableCollection.of( a, b ) );
		collection.add( c );
		assert collection.equalsCollection( UnmodifiableCollection.of( a, b, c ) );
		collection.clear();
		collection.isEmpty();
	}

	@Test
	public void Contains_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert collection.isEmpty();
		assert !collection.contains( a );
		collection.add( a );
		assert collection.contains( a );
		collection.add( b );
		assert collection.contains( a );
		assert collection.contains( b );
		collection.add( c );
		assert collection.contains( a );
		assert collection.contains( b );
		assert collection.contains( c );
		collection.remove( c );
		assert collection.contains( a );
		assert collection.contains( b );
		assert !collection.contains( c );
		collection.remove( a );
		assert !collection.contains( a );
		assert collection.contains( b );
		assert !collection.contains( c );
		collection.clear();
		assert !collection.contains( a );
		assert !collection.contains( b );
		assert !collection.contains( c );
	}

	@Test
	public void TryAdd_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert collection.isEmpty();
		assert collection.tryAdd( a ).isEmpty();
		assert collection.equalsCollection( UnmodifiableCollection.of( a ) );
		assert collection.tryAdd( b ).isEmpty();
		assert collection.equalsCollection( UnmodifiableCollection.of( a, b ) );
		assert collection.tryAdd( c ).isEmpty();
		assert collection.equalsCollection( UnmodifiableCollection.of( a, b, c ) );
	}

	@Test
	public void TryRemove_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert collection.isEmpty();
		assert !collection.tryRemove( a );
		assert !collection.tryRemove( b );
		assert !collection.tryRemove( c );
		collection.add( a );
		collection.add( b );
		collection.add( c );
		assert collection.tryRemove( c );
		assert !collection.tryRemove( c );
		assert collection.equalsCollection( UnmodifiableCollection.of( a, b ) );
		assert collection.tryRemove( a );
		assert !collection.tryRemove( a );
		assert !collection.tryRemove( c );
		assert collection.equalsCollection( UnmodifiableCollection.of( b ) );
		assert collection.tryRemove( b );
		assert !collection.tryRemove( a );
		assert !collection.tryRemove( b );
		assert !collection.tryRemove( c );
		collection.isEmpty();
	}

	@Test
	public void Remove_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert collection.isEmpty();
		TestKit.expect( NoSuchElementException.class, () -> collection.remove( a ) );
		collection.add( a );
		collection.add( b );
		collection.add( c );
		collection.remove( c );
		assert collection.equalsCollection( UnmodifiableCollection.of( a, b ) );
		TestKit.expect( NoSuchElementException.class, () -> collection.remove( c ) );
		collection.remove( a );
		assert collection.equalsCollection( UnmodifiableCollection.of( b ) );
		collection.remove( b );
		collection.isEmpty();
	}

	@Test
	public void Clear_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert collection.isEmpty();
		assert !collection.clear();
		collection.add( a );
		assert collection.clear();
		collection.isEmpty();
		assert !collection.clear();
		collection.isEmpty();
		collection.add( a );
		collection.add( b );
		collection.add( c );
		assert collection.clear();
		collection.isEmpty();
	}

	@Test
	public void Equals_Tests_Out()
	{
		MutableCollection<T> collection1 = newCollection();
		MutableCollection<T> collection2 = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert collection1.equalsCollection( collection2 );
		collection1.add( a );
		assert !collection1.equalsCollection( collection2 );
		collection2.add( a );
		assert collection1.equalsCollection( collection2 );
		collection1.add( b );
		assert !collection1.equalsCollection( collection2 );
		collection2.add( b );
		assert collection1.equalsCollection( collection2 );
		collection1.add( c );
		assert !collection1.equalsCollection( collection2 );
		collection2.add( c );
		assert collection1.equalsCollection( collection2 );
		collection1.clear();
		assert !collection1.equalsCollection( collection2 );
		collection2.clear();
		assert collection1.equalsCollection( collection2 );
	}

	@Test
	public void Enumeration_Of_Empty_Collection_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		UnmodifiableEnumerator<T> enumerator = collection.newUnmodifiableEnumerator();
		assert enumerator.isFinished();
		TestKit.expect( IllegalStateException.class, () -> enumerator.current() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.moveNext() );
	}

	@Test
	public void Enumeration_Of_Single_Element_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		collection.add( a );
		UnmodifiableEnumerator<T> enumerator = collection.newUnmodifiableEnumerator();
		assert !enumerator.isFinished();
		assert enumerator.current().equals( a );
		enumerator.moveNext();
		TestKit.expect( IllegalStateException.class, () -> enumerator.current() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.moveNext() );
	}

	@Test
	public void Enumeration_Of_Multiple_Elements_Tests_Out()
	{
		MutableCollection<T> collection1 = newCollection();
		MutableCollection<T> collection2 = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		collection1.add( a );
		collection1.add( b );
		collection1.add( c );
		UnmodifiableEnumerator<T> enumerator = collection1.newUnmodifiableEnumerator();
		assert !enumerator.isFinished();
		collection2.add( enumerator.current() );
		enumerator.moveNext();
		assert !enumerator.isFinished();
		collection2.add( enumerator.current() );
		enumerator.moveNext();
		assert !enumerator.isFinished();
		collection2.add( enumerator.current() );
		enumerator.moveNext();
		assert enumerator.isFinished();
		assert collection2.equalsCollection( UnmodifiableCollection.of( a, b, c ) );
		TestKit.expect( IllegalStateException.class, () -> enumerator.current() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.moveNext() );
	}

	@Test
	public void Deletion_From_Enumerator_Tests_Out()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		collection.add( a );
		collection.add( b );
		collection.add( c );
		MutableEnumerator<T> enumerator = collection.newMutableEnumerator();
		assert collection.equalsCollection( UnmodifiableCollection.of( a, b, c ) );
		enumerator.deleteCurrent();
		assert collection.size() == 2;
		enumerator.moveNext();
		enumerator.current();
		enumerator.deleteCurrent();
		assert collection.size() == 1;
		enumerator.moveNext();
		enumerator.current();
		enumerator.deleteCurrent();
		enumerator.moveNext();
		assert enumerator.isFinished();
		assert collection.isEmpty();
	}

	@Test
	public void Deletion_From_Enumerator_When_Empty_Throws()
	{
		MutableCollection<T> collection = newCollection();
		MutableEnumerator<T> enumerator = collection.newMutableEnumerator();
		TestKit.expect( IllegalStateException.class, () -> enumerator.deleteCurrent() );
	}

	@Test
	public void Read_Operations_During_Enumeration_Succeed()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		collection.add( a );
		collection.add( b );
		UnmodifiableEnumerator<T> enumerator = collection.newUnmodifiableEnumerator();
		assert !enumerator.isFinished();
		enumerator.moveNext();
		assert !enumerator.isFinished();
		enumerator.current();
		assert collection.contains( a );
		assert collection.countElements() == 2;
		assert collection.size() == 2;
		collection.toArrayOfObject();
		assert !enumerator.isFinished();
		enumerator.current();
		enumerator.moveNext();
		assert enumerator.isFinished();
	}

	@Test
	public void Invalid_Operation_While_Deleting_From_Enumerator_Throws()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		collection.add( a );
		collection.add( b );
		collection.add( c );
		MutableEnumerator<T> enumerator = collection.newMutableEnumerator();
		assert collection.equalsCollection( UnmodifiableCollection.of( a, b, c ) );
		enumerator.deleteCurrent();
		assert collection.size() == 2;
		TestKit.expect( IllegalStateException.class, () -> enumerator.isFinished() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.current() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.deleteCurrent() );
		enumerator.moveNext();
		enumerator.current();
		enumerator.deleteCurrent();
		assert collection.size() == 1;
		TestKit.expect( IllegalStateException.class, () -> enumerator.isFinished() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.current() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.deleteCurrent() );
		enumerator.moveNext();
		enumerator.deleteCurrent();
		TestKit.expect( IllegalStateException.class, () -> enumerator.isFinished() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.current() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.deleteCurrent() );
		enumerator.moveNext();
		assert enumerator.isFinished();
		assert collection.isEmpty();
	}

	@Test
	public void Add_During_Enumeration_Fails_With_Concurrent_Modification()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		collection.add( a );
		collection.add( b );
		UnmodifiableEnumerator<T> enumerator = collection.newUnmodifiableEnumerator();
		assert !enumerator.isFinished();
		enumerator.current();
		enumerator.moveNext();
		assert !enumerator.isFinished();
		enumerator.current();
		collection.add( c );
		enumerator.isFinished(); /** this is supposed to work irrespective of {@link ConcurrentModificationException} */
		TestKit.expect( ConcurrentModificationException.class, () -> enumerator.current() );
		TestKit.expect( ConcurrentModificationException.class, () -> enumerator.moveNext() );
	}

	@Test
	public void Remove_During_Enumeration_Fails_With_Concurrent_Modification()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		collection.add( a );
		collection.add( b );
		UnmodifiableEnumerator<T> enumerator = collection.newUnmodifiableEnumerator();
		assert !enumerator.isFinished();
		enumerator.current();
		enumerator.moveNext();
		assert !enumerator.isFinished();
		enumerator.current();
		collection.remove( a );
		enumerator.isFinished(); /** this is supposed to work irrespective of {@link ConcurrentModificationException} */
		TestKit.expect( ConcurrentModificationException.class, () -> enumerator.current() );
		TestKit.expect( ConcurrentModificationException.class, () -> enumerator.moveNext() );
	}

	@Test
	public void Clear_During_Enumeration_Fails_With_Concurrent_Modification()
	{
		MutableCollection<T> collection = newCollection();
		T a = newElement();
		T b = newElement();
		collection.add( a );
		collection.add( b );
		UnmodifiableEnumerator<T> enumerator = collection.newUnmodifiableEnumerator();
		assert !enumerator.isFinished();
		Collection<T> javaList = new ArrayList<>();
		Kit.collection.add( javaList, enumerator.current() );
		enumerator.moveNext();
		assert !enumerator.isFinished();
		Kit.collection.add( javaList, enumerator.current() );
		assert javaList.containsAll( Arrays.asList( a, b ) );
		assert !isOrdered() || javaList.equals( Arrays.asList( a, b ) );
		collection.clear();
		enumerator.isFinished(); /** this is supposed to work irrespective of {@link ConcurrentModificationException} */
		TestKit.expect( ConcurrentModificationException.class, () -> enumerator.current() );
		TestKit.expect( ConcurrentModificationException.class, () -> enumerator.moveNext() );
	}
}
