package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.IsInvariableArrayProvisoryFieldImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that an array is mutable because it has at least one element which is mutable.
 */
public final class HasMutableArrayElementMutableObjectImmutabilityAssessment<E> extends MutableObjectImmutabilityAssessment
{
	public final Iterable<E> iterableArrayWrapper;
	public final IsInvariableArrayProvisoryFieldImmutabilityAssessment typeAssessment;
	public final int mutableElementIndex;
	public final E mutableElement;
	public final MutableObjectImmutabilityAssessment elementAssessment;

	public HasMutableArrayElementMutableObjectImmutabilityAssessment( Stringizer stringizer, Iterable<E> iterableArrayWrapper, IsInvariableArrayProvisoryFieldImmutabilityAssessment typeAssessment, //
		int mutableElementIndex, E mutableElement, MutableObjectImmutabilityAssessment elementAssessment )
	{
		super( stringizer, iterableArrayWrapper );
		this.iterableArrayWrapper = iterableArrayWrapper;
		this.typeAssessment = typeAssessment;
		this.mutableElementIndex = mutableElementIndex;
		this.mutableElement = mutableElement;
		this.elementAssessment = elementAssessment;
	}

	@Override public Iterable<ImmutabilityAssessment> children() { return List.of( typeAssessment, elementAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is an invariable array" );
		stringBuilder.append( " and element " ).append( stringizer.stringizeObjectIdentity( mutableElement ) ).append( " at index " ).append( mutableElementIndex );
		stringBuilder.append( " is mutable" );
	}
}
