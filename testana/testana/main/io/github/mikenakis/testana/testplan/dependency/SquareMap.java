package io.github.mikenakis.testana.testplan.dependency;

import io.github.mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class SquareMap<X,Y,V>
{
	private static final class Key<X,Y>
	{
		final X x;
		final Y y;

		private Key( X x, Y y )
		{
			assert x != null;
			assert y != null;
			this.x = x;
			this.y = y;
		}

		@Override public boolean equals( Object other )
		{
			if( this == other )
				return true;
			assert getClass() == other.getClass();
			return equals( (Key<?,?>)other );
		}

		public boolean equals( Key<?,?> key )
		{
			if( !x.equals( key.x ) )
				return false;
			if( !y.equals( key.y ) )
				return false;
			return true;
		}

		@Override public int hashCode()
		{
			int result = x.hashCode();
			result = 31 * result + y.hashCode();
			return result;
		}
	}

	private final Map<Key<X,Y>,V> matrix = new HashMap<>();

	public SquareMap()
	{
	}

	public Optional<V> tryGet( X x, Y y )
	{
		Key<X,Y> key = new Key<>( x, y );
		return Optional.ofNullable( Kit.map.tryGet( matrix, key ) );
	}

	public void add( X x, Y y, V value )
	{
		Key<X,Y> key = new Key<>( x, y );
		Kit.map.add( matrix, key, value );
	}

	public boolean tryAdd( X x, Y y, V value )
	{
		Key<X,Y> key = new Key<>( x, y );
		return Kit.map.tryAdd( matrix, key, value );
	}

	public void replace( X x, Y y, V value )
	{
		Key<X,Y> key = new Key<>( x, y );
		Kit.map.replace( matrix, key, value );
	}

	public void addOrReplace( X x, Y y, V value )
	{
		Key<X,Y> key = new Key<>( x, y );
		Kit.map.addOrReplace( matrix, key, value );
	}

	public boolean contains( X x, Y y )
	{
		Key<X,Y> key = new Key<>( x, y );
		return matrix.containsKey( key );
	}

	public int size()
	{
		return matrix.size();
	}

	public boolean addOrRecompute( X x, Y y, V value, Function<V,V> computation )
	{
		Optional<V> previousValue = tryGet( x, y );
		if( previousValue.isEmpty() )
		{
			add( x, y, value );
			return true;
		}
		V newValue = computation.apply( previousValue.get() );
		if( Objects.equals( newValue, previousValue.get() ) )
			return false;
		replace( x, y, newValue );
		return true;
	}
}
