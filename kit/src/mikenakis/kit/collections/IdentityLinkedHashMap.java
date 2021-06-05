package mikenakis.kit.collections;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IdentityLinkedHashMap<K, T> extends AbstractMap<K,T>
{
	static Equivalence<Object> equivalence = Equivalence.identity();
	private final InternalSet entrySet = new InternalSet();

	@Override public Set<Entry<K,T>> entrySet()
	{
		return entrySet;
	}

	@Override public T put( K k, T t )
	{
		return Kit.map.addOrReplace( entrySet.innerMap, equivalence.wrap( k ), t );
	}

	@Override public boolean containsKey( Object key )
	{
		//noinspection SuspiciousMethodCalls
		return entrySet.contains( key );
	}

	@Override public T remove( Object key )
	{
		return Kit.map.tryRemove( entrySet.innerMap, equivalence.wrap( key ) );
	}

	@Override public T get( Object key )
	{
		return Kit.map.tryGet( entrySet.innerMap, equivalence.wrap( key ) );
	}

	private class MyEntry implements Entry<K,T>
	{
		final Entry<Equivalence.Wrapper<K>,T> entry;

		MyEntry( Entry<Equivalence.Wrapper<K>,T> entry )
		{
			this.entry = entry;
		}

		@Override public K getKey()
		{
			return entry.getKey().get();
		}

		@Override public T getValue()
		{
			return entry.getValue();
		}

		@Override public T setValue( T value )
		{
			return entry.setValue( value );
		}
	}

	private class InternalSet extends AbstractSet<Entry<K,T>>
	{
		final Map<Equivalence.Wrapper<K>,T> innerMap = new LinkedHashMap<>();

		@Override public Iterator<Entry<K,T>> iterator()
		{
			Iterator<Entry<Equivalence.Wrapper<K>,T>> iterator = innerMap.entrySet().iterator();
			Function1<Entry<K,T>,Entry<Equivalence.Wrapper<K>,T>> converter = entry -> new MyEntry( entry );
			return new ConvertingIterator<>( iterator, converter );
		}

		@Override public boolean add( Entry<K,T> entry )
		{
			Equivalence.Wrapper<K> wrap = equivalence.wrap( entry.getKey() );
			return Kit.map.tryAdd( innerMap, wrap, entry.getValue() );
		}

		@Override public int size()
		{
			return innerMap.size();
		}

		@Override public boolean contains( Object element )
		{
			return innerMap.containsKey( equivalence.wrap( element ) );
		}
	}
}
