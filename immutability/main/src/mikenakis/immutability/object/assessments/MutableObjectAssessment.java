package mikenakis.immutability.object.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

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
		stringBuilder.append( stringizer.stringize( object ) ).append( " " );
		stringBuilder.append( "is mutable" );
	}
}
