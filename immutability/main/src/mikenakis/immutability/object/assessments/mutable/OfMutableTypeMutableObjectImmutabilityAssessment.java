package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.assessments.MutableTypeImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because its class is mutable.
 */
public final class OfMutableTypeMutableObjectImmutabilityAssessment extends MutableObjectImmutabilityAssessment
{
	public final MutableTypeImmutabilityAssessment typeAssessment;

	public OfMutableTypeMutableObjectImmutabilityAssessment( Stringizer stringizer, Object object, MutableTypeImmutabilityAssessment typeAssessment )
	{
		super( stringizer, object );
		assert object.getClass() == typeAssessment.type;
		this.typeAssessment = typeAssessment;
	}

	@Override public Iterable<ImmutabilityAssessment> children() { return List.of( typeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of a mutable type" );
	}
}
