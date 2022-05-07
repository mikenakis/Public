package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsCompositeProvisoryTypeImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because it is a composite of which at least one component is mutable.
 */
public final class HasMutableComponentMutableObjectImmutabilityAssessment<T,E> extends MutableObjectImmutabilityAssessment
{
	public final T compositeObject;
	public final IsCompositeProvisoryTypeImmutabilityAssessment<T,E> typeAssessment;
	public final int mutableElementIndex;
	public final E mutableElement;
	public final MutableObjectImmutabilityAssessment elementAssessment;

	public HasMutableComponentMutableObjectImmutabilityAssessment( Stringizer stringizer, T compositeObject, IsCompositeProvisoryTypeImmutabilityAssessment<T,E> typeAssessment, //
		int mutableElementIndex, E mutableElement, MutableObjectImmutabilityAssessment elementAssessment )
	{
		super( stringizer, compositeObject );
		this.compositeObject = compositeObject;
		this.typeAssessment = typeAssessment;
		this.mutableElementIndex = mutableElementIndex;
		this.mutableElement = mutableElement;
		this.elementAssessment = elementAssessment;
	}

	@Override public Iterable<ImmutabilityAssessment> children() { return List.of( typeAssessment, elementAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is composite" );
		stringBuilder.append( " and element " ).append( stringizer.stringizeObjectIdentity( mutableElement ) ).append( " at index " ).append( mutableElementIndex );
		stringBuilder.append( " is mutable" );
	}
}
