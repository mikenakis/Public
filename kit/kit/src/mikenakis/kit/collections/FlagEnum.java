package mikenakis.kit.collections;

import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents an Enum where each value is associated with a flag bit and all values together fit within an `int`.
 *
 * Necessary because Java's {@link java.util.EnumSet} does not expose the `int` containing the bits.
 *
 * See {@link FlagSet}
 *
 * @param <E>
 */
public class FlagEnum<E extends Enum<E>>
{
	@SafeVarargs @SuppressWarnings( "varargs" ) public static <E extends Enum<E>> FlagEnum<E> of( Class<E> enumClass, Map.Entry<E,Integer>... entries )
	{
		return of( enumClass, List.of( entries ) );
	}

	public static <E extends Enum<E>> FlagEnum<E> of( Class<E> enumClass, Iterable<Map.Entry<E,Integer>> entries )
	{
		Map<E,Integer> valuesFromBits = Kit.iterable.toMap( entries, e -> e.getKey(), e -> e.getValue() );
		return new FlagEnum<>( enumClass, valuesFromBits );
	}

	private final Map<E,Integer> valuesFromBits;
	private final int mask;

	private FlagEnum( Class<E> enumClass, Map<E,Integer> valuesFromBits )
	{
		assert enumClass.isEnum();
		this.valuesFromBits = valuesFromBits;
		int mask = 0;
		for( E value : enumClass.getEnumConstants() )
		{
			int bit = Kit.map.get( valuesFromBits, value );
			assert Integer.bitCount( bit ) == 1;
			assert (mask & bit) == 0;
			mask |= bit;
		}
		this.mask = mask;
	}

	public FlagSet<E> fromBits( int bits )
	{
		return FlagSet.of( this, bits );
	}

	public int getBit( E value )
	{
		return Kit.map.get( valuesFromBits, value );
	}

	public boolean isValidIntAssertion( int flags )
	{
		assert (flags & ~mask) == 0;
		return true;
	}

	public Collection<E> allValues()
	{
		return valuesFromBits.keySet();
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
			int bit = Kit.map.get( valuesFromBits, value );
			flags |= bit;
		}
		return FlagSet.of( this, flags );
	}
}
