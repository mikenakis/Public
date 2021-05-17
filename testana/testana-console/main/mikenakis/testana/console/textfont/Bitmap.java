package mikenakis.testana.console.textfont;

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
}
