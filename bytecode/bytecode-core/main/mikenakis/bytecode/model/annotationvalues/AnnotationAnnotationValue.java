package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an annotation {@link AnnotationValue}.
 *
 * @author michael.gr
 */
public final class AnnotationAnnotationValue extends AnnotationValue
{
	public static AnnotationAnnotationValue read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		Annotation annotation = Annotation.read( bufferReader, constantPool );
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

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		annotation.write( bufferWriter, constantPool );
	}
}
