package io.github.mikenakis.testana.console.textfont;

public class Glyph
{
	public final char character;
	public final int width;
	private final byte[] bytes;

	public Glyph( char character, int width, byte[] bytes )
	{
		this.character = character;
		this.width = width;
		this.bytes = bytes;
	}

	public int getBit( int y, int x )
	{
		assert x >= 0 && x < width;
		return (bytes[y] >> (width - x - 1)) & 1;
	}

	public int height()
	{
		return bytes.length;
	}

	@Override public String toString()
	{
		return "'" + character + "' (0x" + Integer.toHexString( character ) + "), width=" + width;
	}
}
