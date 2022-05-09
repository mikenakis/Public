package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableTypeAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because its class is mutable.
 */
public final class OfMutableTypeMutableObjectAssessment extends MutableObjectAssessment
{
	public final MutableTypeAssessment typeAssessment;

	public OfMutableTypeMutableObjectAssessment( Object object, MutableTypeAssessment typeAssessment )
	{
		super( object );
		assert object.getClass() == typeAssessment.type;
		this.typeAssessment = typeAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment ); }
}
