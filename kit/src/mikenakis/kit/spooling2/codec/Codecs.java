package mikenakis.kit.spooling2.codec;

import mikenakis.kit.Kit;
import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public final class Codecs
{
	public static final JavaCodec<Boolean> BooleanCodec = new JavaCodec<>( Boolean.class )
	{
		@Override public Boolean defaultInstance() { return false; }
		@Override public String stringFromInstance( Boolean value ) { return value.toString(); }

		@Override public Boolean instanceFromString( String content )
		{
			//PEARL: Boolean.parseBoolean() takes malicious inaction if it does not recognize the string;
			//       we correct this by asserting that the string is either "true" or "false".
			assert( content.equals( "true" ) || content.equals( "false" ) );
			return Boolean.parseBoolean( content );
		}

		@Override public Boolean instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			byte bits = binaryStreamReader.readByte();
			assert bits == 0 || bits == 1;
			return bits != 0;
		}

		@Override public void instanceIntoBinary( Boolean value, BinaryStreamWriter binaryStreamWriter )
		{
			byte bits = value ? (byte)1 : (byte)0;
			binaryStreamWriter.writeByte( bits );
		}
	};

	public static final JavaCodec<Byte> ByteCodec = new JavaCodec<>( Byte.class )
	{
		@Override public Byte defaultInstance() { return (byte)0; }
		@Override public Byte instanceFromString( String content ) { return Byte.parseByte( content ); }
		@Override public String stringFromInstance( Byte value ) { return value.toString(); }
		@Override public Byte instanceFromBinary( BinaryStreamReader binaryStreamReader ) { return binaryStreamReader.readByte(); }
		@Override public void instanceIntoBinary( Byte value, BinaryStreamWriter binaryStreamWriter ) { binaryStreamWriter.writeByte( value ); }
	};

	public static final JavaCodec<Character> CharacterCodec = new JavaCodec<>( Character.class )
	{
		@Override public Character defaultInstance() { return '\0'; }

		@Override public Character instanceFromString( String content )
		{
			assert content.length() == 1;
			return content.charAt( 0 );
		}

		@Override public String stringFromInstance( Character value ) { return value.toString(); }

		@Override public Character instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			short value = ShortCodec.instanceFromBinary( binaryStreamReader );
			return (char)value;
		}

		@Override public void instanceIntoBinary( Character value, BinaryStreamWriter binaryStreamWriter )
		{
			ShortCodec.instanceIntoBinary( (short)(char)value, binaryStreamWriter );
		}
	};

	public static final JavaCodec<BigDecimal> DecimalCodec = new JavaCodec<>( BigDecimal.class )
	{
		@Override public BigDecimal defaultInstance() { return BigDecimal.ZERO; }
		@Override public BigDecimal instanceFromString( String content ) { return new BigDecimal( content ); }
		@Override public String stringFromInstance( BigDecimal value ) { return value.toString(); }

		@Override public BigDecimal instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			String text = Codecs.StringCodec.instanceFromBinary( binaryStreamReader );
			return instanceFromString( text );
		}

		@Override public void instanceIntoBinary( BigDecimal value, BinaryStreamWriter binaryStreamWriter )
		{
			String text = stringFromInstance( value );
			Codecs.StringCodec.instanceIntoBinary( text, binaryStreamWriter );
		}
	};

	public static final JavaCodec<Double> DoubleCodec = new JavaCodec<>( Double.class )
	{
		@Override public Double defaultInstance() { return 0.0; }

		@Override public Double instanceFromString( String content ) { return Double.parseDouble( content ); }

		@Override public String stringFromInstance( Double value ) { return value.toString(); }

		@Override public Double instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			long bits = LongCodec.instanceFromBinary( binaryStreamReader );
			return Double.longBitsToDouble( bits );
		}

		@Override public void instanceIntoBinary( Double value, BinaryStreamWriter binaryStreamWriter )
		{
			long bits = Double.doubleToLongBits( value );
			LongCodec.instanceIntoBinary( bits, binaryStreamWriter );
		}
	};

	public static final JavaCodec<Duration> DurationCodec = new JavaCodec<>( Duration.class )
	{
		@Override public Duration defaultInstance() { return Duration.ZERO; }
		@Override public Duration instanceFromString( String content ) { return Duration.parse( content ); }
		@Override public String stringFromInstance( Duration value ) { return value.toString(); }

		@Override public Duration instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			double days = DoubleCodec.instanceFromBinary( binaryStreamReader );
			return durationFromDays( days );
		}

		@Override public void instanceIntoBinary( Duration value, BinaryStreamWriter binaryStreamWriter )
		{
			double days = daysFromDuration( value );
			DoubleCodec.instanceIntoBinary( days, binaryStreamWriter );
		}

		private static final int SECONDS_PER_DAY = 24 * 60 * 60;
		private static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;

		private static Duration durationFromDays( double days )
		{
			double seconds = days * SECONDS_PER_DAY;
			long wholeSeconds = (long)Math.floor( seconds );
			double fractionalSecond = seconds - wholeSeconds;
			int nanoseconds = (int)(fractionalSecond * NANOSECONDS_PER_SECOND);
			return Duration.ofSeconds( wholeSeconds, nanoseconds );
		}

		private static double daysFromDuration( Duration duration )
		{
			long wholeSeconds = duration.getSeconds();
			double fractionalSecond = duration.getNano() / (double)NANOSECONDS_PER_SECOND;
			double seconds = wholeSeconds + fractionalSecond;
			return seconds / SECONDS_PER_DAY;
		}
	};

	public static final JavaCodec<Float> FloatCodec = new JavaCodec<>( Float.class )
	{
		@Override public Float defaultInstance() { return 0.0f; }
		@Override public Float instanceFromString( String content ) { return Float.parseFloat( content ); }
		@Override public String stringFromInstance( Float value ) { return value.toString(); }

		@Override public Float instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			int bits = IntegerCodec.instanceFromBinary( binaryStreamReader );
			return Float.intBitsToFloat( bits );
		}

		@Override public void instanceIntoBinary( Float value, BinaryStreamWriter binaryStreamWriter )
		{
			int bits = Float.floatToIntBits( value );
			IntegerCodec.instanceIntoBinary( bits, binaryStreamWriter );
		}
	};

	public static final JavaCodec<Instant> InstantCodec = new JavaCodec<>( Instant.class )
	{
		@Override public Instant defaultInstance() { return Instant.EPOCH; }
		@Override public Instant instanceFromString( String content ) { return Instant.parse( content ); }
		@Override public String stringFromInstance( Instant value ) { return value.toString(); }

		@Override public Instant instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			long epochSecond = Codecs.LongCodec.instanceFromBinary( binaryStreamReader );
			int nanos = Codecs.IntegerCodec.instanceFromBinary( binaryStreamReader );
			return Instant.ofEpochSecond( epochSecond, nanos );
		}

		@Override public void instanceIntoBinary( Instant value, BinaryStreamWriter binaryStreamWriter )
		{
			Codecs.LongCodec.instanceIntoBinary( value.getEpochSecond(), binaryStreamWriter );
			Codecs.IntegerCodec.instanceIntoBinary( value.getNano(), binaryStreamWriter );
		}
	};

	public static final JavaCodec<Integer> IntegerCodec = new JavaCodec<>( Integer.class )
	{
		@Override public Integer defaultInstance() { return 0; }
		@Override public Integer instanceFromString( String content ) { return Integer.parseInt( content ); }
		@Override public String stringFromInstance( Integer value ) { return value.toString(); }

		@Override public Integer instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			byte[] bytes = binaryStreamReader.readBytes( 4 );
			assert bytes.length == 4;
			return Kit.bytes.intFromBytes( bytes );
		}

		@Override public void instanceIntoBinary( Integer value, BinaryStreamWriter binaryStreamWriter )
		{
			byte[] bytes = Kit.bytes.bytesFromInt( value );
			assert bytes.length == 4;
			binaryStreamWriter.writeBytes( bytes );
		}
	};

	public static final JavaCodec<Long> LongCodec = new JavaCodec<>( Long.class )
	{
		@Override public Long defaultInstance() { return 0L; }
		@Override public Long instanceFromString( String content ) { return Long.parseLong( content ); }
		@Override public String stringFromInstance( Long value ) { return value.toString(); }

		@Override public Long instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			byte[] bytes = binaryStreamReader.readBytes( 8 );
			assert bytes.length == 8;
			return Kit.bytes.longFromBytes( bytes );
		}

		@Override public void instanceIntoBinary( Long value, BinaryStreamWriter binaryStreamWriter )
		{
			byte[] bytes = Kit.bytes.bytesFromLong( value );
			assert bytes.length == 8;
			binaryStreamWriter.writeBytes( bytes );
		}
	};

	public static final JavaCodec<Short> ShortCodec = new JavaCodec<>( Short.class )
	{
		@Override public Short defaultInstance() { return (short)0; }
		@Override public Short instanceFromString( String content ) { return Short.parseShort( content ); }
		@Override public String stringFromInstance( Short value ) { return value.toString(); }

		@Override public Short instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			byte[] bytes = binaryStreamReader.readBytes( 2 );
			assert bytes.length == 2;
			return Kit.bytes.shortFromBytes( bytes );
		}

		@Override public void instanceIntoBinary( Short value, BinaryStreamWriter binaryStreamWriter )
		{
			byte[] bytes = Kit.bytes.bytesFromShort( value );
			assert bytes.length == 2;
			binaryStreamWriter.writeBytes( bytes );
		}
	};

	public static final JavaCodec<String> StringCodec = new JavaCodec<>( String.class )
	{
		@Override public String defaultInstance() { return ""; }

		@Override public String instanceFromString( String content )
		{
			assert content != null;
			return content;
		}

		@Override public String stringFromInstance( String value )
		{
			return value;
		}

		@Override public String instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			int length = IntegerCodec.instanceFromBinary( binaryStreamReader );
			if( length == 0 )
				return "";
			byte[] bytes = binaryStreamReader.readBytes( length );
			assert bytes.length == length;
			return new String( bytes, StandardCharsets.UTF_8 );
		}

		@Override public void instanceIntoBinary( String value, BinaryStreamWriter binaryStreamWriter )
		{
			byte[] bytes = value.getBytes( StandardCharsets.UTF_8 );
			IntegerCodec.instanceIntoBinary( bytes.length, binaryStreamWriter );
			if( bytes.length > 0 )
				binaryStreamWriter.writeBytes( bytes );
		}
	};

	public static final JavaCodec<UUID> UuidCodec = new JavaCodec<>( UUID.class )
	{
		@Override public UUID defaultInstance() { return new UUID( 0L, 0L ); }
		@Override public UUID instanceFromString( String content ) { return UUID.fromString( content ); }
		@Override public String stringFromInstance( UUID value ) { return value.toString(); }

		@Override public UUID instanceFromBinary( BinaryStreamReader binaryStreamReader )
		{
			long hiBits = LongCodec.instanceFromBinary( binaryStreamReader );
			long loBits = LongCodec.instanceFromBinary( binaryStreamReader );
			return new UUID( hiBits, loBits );
		}

		@Override public void instanceIntoBinary( UUID value, BinaryStreamWriter binaryStreamWriter )
		{
			long hiBits = value.getMostSignificantBits();
			LongCodec.instanceIntoBinary( hiBits, binaryStreamWriter );
			long loBits = value.getLeastSignificantBits();
			LongCodec.instanceIntoBinary( loBits, binaryStreamWriter );
		}
	};

	private static <T> OptionalCodec<T> optional( Codec<T> codec )
	{
		return new OptionalCodec<>( codec );
	}

	public static final OptionalCodec<Boolean   /**/> optionalBooleanCodec   /**/ = optional( BooleanCodec );
	public static final OptionalCodec<Byte      /**/> optionalByteCodec      /**/ = optional( ByteCodec );
	public static final OptionalCodec<Character /**/> optionalCharacterCodec /**/ = optional( CharacterCodec );
	public static final OptionalCodec<BigDecimal/**/> optionalDecimalCodec   /**/ = optional( DecimalCodec );
	public static final OptionalCodec<Double    /**/> optionalDoubleCodec    /**/ = optional( DoubleCodec );
	public static final OptionalCodec<Duration  /**/> optionalDurationCodec  /**/ = optional( DurationCodec );
	public static final OptionalCodec<Float     /**/> optionalFloatCodec     /**/ = optional( FloatCodec );
	public static final OptionalCodec<Instant   /**/> optionalInstantCodec   /**/ = optional( InstantCodec );
	public static final OptionalCodec<Integer   /**/> optionalIntegerCodec   /**/ = optional( IntegerCodec );
	public static final OptionalCodec<Long      /**/> optionalLongCodec      /**/ = optional( LongCodec );
	public static final OptionalCodec<Short     /**/> optionalShortCodec     /**/ = optional( ShortCodec );
	public static final OptionalCodec<String    /**/> optionalStringCodec    /**/ = optional( StringCodec );
	public static final OptionalCodec<UUID      /**/> optionalUuidCodec      /**/ = optional( UuidCodec );

	private Codecs() { }

	private static final Collection<Codec<?>> numericCodecs = Set.of(
		ByteCodec, ShortCodec, IntegerCodec, LongCodec, FloatCodec, DoubleCodec, DecimalCodec,
		optionalByteCodec, optionalShortCodec, optionalIntegerCodec, optionalLongCodec, optionalFloatCodec,
		optionalDoubleCodec, optionalDecimalCodec
	);

	public static boolean isNumeric( Codec<?> codec )
	{
		return numericCodecs.contains( codec );
	}
}
