package mikenakis.immutability.internal.type.field.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is mutable because it is an invariable array field of mutable element type.
 */
public final class InvariableArrayOfMutableElementTypeMutableFieldAssessment extends MutableFieldAssessment
{
	public final MutableTypeAssessment arrayElementTypeAssessment;

	public InvariableArrayOfMutableElementTypeMutableFieldAssessment( Stringizer stringizer, Field field, MutableTypeAssessment arrayElementTypeAssessment )
	{
		super( stringizer, field );
		this.arrayElementTypeAssessment = arrayElementTypeAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( arrayElementTypeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is an invariable array of mutable element type " ).append( stringizer.stringizeClassName( arrayElementTypeAssessment.type ) );
	}
}
