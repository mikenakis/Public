package mikenakis.testana.console.textfont;

public class BlockTextFontRenderer extends TextFontRenderer
{
	private static final char[] chars =
		{
			'\u2591',
			'\u2588'
		};

	public BlockTextFontRenderer( BitmapFontRenderer bitFontRenderer )
	{
		super( bitFontRenderer );
	}

	@Override protected char[][] charGridFromBitmap( Bitmap bitmap )
	{
		char[][] charGrid = newCharGrid( bitmap.height, bitmap.width );
		for( int y = 0;  y < charGrid.length;  y++ )
			for( int x = 0;  x < charGrid[y].length;  x++ )
				charGrid[y][x] = chars[bitmap.getBit( y, x )];
		return charGrid;
	}
}
