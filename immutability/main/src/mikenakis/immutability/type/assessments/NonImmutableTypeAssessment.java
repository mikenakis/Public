package mikenakis.immutability.type.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Common base class for all non-immutable type assessments.
 */
public abstract class NonImmutableTypeAssessment extends TypeAssessment
{
	public final Class<?> type;

	protected NonImmutableTypeAssessment( Stringizer stringizer, Class<?> type )
	{
		super( stringizer );
		this.type = type;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "type " ).append( stringizer.stringizeClassName( type ) );
	}
}
