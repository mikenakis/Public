package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.assessments.Assessment;

import java.util.List;

/**
 * Signifies that a class has a provisory superclass.
 */
public final class ProvisorySuperclassProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final ProvisoryTypeAssessment superclassAssessment;

	public ProvisorySuperclassProvisoryTypeAssessment( Class<?> jvmClass, ProvisoryTypeAssessment superclassAssessment )
	{
		super( jvmClass );
		assert jvmClass.getSuperclass() == superclassAssessment.type;
		this.superclassAssessment = superclassAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( superclassAssessment ); }
}
