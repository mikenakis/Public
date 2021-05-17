package mikenakis.testana.console.textfont;

/**
 * A proportional, 6 by ~5 font.
 * Source for tiny fonts: https://graphicdesign.stackexchange.com/q/91478/160843
 */
public final class Tiny2Font
{
	private static final Glyph[] glyphs = //
		{ //
			new Glyph( ' ', 3, new byte[] { //
				0b000, //
				0b000, //
				0b000, //
				0b000, //
				0b000, //
				0b000, //
			} ), //
			new Glyph( '!', 2, new byte[] { //
				0b11, //
				0b11, //
				0b11, //
				0b00, //
				0b11, //
				0b00, //
			} ), //
			new Glyph( '"', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b00000, //
				0b00000, //
				0b00000, //
				0b00000, //
			} ), //
			new Glyph( '#', 6, new byte[] { //
				0b0110110, //
				0b1111111, //
				0b0110110, //
				0b1111111, //
				0b0110110, //
				0b0000000, //
			} ), //
			new Glyph( '$', 6, new byte[] { //
				0b011110, //
				0b101100, //
				0b011110, //
				0b001101, //
				0b011110, //
				0b001100, //
			} ), //
			new Glyph( '%', 6, new byte[] { //
				0b110011, //
				0b110110, //
				0b001100, //
				0b011011, //
				0b110011, //
				0b000000, //
			} ), //
			new Glyph( '&', 6, new byte[] { //
				0b011000, //
				0b100100, //
				0b011011, //
				0b110100, //
				0b011011, //
				0b000000, //
			} ), //
			new Glyph( '\'', 1, new byte[] { //
				0b11, //
				0b11, //
				0b00, //
				0b00, //
				0b00, //
				0b00, //
			} ), //
			new Glyph( '(', 3, new byte[] { //
				0b011, //
				0b110, //
				0b110, //
				0b110, //
				0b011, //
				0b000, //
			} ), //
			new Glyph( ')', 3, new byte[] { //
				0b110, //
				0b011, //
				0b011, //
				0b011, //
				0b110, //
				0b000, //
			} ), //
			new Glyph( '*', 5, new byte[] { //
				0b00100, //
				0b10101, //
				0b01110, //
				0b10101, //
				0b00100, //
				0b00000, //
			} ), //
			new Glyph( '+', 6, new byte[] { //
				0b000000, //
				0b001100, //
				0b111111, //
				0b001100, //
				0b000000, //
				0b000000, //
			} ),  //
			new Glyph( ',', 3, new byte[] { //
				0b000, //
				0b000, //
				0b000, //
				0b011, //
				0b110, //
				0b000, //
			} ), //
			new Glyph( '-', 4, new byte[] { //
				0b0000, //
				0b0000, //
				0b1111, //
				0b0000, //
				0b0000, //
				0b0000, //
			} ), //
			new Glyph( '.', 2, new byte[] { //
				0b00, //
				0b00, //
				0b00, //
				0b00, //
				0b11, //
				0b00, //
			} ),  //
			new Glyph( '/', 6, new byte[] { //
				0b000011, //
				0b000110, //
				0b001100, //
				0b011000, //
				0b110000, //
				0b000000, //
			} ), //
			new Glyph( '0', 5, new byte[] { //
				0b01110, //
				0b11011, //
				0b11011, //
				0b11011, //
				0b01110, //
				0b00000, //
			} ), //
			new Glyph( '1', 3, new byte[] { //
				0b011, //
				0b111, //
				0b011, //
				0b011, //
				0b011, //
				0b000, //
			} ), //
			new Glyph( '2', 4, new byte[] { //
				0b1110, //
				0b0011, //
				0b0110, //
				0b1100, //
				0b1111, //
				0b0000, //
			} ),  //
			new Glyph( '3', 4, new byte[] { //
				0b1110, //
				0b0011, //
				0b0110, //
				0b0011, //
				0b1110, //
				0b0000, //
			} ), //
			new Glyph( '4', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b11111, //
				0b00011, //
				0b00011, //
				0b00000, //
			} ), //
			new Glyph( '5', 5, new byte[] { //
				0b11111, //
				0b11000, //
				0b11110, //
				0b00011, //
				0b11110, //
				0b00000, //
			} ), //
			new Glyph( '6', 5, new byte[] { //
				0b01110, //
				0b11000, //
				0b11110, //
				0b11011, //
				0b01110, //
				0b00000, //
			} ), //
			new Glyph( '7', 5, new byte[] { //
				0b11111, //
				0b00011, //
				0b00110, //
				0b01100, //
				0b01100, //
				0b00000, //
			} ), //
			new Glyph( '8', 5, new byte[] { //
				0b01110, //
				0b11011, //
				0b01110, //
				0b11011, //
				0b01110, //
				0b00000, //
			} ), //
			new Glyph( '9', 5, new byte[] { //
				0b01110, //
				0b11011, //
				0b01111, //
				0b00011, //
				0b01110, //
				0b00000, //
			} ), //
			new Glyph( ':', 2, new byte[] { //
				0b00, //
				0b11, //
				0b00, //
				0b11, //
				0b00, //
				0b00, //
			} ), //
			new Glyph( ';', 3, new byte[] { //
				0b000, //
				0b011, //
				0b000, //
				0b011, //
				0b011, //
				0b110, //
			} ), //
			new Glyph( '<', 4, new byte[] { //
				0b0011, //
				0b0110, //
				0b1100, //
				0b0110, //
				0b0011, //
				0b0000, //
			} ), //
			new Glyph( '=', 4, new byte[] { //
				0b0000, //
				0b1111, //
				0b0000, //
				0b1111, //
				0b0000, //
				0b0000, //
			} ), //
			new Glyph( '>', 4, new byte[] { //
				0b1100, //
				0b0110, //
				0b0011, //
				0b0110, //
				0b1100, //
				0b0000, //
			} ), //
			new Glyph( '?', 4, new byte[] { //
				0b1110, //
				0b0011, //
				0b0110, //
				0b0000, //
				0b0110, //
				0b0000, //
			} ), //
			new Glyph( '@', 5, new byte[] { //
				0b011110, //
				0b110011, //
				0b110111, //
				0b110011, //
				0b011100, //
				0b000000, //
			} ), //
			new Glyph( 'A', 5, new byte[] { //
				0b01110, //
				0b11011, //
				0b11111, //
				0b11011, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'B', 5, new byte[] { //
				0b11110, //
				0b11011, //
				0b11110, //
				0b11011, //
				0b11110, //
				0b00000, //
			} ), //
			new Glyph( 'C', 4, new byte[] { //
				0b0111, //
				0b1100, //
				0b1100, //
				0b1100, //
				0b0111, //
				0b0000, //
			} ), //
			new Glyph( 'D', 5, new byte[] { //
				0b11110, //
				0b11011, //
				0b11011, //
				0b11011, //
				0b11110, //
				0b00000, //
			} ), //
			new Glyph( 'E', 4, new byte[] { //
				0b1111, //
				0b1100, //
				0b1111, //
				0b1100, //
				0b1111, //
				0b0000, //
			} ), //
			new Glyph( 'F', 4, new byte[] { //
				0b1111, //
				0b1100, //
				0b1111, //
				0b1100, //
				0b1100, //
				0b0000, //
			} ), //
			new Glyph( 'G', 5, new byte[] { //
				0b011110, //
				0b110000, //
				0b110111, //
				0b110011, //
				0b011110, //
				0b000000, //
			} ), //
			new Glyph( 'H', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b11111, //
				0b11011, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'I', 2, new byte[] { //
				0b11, //
				0b11, //
				0b11, //
				0b11, //
				0b11, //
				0b00, //
			} ), //
			new Glyph( 'J', 4, new byte[] { //
				0b0011, //
				0b0011, //
				0b0011, //
				0b0011, //
				0b1110, //
				0b0000, //
			} ), //
			new Glyph( 'K', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b11110, //
				0b11011, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'L', 4, new byte[] { //
				0b1100, //
				0b1100, //
				0b1100, //
				0b1100, //
				0b1111, //
				0b0000, //
			} ), //
			new Glyph( 'M', 5, new byte[] { //
				0b10001, //
				0b11011, //
				0b11111, //
				0b11011, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'N', 5, new byte[] { //
				0b10011, //
				0b11011, //
				0b11111, //
				0b11011, //
				0b11001, //
				0b00000, //
			} ), //
			new Glyph( 'O', 5, new byte[] { //
				0b01110, //
				0b11011, //
				0b11011, //
				0b11011, //
				0b01110, //
				0b00000, //
			} ), //
			new Glyph( 'P', 5, new byte[] { //
				0b11110, //
				0b11011, //
				0b11110, //
				0b11000, //
				0b11000, //
				0b00000, //
			} ), //
			new Glyph( 'Q', 5, new byte[] { //
				0b01110, //
				0b11011, //
				0b11011, //
				0b11011, //
				0b01110, //
				0b00011, //
			} ), //
			new Glyph( 'R', 5, new byte[] { //
				0b11110, //
				0b11011, //
				0b11110, //
				0b11011, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'S', 5, new byte[] { //
				0b01111, //
				0b11000, //
				0b01110, //
				0b00011, //
				0b11110, //
				0b00000, //
			} ), //
			new Glyph( 'T', 6, new byte[] { //
				0b111111, //
				0b001100, //
				0b001100, //
				0b001100, //
				0b001100, //
				0b000000, //
			} ), //
			new Glyph( 'U', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b11011, //
				0b11011, //
				0b11110, //
				0b00000, //
			} ), //
			new Glyph( 'V', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b11011, //
				0b01110, //
				0b00100, //
				0b00000, //
			} ), //
			new Glyph( 'W', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b11111, //
				0b11011, //
				0b10001, //
				0b00000, //
			} ), //
			new Glyph( 'X', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b01110, //
				0b11011, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'Y', 5, new byte[] { //
				0b11011, //
				0b11011, //
				0b01110, //
				0b01100, //
				0b01100, //
				0b00000, //
			} ), //
			new Glyph( 'Z', 4, new byte[] { //
				0b1111, //
				0b0011, //
				0b0110, //
				0b1100, //
				0b1111, //
				0b0000, //
			} ), //
			new Glyph( '[', 4, new byte[] { //
				0b1111, //
				0b1100, //
				0b1100, //
				0b1100, //
				0b1111, //
				0b0000, //
			} ), //
			new Glyph( '\\', 6, new byte[] { //
				0b110000, //
				0b011000, //
				0b001100, //
				0b000110, //
				0b000011, //
				0b000000, //
			} ), //
			new Glyph( ']', 4, new byte[] { //
				0b1111, //
				0b0011, //
				0b0011, //
				0b0011, //
				0b1111, //
				0b00, //
			} ), //
			new Glyph( '^', 5, new byte[] { //
				0b00100, //
				0b01110, //
				0b11011, //
				0b00000, //
				0b00000, //
				0b00000, //
			} ), //
			new Glyph( '_', 4, new byte[] { //
				0b0000, //
				0b0000, //
				0b0000, //
				0b0000, //
				0b1111, //
				0b0000, //
			} ), //
			new Glyph( '`', 3, new byte[] { //
				0b110, //
				0b011, //
				0b000, //
				0b000, //
				0b000, //
				0b000, //
			} ), //
			new Glyph( 'a', 5, new byte[] { //
				0b00000, //
				0b11110, //
				0b00111, //
				0b11011, //
				0b01111, //
				0b0000, //
			} ), //
			new Glyph( 'b', 5, new byte[] { //
				0b11000, //
				0b11110, //
				0b11011, //
				0b11011, //
				0b11110, //
				0b00000, //
			} ), //
			new Glyph( 'c', 4, new byte[] { //
				0b0000, //
				0b0111, //
				0b1100, //
				0b1100, //
				0b0111, //
				0b0000, //
			} ), //
			new Glyph( 'd', 5, new byte[] { //
				0b00011, //
				0b01111, //
				0b11011, //
				0b11011, //
				0b01111, //
				0b0000, //
			} ), //
			new Glyph( 'e', 5, new byte[] { //
				0b00000, //
				0b01110, //
				0b11011, //
				0b11100, //
				0b01111, //
				0b00000, //
			} ), //
			new Glyph( 'f', 4, new byte[] { //
				0b0011, //
				0b0110, //
				0b1111, //
				0b0110, //
				0b0110, //
				0b0000, //
			} ), //
			new Glyph( 'g', 5, new byte[] { //
				0b00000, //
				0b01110, //
				0b11011, //
				0b01111, //
				0b00011, //
				0b01110, //
			} ), //
			new Glyph( 'h', 5, new byte[] { //
				0b11000, //
				0b11110, //
				0b11011, //
				0b11011, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'i', 2, new byte[] { //
				0b11, //
				0b00, //
				0b11, //
				0b11, //
				0b11, //
				0b00, //
			} ), //
			new Glyph( 'j', 3, new byte[] { //
				0b011, //
				0b000, //
				0b011, //
				0b011, //
				0b011, //
				0b110, //
			} ), //
			new Glyph( 'k', 5, new byte[] { //
				0b11000, //
				0b11011, //
				0b11110, //
				0b11110, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'l', 3, new byte[] { //
				0b110, //
				0b110, //
				0b110, //
				0b110, //
				0b011, //
				0b000, //
			} ), //
			new Glyph( 'm', 7, new byte[] { //
				0b0000000, //
				0b1110110, //
				0b1101011, //
				0b1101011, //
				0b1101011, //
				0b0000000, //
			} ), //
			new Glyph( 'n', 5, new byte[] { //
				0b00000, //
				0b10110, //
				0b11011, //
				0b11011, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'o', 5, new byte[] { //
				0b00000, //
				0b01110, //
				0b11011, //
				0b11011, //
				0b01110, //
				0b00000, //
			} ), //
			new Glyph( 'p', 5, new byte[] { //
				0b00000, //
				0b11110, //
				0b11011, //
				0b11011, //
				0b11110, //
				0b11000, //
			} ), //
			new Glyph( 'q', 5, new byte[] { //
				0b00000, //
				0b01111, //
				0b11011, //
				0b11011, //
				0b01111, //
				0b00011, //
			} ), //
			new Glyph( 'r', 5, new byte[] { //
				0b00000, //
				0b11011, //
				0b11110, //
				0b11000, //
				0b11000, //
				0b00000, //
			} ), //
			new Glyph( 's', 4, new byte[] { //
				0b0000, //
				0b0111, //
				0b1100, //
				0b0011, //
				0b1110, //
				0b0000, //
			} ), //
			new Glyph( 't', 4, new byte[] { //
				0b0110, //
				0b1111, //
				0b0110, //
				0b0110, //
				0b0011, //
				0b0000, //
			} ), //
			new Glyph( 'u', 5, new byte[] { //
				0b00000, //
				0b11011, //
				0b11011, //
				0b11011, //
				0b01111, //
				0b00000, //
			} ), //
			new Glyph( 'v', 5, new byte[] { //
				0b00000, //
				0b11011, //
				0b11011, //
				0b01110, //
				0b00100, //
				0b00000, //
			} ), //
			new Glyph( 'w', 7, new byte[] { //
				0b0000000, //
				0b1100011, //
				0b1100011, //
				0b1101011, //
				0b0110110, //
				0b0000000, //
			} ), //
			new Glyph( 'x', 5, new byte[] { //
				0b00000, //
				0b11011, //
				0b01110, //
				0b01110, //
				0b11011, //
				0b00000, //
			} ), //
			new Glyph( 'y', 5, new byte[] { //
				0b00000, //
				0b11011, //
				0b11011, //
				0b01111, //
				0b00011, //
				0b01110, //
			} ), //
			new Glyph( 'z', 5, new byte[] { //
				0b00000, //
				0b11111, //
				0b00110, //
				0b01100, //
				0b11111, //
				0b00000, //
			} ), //
			new Glyph( '{', 4, new byte[] { //
				0b0111, //
				0b0110, //
				0b1100, //
				0b0110, //
				0b0111, //
				0b0000, //
			} ), //
			new Glyph( '|', 2, new byte[] { //
				0b11, //
				0b11, //
				0b00, //
				0b11, //
				0b11, //
				0b00, //
			} ), //
			new Glyph( '}', 4, new byte[] { //
				0b1110, //
				0b0110, //
				0b0011, //
				0b0110, //
				0b1110, //
				0b0000, //
			} ), //
			new Glyph( '~', 5, new byte[] { //
				0b10110, //
				0b01101, //
				0b00000, //
				0b00000, //
				0b00000, //
				0b00000, //
			} ) };

	public static final Font Instance = new Font( glyphs );
}
