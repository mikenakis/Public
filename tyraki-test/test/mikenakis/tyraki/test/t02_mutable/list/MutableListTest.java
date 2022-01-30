package mikenakis.tyraki.test.t02_mutable.list;

import mikenakis.kit.Kit;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.UnmodifiableList;
import mikenakis.tyraki.test.t02_mutable.MutableCollectionTest;
import org.junit.Test;

/**
 * Test.
 *
 * @author michael.gr
 */
public abstract class MutableListTest<T> extends MutableCollectionTest<T>
{
	protected MutableListTest()
	{
	}

	@Override protected final MutableCollection<T> newCollection()
	{
		return newList();
	}

	protected abstract MutableList<T> newList();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void ArrayList_Get()
	{
		MutableList<T> list = newList();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.get( -1 ) );
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.get( 0 ) );
		list.add( a );
		assert list.get( 0 ) == a;
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.get( -1 ) );
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.get( 1 ) );
		list.add( b );
		list.add( c );
		assert list.get( 0 ) == a;
		assert list.get( 1 ) == b;
		assert list.get( 2 ) == c;
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.get( 3 ) );
	}

	@Test
	public void ArrayList_IndexOf()
	{
		MutableList<T> list = newList();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert list.indexOf( a ) == -1;
		assert list.indexOf( b ) == -1;
		assert list.indexOf( c ) == -1;
		list.add( a );
		assert list.indexOf( a ) == 0;
		assert list.indexOf( b ) == -1;
		assert list.indexOf( c ) == -1;
		list.add( b );
		assert list.indexOf( a ) == 0;
		assert list.indexOf( b ) == 1;
		assert list.indexOf( c ) == -1;
		list.add( c );
		assert list.indexOf( a ) == 0;
		assert list.indexOf( b ) == 1;
		assert list.indexOf( c ) == 2;
	}

	@Test
	public void ArrayList_lastIndexOf()
	{
		MutableList<T> list = newList();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		assert list.lastIndexOf( a ) == -1;
		assert list.lastIndexOf( b ) == -1;
		assert list.lastIndexOf( c ) == -1;
		list.add( a );
		assert list.lastIndexOf( a ) == 0;
		assert list.lastIndexOf( b ) == -1;
		assert list.lastIndexOf( c ) == -1;
		list.add( b );
		assert list.lastIndexOf( a ) == 0;
		assert list.lastIndexOf( b ) == 1;
		assert list.lastIndexOf( c ) == -1;
		list.add( c );
		assert list.lastIndexOf( a ) == 0;
		assert list.lastIndexOf( b ) == 1;
		assert list.lastIndexOf( c ) == 2;
	}

	@Test
	public void ArrayList_Insert()
	{
		MutableList<T> list = newList();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.insertAt( -1, a ) );
		list.insertAt( 0, b );
		assert list.equalsList( UnmodifiableList.of( b ) );
		list.insertAt( 0, a );
		assert list.equalsList( UnmodifiableList.of( a, b ) );
		list.insertAt( 2, c );
		assert list.equalsList( UnmodifiableList.of( a, b, c ) );
	}

	@Test
	public void ArrayList_Replace()
	{
		MutableList<T> list = newList();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.replaceAt( -1, a ) );
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.replaceAt( 0, a ) );
		list.add( a );
		list.add( b );
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.replaceAt( -1, a ) );
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.replaceAt( 2, a ) );
		list.replaceAt( 1, c );
		assert list.equalsList( UnmodifiableList.of( a, c ) );
		list.replaceAt( 0, b );
		assert list.equalsList( UnmodifiableList.of( b, c ) );
	}

	@Test
	public void ArrayList_RemoveAt()
	{
		MutableList<T> list = newList();
		T a = newElement();
		T b = newElement();
		T c = newElement();
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.removeAt( -1 ) );
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.removeAt( 0 ) );
		list.add( a );
		list.add( b );
		list.add( c );
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.removeAt( -1 ) );
		Kit.testing.expectException( ArrayIndexOutOfBoundsException.class, () -> list.removeAt( 3 ) );
		assert list.get( 1 ) == b;
		list.removeAt( 1 );
		assert list.equalsList( UnmodifiableList.of( a, c ) );
		assert list.get( 1 ) == c;
		list.removeAt( 1 );
		assert list.equalsList( UnmodifiableList.of( a ) );
		assert list.get( 0 ) == a;
		list.removeAt( 0 );
		assert list.isEmpty();
	}
}
