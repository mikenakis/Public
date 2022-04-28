package mikenakis.immutability.type.assessments.mutable;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;

/**
 * Signifies that a type is mutable because it is an array.
 */
public final class ArrayAssessment extends MutableTypeAssessment
{
	public ArrayAssessment( Stringizer stringizer, Class<?> jvmClass )
	{
		super( stringizer, jvmClass );
		assert jvmClass.isArray();
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is an array class" );
	}
}
