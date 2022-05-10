package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisorySuperclassProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because its super object is mutable.
 */
public final class MutableSuperclassMutableObjectAssessment extends MutableObjectAssessment
{
	public final ProvisoryTypeAssessment typeAssessment;
	public final MutableObjectAssessment mutableSuperObjectAssessment;

	public MutableSuperclassMutableObjectAssessment( Object object, ProvisoryTypeAssessment typeAssessment, MutableObjectAssessment mutableSuperObjectAssessment )
	{
		super( object );
		assert typeAssessment.type == object.getClass();
		assert typeAssessment instanceof ProvisorySuperclassProvisoryTypeAssessment;
		assert mutableSuperObjectAssessment.object == object;
		this.typeAssessment = typeAssessment;
		this.mutableSuperObjectAssessment = mutableSuperObjectAssessment;
	}

	@Override public List<? extends Assessment> children()
	{
		return List.of( typeAssessment, mutableSuperObjectAssessment );
	}
}
