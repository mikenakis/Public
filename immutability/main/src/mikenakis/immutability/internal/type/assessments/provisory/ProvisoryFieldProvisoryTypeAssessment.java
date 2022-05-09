package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.util.List;

/**
 * Signifies that a class is provisory because it contains a provisory field.
 */
public final class ProvisoryFieldProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final ProvisoryFieldAssessment fieldAssessment;

	public ProvisoryFieldProvisoryTypeAssessment( Class<?> jvmClass, ProvisoryFieldAssessment fieldAssessment )
	{
		super( jvmClass );
		this.fieldAssessment = fieldAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( fieldAssessment ); }
}
