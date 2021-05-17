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
 * Represents the "RuntimeInvisibleAnnotations" {@link Attribute} of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeInvisibleAnnotationsAttribute extends AnnotationsAttribute
{
	public static final String NAME = "RuntimeInvisibleAnnotations";

	public static Optional<RuntimeInvisibleAnnotationsAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asRuntimeInvisibleAnnotationsAttribute() );
	}

	public RuntimeInvisibleAnnotationsAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	public RuntimeInvisibleAnnotationsAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, constantPool, NAME, bufferReader );
	}

	@Override public Optional<RuntimeInvisibleAnnotationsAttribute> tryAsRuntimeInvisibleAnnotationsAttribute()
	{
		return Optional.of( this );
	}
}
