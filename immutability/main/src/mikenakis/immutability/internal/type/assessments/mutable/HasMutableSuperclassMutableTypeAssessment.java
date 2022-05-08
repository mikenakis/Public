package mikenakis.immutability.internal.type.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because its superclass is mutable.
 */
public class HasMutableSuperclassMutableTypeAssessment extends MutableTypeAssessment
{
	public final MutableTypeAssessment superclassAssessment;

	public HasMutableSuperclassMutableTypeAssessment( Stringizer stringizer, Class<?> jvmClass, MutableTypeAssessment superclassAssessment )
	{
		super( stringizer, jvmClass );
		this.superclassAssessment = superclassAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( superclassAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it extends mutable class " ).append( stringizer.stringizeClassName( superclassAssessment.type ) );
	}
}
