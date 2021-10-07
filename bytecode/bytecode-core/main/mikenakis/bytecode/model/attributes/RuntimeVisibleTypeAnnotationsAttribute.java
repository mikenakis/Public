package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.TypeAnnotation;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.reading.ReadingLocationMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "RuntimeVisibleTypeAnnotations" {@link Attribute} of a java class file.
 * <p>
 * See https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.20
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeVisibleTypeAnnotationsAttribute extends TypeAnnotationsAttribute
{
	public static RuntimeVisibleTypeAnnotationsAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool, Optional<ReadingLocationMap> locationMap )
	{
		List<TypeAnnotation> entries = readTypeAnnotations( bufferReader, constantPool, locationMap );
		return of( entries );
	}

	public static RuntimeVisibleTypeAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeVisibleTypeAnnotationsAttribute of( List<TypeAnnotation> entries )
	{
		return new RuntimeVisibleTypeAnnotationsAttribute( entries );
	}

	private RuntimeVisibleTypeAnnotationsAttribute( List<TypeAnnotation> entries )
	{
		super( tag_RuntimeVisibleTypeAnnotations, entries );
	}

	@Deprecated @Override public RuntimeVisibleTypeAnnotationsAttribute asRuntimeVisibleTypeAnnotationsAttribute() { return this; }
}
