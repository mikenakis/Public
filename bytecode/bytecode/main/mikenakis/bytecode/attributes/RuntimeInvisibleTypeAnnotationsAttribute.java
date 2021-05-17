package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ByteCodeField;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;

import java.util.Optional;

/**
 * Represents the "RuntimeInvisibleTypeAnnotations" {@link Attribute} of a java class file.
 *
 * See https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.19
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeInvisibleTypeAnnotationsAttribute extends TypeAnnotationsAttribute
{
	public static final String NAME = "RuntimeInvisibleTypeAnnotations";

	public static Optional<RuntimeInvisibleTypeAnnotationsAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asRuntimeInvisibleTypeAnnotationsAttribute() );
	}

	public RuntimeInvisibleTypeAnnotationsAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	public RuntimeInvisibleTypeAnnotationsAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, constantPool, NAME, bufferReader );
	}

	@Override public Optional<RuntimeInvisibleTypeAnnotationsAttribute> tryAsRuntimeInvisibleTypeAnnotationsAttribute()
	{
		return Optional.of( this );
	}
}
