package mikenakis.immutability;

import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
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

	public final List<String> assessmentTextLines()
	{
		ArrayList<String> lines = new ArrayList<>();
		MyKit.tree( this, a -> a.children(), a -> a.toString(), s -> lines.add( s ) );
		return lines;
	}

	public final String assessmentTextInFull()
	{
		StringBuilder stringBuilder = new StringBuilder();
		assessmentTextLines().forEach( s -> stringBuilder.append( "    " ).append( s ).append( "\r\n" ) );
		return stringBuilder.toString();
	}
}
