package mikenakis.testana.console.textfont;

import java.util.List;

public abstract class TextFontRenderer
{
	private final BitmapFontRenderer bitmapFontRenderer;

	protected TextFontRenderer( BitmapFontRenderer bitmapFontRenderer )
	{
		this.bitmapFontRenderer = bitmapFontRenderer;
	}

	public final List<String> render( String text )
	{
		int bitmapHeight = bitmapFontRenderer.getLineSpacing() + bitmapFontRenderer.getHeight();
		int bitmapWidth = bitmapFontRenderer.getLength( text );
		Bitmap bitmap = new Bitmap( bitmapHeight, bitmapWidth );
		bitmapFontRenderer.renderTextToBitmap( text, bitmap );
		char[][] charGrid = charGridFromBitmap( bitmap );
		return stringsFromCharGrid( charGrid );
	}

	protected abstract char[][] charGridFromBitmap( Bitmap bitmap );

	protected static char[][] newCharGrid( int height, int width )
	{
		char[][] charGrid = new char[height][];
		for( int i = 0; i < charGrid.length; i++ )
			charGrid[i] = new char[width];
		return charGrid;
	}

	protected static List<String> stringsFromCharGrid( char[][] charGrid )
	{
		String[] strings = new String[charGrid.length];
		for( int i = 0; i < charGrid.length; i++ )
			strings[i] = new String( charGrid[i] );
		return List.of( strings );
	}
}
