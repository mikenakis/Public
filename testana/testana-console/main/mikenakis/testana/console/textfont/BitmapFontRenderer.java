package mikenakis.testana.console.textfont;

public class BitmapFontRenderer
{
	private final Font font;
	private int characterSpacing;
	private int lineSpacing;

	public BitmapFontRenderer( Font font, int characterSpacing, int lineSpacing )
	{
		this.font = font;
		this.characterSpacing = characterSpacing;
		this.lineSpacing = lineSpacing;
	}

	public int getCharacterSpacing() { return characterSpacing; }
	public void setCharacterSpacing( int characterSpacing ) { this.characterSpacing = characterSpacing; }
	public int getLineSpacing() { return lineSpacing; }
	public void setLineSpacing( int lineSpacing ) { this.lineSpacing = lineSpacing; }
	public int getHeight() { return font.height; }

	public int getLength( String text )
	{
		int length = 0;
		int n = text.length();
		for( int i = 0; i < n; i++ )
		{
			char c = text.charAt( i );
			Glyph glyph = font.glyph( c );
			length += glyph.width;
			if( i < n - 1 )
				length += characterSpacing;
		}
		return length;
	}

	public void renderTextToBitmap( String text, Bitmap bitmap )
	{
		int bitmapX = 0;
		int textLength = text.length();
		for( int i = 0; i < textLength; i++ )
		{
			char c = text.charAt( i );
			Glyph glyph = font.glyph( c );
			bitmapX += renderGlyph( bitmap, bitmapX, lineSpacing, glyph );
			if( i < textLength - 1 )
				bitmapX += renderSpacing( bitmap, bitmapX, characterSpacing );
		}
	}

	private static int renderGlyph( Bitmap bitmap, int bitmapX, int lineSpacing, Glyph glyph )
	{
		for( int bitmapY = 0; bitmapY < bitmap.height; bitmapY++ )
			for( int glyphX = 0;  glyphX < glyph.width; glyphX++ )
			{
				int bit;
				if( bitmapY < lineSpacing || bitmapY >= lineSpacing + glyph.height() )
					bit = 0;
				else
					bit = glyph.getBit( bitmapY - lineSpacing, glyphX );
				bitmap.setBit( bitmapY, bitmapX + glyphX, bit );
			}
		return glyph.width;
	}

	private static int renderSpacing( Bitmap bitmap, int bitmapX, int characterSpacing )
	{
		for( int bitmapY = 0; bitmapY < bitmap.height; bitmapY++ )
			for( int glyphX = 0; glyphX < characterSpacing; glyphX++ )
				bitmap.setBit( bitmapY, bitmapX + glyphX, 0 );
		return characterSpacing;
	}
}
