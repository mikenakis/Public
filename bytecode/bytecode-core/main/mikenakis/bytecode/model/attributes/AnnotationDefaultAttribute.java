package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
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
	public static AnnotationDefaultAttribute read( AttributeReader attributeReader )
	{
		AnnotationValue annotationValue = AnnotationValue.read( attributeReader );
		return of( annotationValue );
	}

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

	@Override public void intern( Interner interner )
	{
		annotationValue.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		annotationValue.write( constantWriter );
	}
}
