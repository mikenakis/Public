package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "AnnotationDefault" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationDefaultAttribute extends KnownAttribute
{
	public static AnnotationDefaultAttribute of( AnnotationValue annotationValue )
	{
		return new AnnotationDefaultAttribute( annotationValue );
	}

	public final AnnotationValue annotationValue;

	private AnnotationDefaultAttribute( AnnotationValue annotationValue )
	{
		super( tag_AnnotationDefault );
		this.annotationValue = annotationValue;
	}

	@Deprecated @Override public AnnotationDefaultAttribute asAnnotationDefaultAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "value = " + annotationValue; }
}
