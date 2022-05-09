package mikenakis.immutability.internal.type.field.assessments.provisory;

import mikenakis.immutability.internal.type.field.assessments.NonImmutableFieldAssessment;

import java.lang.reflect.Field;

/**
 * Signifies that a field cannot be conclusively assessed as mutable or immutable.
 */
public abstract class ProvisoryFieldAssessment extends NonImmutableFieldAssessment
{
	protected ProvisoryFieldAssessment( Field field ) { super( field ); }
}
