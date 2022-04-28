package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.helpers.Helpers;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;

/**
 * Signifies that a class is immutable in all regards except that it is extensible.
 *
 * This means that a field with a field-type of this class may receive a value which is of a more derived class, which may be mutable, so the immutability of
 * the value of the field has to be assessed.
 *
 * If the mode is {@link Mode#Assessed} then the class has been assessed as extensible.
 *
 * If the mode is {@link Mode#Preassessed} then the class would have normally been assessed as mutable, but this preassessment promises that the class
 * will behave as immutable, with the caveat that it is extensible.
 */
public final class ExtensibleAssessment extends ProvisoryTypeAssessment
{
	public final Mode mode;

	public ExtensibleAssessment( Stringizer stringizer, Mode mode, Class<?> jvmClass )
	{
		super( stringizer, jvmClass );
		assert Helpers.isClass( jvmClass );
		assert Helpers.isExtensible( jvmClass );
		this.mode = mode;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because is  " ).append( mode.text ).append( " as an extensible class" );
	}
}
