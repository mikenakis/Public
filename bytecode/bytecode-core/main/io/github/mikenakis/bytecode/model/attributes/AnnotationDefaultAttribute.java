package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.AnnotationValue;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "AnnotationDefault" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author michael.gr
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
