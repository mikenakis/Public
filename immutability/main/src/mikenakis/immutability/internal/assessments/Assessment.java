package mikenakis.immutability.internal.assessments;

import java.util.List;

/**
 * Common base class for all assessments. Exists just so that we can create trees of assessments.
 */
public abstract class Assessment
{
	protected Assessment()
	{
	}

	public Iterable<? extends Assessment> children() { return List.of(); }
}
