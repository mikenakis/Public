package mikenakis.immutability.type.field.assessments.provisory;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.field.assessments.NonImmutableFieldImmutabilityAssessment;

import java.lang.reflect.Field;

/**
 * Signifies that a field cannot be conclusively assessed as mutable or immutable.
 */
public abstract class ProvisoryFieldImmutabilityAssessment extends NonImmutableFieldImmutabilityAssessment
{
	protected ProvisoryFieldImmutabilityAssessment( Stringizer stringizer, Field field ) { super( stringizer, field ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " is provisory" );
	}
}
