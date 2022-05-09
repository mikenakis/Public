package mikenakis.immutability.internal.type.assessments;

import mikenakis.immutability.internal.assessments.Assessment;

public abstract class TypeAssessment extends Assessment
{
	public enum Mode
	{
		Assessed,
		Preassessed,
		PreassessedByDefault
	}

	protected TypeAssessment()
	{
	}
}
