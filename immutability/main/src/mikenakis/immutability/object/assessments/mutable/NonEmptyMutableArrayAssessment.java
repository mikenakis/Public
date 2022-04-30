package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.type.assessments.mutable.MutableArrayAssessment;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Signifies that an object is mutable because it is a non-empty array.
 */
public final class NonEmptyMutableArrayAssessment extends MutableObjectAssessment
{
	public final  MutableArrayAssessment typeAssessment;

	public NonEmptyMutableArrayAssessment( Stringizer stringizer, Object object, MutableArrayAssessment typeAssessment )
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
