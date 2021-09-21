package mikenakis.bytecode.model;

import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Locale;
import java.util.Objects;

public class FlagSet<E extends Enum<E>>
{
	public static <E extends Enum<E>> FlagSet<E> of( FlagEnum<E> flagEnum, int flags )
	{
		return new FlagSet<>( flagEnum, flags );
	}

	private final FlagEnum<E> flagEnum;
	private final int flags;

	private FlagSet( FlagEnum<E> flagEnum, int flags )
	{
		assert flagEnum.isValidIntAssertion( flags );
		this.flagEnum = flagEnum;
		this.flags = flags;
	}

	public boolean contains( E flag )
	{
		int bit = flagEnum.getFlag( flag );
		return (flags & bit) != 0;
	}

	public boolean isEmpty()
	{
		return flags == 0;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "0x" ).append( Integer.toHexString( flags ) );
		stringBuilder.append( " (" );
		boolean first = true;
		for( E value : flags() )
		{
			first = Kit.stringBuilder.appendDelimiter( stringBuilder, first, " " );
			stringBuilder.append( value.toString().toLowerCase( Locale.ROOT ) );
		}
		stringBuilder.append( ")" );
		return stringBuilder.toString();
	}

	public Iterable<E> flags()
	{
		return Kit.iterable.fromStream( flagEnum.all().stream().filter( v -> contains( v ) ) );
	}

	public int toInt()
	{
		return flags;
	}

	public FlagSet<E> tryWithout( E value )
	{
		int bit = flagEnum.getFlag( value );
		int newFlags = flags & ~bit;
		if( newFlags == flags )
			return this;
		return new FlagSet<>( flagEnum, newFlags );
	}

	public FlagSet<E> tryWith( E value )
	{
		int bit = flagEnum.getFlag( value );
		int newFlags = flags | bit;
		if( newFlags == flags )
			return this;
		return new FlagSet<>( flagEnum, newFlags );
	}

	public FlagSet<E> tryWithout( FlagSet<E> other )
	{
		assert other.flagEnum == flagEnum;
		int newFlags = flags & ~other.flags;
		if( newFlags == flags )
			return this;
		return new FlagSet<>( flagEnum, newFlags );
	}

	public FlagSet<E> tryWith( FlagSet<E> other )
	{
		assert other.flagEnum == flagEnum;
		int newFlags = flags | other.flags;
		if( newFlags == flags )
			return this;
		return new FlagSet<>( flagEnum, newFlags );
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof FlagSet<?> kin )
			return equals( kin );
		assert false;
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( flagEnum, flags );
	}

	public boolean equals( FlagSet<?> other )
	{
		if( flagEnum != other.flagEnum )
			return false;
		return flags == other.flags;
	}
}
