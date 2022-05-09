package mikenakis.immutability.internal.type.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because its superclass is mutable.
 */
public class MutableSuperclassMutableTypeAssessment extends MutableTypeAssessment
{
	public final MutableTypeAssessment superclassAssessment;

	public MutableSuperclassMutableTypeAssessment( Class<?> jvmClass, MutableTypeAssessment superclassAssessment )
	{
		super( jvmClass );
		this.superclassAssessment = superclassAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( superclassAssessment ); }
}
