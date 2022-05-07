package mikenakis.immutability.type.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Common base class for all non-immutable type assessments.
 */
public abstract class NonImmutableTypeImmutabilityAssessment extends TypeImmutabilityAssessment
{
	public final Class<?> type;

	protected NonImmutableTypeImmutabilityAssessment( Stringizer stringizer, Class<?> type )
	{
		super( stringizer );
		this.type = type;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "type " ).append( stringizer.stringizeClassName( type ) );
	}
}
