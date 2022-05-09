package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that a class has a provisory ancestor.
 */
public final class ProvisoryAncestorProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final ProvisoryTypeAssessment ancestorAssessment;

	public ProvisoryAncestorProvisoryTypeAssessment( Class<?> jvmClass, ProvisoryTypeAssessment ancestorAssessment )
	{
		super( jvmClass );
		this.ancestorAssessment = ancestorAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( ancestorAssessment ); }
}
