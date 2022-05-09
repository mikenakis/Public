package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.provisory.SelfAssessableProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that a self-assessable object has assessed itself as mutable.
 */
public final class SelfAssessedMutableObjectAssessment extends MutableObjectAssessment
{
	public final SelfAssessableProvisoryTypeAssessment typeAssessment;
	public final ImmutabilitySelfAssessable object;

	public SelfAssessedMutableObjectAssessment( Stringizer stringizer, SelfAssessableProvisoryTypeAssessment typeAssessment, //
		ImmutabilitySelfAssessable object )
	{
		super( stringizer, object );
		this.typeAssessment = typeAssessment;
		this.object = object;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it self-assessed itself as mutable" );
	}
}
