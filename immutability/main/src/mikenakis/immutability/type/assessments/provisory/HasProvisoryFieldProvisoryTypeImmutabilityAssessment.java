package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that a class is provisory because it contains a provisory field.
 */
public final class HasProvisoryFieldProvisoryTypeImmutabilityAssessment extends ProvisoryTypeImmutabilityAssessment
{
	public final ProvisoryFieldImmutabilityAssessment fieldAssessment;

	public HasProvisoryFieldProvisoryTypeImmutabilityAssessment( Stringizer stringizer, Class<?> jvmClass, ProvisoryFieldImmutabilityAssessment fieldAssessment )
	{
		super( stringizer, jvmClass );
		this.fieldAssessment = fieldAssessment;
	}

	@Override public List<? extends ImmutabilityAssessment> children()
	{
		return List.of( fieldAssessment );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because field " ).append( Stringizer.stringizeFieldName( fieldAssessment.field ) ).append( " is provisory" );
	}
}
