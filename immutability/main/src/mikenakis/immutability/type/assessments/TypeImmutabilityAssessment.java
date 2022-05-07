package mikenakis.immutability.type.assessments;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;

public abstract class TypeImmutabilityAssessment extends ImmutabilityAssessment
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

	protected TypeImmutabilityAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}
}
