package mikenakis.kit.spooling2.codec;

import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;

public abstract class Codec<T>
{
	protected Codec()
	{
	}

	public boolean isOptional() { return false; }

	public abstract T defaultInstance();

	public abstract boolean isInstance( Object instance );

	public abstract boolean instancesEqual( T left, T right );

	/**
	 * Converts the given value to a String.
	 *
	 * @param value the value to convert to string
	 *
	 * @return the String representation of the given value
	 */
	public abstract String stringFromInstance( T value );

	/**
	 * Parses the given string into a value of this {@link Codec}.
	 *
	 * @param content the string to parse
	 *
	 * @return a new value of this {@link Codec}
	 *
	 * @throws RuntimeException if the string is not parsable.
	 */
	public abstract T instanceFromString( String content );

	/**
	 * Reads a value from the given {@link BinaryStreamReader}.
	 *
	 * @param binaryStreamReader the {@link BinaryStreamReader} to read from.
	 *
	 * @return the value.
	 */
	public abstract T instanceFromBinary( BinaryStreamReader binaryStreamReader );

	/**
	 * Writes a value into the given {@link BinaryStreamWriter}.
	 *
	 * @param value                  the value to write.
	 * @param binaryStreamWriter the {@link BinaryStreamWriter} to write into.
	 */
	public abstract void instanceIntoBinary( T value, BinaryStreamWriter binaryStreamWriter );
}
