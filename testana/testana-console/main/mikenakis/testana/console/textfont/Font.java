package mikenakis.testana.console.textfont;

public final class Font
{
	private static final int base = 32;

	public final int height;
	private final Glyph[] glyphs;

	public Font( Glyph[] glyphs )
	{
		assert glyphs.length == 127 - base;
		height = glyphs[0].height();
		this.glyphs = new Glyph[127 - base];
		for( char c = ' ';  c <= '~';  c++ )
		{
			Glyph glyph = glyphs[c - base];
			assert glyph.character == c;
			assert glyph.height() == height;
			this.glyphs[c - base] = glyph;
		}
	}

	public Glyph glyph( char character )
	{
		return glyphs[character - base];
	}
}
