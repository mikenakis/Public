package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that a class has a provisory ancestor.
 */
public final class HasProvisoryAncestorProvisoryTypeImmutabilityAssessment extends ProvisoryTypeImmutabilityAssessment
{
	public final ProvisoryTypeImmutabilityAssessment ancestorAssessment;

	public HasProvisoryAncestorProvisoryTypeImmutabilityAssessment( Stringizer stringizer, Class<?> jvmClass, ProvisoryTypeImmutabilityAssessment ancestorAssessment )
	{
		super( stringizer, jvmClass );
		this.ancestorAssessment = ancestorAssessment;
	}

	@Override public List<? extends ImmutabilityAssessment> children()
	{
		return List.of( ancestorAssessment );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it extends provisory type " ).append( stringizer.stringizeClassName( ancestorAssessment.type ) );
	}
}
