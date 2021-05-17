package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;

import java.util.Optional;

/**
 * Represents the "RuntimeVisibleParameterAnnotations" {@link Attribute} of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeVisibleParameterAnnotationsAttribute extends ParameterAnnotationsAttribute
{
	public static final String NAME = "RuntimeVisibleParameterAnnotations";

	public RuntimeVisibleParameterAnnotationsAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	public RuntimeVisibleParameterAnnotationsAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, constantPool, NAME, bufferReader );
	}

	@Override public Optional<RuntimeVisibleParameterAnnotationsAttribute> tryAsRuntimeVisibleParameterAnnotationsAttribute()
	{
		return Optional.of( this );
	}
}
