package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.model.ByteCodeField;
import io.github.mikenakis.bytecode.model.TypeAnnotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "RuntimeInvisibleTypeAnnotations" {@link Attribute} of a java class file.
 * <p>
 * See <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.19">JVMS-4.7.19</a>
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 * {@link CodeAttribute}
 *
 * @author michael.gr
 */
public final class RuntimeInvisibleTypeAnnotationsAttribute extends TypeAnnotationsAttribute
{
	public static RuntimeInvisibleTypeAnnotationsAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool, Optional<ReadingLocationMap> locationMap )
	{
		List<TypeAnnotation> entries = readTypeAnnotations( bufferReader, constantPool, locationMap );
		return of( entries );
	}

	public static RuntimeInvisibleTypeAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeInvisibleTypeAnnotationsAttribute of( List<TypeAnnotation> entries )
	{
		return new RuntimeInvisibleTypeAnnotationsAttribute( entries );
	}

	private RuntimeInvisibleTypeAnnotationsAttribute( List<TypeAnnotation> entries ) { super( tag_RuntimeInvisibleTypeAnnotations, entries ); }
	@Override public boolean isOptional() { return true; }
	@Deprecated @Override public RuntimeInvisibleTypeAnnotationsAttribute asRuntimeInvisibleTypeAnnotationsAttribute() { return this; }
}
