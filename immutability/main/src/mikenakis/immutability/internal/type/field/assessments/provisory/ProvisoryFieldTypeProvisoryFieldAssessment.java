package mikenakis.immutability.internal.type.field.assessments.provisory;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field is provisory because it is of a field type which is provisory.
 */
public final class ProvisoryFieldTypeProvisoryFieldAssessment extends ProvisoryFieldAssessment
{
	public final ProvisoryTypeAssessment provisoryTypeAssessment;

	public ProvisoryFieldTypeProvisoryFieldAssessment( Stringizer stringizer, Field field, ProvisoryTypeAssessment provisoryTypeAssessment )
	{
		super( stringizer, field );
		this.provisoryTypeAssessment = provisoryTypeAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( provisoryTypeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of provisory type " ).append( stringizer.stringizeClassName( field.getType() ) );
	}
}
