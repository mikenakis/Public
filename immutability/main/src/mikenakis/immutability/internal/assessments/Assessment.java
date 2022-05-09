package mikenakis.immutability.internal.assessments;

import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

/**
 * Common base class for all assessments. Exists just so that we can create trees of assessments.
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
		stringBuilder.append( ". (" ).append( getClass().getSimpleName() ).append( ")" );
		return stringBuilder.toString();
	}
}
