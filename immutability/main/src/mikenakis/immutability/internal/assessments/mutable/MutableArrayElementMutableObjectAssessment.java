package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.NonImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.ProvisoryTypeAssessment;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Signifies that an array is mutable because it has at least one element which is mutable.
 */
public final class MutableArrayElementMutableObjectAssessment extends MutableObjectAssessment
{
	public final Object object;
	public final ProvisoryTypeAssessment typeAssessment;
	public final int mutableElementIndex;
	public final MutableObjectAssessment elementAssessment;

	public MutableArrayElementMutableObjectAssessment( Object object, ProvisoryTypeAssessment typeAssessment, int mutableElementIndex, MutableObjectAssessment elementAssessment )
	{
		assert object.getClass().isArray();
		assert Array.get( object, mutableElementIndex ) == elementAssessment.object();
		assert typeAssessment.type == object.getClass();
		this.object = object;
		this.typeAssessment = typeAssessment;
		this.mutableElementIndex = mutableElementIndex;
		this.elementAssessment = elementAssessment;
	}

	@Override public Object object() { return object; }
	@Override public NonImmutableTypeAssessment typeAssessment() { return typeAssessment; }
	@Override public List<Assessment> children() { return List.of( elementAssessment ); }
}
