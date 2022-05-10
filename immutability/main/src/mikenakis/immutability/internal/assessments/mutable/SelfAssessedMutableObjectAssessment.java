package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.NonImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.SelfAssessableProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that a self-assessable object has assessed itself as mutable.
 */
public final class SelfAssessedMutableObjectAssessment extends MutableObjectAssessment
{
	public final ImmutabilitySelfAssessable object;
	public final SelfAssessableProvisoryTypeAssessment typeAssessment;

	public SelfAssessedMutableObjectAssessment( SelfAssessableProvisoryTypeAssessment typeAssessment, //
		ImmutabilitySelfAssessable object )
	{
		this.object = object;
		this.typeAssessment = typeAssessment;
	}

	@Override public Object object() { return object; }
	@Override public NonImmutableTypeAssessment typeAssessment() { return typeAssessment; }
	@Override public List<Assessment> children() { return List.of( typeAssessment ); }
}
