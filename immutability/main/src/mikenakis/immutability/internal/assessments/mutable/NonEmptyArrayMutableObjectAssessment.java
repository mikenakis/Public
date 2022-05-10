package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.NonImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.mutable.ArrayMutableTypeAssessment;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Signifies that an object is mutable because it is a non-empty array.
 */
public final class NonEmptyArrayMutableObjectAssessment extends MutableObjectAssessment
{
	public final Object object;
	public final ArrayMutableTypeAssessment typeAssessment;

	public NonEmptyArrayMutableObjectAssessment( Object object, ArrayMutableTypeAssessment typeAssessment )
	{
		assert object.getClass().isArray();
		assert Array.getLength( object ) > 0;
		this.object = object;
		this.typeAssessment = typeAssessment;
	}

	@Override public Object object() { return object; }
	@Override public NonImmutableTypeAssessment typeAssessment() { return typeAssessment; }
	@Override public List<Assessment> children() { return List.of( typeAssessment ); }
}
