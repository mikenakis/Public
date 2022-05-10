package mikenakis.immutability.internal.type.assessments.nonimmutable.provisory;

import mikenakis.immutability.ImmutabilitySelfAssessable;

/**
 * Signifies that a type is provisory because it is self-assessable. (Instances implement the {@link ImmutabilitySelfAssessable} interface.)
 */
public final class SelfAssessableProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public SelfAssessableProvisoryTypeAssessment( Class<?> type )
	{
		super( type );
		assert ImmutabilitySelfAssessable.class.isAssignableFrom( type );
	}
}
