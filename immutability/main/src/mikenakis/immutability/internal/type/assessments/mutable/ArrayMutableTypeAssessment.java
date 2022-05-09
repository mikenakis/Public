package mikenakis.immutability.internal.type.assessments.mutable;

/**
 * Signifies that a type is mutable because it is an array.
 */
public final class ArrayMutableTypeAssessment extends MutableTypeAssessment
{
	public ArrayMutableTypeAssessment( Class<?> jvmClass )
	{
		super( jvmClass );
		assert jvmClass.isArray();
	}
}
