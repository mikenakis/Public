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
 * Represents the "RuntimeVisibleTypeAnnotations" {@link Attribute} of a java class file.
 *
 * See https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.20
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeVisibleTypeAnnotationsAttribute extends TypeAnnotationsAttribute
{
	public static final String NAME = "RuntimeVisibleTypeAnnotations";

	public static Optional<RuntimeVisibleTypeAnnotationsAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asRuntimeVisibleTypeAnnotationsAttribute() );
	}

	public RuntimeVisibleTypeAnnotationsAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	public RuntimeVisibleTypeAnnotationsAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, constantPool, NAME, bufferReader );
	}

	@Override public Optional<RuntimeVisibleTypeAnnotationsAttribute> tryAsRuntimeVisibleTypeAnnotationsAttribute()
	{
		return Optional.of( this );
	}
}
