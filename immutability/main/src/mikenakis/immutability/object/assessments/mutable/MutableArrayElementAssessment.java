package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.type.field.assessments.provisory.InvariableArrayFieldAssessment;

import java.util.List;

/**
 * Signifies that an array is mutable because it has at least one element which is mutable.
 */
public final class MutableArrayElementAssessment<E> extends MutableObjectAssessment
{
	public final Iterable<E> iterableArrayWrapper;
	public final InvariableArrayFieldAssessment typeAssessment;
	public final int mutableElementIndex;
	public final E mutableElement;
	public final MutableObjectAssessment elementAssessment;

	public MutableArrayElementAssessment( Stringizer stringizer, Iterable<E> iterableArrayWrapper, InvariableArrayFieldAssessment typeAssessment, //
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
