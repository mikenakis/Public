package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.assessments.Assessment;

import java.util.List;

/**
 * Signifies that a class has a provisory superclass.
 */
public final class ProvisorySuperclassProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final ProvisoryTypeAssessment ancestorAssessment;

	public ProvisorySuperclassProvisoryTypeAssessment( Class<?> jvmClass, ProvisoryTypeAssessment ancestorAssessment )
	{
		super( jvmClass );
		assert jvmClass.getSuperclass() == ancestorAssessment.type;
		this.ancestorAssessment = ancestorAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( ancestorAssessment ); }
}
