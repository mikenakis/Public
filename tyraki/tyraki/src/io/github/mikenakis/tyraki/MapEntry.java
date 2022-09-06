package io.github.mikenakis.tyraki;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * A concrete {@link Binding}.
 *
 * @param <K> the type of the key.
 * @param <V> the type of the value.
 *
 * @author michael.gr
 */
public class MapEntry<K, V> implements Binding<K,V>
{
	public static <K,V> MapEntry<K,V> of( Binding<? extends K,? extends V> binding )
	{
		return new MapEntry<>( binding.getKey(), binding.getValue() );
	}

	public static <K,V> MapEntry<K,V> of( K key, V value )
	{
		return new MapEntry<>( key, value );
	}

	public final K key;
	public final V value;

	private MapEntry( K key, V value )
	{
		assert key != null;
		this.key = key;
		this.value = value;
	}

	@Override public K getKey()
	{
		return key;
	}

	@Override public V getValue()
	{
		return value;
	}

	public boolean equals( MapEntry<K,V> other )
	{
		if( other == null )
			return false;
		if( this == other )
			return true;
		return equals( other.key, other.value );
	}

	public boolean equals( Binding<K,V> other )
	{
		if( other == null )
			return false;
		if( this == other )
			return true;
		return equals( other.getKey(), other.getValue() );
	}

	public boolean equals( K otherKey, V otherValue )
	{
		if( !key.equals( otherKey ) )
			return false;
		if( !Kit.equalByValue( value, otherValue ) )
			return false;
		return true;
	}

	@Deprecated @Override public boolean equals( Object obj )
	{
		if( obj == null )
			return false;
		if( obj instanceof MapEntry )
		{
			@SuppressWarnings( "unchecked" )
			MapEntry<K,V> other = (MapEntry<K,V>)obj;
			return equals( other.key, other.value );
		}
		if( obj instanceof Binding )
		{
			@SuppressWarnings( "unchecked" )
			Binding<K,V> other = (Binding<K,V>)obj;
			return equals( other.getKey(), other.getValue() );
		}
		assert false;
		return false;
	}

	@Override public int hashCode()
	{
		int result = key.hashCode();
		result = 31 * result + Objects.hashCode( value );
		return result;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "{" + key + "} -> {" + value + "}";
	}
}
