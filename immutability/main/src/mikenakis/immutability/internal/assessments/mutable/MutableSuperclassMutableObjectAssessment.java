package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.NonImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.ProvisorySuperclassProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.ProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because its super object is mutable.
 */
public final class MutableSuperclassMutableObjectAssessment extends MutableObjectAssessment
{
	public final Object object;
	public final ProvisoryTypeAssessment typeAssessment;
	public final MutableObjectAssessment mutableSuperObjectAssessment;

	public MutableSuperclassMutableObjectAssessment( Object object, ProvisoryTypeAssessment typeAssessment, MutableObjectAssessment mutableSuperObjectAssessment )
	{
		assert typeAssessment.type == object.getClass();
		assert typeAssessment instanceof ProvisorySuperclassProvisoryTypeAssessment;
		assert mutableSuperObjectAssessment.object() == object;
		this.object = object;
		this.typeAssessment = typeAssessment;
		this.mutableSuperObjectAssessment = mutableSuperObjectAssessment;
	}

	@Override public Object object() { return object; }
	@Override public NonImmutableTypeAssessment typeAssessment() { return typeAssessment; }
	@Override public List<Assessment> children() { return List.of( typeAssessment, mutableSuperObjectAssessment ); }
}
