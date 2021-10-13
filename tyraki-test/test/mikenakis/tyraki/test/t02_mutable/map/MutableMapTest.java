package mikenakis.tyraki.test.t02_mutable.map;

import mikenakis.kit.Kit;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableMap;
import mikenakis.tyraki.exceptions.DuplicateKeyException;
import mikenakis.tyraki.exceptions.KeyNotFoundException;
import mikenakis.tyraki.test.t02_mutable.MutableCollectionTest;
import org.junit.Test;

import java.util.Objects;

/**
 * Test.
 *
 * @author michael.gr
 */
public abstract class MutableMapTest<K> extends MutableCollectionTest<Binding<K,String>>
{
	private static final String[] values = { "v1", "v3", "v4", "v5", "v6", "v7", "v8", "v9" };
	private int valueIndex;

	protected MutableMapTest()
	{
	}

	@Override protected MutableCollection<Binding<K,String>> newCollection()
	{
		return newMap().mutableEntries();
	}

	@Override protected Binding<K,String> newElement()
	{
		K key = newKey();
		String value = values[valueIndex++ % values.length];
		return MapEntry.of( key, value );
	}

	protected abstract MutableMap<K,String> newMap();

	protected abstract K newKey();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void Newly_Created_HashMap_Is_Empty()
	{
		assert newMap().isEmpty();
		assert newMap().keys().isEmpty();
		assert newMap().values().isEmpty();
	}

	@Test
	public void HashMap_Add()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		Binding<K,String> b44 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		map.add( b11.getKey(), b11.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey() ) );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue() ) );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11 ) );
		map.add( b22.getKey(), b22.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey(), b22.getKey() ) );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue(), b22.getValue() ) );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22 ) );
		map.add( b33.getKey(), b33.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey(), b22.getKey(), b33.getKey() ) );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue(), b22.getValue(), b33.getValue() ) );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22, b33 ) );
		map.add( b44.getKey(), b44.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey(), b22.getKey(), b33.getKey(), b44.getKey() ) );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue(), b22.getValue(), b33.getValue(), b44.getValue() ) );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22, b33, b44 ) );
	}

	@Test
	public void HashMap_ContainsKey()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		assert !map.containsKey( b11.getKey() );
		assert !map.containsKey( b22.getKey() );
		assert !map.containsKey( b33.getKey() );
		map.add( b11.getKey(), b11.getValue() );
		assert map.containsKey( b11.getKey() );
		assert !map.containsKey( b22.getKey() );
		assert !map.containsKey( b33.getKey() );
		map.add( b22.getKey(), b22.getValue() );
		assert map.containsKey( b11.getKey() );
		assert map.containsKey( b22.getKey() );
		assert !map.containsKey( b33.getKey() );
		map.add( b33.getKey(), b33.getValue() );
		assert map.containsKey( b11.getKey() );
		assert map.containsKey( b22.getKey() );
		assert map.containsKey( b33.getKey() );
	}

	@Test
	public void HashMap_ContainsValue()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		assert !map.containsValue( b11.getValue() );
		assert !map.containsValue( b22.getValue() );
		assert !map.containsValue( b33.getValue() );
		map.add( b11.getKey(), b11.getValue() );
		assert map.containsValue( b11.getValue() );
		assert !map.containsValue( b22.getValue() );
		assert !map.containsValue( b33.getValue() );
		map.add( b22.getKey(), b22.getValue() );
		assert map.containsValue( b11.getValue() );
		assert map.containsValue( b22.getValue() );
		assert !map.containsValue( b33.getValue() );
		map.add( b33.getKey(), b33.getValue() );
		assert map.containsValue( b11.getValue() );
		assert map.containsValue( b22.getValue() );
		assert map.containsValue( b33.getValue() );
	}

	@Test
	public void HashMap_Equals()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map1 = newMap();
		MutableMap<K,String> map2 = newMap();
		assert map1.equals( map2 );
		map1.add( b11.getKey(), b11.getValue() );
		assert !map1.equals( map2 );
		map2.add( b11.getKey(), b11.getValue() );
		assert map1.equals( map2 );
		map1.add( b22.getKey(), b22.getValue() );
		assert !map1.equals( map2 );
		map2.add( b22.getKey(), b22.getValue() );
		assert map1.equals( map2 );
		map1.add( b33.getKey(), b33.getValue() );
		assert !map1.equals( map2 );
		map2.add( b33.getKey(), b22.getValue() );
		assert !map1.equals( map2 );
		map2.replaceValue( b33.getKey(), b33.getValue() );
		assert map1.equals( map2 );
		map1.clear();
		assert !map1.equals( map2 );
		map2.clear();
		assert map1.equals( map2 );
	}

	@Test
	public void HashMap_TryGet()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		assert map.tryGet( b11.getKey() ).isEmpty();
		assert map.tryGet( b22.getKey() ).isEmpty();
		assert map.tryGet( b33.getKey() ).isEmpty();
		map.add( b11.getKey(), b11.getValue() );
		assert Objects.equals( map.get( b11.getKey() ), b11.getValue() );
		assert map.tryGet( b22.getKey() ).isEmpty();
		assert map.tryGet( b33.getKey() ).isEmpty();
		map.add( b22.getKey(), b22.getValue() );
		assert Objects.equals( map.get( b11.getKey() ), b11.getValue() );
		assert Objects.equals( map.get( b22.getKey() ), b22.getValue() );
		assert map.tryGet( b33.getKey() ).isEmpty();
		map.add( b33.getKey(), b33.getValue() );
		assert Objects.equals( map.get( b11.getKey() ), b11.getValue() );
		assert Objects.equals( map.get( b22.getKey() ), b22.getValue() );
		assert Objects.equals( map.get( b33.getKey() ), b33.getValue() );
	}

	@Test
	public void HashMap_Get()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		testGetFailure( map, b11.getKey() );
		testGetFailure( map, b22.getKey() );
		testGetFailure( map, b33.getKey() );
		map.add( b11.getKey(), b11.getValue() );
		testGetSuccess( map, b11.getKey(), b11.getValue() );
		testGetFailure( map, b22.getKey() );
		testGetFailure( map, b33.getKey() );
		map.add( b22.getKey(), b22.getValue() );
		testGetSuccess( map, b11.getKey(), b11.getValue() );
		testGetSuccess( map, b22.getKey(), b22.getValue() );
		testGetFailure( map, b33.getKey() );
		map.add( b33.getKey(), b33.getValue() );
		testGetSuccess( map, b11.getKey(), b11.getValue() );
		testGetSuccess( map, b22.getKey(), b22.getValue() );
		testGetSuccess( map, b33.getKey(), b33.getValue() );
	}

	private static <K, T> void testGetSuccess( UnmodifiableMap<K,T> aMap, K key, T value )
	{
		T foundValue = aMap.get( key );
		assert foundValue == value;
	}

	private void testGetFailure( UnmodifiableMap<K,String> aMap, K key )
	{
		KeyNotFoundException exception = Kit.testing.expectException( KeyNotFoundException.class, () -> aMap.get( key ) );
		assert exception.key == key;
	}

	@Test
	public void HashMap_UnmodifiableBindings()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		map.add( b11.getKey(), b11.getValue() );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11 ) );
		map.add( b22.getKey(), b22.getValue() );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22 ) );
		map.add( b33.getKey(), b33.getValue() );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22, b33 ) );
		map.removeKey( b11.getKey() );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b22, b33 ) );
		map.clear();
		assert map.isEmpty();
	}

	@Test
	public void HashMap_UnmodifiableKeys()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		assert map.keys().isEmpty();
		map.add( b11.getKey(), b11.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey() ) );
		map.add( b22.getKey(), b22.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey(), b22.getKey() ) );
		map.add( b33.getKey(), b33.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey(), b22.getKey(), b33.getKey() ) );
		map.removeKey( b11.getKey() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b22.getKey(), b33.getKey() ) );
		map.clear();
		assert map.keys().isEmpty();
	}

	@Test
	public void HashMap_UnmodifiableValues()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		assert map.values().isEmpty();
		map.add( b11.getKey(), b11.getValue() );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue() ) );
		map.add( b22.getKey(), b22.getValue() );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue(), b22.getValue() ) );
		map.add( b33.getKey(), b33.getValue() );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue(), b22.getValue(), b33.getValue() ) );
		map.removeKey( b11.getKey() );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b22.getValue(), b33.getValue() ) );
		map.clear();
		assert map.values().isEmpty();
	}

	@Test
	public void HashMap_Bindings()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		map.mutableEntries().add( b11 );
		assert map.size() == 1;
		assert Objects.equals( map.get( b11.getKey() ), b11.getValue() );
		map.mutableEntries().add( b22 );
		assert map.size() == 2;
		assert Objects.equals( map.get( b22.getKey() ), b22.getValue() );
		map.mutableEntries().add( b33 );
		assert map.size() == 3;
		assert Objects.equals( map.get( b33.getKey() ), b33.getValue() );
		assert map.mutableEntries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22, b33 ) );
		map.mutableEntries().remove( b33 );
		assert map.size() == 2;
		assert !map.containsKey( b33.getKey() );
		assert !map.containsValue( b33.getValue() );
		assert map.mutableEntries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22 ) );
		map.clear();
		assert map.isEmpty();
	}

	@Test
	public void Adding_Duplicate_Key_Throws()
	{
		Binding<K,String> b11 = newElement();
		Binding<K,String> b22 = newElement();
		Binding<K,String> b33 = newElement();
		Binding<K,String> b32 = MapEntry.of( b33.getKey(), b22.getValue() );
		assert b32.getKey().equals( b33.getKey() );
		assert Objects.equals( b32.getValue(), b22.getValue() );
		MutableMap<K,String> map = newMap();
		assert map.isEmpty();
		map.add( b11.getKey(), b11.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey() ) );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue() ) );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11 ) );
		map.add( b22.getKey(), b22.getValue() );
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey(), b22.getKey() ) );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue(), b22.getValue() ) );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22 ) );
		map.add( b33.getKey(), b22.getValue() ); //adding V2 again
		assert map.keys().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getKey(), b22.getKey(), b33.getKey() ) );
		assert map.values().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11.getValue(), b22.getValue(), b22.getValue() ) );
		assert map.entries().equalsUnmodifiableCollection( UnmodifiableCollection.of( b11, b22, b32 ) );
		DuplicateKeyException exception = Kit.testing.expectException( DuplicateKeyException.class, () -> map.add( b33.getKey(), b33.getValue() ) );
		assert exception.key == b33.getKey();
		assert exception.oldValue.equals( b22.getValue() );
		assert exception.newValue.equals( b33.getValue() );
	}
}
