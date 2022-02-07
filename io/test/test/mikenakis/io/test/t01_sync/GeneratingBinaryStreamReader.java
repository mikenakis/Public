package mikenakis.io.test.t01_sync;

import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.nio.charset.StandardCharsets;

/**
 * A Channel that generates data.
 *
 * @author michael.gr
 */
public final class GeneratingBinaryStreamReader extends Mutable implements BinaryStreamReader.Defaults
{
	private static byte[] bytesFromString( String s )
	{
		return s.getBytes( StandardCharsets.UTF_8 );
	}

	@SuppressWarnings( "SpellCheckingInspection" ) private static final byte[][] words =
		{
			bytesFromString( "Luke Skywalker" ),
			bytesFromString( "C-3PO" ),
			bytesFromString( "R2-D2" ),
			bytesFromString( "Darth Vader" ),
			bytesFromString( "Leia Organa" ),
			bytesFromString( "Owen Lars" ),
			bytesFromString( "Beru Whitesun lars" ),
			bytesFromString( "R5-D4" ),
			bytesFromString( "Biggs Darklighter" ),
			bytesFromString( "Obi-Wan Kenobi" ),
			bytesFromString( "Anakin Skywalker" ),
			bytesFromString( "Wilhuff Tarkin" ),
			bytesFromString( "Chewbacca" ),
			bytesFromString( "Han Solo" ),
			bytesFromString( "Greedo" ),
			bytesFromString( "Jabba Desilijic Tiure" ),
			bytesFromString( "Wedge Antilles" ),
			bytesFromString( "Jek Tono Porkins" ),
			bytesFromString( "Yoda" ),
			bytesFromString( "Palpatine" ),
			bytesFromString( "Boba Fett" ),
			bytesFromString( "IG-88" ),
			bytesFromString( "Bossk" ),
			bytesFromString( "Lando Calrissian" ),
			bytesFromString( "Lobot" ),
			bytesFromString( "Ackbar" ),
			bytesFromString( "Mon Mothma" ),
			bytesFromString( "Arvel Crynyd" ),
			bytesFromString( "Wicket Systri Warrick" ),
			bytesFromString( "Nien Nunb" ),
			bytesFromString( "Qui-Gon Jinn" ),
			bytesFromString( "Nute Gunray" ),
			bytesFromString( "Finis Valorum" ),
			bytesFromString( "Padmé Amidala" ),
			bytesFromString( "Jar Jar Binks" ),
			bytesFromString( "Roos Tarpals" ),
			bytesFromString( "Rugor Nass" ),
			bytesFromString( "Ric Olié" ),
			bytesFromString( "Watto" ),
			bytesFromString( "Sebulba" ),
			bytesFromString( "Quarsh Panaka" ),
			bytesFromString( "Shmi Skywalker" ),
			bytesFromString( "Darth Maul" ),
			bytesFromString( "Bib Fortuna" ),
			bytesFromString( "Ayla Secura" ),
			bytesFromString( "Ratts Tyerel" ),
			bytesFromString( "Dud Bolt" ),
			bytesFromString( "Gasgano" ),
			bytesFromString( "Ben Quadinaros" ),
			bytesFromString( "Mace Windu" ),
			bytesFromString( "Ki-Adi-Mundi" ),
			bytesFromString( "Kit Fisto" ),
			bytesFromString( "Eeth Koth" ),
			bytesFromString( "Adi Gallia" ),
			bytesFromString( "Saesee Tiin" ),
			bytesFromString( "Yarael Poof" ),
			bytesFromString( "Plo Koon" ),
			bytesFromString( "Mas Amedda" ),
			bytesFromString( "Gregar Typho" ),
			bytesFromString( "Cordé" ),
			bytesFromString( "Cliegg Lars" ),
			bytesFromString( "Poggle the Lesser" ),
			bytesFromString( "Luminara Unduli" ),
			bytesFromString( "Barriss Offee" ),
			bytesFromString( "Dormé" ),
			bytesFromString( "Dooku" ),
			bytesFromString( "Bail Prestor Organa" ),
			bytesFromString( "Jango Fett" ),
			bytesFromString( "Zam Wesell" ),
			bytesFromString( "Dexter Jettster" ),
			bytesFromString( "Lama Su" ),
			bytesFromString( "Taun We" ),
			bytesFromString( "Jocasta Nu" ),
			bytesFromString( "R4-P17" ),
			bytesFromString( "Wat Tambor" ),
			bytesFromString( "San Hill" ),
			bytesFromString( "Shaak Ti" ),
			bytesFromString( "Grievous" ),
			bytesFromString( "Tarfful" ),
			bytesFromString( "Raymus Antilles" ),
			bytesFromString( "Sly Moore" ),
			bytesFromString( "Tion Medon" ),
			bytesFromString( "Finn" ),
			bytesFromString( "Rey" ),
			bytesFromString( "Poe Dameron" ),
			bytesFromString( "BB8" )
		};

	private final RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator( mutationContext );
	private final int wordCount;
	private byte[] currentWord = new byte[0];
	private int currentWordPosition = 0;
	private int wordIndex = 0;
	private boolean eof = false;

	public GeneratingBinaryStreamReader( MutationContext mutationContext, int wordCount, int seed )
	{
		super( mutationContext );
		assert wordCount >= 0;
		this.wordCount = wordCount;
		randomNumberGenerator.setSeed( seed );
		loadNextWord();
	}

	@Override public int readBuffer( byte[] bytes, int index, int count )
	{
		if( eof )
			return -1;  //XXX MINUS ONE
		int i;
		for( i = 0; i < count; i++ )
		{
			int nextByte = getNextByte();
			if( nextByte == -1 )
			{
				eof = true;
				break;
			}
			bytes[index++] = (byte)nextByte;
		}
		assert i >= 0;
		return i;
	}

	@Override public boolean isFinished()
	{
		return eof;
	}

	private int getNextByte()
	{
		if( currentWordPosition >= currentWord.length )
		{
			if( currentWordPosition == currentWord.length )
			{
				currentWordPosition++;
				return '\n';
			}
			if( wordIndex >= wordCount )
				return -1;
			loadNextWord();
		}
		return currentWord[currentWordPosition++];
	}

	private void loadNextWord()
	{
		currentWord = words[randomNumberGenerator.nextInt() % words.length];
		currentWordPosition = 0;
		wordIndex++;
	}
}
