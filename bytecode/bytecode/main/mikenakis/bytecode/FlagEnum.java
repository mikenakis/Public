package mikenakis.bytecode;

import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlagEnum<E extends Enum<E>>
{
	@SafeVarargs @SuppressWarnings( "varargs" ) public static <E extends Enum<E>> FlagEnum<E> of( Class<E> enumClass, Map.Entry<E,Integer>... entries )
	{
		return of( enumClass, List.of( entries ) );
	}

	public static <E extends Enum<E>> FlagEnum<E> of( Class<E> enumClass, Collection<Map.Entry<E,Integer>> entries )
	{
		return new FlagEnum<>( enumClass, entries.stream().collect( Collectors.toUnmodifiableMap( e -> e.getKey(), e -> e.getValue() ) ) );
	}

	private final Class<E> enumClass;
	private final Map<E,Integer> bitMap;

	private FlagEnum( Class<E> enumClass, Map<E,Integer> bitMap )
	{
		assert enumClass.isEnum();
		this.enumClass = enumClass;
		this.bitMap = bitMap;
	}

	public EnumSet<E> fromInt( int flags )
	{
		EnumSet<E> enumSet = EnumSet.noneOf( enumClass );
		for( E value : enumClass.getEnumConstants() )
		{
			int bit = Kit.map.get( bitMap, value );
			if( (flags & bit) != 0 )
				Kit.collection.add( enumSet, value );
		}
		assert toInt( enumSet ) == flags; //flags contain unknown bits.
		return enumSet;
	}

	public int toInt( EnumSet<E> set )
	{
		int flags = 0;
		for( E value : set )
		{
			int bit = Kit.map.get( bitMap, value );
			flags |= bit;
		}
		return flags;
	}

	public String toString( EnumSet<E> set )
	{
		StringBuilder stringBuilder = new StringBuilder();
		toStringBuilder( stringBuilder, set );
		return stringBuilder.toString();
	}

	public void toStringBuilder( StringBuilder stringBuilder, EnumSet<E> set )
	{
		boolean first = true;
		for( E value : set )
		{
			if( first )
				first = false;
			else
				stringBuilder.append( ' ' );
			stringBuilder.append( value.toString() );
		}
	}
}
