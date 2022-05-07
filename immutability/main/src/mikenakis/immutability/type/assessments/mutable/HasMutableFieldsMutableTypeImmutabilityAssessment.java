package mikenakis.immutability.type.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.MutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.mutable.MutableFieldImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because it has mutable fields.
 */
public class HasMutableFieldsMutableTypeImmutabilityAssessment extends MutableTypeImmutabilityAssessment
{
	public final List<MutableFieldImmutabilityAssessment> mutableFieldAssessments;

	public HasMutableFieldsMutableTypeImmutabilityAssessment( Stringizer stringizer, Class<?> jvmClass, List<MutableFieldImmutabilityAssessment> mutableFieldAssessments )
	{
		super( stringizer, jvmClass );
		this.mutableFieldAssessments = mutableFieldAssessments;
	}

	@Override public Iterable<? extends ImmutabilityAssessment> children() { return mutableFieldAssessments; }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it has mutable fields" );
	}
}
