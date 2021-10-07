package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.TypeAnnotation;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.reading.ReadingLocationMap;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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
 * @author Michael Belivanakis (michael.gr)
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
