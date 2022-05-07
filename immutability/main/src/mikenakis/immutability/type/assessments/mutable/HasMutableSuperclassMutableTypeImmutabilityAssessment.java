package mikenakis.immutability.type.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.MutableTypeImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because its superclass is mutable.
 */
public class HasMutableSuperclassMutableTypeImmutabilityAssessment extends MutableTypeImmutabilityAssessment
{
	public final MutableTypeImmutabilityAssessment superclassAssessment;

	public HasMutableSuperclassMutableTypeImmutabilityAssessment( Stringizer stringizer, Class<?> jvmClass, MutableTypeImmutabilityAssessment superclassAssessment )
	{
		super( stringizer, jvmClass );
		this.superclassAssessment = superclassAssessment;
	}

	@Override public Iterable<ImmutabilityAssessment> children() { return List.of( superclassAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it extends mutable class " ).append( stringizer.stringizeClassName( superclassAssessment.type ) );
	}
}
