package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.Decomposer;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;

/**
 * Signifies that a class is provisory because it is composite, with components whose immutability will need to be assessed.
 *
 * This is used for classes that are not iterable but contain elements, i.e. implementations of {@link java.util.Map}, which is conceptually a collection
 * of entries, but does not extend {@link java.util.Collection<java.util.Map.Entry>} and instead offers an {@link java.util.Map#entrySet()} method.
 *
 * If the mode is {@link TypeAssessment.Mode#Assessed} then the class has been assessed to be immutable in all other respects.
 *
 * If the mode is {@link TypeAssessment.Mode#Preassessed} then this assessment overrules an assessment that would normally come out as mutable, and promises that this class
 * will behave as immutable, with the caveat that it is iterable.
 */
public final class CompositeProvisoryTypeAssessment<T,E> extends ProvisoryTypeAssessment
{
	public final Mode mode;
	public final Decomposer<T,E> decomposer;

	public CompositeProvisoryTypeAssessment( Stringizer stringizer, Mode mode, Class<T> type, Decomposer<T,E> decomposer )
	{
		super( stringizer, type );
		this.mode = mode;
		this.decomposer = decomposer;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it " ).append( mode.text ).append( " composite" );
	}
}
