package mikenakis.immutability.internal.type.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

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
