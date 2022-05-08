package mikenakis.immutability.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because its class is mutable.
 */
public final class OfMutableTypeMutableObjectAssessment extends MutableObjectAssessment
{
	public final MutableTypeAssessment typeAssessment;

	public OfMutableTypeMutableObjectAssessment( Stringizer stringizer, Object object, MutableTypeAssessment typeAssessment )
	{
		super( stringizer, object );
		assert object.getClass() == typeAssessment.type;
		this.typeAssessment = typeAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of a mutable type" );
	}
}
