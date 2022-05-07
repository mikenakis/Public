package mikenakis.immutability.type.field.assessments.mutable;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.field.annotations.InvariableField;

import java.lang.reflect.Field;

/**
 * Signifies that a field is mutable because it is variable, and it has not been annotated with @{@link InvariableField}.
 * (Thus, the field is mutable regardless of the immutability assessment of the field type.)
 */
public final class VariableMutableFieldImmutabilityAssessment extends MutableFieldImmutabilityAssessment
{
	public VariableMutableFieldImmutabilityAssessment( Stringizer stringizer, Field field ) { super( stringizer, field ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is not final and it has not been annotated with @" ).append( InvariableField.class.getSimpleName() );
	}
}
