package mikenakis.testana.console.textfont;

import java.util.ArrayList;
import java.util.List;

public class Bitmap
{
	public final int height;
	public final int width;
	private final int[][] bits;

	public Bitmap( int height, int width )
	{
		this.height = height;
		this.width = width;
		bits = new int[height][];
		for( int y = 0; y < height; y++ )
			bits[y] = new int[width];
	}

	public int getBit( int y, int x )
	{
		return bits[y][x];
	}

	public void setBit( int y, int x, int bit )
	{
		assert bit == 0 || bit == 1;
		bits[y][x] = bit;
	}

	public String toString() { return String.join( "\n", toStrings() ); }

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

	public List<String> toStrings()
	{
		int charGridHeight = (height + 1) / 2;
		int charGridWidth = (width + 1) / 2;
		List<String> strings = new ArrayList<>();
		for( int charY = 0; charY < charGridHeight; charY++ )
		{
			StringBuilder stringBuilder = new StringBuilder();
			for( int charX = 0; charX < charGridWidth; charX++ )
			{
				int bit1000 = getBit( this, charY * 2, charX * 2 );
				int bit0100 = getBit( this, charY * 2, charX * 2 + 1 );
				int bit0010 = getBit( this, charY * 2 + 1, charX * 2 );
				int bit0001 = getBit( this, charY * 2 + 1, charX * 2 + 1 );
				stringBuilder.append( chars[bit1000 << 3 | bit0100 << 2 | bit0010 << 1 | bit0001] );
			}
			strings.add( stringBuilder.toString() );
		}
		return strings;
	}

	private static int getBit( Bitmap bitmap, int y, int x )
	{
		if( y >= bitmap.height || x >= bitmap.width )
			return 0;
		return bitmap.getBit( y, x );
	}
}
