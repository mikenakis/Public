package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.IsInvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment;

import java.util.List;

/**
 * Signifies that an array is mutable because it has at least one element which is mutable.
 */
public final class HasMutableArrayElementMutableObjectAssessment<E> extends MutableObjectAssessment
{
	public final Iterable<E> iterableArrayWrapper;
	public final IsInvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment typeAssessment;
	public final int mutableElementIndex;
	public final E mutableElement;
	public final MutableObjectAssessment elementAssessment;

	public HasMutableArrayElementMutableObjectAssessment( Stringizer stringizer, Iterable<E> iterableArrayWrapper, IsInvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment typeAssessment, //
		int mutableElementIndex, E mutableElement, MutableObjectAssessment elementAssessment )
	{
		super( stringizer, iterableArrayWrapper );
		this.iterableArrayWrapper = iterableArrayWrapper;
		this.typeAssessment = typeAssessment;
		this.mutableElementIndex = mutableElementIndex;
		this.mutableElement = mutableElement;
		this.elementAssessment = elementAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment, elementAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is an invariable array" );
		stringBuilder.append( " and element " ).append( stringizer.stringizeObjectIdentity( mutableElement ) ).append( " at index " ).append( mutableElementIndex );
		stringBuilder.append( " is mutable" );
	}
}
