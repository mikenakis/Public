package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that a class is provisory due to multiple reasons.
 */
public final class MultiReasonProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final List<ProvisoryTypeAssessment> provisoryReasons;

	public MultiReasonProvisoryTypeAssessment( Class<?> jvmClass, List<ProvisoryTypeAssessment> provisoryReasons )
	{
		super( jvmClass );
		assert Helpers.isClass( jvmClass ); //the type must be a class; there is a different assessment for interfaces.
		this.provisoryReasons = provisoryReasons;
	}

	@Override public List<? extends Assessment> children()
	{
		return provisoryReasons;
	}
}
