package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.MultiReasonProvisoryTypeImmutabilityAssessment;

import java.util.List;
import java.util.stream.Stream;

/**
 * Signifies that an object is mutable due to multiple reasons.
 */
public final class MultiReasonMutableObjectImmutabilityAssessment extends MutableObjectImmutabilityAssessment
{
	public final MultiReasonProvisoryTypeImmutabilityAssessment typeAssessment;
	public final List<MutableObjectImmutabilityAssessment> reasons;

	public MultiReasonMutableObjectImmutabilityAssessment( Stringizer stringizer, Object object, MultiReasonProvisoryTypeImmutabilityAssessment typeAssessment, //
		List<MutableObjectImmutabilityAssessment> reasons )
	{
		super( stringizer, object );
		this.typeAssessment = typeAssessment;
		this.reasons = reasons;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " due to multiple reasons" );
	}

	@Override public List<? extends Assessment> children()
	{
		return Stream.concat( Stream.of( typeAssessment ), reasons.stream() ).toList();
	}
}
