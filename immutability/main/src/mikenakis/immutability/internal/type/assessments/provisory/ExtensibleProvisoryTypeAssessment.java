package mikenakis.immutability.internal.type.assessments.provisory;

import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;

/**
 * Signifies that a class is immutable in all regards except that it is extensible.
 * <p>
 * This means that a field with a field-type of this class may receive a value which is of a more derived class, which may be mutable, so the immutability of
 * the value of the field has to be assessed.
 * <p>
 * If the mode is {@link Mode#Assessed} then the class has been assessed as extensible.
 * <p>
 * If the mode is {@link Mode#Preassessed} then the class would have normally been assessed as mutable, but this preassessment promises that the class will
 * behave as immutable, with the caveat that it is extensible.
 */
public final class ExtensibleProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final Mode mode;

	public ExtensibleProvisoryTypeAssessment( Mode mode, Class<?> jvmClass )
	{
		super( jvmClass );
		assert Helpers.isClass( jvmClass );
		assert Helpers.isExtensible( jvmClass );
		this.mode = mode;
	}
}
