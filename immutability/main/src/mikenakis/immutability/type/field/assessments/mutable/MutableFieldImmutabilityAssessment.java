package mikenakis.immutability.type.field.assessments.mutable;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.field.assessments.NonImmutableFieldImmutabilityAssessment;

import java.lang.reflect.Field;

/**
 * Signifies that a field is mutable.
 */
public abstract class MutableFieldImmutabilityAssessment extends NonImmutableFieldImmutabilityAssessment
{
	protected MutableFieldImmutabilityAssessment( Stringizer stringizer, Field field ) { super( stringizer, field ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " is mutable" );
	}
}
