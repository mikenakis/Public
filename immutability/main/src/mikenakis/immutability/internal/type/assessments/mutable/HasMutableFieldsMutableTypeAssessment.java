package mikenakis.immutability.internal.type.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because it has mutable fields.
 */
public class HasMutableFieldsMutableTypeAssessment extends MutableTypeAssessment
{
	public final List<MutableFieldAssessment> mutableFieldAssessments;

	public HasMutableFieldsMutableTypeAssessment( Stringizer stringizer, Class<?> jvmClass, List<MutableFieldAssessment> mutableFieldAssessments )
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
