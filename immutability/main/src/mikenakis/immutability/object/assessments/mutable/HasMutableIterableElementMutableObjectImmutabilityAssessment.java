package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsIterableProvisoryTypeImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because it is {@link Iterable}, and it has at least one element which is mutable.
 */
public final class HasMutableIterableElementMutableObjectImmutabilityAssessment<E> extends MutableObjectImmutabilityAssessment
{
	public final Iterable<E> iterableObject;
	public final IsIterableProvisoryTypeImmutabilityAssessment typeAssessment;
	public final int mutableElementIndex;
	public final E mutableElement;
	public final MutableObjectImmutabilityAssessment elementAssessment;

	public HasMutableIterableElementMutableObjectImmutabilityAssessment( Stringizer stringizer, Iterable<E> iterableObject, IsIterableProvisoryTypeImmutabilityAssessment typeAssessment, //
		int mutableElementIndex, E mutableElement, MutableObjectImmutabilityAssessment elementAssessment )
	{
		super( stringizer, iterableObject );
		this.iterableObject = iterableObject;
		this.typeAssessment = typeAssessment;
		this.mutableElementIndex = mutableElementIndex;
		this.mutableElement = mutableElement;
		this.elementAssessment = elementAssessment;
	}

	@Override public Iterable<ImmutabilityAssessment> children() { return List.of( typeAssessment, elementAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because its class is iterable-provisory" );
		stringBuilder.append( " and element " ).append( stringizer.stringizeObjectIdentity( mutableElement ) ).append( " at index " ).append( mutableElementIndex );
		stringBuilder.append( " is mutable" );
	}
}
