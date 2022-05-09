package mikenakis.immutability.internal.type.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because it has mutable fields.
 */
public class MutableFieldsMutableTypeAssessment extends MutableTypeAssessment
{
	public final List<MutableFieldAssessment> mutableFieldAssessments;

	public MutableFieldsMutableTypeAssessment( Class<?> jvmClass, List<MutableFieldAssessment> mutableFieldAssessments )
	{
		super( jvmClass );
		this.mutableFieldAssessments = mutableFieldAssessments;
	}

	@Override public Iterable<? extends Assessment> children() { return mutableFieldAssessments; }
}
