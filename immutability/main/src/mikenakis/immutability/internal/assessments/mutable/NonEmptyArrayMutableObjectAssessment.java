package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.ArrayMutableTypeAssessment;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Signifies that an object is mutable because it is a non-empty array.
 */
public final class NonEmptyArrayMutableObjectAssessment extends MutableObjectAssessment
{
	public final ArrayMutableTypeAssessment typeAssessment;

	public NonEmptyArrayMutableObjectAssessment( Object object, ArrayMutableTypeAssessment typeAssessment )
	{
		super( object );
		assert object.getClass().isArray();
		assert Array.getLength( object ) > 0;
		this.typeAssessment = typeAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment ); }
}
