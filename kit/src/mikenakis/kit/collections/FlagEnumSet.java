package mikenakis.kit.collections;

import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a set of values of a {@link FlagEnum}.
 *
 * See {@link FlagEnum}.
 *
 * @param <E>
 */
public class FlagEnumSet<E extends Enum<E>>
{
	public static <E extends Enum<E>> FlagEnumSet<E> of( FlagEnum<E> flagEnum, int flags )
	{
		return new FlagEnumSet<>( flagEnum, flags );
	}

	private final FlagEnum<E> flagEnum;
	private final int bits;

	private FlagEnumSet( FlagEnum<E> flagEnum, int bits )
	{
		assert flagEnum.isValidIntAssertion( bits );
		this.flagEnum = flagEnum;
		this.bits = bits;
	}

	public boolean contains( E value )
	{
		int bit = flagEnum.getBit( value );
		return (bits & bit) != 0;
	}

	public boolean isEmpty()
	{
		return bits == 0;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "0x" ).append( Integer.toHexString( bits ) );
		stringBuilder.append( " (" );
		boolean first = true;
		for( E value : values() )
		{
			first = Kit.stringBuilder.appendDelimiter( stringBuilder, first, " " );
			stringBuilder.append( value.toString().toLowerCase( Locale.ROOT ) );
		}
		stringBuilder.append( ")" );
		return stringBuilder.toString();
	}

	public Iterable<E> values()
	{
		return Kit.iterable.fromStream( flagEnum.allValues().stream().filter( v -> contains( v ) ) );
	}

	public int getBits()
	{
		return bits;
	}

	public FlagEnumSet<E> tryWithout( E value )
	{
		int bit = flagEnum.getBit( value );
		int newFlags = bits & ~bit;
		return newFlags == bits ? this : new FlagEnumSet<>( flagEnum, newFlags );
	}

	public FlagEnumSet<E> tryWith( E value )
	{
		int bit = flagEnum.getBit( value );
		int newFlags = bits | bit;
		return newFlags == bits ? this : new FlagEnumSet<>( flagEnum, newFlags );
	}

	public FlagEnumSet<E> tryWithout( FlagEnumSet<E> other )
	{
		assert other.flagEnum == flagEnum;
		int newFlags = bits & ~other.bits;
		return newFlags == bits ? this : new FlagEnumSet<>( flagEnum, newFlags );
	}

	public FlagEnumSet<E> tryWith( FlagEnumSet<E> other )
	{
		assert other.flagEnum == flagEnum;
		int newFlags = bits | other.bits;
		return newFlags == bits ? this : new FlagEnumSet<>( flagEnum, newFlags );
	}

	@Deprecated @Override public boolean equals( Object other )
	{
		return other instanceof FlagEnumSet<?> kin ? equals( kin ) : Kit.fail();
	}

	@Override public int hashCode()
	{
		return Objects.hash( flagEnum, bits );
	}

	public boolean equals( FlagEnumSet<?> other )
	{
		return flagEnum == other.flagEnum ? bits == other.bits : Kit.fail();
	}
}
