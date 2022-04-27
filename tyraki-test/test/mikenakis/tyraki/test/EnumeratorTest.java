package mikenakis.tyraki.test;

import mikenakis.debug.Debug;
import mikenakis.testkit.TestKit;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import org.junit.Test;

/**
 * Test.
 *
 * @author michael.gr
 */
public abstract class EnumeratorTest
{
	protected interface Factory<T>
	{
		MutableEnumerator<T> getResults();
	}

	protected EnumeratorTest()
	{
		if( !Debug.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@SuppressWarnings( "unchecked" )
	protected abstract <T> Factory<T> onCreateFactory( T... elements );

	@Test
	public void Empty_Results_Traversal()
	{
		Factory<String> factory = onCreateFactory();
		MutableEnumerator<String> enumerator = factory.getResults();
		checkForFinished( enumerator );
		assert enumerator.isFinished();
		checkForFinished( enumerator );
		assert enumerator.isFinished();
	}

	@Test
	public void Single_Element_Results_Traversal()
	{
		Factory<String> factory = onCreateFactory( "a" );
		MutableEnumerator<String> enumerator = factory.getResults();
		assert !enumerator.isFinished();
		assert enumerator.current().equals( "a" );
		assert !enumerator.isFinished();
		enumerator.moveNext();
		assert enumerator.isFinished();
		checkForFinished( enumerator );
	}

	@Test
	public void Single_Element_Results_Removal()
	{
		Factory<String> factory = onCreateFactory( "a" );
		MutableEnumerator<String> enumerator = factory.getResults();
		assert !enumerator.isFinished();
		assert enumerator.current().equals( "a" );
		assert !enumerator.isFinished();
		enumerator.deleteCurrent();
		checkForDeleted( enumerator );
		assert collect( factory ).isEmpty();
		enumerator.moveNext();
		assert enumerator.isFinished();
		checkForFinished( enumerator );
	}

	@Test
	public void Multiple_Element_Results_Traversal()
	{
		Factory<String> factory = onCreateFactory( "a", "b", "c" );
		MutableEnumerator<String> enumerator = factory.getResults();
		assert !enumerator.isFinished();
		assert enumerator.current().equals( "a" );
		assert !enumerator.isFinished();
		enumerator.moveNext();
		assert enumerator.current().equals( "b" );
		assert !enumerator.isFinished();
		enumerator.moveNext();
		assert enumerator.current().equals( "c" );
		assert !enumerator.isFinished();
		enumerator.moveNext();
		assert enumerator.isFinished();
		checkForFinished( enumerator );
	}

	@Test
	public void Multiple_Element_Results_Removal()
	{
		Factory<String> factory = onCreateFactory( "a", "b", "c" );
		MutableEnumerator<String> enumerator = factory.getResults();
		assert !enumerator.isFinished();
		assert enumerator.current().equals( "a" );
		assert !enumerator.isFinished();
		enumerator.moveNext();
		assert enumerator.current().equals( "b" );
		enumerator.deleteCurrent();
		checkForDeleted( enumerator );
		enumerator.moveNext();
		assert collect( factory ).equalsCollection( UnmodifiableCollection.of( "a", "c" ) );
		assert !enumerator.isFinished();
		assert enumerator.current().equals( "c" );
		enumerator.deleteCurrent();
		checkForDeleted( enumerator );
		enumerator.moveNext();
		assert collect( factory ).equalsCollection( UnmodifiableCollection.of( "a" ) );
		checkForFinished( enumerator );
		assert enumerator.isFinished();
		checkForFinished( enumerator );
	}

	private static void checkForFinished( MutableEnumerator<?> enumerator )
	{
		TestKit.expect( IllegalStateException.class, () -> enumerator.current() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.deleteCurrent() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.moveNext() );
	}

	private static void checkForDeleted( MutableEnumerator<?> enumerator )
	{
		TestKit.expect( IllegalStateException.class, () -> enumerator.current() );
		TestKit.expect( IllegalStateException.class, () -> enumerator.deleteCurrent() );
	}

	private <T> UnmodifiableCollection<T> collect( Factory<T> factory )
	{
		MutableEnumerator<T> enumerator = factory.getResults();
		MutableCollection<T> collection = SingleThreadedMutableCollections.instance().newArrayList();
		while( !enumerator.isFinished() )
		{
			T element = enumerator.current();
			collection.add( element );
			enumerator.moveNext();
		}
		return collection;
	}
}
