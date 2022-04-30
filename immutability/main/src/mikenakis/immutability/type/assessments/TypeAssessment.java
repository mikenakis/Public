package mikenakis.immutability.type.assessments;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;

public abstract class TypeAssessment extends Assessment
{
	public enum Mode
	{
		Assessed( "is" ),
		Preassessed( "is preassessed as" ),
		PreassessedByDefault( "is preassessed by default as" );

		public final String text;

		Mode( String text )
		{
			this.text = text;
		}
	}

	protected TypeAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}
}
