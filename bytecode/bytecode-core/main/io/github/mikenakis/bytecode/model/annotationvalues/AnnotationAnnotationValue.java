package io.github.mikenakis.bytecode.model.annotationvalues;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.Annotation;
import io.github.mikenakis.bytecode.model.AnnotationValue;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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
