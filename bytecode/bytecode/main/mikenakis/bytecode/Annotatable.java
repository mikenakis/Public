package mikenakis.bytecode;

import mikenakis.bytecode.attributes.RuntimeVisibleAnnotationsAttribute;

import java.util.Optional;

/**
 * Represents something that has annotations.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Annotatable extends Attributable
{
	default Optional<ByteCodeAnnotation> tryGetAnnotationByName( String annotationTypeName )
	{
//		return RuntimeVisibleAnnotationsAttribute.tryFrom( getAttributes() ).flatMap( attribute -> attribute.tryGetAnnotationByName( annotationTypeName ) );
		Optional<RuntimeVisibleAnnotationsAttribute> runtimeVisibleAnnotationsAttribute = RuntimeVisibleAnnotationsAttribute.tryFrom( getAttributes() );
		if( runtimeVisibleAnnotationsAttribute.isEmpty() )
			return Optional.empty();
		return runtimeVisibleAnnotationsAttribute.get().tryGetAnnotationByName( annotationTypeName );
	}

	default ByteCodeAnnotation getAnnotationByName( String annotationTypeName )
	{
		return tryGetAnnotationByName( annotationTypeName ).orElseThrow();
	}

	default boolean hasAnnotation( String annotationTypeName )
	{
		return tryGetAnnotationByName( annotationTypeName ).isPresent();
	}

	default ByteCodeAnnotation addAnnotation( ByteCodeAnnotation.Factory annotationFactory )
	{
		RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute = RuntimeVisibleAnnotationsAttribute.from( getAttributes(), this::markAsDirty );
		return runtimeVisibleAnnotationsAttribute.addAnnotation( annotationFactory );
	}

	void markAsDirty();
}
