package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.util.List;

/**
 * Signifies that a class is provisory because it contains a provisory field.
 */
public final class HasProvisoryFieldProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final ProvisoryFieldAssessment fieldAssessment;

	public HasProvisoryFieldProvisoryTypeAssessment( Stringizer stringizer, Class<?> jvmClass, ProvisoryFieldAssessment fieldAssessment )
	{
		super( stringizer, jvmClass );
		this.fieldAssessment = fieldAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( fieldAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because field " ).append( Stringizer.stringizeFieldName( fieldAssessment.field ) ).append( " is provisory" );
	}
}
