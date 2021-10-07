package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

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
	public static AnnotationDefaultAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		AnnotationValue annotationValue = AnnotationValue.read( bufferReader, constantPool );
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

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		annotationValue.write( bufferWriter, constantPool );
	}
}
