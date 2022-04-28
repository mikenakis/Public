package mikenakis.immutability.type.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;

import java.util.List;

/**
 * Signifies that a class is mutable because its superclass is mutable.
 */
public class MutableSuperclassAssessment extends MutableTypeAssessment
{
	public final MutableTypeAssessment superclassAssessment;

	public MutableSuperclassAssessment( Stringizer stringizer, Class<?> jvmClass, MutableTypeAssessment superclassAssessment )
	{
		super( stringizer, jvmClass );
		this.superclassAssessment = superclassAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( superclassAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it extends mutable class '" ).append( stringizer.stringize( superclassAssessment.type ) ).append( "'" );
	}
}
