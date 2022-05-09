package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because its ancestor is mutable.
 */
public final class MutableAncestorMutableObjectAssessment extends MutableObjectAssessment
{
	public final ProvisoryTypeAssessment typeAssessment;
	public final MutableObjectAssessment ancestorAssessment;

	public MutableAncestorMutableObjectAssessment( Object object, ProvisoryTypeAssessment typeAssessment, //
		MutableObjectAssessment ancestorAssessment )
	{
		super( object );
		this.typeAssessment = typeAssessment;
		this.ancestorAssessment = ancestorAssessment;
	}

	@Override public List<? extends Assessment> children()
	{
		return List.of( typeAssessment, ancestorAssessment );
	}
}
