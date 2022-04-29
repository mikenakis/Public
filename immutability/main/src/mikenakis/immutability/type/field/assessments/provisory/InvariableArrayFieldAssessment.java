package mikenakis.immutability.type.field.assessments.provisory;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.field.annotations.InvariableField;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that an array field is provisory because it is an invariable array field, so each array element must be assessed.
 * (It has already been established that the array field is invariable, meaning that the field is either {@code final} or annotated
 * with @{@link InvariableField} and also that it has been annotated with @{@link InvariableArray}.)
 */
public final class InvariableArrayFieldAssessment extends ProvisoryFieldAssessment
{
	public final ProvisoryTypeAssessment arrayElementTypeAssessment;

	public InvariableArrayFieldAssessment( Stringizer stringizer, Field field, ProvisoryTypeAssessment arrayElementTypeAssessment )
	{
		super( stringizer, field );
		this.arrayElementTypeAssessment = arrayElementTypeAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( arrayElementTypeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is an invariable array of provisory element type " ).append( stringizer.stringizeClassName( arrayElementTypeAssessment.type ) );
	}
}
