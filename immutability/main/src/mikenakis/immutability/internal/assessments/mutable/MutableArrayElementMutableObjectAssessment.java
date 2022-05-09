package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment;

import java.util.List;

/**
 * Signifies that an array is mutable because it has at least one element which is mutable.
 */
public final class MutableArrayElementMutableObjectAssessment<E> extends MutableObjectAssessment
{
	public final Iterable<E> iterableArrayWrapper;
	public final InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment typeAssessment;
	public final int mutableElementIndex;
	public final E mutableElement;
	public final MutableObjectAssessment elementAssessment;

	public MutableArrayElementMutableObjectAssessment( Iterable<E> iterableArrayWrapper, InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment typeAssessment, //
		int mutableElementIndex, E mutableElement, MutableObjectAssessment elementAssessment )
	{
		super( iterableArrayWrapper );
		this.iterableArrayWrapper = iterableArrayWrapper;
		this.typeAssessment = typeAssessment;
		this.mutableElementIndex = mutableElementIndex;
		this.mutableElement = mutableElement;
		this.elementAssessment = elementAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment, elementAssessment ); }
}
