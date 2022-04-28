package mikenakis.immutability.type.field.assessments.mutable;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.annotations.InvariableArray;
import mikenakis.immutability.type.field.FieldImmutabilityAssessor;

import java.lang.reflect.Field;

/**
 * Signifies that an invariable field is mutable because it is an array field, and it has not been annotated with @{@link InvariableArray}.
 * (So each array element is still variable, regardless of the immutability assessment of the element itself.)
 * Note: we could turn this into a provisory assessment because the array may still turn out to be immutable if it is of zero length,
 * (if the array has no elements, then there are no elements to vary,) but this would complicate things, and it would not help much, because
 * it would not save us from any runtime checks.
 */
public final class ArrayMutableFieldAssessment extends MutableFieldAssessment
{
	public ArrayMutableFieldAssessment( Stringizer stringizer, Field field )
	{
		super( stringizer, field );
		assert FieldImmutabilityAssessor.isInvariableField( field );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because is an array and it has not been annotated with @" ).append( InvariableArray.class.getSimpleName() );
	}
}
