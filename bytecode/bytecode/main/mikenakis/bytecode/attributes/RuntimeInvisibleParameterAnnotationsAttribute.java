package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;

import java.util.Optional;

/**
 * Represents the "RuntimeInvisibleParameterAnnotations" {@link Attribute} of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeInvisibleParameterAnnotationsAttribute extends ParameterAnnotationsAttribute
{
	public static final String NAME = "RuntimeInvisibleParameterAnnotations";

	public RuntimeInvisibleParameterAnnotationsAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	public RuntimeInvisibleParameterAnnotationsAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, constantPool, NAME, bufferReader );
	}

	@Override public Optional<RuntimeInvisibleParameterAnnotationsAttribute> tryAsRuntimeInvisibleParameterAnnotationsAttribute()
	{
		return Optional.of( this );
	}
}
