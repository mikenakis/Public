package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.assessments.provisory.IsSelfAssessableProvisoryTypeImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that a self-assessable object has assessed itself as mutable.
 */
public final class SelfAssessedMutableObjectImmutabilityAssessment extends MutableObjectImmutabilityAssessment
{
	public final IsSelfAssessableProvisoryTypeImmutabilityAssessment typeAssessment;
	public final ImmutabilitySelfAssessable object;

	public SelfAssessedMutableObjectImmutabilityAssessment( Stringizer stringizer, IsSelfAssessableProvisoryTypeImmutabilityAssessment typeAssessment, //
		ImmutabilitySelfAssessable object )
	{
		super( stringizer, object );
		this.typeAssessment = typeAssessment;
		this.object = object;
	}

	@Override public Iterable<ImmutabilityAssessment> children() { return List.of( typeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it self-assessed itself as mutable" );
	}
}
