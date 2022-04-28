package mikenakis.tyraki.test;

import mikenakis.kit.Kit;
import mikenakis.testkit.TestKit;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Test.
 *
 * @author michael.gr
 */
public abstract class IteratorTest
{
	protected IteratorTest()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@SuppressWarnings( "unchecked" )
	protected abstract <T> Iterable<T> onCreateIterable( T... elements );

	@Test
	public void Empty_Iterator_Traversal()
	{
		Iterable<String> iterable = onCreateIterable();
		Iterator<String> iterator = iterable.iterator();
		checkForIllegalStateException( iterator );
		checkForNoSuchElementException( iterator );
		assert !iterator.hasNext();
		checkForIllegalStateException( iterator );
		checkForNoSuchElementException( iterator );
	}

	@Test
	public void Single_Element_Iterator_Traversal()
	{
		Iterable<String> iterable = onCreateIterable( "a" );
		Iterator<String> iterator = iterable.iterator();
		checkForIllegalStateException( iterator );
		assert iterator.hasNext();
		assert iterator.next().equals( "a" );
		assert !iterator.hasNext();
		checkForNoSuchElementException( iterator );
	}

	@Test
	public void Single_Element_Iterator_Blind_Traversal() //blind traversal is when we don't invoke 'hasNext()' before invoking 'next()'.
	{
		Iterable<String> iterable = onCreateIterable( "a" );
		Iterator<String> iterator = iterable.iterator();
		checkForIllegalStateException( iterator );
		//assert iterator.hasNext(); do not invoke hasNext(), since this is a blind traversal!
		assert iterator.next().equals( "a" );
		checkForNoSuchElementException( iterator );
		assert !iterator.hasNext();
		checkForNoSuchElementException( iterator );
	}

	@Test
	public void Single_Element_Iterator_Removal()
	{
		Iterable<String> iterable = onCreateIterable( "a" );
		Iterator<String> iterator = iterable.iterator();
		checkForIllegalStateException( iterator );
		assert iterator.hasNext();
		assert iterator.next().equals( "a" );
		checkForNoSuchElementException( iterator );
		assert !iterator.hasNext();
		checkForNoSuchElementException( iterator );
		iterator.remove();
		assert collect( iterable ).isEmpty();
		assert !iterator.hasNext();
		checkForIllegalStateException( iterator );
	}

	@Test
	public void Single_Element_Iterator_Blind_Removal()
	{
		Iterable<String> iterable = onCreateIterable( "a" );
		Iterator<String> iterator = iterable.iterator();
		checkForIllegalStateException( iterator );
		//assert iterator.hasNext(); do not invoke hasNext(), since this is a blind traversal!
		assert iterator.next().equals( "a" );
		checkForNoSuchElementException( iterator );
		//assert !iterator.hasNext(); do not invoke hasNext(), since this is a blind traversal!
		checkForNoSuchElementException( iterator );
		iterator.remove();
		assert collect( iterable ).isEmpty();
		assert !iterator.hasNext();
		checkForIllegalStateException( iterator );
	}

	@Test
	public void Multiple_Element_Iterator_Traversal()
	{
		Iterable<String> iterable = onCreateIterable( "a", "b", "c" );
		Iterator<String> iterator = iterable.iterator();
		checkForIllegalStateException( iterator );
		assert iterator.hasNext();
		assert iterator.next().equals( "a" );
		assert iterator.hasNext();
		assert iterator.next().equals( "b" );
		assert iterator.hasNext();
		assert iterator.next().equals( "c" );
		assert !iterator.hasNext();
	}

	@Test
	public void Multiple_Element_Iterator_Removal()
	{
		Iterable<String> iterable = onCreateIterable( "a", "b", "c" );
		Iterator<String> iterator = iterable.iterator();
		checkForIllegalStateException( iterator );
		assert iterator.hasNext();
		assert iterator.next().equals( "a" );
		assert iterator.hasNext();
		assert iterator.next().equals( "b" );
		iterator.remove();
		assert collect( iterable ).equalsCollection( UnmodifiableCollection.of( "a", "c" ) );
		checkForIllegalStateException( iterator );
		assert iterator.hasNext();
		assert iterator.next().equals( "c" );
		iterator.remove();
		assert collect( iterable ).equalsCollection( UnmodifiableCollection.of( "a" ) );
		checkForIllegalStateException( iterator );
		assert !iterator.hasNext();
		checkForIllegalStateException( iterator );
	}

	@Test
	public void Multiple_Element_Iterator_Blind_Removal()
	{
		Iterable<String> iterable = onCreateIterable( "a", "b", "c" );
		Iterator<String> iterator = iterable.iterator();
		checkForIllegalStateException( iterator );
		//assert iterator.hasNext(); do not invoke hasNext(), since this is a blind traversal!
		assert iterator.next().equals( "a" );
		//assert iterator.hasNext(); do not invoke hasNext(), since this is a blind traversal!
		assert iterator.next().equals( "b" );
		iterator.remove();
		assert collect( iterable ).equalsCollection( UnmodifiableCollection.of( "a", "c" ) );
		checkForIllegalStateException( iterator );
		//assert iterator.hasNext(); do not invoke hasNext(), since this is a blind traversal!
		assert iterator.next().equals( "c" );
		iterator.remove();
		assert collect( iterable ).equalsCollection( UnmodifiableCollection.of( "a" ) );
		checkForIllegalStateException( iterator );
		//assert !iterator.hasNext(); do not invoke hasNext(), since this is a blind traversal!
		checkForIllegalStateException( iterator );
	}

	private static void checkForIllegalStateException( Iterator<?> iterator )
	{
		TestKit.expect( IllegalStateException.class, () -> iterator.remove() );
	}

	private static void checkForNoSuchElementException( Iterator<?> iterator )
	{
		TestKit.expect( NoSuchElementException.class, () -> iterator.next() );
	}

	private <T> UnmodifiableCollection<T> collect( Iterable<T> iterable )
	{
		MutableCollection<T> collection = SingleThreadedMutableCollections.instance().newArrayList();
		for( T element : iterable )
			collection.add( element );
		return collection;
	}
}
