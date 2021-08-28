package mikenakis.kit.spooling2.codec;

import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;

import java.util.Optional;

public final class OptionalCodec<T> extends Codec<Optional<T>>
{
	public final Codec<T> codec;

	public OptionalCodec( Codec<T> codec )
	{
		this.codec = codec;
	}

	@Override public boolean isOptional() { return true; }

	@Override public Optional<T> defaultInstance()
	{
		return Optional.empty();
	}

	@Override public boolean instancesEqual( Optional<T> left, Optional<T> right )
	{
		if( left.isEmpty() && right.isEmpty() )
			return true;
		if( left.isEmpty() )
			return false;
		if( right.isEmpty() )
			return false;
		return codec.instancesEqual( left.get(), right.get() );
	}

	@Override public Optional<T> instanceFromString( String content )
	{
		if( content.equalsIgnoreCase( "null" ) )
			return Optional.empty();
		T value = codec.instanceFromString( content );
		return Optional.of( value );
	}

	@Override public String stringFromInstance( Optional<T> value )
	{
		if( value.isEmpty() )
			return "null";
		return codec.stringFromInstance( value.get() );
	}

	@Override public boolean isInstance( Object value )
	{
		if( !(value instanceof Optional) )
			return false;
		@SuppressWarnings( "unchecked" ) Optional<T> valueAsOptional = (Optional<T>)value;
		if( valueAsOptional.isEmpty() )
			return true;
		return codec.isInstance( valueAsOptional.get() );
	}

	@Override public Optional<T> instanceFromBinary( BinaryStreamReader binaryStreamReader )
	{
		boolean isPresent = Codecs.BooleanCodec.instanceFromBinary( binaryStreamReader );
		if( !isPresent )
			return Optional.empty();
		T value = codec.instanceFromBinary( binaryStreamReader );
		return Optional.of( value );
	}

	@Override public void instanceIntoBinary( Optional<T> value, BinaryStreamWriter binaryStreamWriter )
	{
		Codecs.BooleanCodec.instanceIntoBinary( value.isPresent(), binaryStreamWriter );
		value.ifPresent( t -> codec.instanceIntoBinary( t, binaryStreamWriter ) );
	}
}
