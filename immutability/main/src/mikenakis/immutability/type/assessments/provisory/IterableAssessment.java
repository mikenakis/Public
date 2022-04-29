package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;

/**
 * Signifies that a class is provisory because it is an iterable with elements whose immutability will need to be assessed.
 *
 * This is used for collection classes that behave immutably but are provided by external libraries, so we cannot annotate their internal arrays
 * as {@link InvariableArray}.
 *
 * If the mode is {@link Mode#Assessed} then class has been assessed to be otherwise immutable.
 *
 * If the mode is {@link Mode#Preassessed} then this assessment overrules an assessment that would normally come out as mutable, and promises that this class
 * will behave as immutable, with the caveat that it is iterable.
 */
public final class IterableAssessment extends ProvisoryTypeAssessment
{
	public final Mode mode;

	public IterableAssessment( Stringizer stringizer, Mode mode, Class<? extends Iterable<?>> type )
	{
		super( stringizer, type );
		assert Iterable.class.isAssignableFrom( type );
		this.mode = mode;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is " ).append( mode.text ).append( " as iterable" );
	}
}
