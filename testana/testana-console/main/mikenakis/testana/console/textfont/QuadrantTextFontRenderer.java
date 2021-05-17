package mikenakis.testana.console.textfont;

/**
 * A {@link TextFontRenderer} that renders using half block and quadrant block characters.
 * See https://en.wikipedia.org/wiki/Block_Elements
 */
public class QuadrantTextFontRenderer extends TextFontRenderer
{
	private static final char[] chars = //
		{
			/* 00/00 */ ' ',
			/* 00/01 */ '\u2597', // ▗
			/* 00/10 */ '\u2596', // ▖
			/* 00/11 */ '\u2584', // ▄
			/* 01/00 */ '\u259d', // ▝
			/* 01/01 */ '\u2590', // ▐
			/* 01/10 */ '\u259e', // ▞
			/* 01/11 */ '\u259f', // ▟
			/* 10/00 */ '\u2598', // ▘
			/* 10/01 */ '\u259a', // ▚
			/* 10/10 */ '\u258c', // ▌
			/* 10/11 */ '\u2599', // ▙
			/* 11/00 */ '\u2580', // ▀
			/* 11/01 */ '\u259c', // ▜
			/* 11/10 */ '\u259b', // ▛
			/* 11/11 */ '\u2588' //  █
		};

	public QuadrantTextFontRenderer( BitmapFontRenderer bitFontRenderer )
	{
		super( bitFontRenderer );
	}

	@Override protected char[][] charGridFromBitmap( Bitmap bitmap )
	{
		int charGridHeight = (bitmap.height + 1) / 2;
		int charGridWidth = (bitmap.width + 1) / 2;
		char[][] charGrid = newCharGrid( charGridHeight, charGridWidth );
		for( int charY = 0; charY < charGridHeight; charY++ )
			for( int charX = 0; charX < charGridWidth; charX++ )
			{
				int bitmapY = charY * 2;
				int bitmapX = charX * 2;
				int bit1000 = getBit( bitmap, bitmapY, bitmapX );
				int bit0100 = getBit( bitmap, bitmapY, bitmapX + 1 );
				int bit0010 = getBit( bitmap, bitmapY + 1, bitmapX );
				int bit0001 = getBit( bitmap, bitmapY + 1, bitmapX + 1 );
				charGrid[charY][charX] = chars[bit1000 << 3 | bit0100 << 2 | bit0010 << 1 | bit0001];
			}
		return charGrid;
	}

	private static int getBit( Bitmap bitmap, int y, int x )
	{
		if( y >= bitmap.height || x >= bitmap.width )
			return 0;
		return bitmap.getBit( y, x );
	}
}
