package mikenakis.immutability.type.field.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is mutable because even though it is invariable, it is of a mutable type.
 */
public final class OfMutableFieldTypeMutableFieldAssessment extends MutableFieldAssessment
{
	public final MutableTypeAssessment fieldTypeAssessment;

	public OfMutableFieldTypeMutableFieldAssessment( Stringizer stringizer, Field field, MutableTypeAssessment fieldTypeAssessment )
	{
		super( stringizer, field );
		this.fieldTypeAssessment = fieldTypeAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( fieldTypeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of mutable type " ).append( stringizer.stringizeClassName( fieldTypeAssessment.type ) );
	}
}
