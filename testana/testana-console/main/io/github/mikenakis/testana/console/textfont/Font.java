package io.github.mikenakis.testana.console.textfont;

public final class Font
{
	private static final int base = 32;

	public final int height;
	public final int characterSpacing;
	private final Glyph[] glyphs;

	public Font( int height, int characterSpacing, Glyph[] glyphs )
	{
		assert glyphs.length == 127 - base;
		this.height = height;
		this.characterSpacing = characterSpacing;
		this.glyphs = new Glyph[127 - base];
		for( char c = ' '; c <= '~'; c++ )
		{
			Glyph glyph = glyphs[c - base];
			assert glyph.character == c;
			assert glyph.height() == height;
			this.glyphs[c - base] = glyph;
		}
	}

	private Glyph glyph( char character )
	{
		return glyphs[character - base];
	}

	public int getLength( String text )
	{
		int length = 0;
		int n = text.length();
		for( int i = 0; i < n; i++ )
		{
			char c = text.charAt( i );
			Glyph glyph = glyph( c );
			length += glyph.width;
			if( i < n - 1 )
				length += characterSpacing;
		}
		return length;
	}

	public Bitmap render( String text )
	{
		int bitmapWidth = getLength( text );
		Bitmap bitmap = new Bitmap( height, bitmapWidth );
		int bitmapX = 0;
		int textLength = text.length();
		for( int i = 0; i < textLength; i++ )
		{
			char c = text.charAt( i );
			Glyph glyph = glyph( c );
			for( int bitmapY = 0; bitmapY < bitmap.height; bitmapY++ )
				for( int glyphX = 0; glyphX < glyph.width; glyphX++ )
				{
					int bit = glyph.getBit( bitmapY, glyphX );
					bitmap.setBit( bitmapY, bitmapX + glyphX, bit );
				}
			bitmapX += glyph.width + characterSpacing;
		}
		return bitmap;
	}
}
