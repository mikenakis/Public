package mikenakis.immutability.type.field.assessments.provisory;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Signifies that a field cannot be conclusively assessed as mutable or immutable because it is of a type assessed as provisory.
 */
public final class ProvisoryFieldTypeAssessment extends ProvisoryFieldAssessment
{
	public final ProvisoryTypeAssessment provisoryTypeAssessment;

	public ProvisoryFieldTypeAssessment( Stringizer stringizer, Field field, ProvisoryTypeAssessment provisoryTypeAssessment )
	{
		super( stringizer, field );
		this.provisoryTypeAssessment = provisoryTypeAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( provisoryTypeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of provisory type '" ).append( stringizer.stringize( field.getType() ) ).append( "'" );
	}
}
