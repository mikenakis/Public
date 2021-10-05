package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an annotation {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AnnotationAnnotationValue extends AnnotationValue
{
	public static AnnotationAnnotationValue read( AttributeReader attributeReader )
	{
		Annotation annotation = Annotation.read( attributeReader );
		return of( annotation );
	}

	public static final String NAME = "annotation";

	public static AnnotationAnnotationValue of( Annotation annotation )
	{
		return new AnnotationAnnotationValue( annotation );
	}

	public final Annotation annotation;

	private AnnotationAnnotationValue( Annotation annotation )
	{
		super( tagAnnotation );
		this.annotation = annotation;
	}

	@Deprecated @Override public AnnotationAnnotationValue asAnnotationAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "annotation = { " + annotation + " }"; }

	@Override public void intern( Interner interner )
	{
		annotation.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		annotation.write( constantWriter );
	}
}
