package mikenakis.immutability.internal.type.field.assessments.provisory;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is provisory because it is of a field type which is provisory.
 */
public final class ProvisoryFieldTypeProvisoryFieldAssessment extends ProvisoryFieldAssessment
{
	public final ProvisoryTypeAssessment provisoryTypeAssessment;

	public ProvisoryFieldTypeProvisoryFieldAssessment( Field field, ProvisoryTypeAssessment provisoryTypeAssessment )
	{
		super( field );
		this.provisoryTypeAssessment = provisoryTypeAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( provisoryTypeAssessment ); }
}
