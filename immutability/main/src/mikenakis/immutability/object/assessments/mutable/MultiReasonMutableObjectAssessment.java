package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.type.assessments.provisory.MultiReasonProvisoryTypeAssessment;

import java.util.List;
import java.util.stream.Stream;

/**
 * Signifies that an object is mutable due to multiple reasons.
 */
public final class MultiReasonMutableObjectAssessment extends MutableObjectAssessment
{
	public final MultiReasonProvisoryTypeAssessment typeAssessment;
	public final List<MutableObjectAssessment> mutableReasons;

	public MultiReasonMutableObjectAssessment( Stringizer stringizer, Object object, MultiReasonProvisoryTypeAssessment typeAssessment, //
		List<MutableObjectAssessment> mutableReasons )
	{
		super( stringizer, object );
		this.typeAssessment = typeAssessment;
		this.mutableReasons = mutableReasons;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " due to multiple reasons" );
	}

	@Override public List<? extends Assessment> children()
	{
		return Stream.concat( Stream.of( typeAssessment ), mutableReasons.stream() ).toList();
	}
}
