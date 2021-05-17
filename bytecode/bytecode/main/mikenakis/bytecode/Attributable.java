package mikenakis.bytecode;

import mikenakis.bytecode.attributes.RuntimeVisibleAnnotationsAttribute;

import java.util.Optional;

/**
 * Represents something that has attributes.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Attributable
{
	Attributes getAttributes();

	default Optional<Attribute> tryGetAttributeByName( String name )
	{
		Attributes attributes = getAttributes();
		return attributes.tryGetAttributeByName( name );
	}

	default Optional<RuntimeVisibleAnnotationsAttribute> tryGetRuntimeVisibleAnnotationsAttribute()
	{
		return tryGetAttributeByName( RuntimeVisibleAnnotationsAttribute.NAME ).map( attribute -> attribute.asRuntimeVisibleAnnotationsAttribute() );
	}
}
