package mikenakis.immutability.object.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Signifies that an object is mutable.
 */
public abstract class MutableObjectImmutabilityAssessment extends ObjectImmutabilityAssessment
{
	public final Object object;

	protected MutableObjectImmutabilityAssessment( Stringizer stringizer, Object object )
	{
		super( stringizer );
		this.object = object;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "object " ).append( stringizer.stringizeObjectIdentity( object ) ).append( " is mutable" );
	}
}
