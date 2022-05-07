package mikenakis.immutability.type.field.assessments.provisory;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is provisory because it is of a field type which is provisory.
 */
public final class OfProvisoryTypeProvisoryFieldImmutabilityAssessment extends ProvisoryFieldImmutabilityAssessment
{
	public final ProvisoryTypeImmutabilityAssessment provisoryTypeAssessment;

	public OfProvisoryTypeProvisoryFieldImmutabilityAssessment( Stringizer stringizer, Field field, ProvisoryTypeImmutabilityAssessment provisoryTypeAssessment )
	{
		super( stringizer, field );
		this.provisoryTypeAssessment = provisoryTypeAssessment;
	}

	@Override public List<? extends ImmutabilityAssessment> children() { return List.of( provisoryTypeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of provisory type " ).append( stringizer.stringizeClassName( field.getType() ) );
	}
}
