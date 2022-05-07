package mikenakis.immutability.type.field.assessments.provisory;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.field.annotations.InvariableField;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is provisory because it is an invariable array field.
 * (It has already been established that the array field is invariable, meaning that the field is either {@code final} or annotated
 * with @{@link InvariableField} and that it has also been annotated with @{@link InvariableArray}.)
 */
public final class IsInvariableArrayProvisoryFieldImmutabilityAssessment extends ProvisoryFieldImmutabilityAssessment
{
	public final ProvisoryTypeImmutabilityAssessment arrayElementTypeAssessment;

	public IsInvariableArrayProvisoryFieldImmutabilityAssessment( Stringizer stringizer, Field field, ProvisoryTypeImmutabilityAssessment arrayElementTypeAssessment )
	{
		super( stringizer, field );
		this.arrayElementTypeAssessment = arrayElementTypeAssessment;
	}

	@Override public List<? extends ImmutabilityAssessment> children() { return List.of( arrayElementTypeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is an invariable array of provisory element type " ).append( stringizer.stringizeClassName( arrayElementTypeAssessment.type ) );
	}
}
