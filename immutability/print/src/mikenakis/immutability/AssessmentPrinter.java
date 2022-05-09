package mikenakis.immutability;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates human-readable text for assessments.
 * <p>
 * TODO: move functionality from 'main' here.
 *
 * @author michael.gr
 */
public final class AssessmentPrinter
{
	public static final AssessmentPrinter instance = new AssessmentPrinter( Stringizer.defaultInstance );

	private final Stringizer stringizer;

	/**
	 * This constructor is public only for use by the tests. For regular usage, invoke the static {@link #instance}.
	 *
	 * @param stringizer
	 */
	public AssessmentPrinter( Stringizer stringizer )
	{
		this.stringizer = stringizer;
	}

	public List<String> getAssessmentTextTree( Assessment assessment )
	{
		List<String> lines = new ArrayList<>();
		TextTree.tree( assessment, this::getAssessmentChildren, this::getAssessmentText, s -> lines.add( s ) );
		return lines;
	}

	public Iterable<? extends Assessment> getAssessmentChildren( Assessment assessment )
	{
		return assessment.children(); //TODO
	}

	public String getAssessmentText( Assessment assessment )
	{
		return assessment.toString(); //TODO
	}
}
