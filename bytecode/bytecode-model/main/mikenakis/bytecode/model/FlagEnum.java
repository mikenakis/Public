package mikenakis.bytecode.model;

import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlagEnum<E extends Enum<E>>
{
	@SafeVarargs @SuppressWarnings( "varargs" ) public static <E extends Enum<E>> FlagEnum<E> of( Class<E> enumClass, Map.Entry<E,Integer>... entries )
	{
		return of( enumClass, List.of( entries ) );
	}

	@ExcludeFromJacocoGeneratedReport private static <T> T dummyMergeFunction( T a, T b )
	{
		assert false;
		return a;
	}

	public static <E extends Enum<E>> FlagEnum<E> of( Class<E> enumClass, Collection<Map.Entry<E,Integer>> entries )
	{
		return new FlagEnum<>( enumClass, entries.stream().collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue(), FlagEnum::dummyMergeFunction, LinkedHashMap::new ) ) );
	}

	private final Map<E,Integer> bitMap;
	private final int mask;

	private FlagEnum( Class<E> enumClass, Map<E,Integer> bitMap )
	{
		assert enumClass.isEnum();
		this.bitMap = bitMap;
		int mask = 0;
		for( E value : enumClass.getEnumConstants() )
		{
			int bit = Kit.map.get( bitMap, value );
			assert Integer.bitCount( bit ) == 1;
			assert (mask & bit) == 0;
			mask |= bit;
		}
		this.mask = mask;
	}

	public FlagSet<E> fromInt( int flags )
	{
		return FlagSet.of( this, flags );
	}

	public int getFlag( E value )
	{
		return Kit.map.get( bitMap, value );
	}

	public boolean isValidIntAssertion( int flags )
	{
		assert (flags & ~mask) == 0;
		return true;
	}

	public Collection<E> all()
	{
		return bitMap.keySet();
	}

	public FlagSet<E> none()
	{
		return FlagSet.of( this, 0 );
	}

	@SafeVarargs @SuppressWarnings( "varargs" ) public final FlagSet<E> of( E... values )
	{
		return of( List.of( values ) );
	}

	public FlagSet<E> of( Iterable<E> values )
	{
		int flags = 0;
		for( E value : values )
		{
			int bit = Kit.map.get( bitMap, value );
			flags |= bit;
		}
		return FlagSet.of( this, flags );
	}
}
