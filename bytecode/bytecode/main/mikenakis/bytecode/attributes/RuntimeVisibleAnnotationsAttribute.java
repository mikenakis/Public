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
 * Represents the "RuntimeVisibleAnnotations" {@link Attribute} of a java class file.
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 *
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeVisibleAnnotationsAttribute extends AnnotationsAttribute
{
	public static final String NAME = "RuntimeVisibleAnnotations";

	public static Optional<RuntimeVisibleAnnotationsAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asRuntimeVisibleAnnotationsAttribute() );
	}

	public static RuntimeVisibleAnnotationsAttribute from( Attributes attributes, Runnable observer )
	{
		Optional<RuntimeVisibleAnnotationsAttribute> existingAttribute = tryFrom( attributes );
		if( existingAttribute.isPresent() )
			return existingAttribute.get();
		RuntimeVisibleAnnotationsAttribute attribute = new RuntimeVisibleAnnotationsAttribute( observer );
		attributes.addAttribute( attribute );
		return attribute;
	}

	public RuntimeVisibleAnnotationsAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	public RuntimeVisibleAnnotationsAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, constantPool, NAME, bufferReader );
	}

	@Override public Optional<RuntimeVisibleAnnotationsAttribute> tryAsRuntimeVisibleAnnotationsAttribute()
	{
		return Optional.of( this );
	}
}
