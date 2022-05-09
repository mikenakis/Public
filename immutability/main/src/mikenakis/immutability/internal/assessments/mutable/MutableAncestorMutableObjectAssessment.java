package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because its ancestor is mutable.
 */
public final class MutableAncestorMutableObjectAssessment extends MutableObjectAssessment
{
	public final ProvisoryTypeAssessment typeAssessment;
	public final MutableObjectAssessment ancestorAssessment;

	public MutableAncestorMutableObjectAssessment( Stringizer stringizer, Object object, ProvisoryTypeAssessment typeAssessment, //
		MutableObjectAssessment ancestorAssessment )
	{
		super( stringizer, object );
		this.typeAssessment = typeAssessment;
		this.ancestorAssessment = ancestorAssessment;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " due to multiple reasons" );
	}

	@Override public List<? extends Assessment> children()
	{
		return List.of( typeAssessment, ancestorAssessment );
	}
}
