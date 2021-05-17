package mikenakis.bytecode.dumping;

/**
 * Style for emitting text.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public enum Style
{
	RAW_ONLY( true, false ), //synonyms considered: BASIC, STANDARD, PLAIN, SIMPLE, REGULAR, RAW, CRUDE
	MIXED( true, true ), //synonyms considered: MIXED, ENRICHED, AUGMENTED, DECORATED, GILDED, ENHANCED
	GILDED_ONLY( false, true ); //synonyms considered: RAREFIED, TRIMMED, CLIPPED, PRUNED, REFINED, FILTERED

	public final boolean raw;
	public final boolean gild;

	Style( boolean raw, boolean gild )
	{
		this.raw = raw;
		this.gild = gild;
	}
}
