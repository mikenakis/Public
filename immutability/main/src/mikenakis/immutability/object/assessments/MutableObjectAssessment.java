package mikenakis.immutability.object.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that an object is mutable.
 */
public abstract class MutableObjectAssessment extends ObjectAssessment
{
	public final Object object;

	protected MutableObjectAssessment( Stringizer stringizer, Object object )
	{
		super( stringizer );
		this.object = object;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "object " );
		stringBuilder.append( stringizer.stringizeObjectIdentity( object ) ).append( " " );
		stringBuilder.append( "is mutable" );
	}
}
