package mikenakis.immutability.type.field.assessments.provisory;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.annotations.InvariableArray;
import mikenakis.immutability.annotations.Invariable;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is provisory because it is an invariable array field.
 * <p>
 * (It has already been established that the array field is invariable, meaning that the field is either {@code final} or annotated with @{@link Invariable} and
 * that it has also been annotated with @{@link InvariableArray}.)
 */
public final class IsInvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment extends ProvisoryFieldAssessment
{
	public final ProvisoryTypeAssessment arrayElementTypeAssessment;

	public IsInvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment( Stringizer stringizer, Field field, ProvisoryTypeAssessment arrayElementTypeAssessment )
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
