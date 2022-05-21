package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.ByteCodeField;
import io.github.mikenakis.bytecode.model.TypeAnnotation;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Common base class for the "RuntimeVisibleTypeAnnotations" or "RuntimeInvisibleTypeAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType} {@link ByteCodeMethod} {@link ByteCodeField} {@link CodeAttribute}
 *
 * @author michael.gr
 */
public abstract class TypeAnnotationsAttribute extends KnownAttribute
{
	protected static List<TypeAnnotation> readTypeAnnotations( BufferReader bufferReader, ReadingConstantPool constantPool, Optional<ReadingLocationMap> locationMap )
	{
		int typeAnnotationCount = bufferReader.readUnsignedShort();
		assert typeAnnotationCount > 0;
		List<TypeAnnotation> typeAnnotations = new ArrayList<>( typeAnnotationCount );
		for( int i = 0; i < typeAnnotationCount; i++ )
			typeAnnotations.add( TypeAnnotation.read( bufferReader, constantPool, locationMap ) );
		return typeAnnotations;
	}

	public final List<TypeAnnotation> typeAnnotations;

	TypeAnnotationsAttribute( int tag, List<TypeAnnotation> typeAnnotations )
	{
		super( tag );
		this.typeAnnotations = typeAnnotations;
	}

	@Deprecated @Override public TypeAnnotationsAttribute asTypeAnnotationsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return name() + ' ' + typeAnnotations.size() + " entries"; }

	@Override public final void intern( Interner interner )
	{
		for( TypeAnnotation typeAnnotation : typeAnnotations )
			typeAnnotation.intern( interner );
	}

	@Override public final void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		TypeAnnotation.writeTypeAnnotations( bufferWriter, constantPool, locationMap, typeAnnotations );
	}
}
