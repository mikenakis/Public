package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Common base class for the "RuntimeVisibleAnnotations" or "RuntimeInvisibleAnnotations" {@link Attribute}s of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author michael.gr
 */
public abstract class AnnotationsAttribute extends KnownAttribute
{
	protected static List<Annotation> readAnnotations( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<Annotation> annotations = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Annotation byteCodeAnnotation = Annotation.read( bufferReader, constantPool );
			annotations.add( byteCodeAnnotation );
		}
		return annotations;
	}

	public final List<Annotation> annotations;

	AnnotationsAttribute( int tag, List<Annotation> annotations )
	{
		super( tag );
		this.annotations = annotations;
	}

	@Deprecated @Override public AnnotationsAttribute asAnnotationsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return annotations.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( Annotation annotation : annotations )
			annotation.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( annotations.size() );
		for( Annotation annotation : annotations )
			annotation.write( bufferWriter, constantPool );
	}
}
