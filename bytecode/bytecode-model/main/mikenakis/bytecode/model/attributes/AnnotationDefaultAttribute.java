package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.ElementValue;
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
public final class AnnotationDefaultAttribute extends Attribute
{
	public static AnnotationDefaultAttribute of( ElementValue annotationValue )
	{
		return new AnnotationDefaultAttribute( annotationValue );
	}

	public static final String name = "AnnotationDefault";
	public static final Kind kind = new Kind( name );

	private final ElementValue annotationValue;

	private AnnotationDefaultAttribute( ElementValue annotationValue )
	{
		super( kind );
		this.annotationValue = annotationValue;
	}

	public ElementValue annotationValue()
	{
		return annotationValue;
	}

	@Deprecated @Override public AnnotationDefaultAttribute asAnnotationDefaultAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "value = " + annotationValue;
	}
}
