package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;

/**
 * Signifies that a type is provisory because it is an interface.
 */
public final class InterfaceProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public InterfaceProvisoryTypeAssessment( Stringizer stringizer, Class<?> type )
	{
		super( stringizer, type );
		assert type.isInterface();
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is an interface" );
	}
}
