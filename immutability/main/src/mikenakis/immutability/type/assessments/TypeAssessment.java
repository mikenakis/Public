package mikenakis.immutability.type.assessments;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;

public abstract class TypeAssessment extends Assessment
{
	public enum Mode
	{
		Assessed( "assessed" ),
		Preassessed( "preassessed" ),
		PreassessedByDefault( "preassessed by default" );

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
