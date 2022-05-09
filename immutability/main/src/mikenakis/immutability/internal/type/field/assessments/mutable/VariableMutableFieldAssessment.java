package mikenakis.immutability.internal.type.field.assessments.mutable;

import mikenakis.immutability.annotations.Invariable;

import java.lang.reflect.Field;

/**
 * Signifies that a field is mutable because it is not {@code final}, and it has not been annotated with @{@link Invariable}. (Thus, the field is mutable
 * regardless of the immutability assessment of the field type.)
 */
public final class VariableMutableFieldAssessment extends MutableFieldAssessment
{
	public VariableMutableFieldAssessment( Field field ) { super( field ); }
}
