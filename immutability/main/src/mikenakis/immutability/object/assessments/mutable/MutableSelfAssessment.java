package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.assessments.provisory.SelfAssessableAssessment;

import java.util.List;

/**
 * Signifies that a self-assessable object has assessed itself as mutable.
 */
public final class MutableSelfAssessment extends MutableObjectAssessment
{
	public final SelfAssessableAssessment typeAssessment;
	public final ImmutabilitySelfAssessable object;

	public MutableSelfAssessment( Stringizer stringizer, SelfAssessableAssessment typeAssessment, //
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
