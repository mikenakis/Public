package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that a class has a provisory ancestor.
 */
public final class HasProvisoryAncestorProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final ProvisoryTypeAssessment ancestorAssessment;

	public HasProvisoryAncestorProvisoryTypeAssessment( Stringizer stringizer, Class<?> jvmClass, ProvisoryTypeAssessment ancestorAssessment )
	{
		super( stringizer, jvmClass );
		this.ancestorAssessment = ancestorAssessment;
	}

	@Override public List<? extends Assessment> children() { return List.of( ancestorAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it extends provisory type " ).append( stringizer.stringizeClassName( ancestorAssessment.type ) );
	}
}
