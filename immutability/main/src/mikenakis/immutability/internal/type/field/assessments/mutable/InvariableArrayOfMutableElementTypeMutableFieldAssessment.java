package mikenakis.immutability.internal.type.field.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is mutable because it is an invariable array field of mutable element type.
 */
public final class InvariableArrayOfMutableElementTypeMutableFieldAssessment extends MutableFieldAssessment
{
	public final MutableTypeAssessment arrayElementTypeAssessment;

	public InvariableArrayOfMutableElementTypeMutableFieldAssessment( Field field, MutableTypeAssessment arrayElementTypeAssessment )
	{
		super( field );
		assert field.getType().isArray();
		assert field.getType().getComponentType() == arrayElementTypeAssessment.type;
		this.arrayElementTypeAssessment = arrayElementTypeAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( arrayElementTypeAssessment ); }
}
