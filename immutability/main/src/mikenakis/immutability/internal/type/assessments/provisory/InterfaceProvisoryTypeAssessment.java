package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;

/**
 * Signifies that a type is provisory because it is an interface.
 */
public final class InterfaceProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public InterfaceProvisoryTypeAssessment( Class<?> type )
	{
		super( type );
		assert type.isInterface();
	}
}
