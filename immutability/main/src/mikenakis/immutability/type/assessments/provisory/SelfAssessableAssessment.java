package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;

/**
 * Signifies that a type is provisory because it is self-assessable.
 * (Instances implement the {@link ImmutabilitySelfAssessable} interface.)
 */
public final class SelfAssessableAssessment extends ProvisoryTypeAssessment
{
	public SelfAssessableAssessment( Stringizer stringizer, Class<?> type )
	{
		super( stringizer, type );
		assert ImmutabilitySelfAssessable.class.isAssignableFrom( type );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because instances of this type are self-assessable" );
	}
}
