package mikenakis.immutability;

import mikenakis.immutability.helpers.Stringizable;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

/**
 * Base class for all assessments.
 */
public abstract class Assessment extends Stringizable
{
	protected Assessment( Stringizer stringizer )
	{
		super( stringizer );
	}

	public Iterable<? extends Assessment> children() { return List.of(); }

	protected abstract void appendToStringBuilder( StringBuilder stringBuilder );

	@ExcludeFromJacocoGeneratedReport @Override public final String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		appendToStringBuilder( stringBuilder );
		stringBuilder.append( " (" ).append( getClass().getSimpleName() ).append( ")" );
		return stringBuilder.toString();
	}
}
