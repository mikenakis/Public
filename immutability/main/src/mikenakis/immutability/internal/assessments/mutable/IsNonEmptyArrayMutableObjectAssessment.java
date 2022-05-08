package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.mutable.IsArrayMutableTypeAssessment;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Signifies that an object is mutable because it is a non-empty array.
 */
public final class IsNonEmptyArrayMutableObjectAssessment extends MutableObjectAssessment
{
	public final IsArrayMutableTypeAssessment typeAssessment;

	public IsNonEmptyArrayMutableObjectAssessment( Stringizer stringizer, Object object, IsArrayMutableTypeAssessment typeAssessment )
	{
		super( stringizer, object );
		assert object.getClass().isArray();
		assert Array.getLength( object ) > 0;
		this.typeAssessment = typeAssessment;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is a non-empty array." );
	}
}
