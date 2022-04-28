package mikenakis.immutability.type.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.TypeImmutabilityAssessor;

/**
 * Signifies that a class is immutable.
 *
 * Contrary to provisory and mutable assessments, which must contain extra information to explain why the assessment was issued,
 * immutability assessments do not need to contain any extra information, because immutability is usually the goal, so a programmer
 * is unlikely to want to troubleshoot why a class was assessed as immutable. Thus, there will be only one instance of this class,
 * available as {@link TypeImmutabilityAssessor#immutableClassAssessmentInstance}.
 */
public final class ImmutableTypeAssessment extends TypeAssessment
{
	public ImmutableTypeAssessment( Stringizer stringizer )
	{
		super( stringizer );
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "immutable" );
	}
}
