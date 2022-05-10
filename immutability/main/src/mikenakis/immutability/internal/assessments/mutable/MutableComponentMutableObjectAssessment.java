package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.NonImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.CompositeProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because it is a composite of which at least one component is mutable.
 */
public final class MutableComponentMutableObjectAssessment<T, E> extends MutableObjectAssessment
{
	public final T compositeObject;
	public final CompositeProvisoryTypeAssessment<T,E> typeAssessment;
	public final int mutableElementIndex;
	public final E mutableElement;
	public final MutableObjectAssessment elementAssessment;

	public MutableComponentMutableObjectAssessment( T compositeObject, CompositeProvisoryTypeAssessment<T,E> typeAssessment, //
		int mutableElementIndex, E mutableElement, MutableObjectAssessment elementAssessment )
	{
		this.compositeObject = compositeObject;
		this.typeAssessment = typeAssessment;
		this.mutableElementIndex = mutableElementIndex;
		this.mutableElement = mutableElement;
		this.elementAssessment = elementAssessment;
	}

	@Override public Object object() { return compositeObject; }
	@Override public NonImmutableTypeAssessment typeAssessment() { return typeAssessment; }
	@Override public List<Assessment> children() { return List.of( typeAssessment, elementAssessment ); }
}
