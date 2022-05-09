package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.SelfAssessableProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that a self-assessable object has assessed itself as mutable.
 */
public final class SelfAssessedMutableObjectAssessment extends MutableObjectAssessment
{
	public final SelfAssessableProvisoryTypeAssessment typeAssessment;
	public final ImmutabilitySelfAssessable object;

	public SelfAssessedMutableObjectAssessment( SelfAssessableProvisoryTypeAssessment typeAssessment, //
		ImmutabilitySelfAssessable object )
	{
		super( object );
		this.typeAssessment = typeAssessment;
		this.object = object;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment ); }
}
