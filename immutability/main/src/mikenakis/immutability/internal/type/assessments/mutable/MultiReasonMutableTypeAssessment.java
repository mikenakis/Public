package mikenakis.immutability.internal.type.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable due to multiple reasons.
 */
public class MultiReasonMutableTypeAssessment extends MutableTypeAssessment
{
	public final List<MutableTypeAssessment> mutableTypeAssessments;

	public MultiReasonMutableTypeAssessment( Class<?> jvmClass, List<MutableTypeAssessment> mutableTypeAssessments )
	{
		super( jvmClass );
		this.mutableTypeAssessments = mutableTypeAssessments;
	}

	@Override public Iterable<? extends Assessment> children() { return mutableTypeAssessments; }
}
