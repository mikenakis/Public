package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that a class is provisory due to multiple reasons.
 */
public final class MultiReasonProvisoryTypeImmutabilityAssessment extends ProvisoryTypeImmutabilityAssessment
{
	public final List<ProvisoryTypeImmutabilityAssessment> reasons;

	public MultiReasonProvisoryTypeImmutabilityAssessment( Stringizer stringizer, Class<?> jvmClass, List<ProvisoryTypeImmutabilityAssessment> reasonAssessments )
	{
		super( stringizer, jvmClass );
		assert Helpers.isClass( jvmClass ); //the type must be a class; there is a different assessment for interfaces.
		reasons = reasonAssessments;
	}

	@Override public List<? extends Assessment> children()
	{
		return reasons;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " due to multiple reasons" );
	}
}
