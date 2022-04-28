package mikenakis.immutability.type.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.type.field.assessments.mutable.MutableFieldAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because it has mutable fields.
 */
public class MutableFieldsAssessment extends MutableTypeAssessment
{
	public final List<MutableFieldAssessment> mutableFieldAssessments;

	public MutableFieldsAssessment( Stringizer stringizer, Class<?> jvmClass, List<MutableFieldAssessment> mutableFieldAssessments )
	{
		super( stringizer, jvmClass );
		this.mutableFieldAssessments = mutableFieldAssessments;
	}

	@Override public Iterable<? extends Assessment> children() { return mutableFieldAssessments; }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it has mutable fields" );
	}
}
