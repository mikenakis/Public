package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.assessments.mutable.IsArrayMutableTypeImmutabilityAssessment;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Signifies that an object is mutable because it is a non-empty array.
 */
public final class IsNonEmptyArrayMutableObjectImmutabilityAssessment extends MutableObjectImmutabilityAssessment
{
	public final IsArrayMutableTypeImmutabilityAssessment typeAssessment;

	public IsNonEmptyArrayMutableObjectImmutabilityAssessment( Stringizer stringizer, Object object, IsArrayMutableTypeImmutabilityAssessment typeAssessment )
	{
		super( stringizer, object );
		assert object.getClass().isArray();
		assert Array.getLength( object ) > 0;
		this.typeAssessment = typeAssessment;
	}

	@Override public Iterable<ImmutabilityAssessment> children() { return List.of( typeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is a non-empty array." );
	}
}
