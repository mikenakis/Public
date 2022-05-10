package mikenakis.immutability.internal.type.field.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is mutable because even though it is invariable, it is of a mutable type.
 */
public final class MutableFieldTypeMutableFieldAssessment extends MutableFieldAssessment
{
	public final MutableTypeAssessment fieldTypeAssessment;

	public MutableFieldTypeMutableFieldAssessment( Field field, MutableTypeAssessment fieldTypeAssessment )
	{
		super( field );
		assert field.getType() == fieldTypeAssessment.type;
		this.fieldTypeAssessment = fieldTypeAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( fieldTypeAssessment ); }
}
