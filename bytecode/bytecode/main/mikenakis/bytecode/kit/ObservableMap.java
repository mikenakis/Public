package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An observable {@link Collection}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ObservableMap<K, V> extends AbstractMap<K,V>
{
	private final Map<K,V> delegee;
	private final Runnable observer;
	private final ObservableSet<Entry<K,V>> entrySet;
	private final ObservableSet<K> keySet;
	private final ObservableSet<V> valueSet;

	public ObservableMap( Map<K,V> delegee, Runnable observer )
	{
		this.delegee = delegee;
		this.observer = observer;
		entrySet = new ObservableSet<>( delegee.entrySet(), observer );
		keySet = new ObservableSet<>( delegee.keySet(), observer );
		valueSet = new ObservableSet<>( delegee.values(), observer );
	}

	@Override public int size()
	{
		return delegee.size();
	}

	@Override public boolean containsValue( Object value )
	{
		return delegee.containsValue( value );
	}

	@Override public boolean containsKey( Object key )
	{
		return delegee.containsKey( key );
	}

	@Override public V get( Object key )
	{
		return Kit.map.tryGet( delegee, key );
	}

	@Override public V put( K key, V value )
	{
		V oldValue = Kit.map.tryGet( delegee, key );
		if( oldValue != null )
		{
			if( oldValue.equals( value ) )
				return oldValue;
			Kit.map.remove( delegee, key );
		}
		Kit.map.add( delegee, key, value );
		observer.run();
		return oldValue;
	}

	@Override public V remove( Object key )
	{
		V oldValue = Kit.map.tryRemove( delegee, key );
		if( oldValue == null )
			return null;
		observer.run();
		return oldValue;
	}

	@Override public void clear()
	{
		if( isEmpty() )
			return;
		delegee.clear();
		observer.run();
	}

	@Override public Set<K> keySet()
	{
		assert keySet.delegee == delegee.keySet();
		return keySet;
	}

	@Override public Collection<V> values()
	{
		assert valueSet.delegee == delegee.values();
		return valueSet;
	}

	@Override public Set<Entry<K,V>> entrySet()
	{
		assert entrySet.delegee == delegee.entrySet();
		return entrySet;
	}

	@Override public String toString()
	{
		return delegee.toString();
	}

	@SuppressWarnings( "EqualsWhichDoesntCheckParameterClass" )
	@Override public boolean equals( Object o )
	{
		return delegee.equals( o );
	}

	@Override public int hashCode()
	{
		return delegee.hashCode();
	}
}
