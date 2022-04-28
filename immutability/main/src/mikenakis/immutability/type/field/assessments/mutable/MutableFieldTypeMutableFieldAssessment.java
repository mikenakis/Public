package mikenakis.immutability.type.field.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is mutable because even though it is final, it is of a mutable type.
 */
public final class MutableFieldTypeMutableFieldAssessment extends MutableFieldAssessment
{
	public final MutableTypeAssessment fieldTypeAssessment;

	public MutableFieldTypeMutableFieldAssessment( Stringizer stringizer, Field field, MutableTypeAssessment fieldTypeAssessment )
	{
		super( stringizer, field );
		this.fieldTypeAssessment = fieldTypeAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( fieldTypeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of mutable type " ).append( stringizer.stringize( fieldTypeAssessment.type ) );
	}
}
