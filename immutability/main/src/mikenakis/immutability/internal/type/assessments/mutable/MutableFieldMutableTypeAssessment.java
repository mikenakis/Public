package mikenakis.immutability.internal.type.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because it has mutable fields.
 */
public class MutableFieldMutableTypeAssessment extends MutableTypeAssessment
{
	public final MutableFieldAssessment mutableFieldAssessment;

	public MutableFieldMutableTypeAssessment( Class<?> jvmClass, MutableFieldAssessment mutableFieldAssessment )
	{
		super( jvmClass );
		this.mutableFieldAssessment = mutableFieldAssessment;
	}

	@Override public Iterable<? extends Assessment> children() { return List.of( mutableFieldAssessment ); }
}
