package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.reading.AttributeReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeInvisibleParameterAnnotations" {@link Attribute} of a java class file.
 * <p>
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeInvisibleParameterAnnotationsAttribute extends ParameterAnnotationsAttribute
{
	public static RuntimeInvisibleParameterAnnotationsAttribute read( AttributeReader attributeReader )
	{
		List<ParameterAnnotationSet> entries = readParameterAnnotationsAttributeEntries( attributeReader );
		return of( entries );
	}

	public static RuntimeInvisibleParameterAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeInvisibleParameterAnnotationsAttribute of( List<ParameterAnnotationSet> entries )
	{
		return new RuntimeInvisibleParameterAnnotationsAttribute( entries );
	}

	private RuntimeInvisibleParameterAnnotationsAttribute( List<ParameterAnnotationSet> entries ) { super( tag_RuntimeInvisibleParameterAnnotations, entries ); }
	@Override public boolean isOptional() { return true; }
	@Deprecated @Override public RuntimeInvisibleParameterAnnotationsAttribute asRuntimeInvisibleParameterAnnotationsAttribute() { return this; }
}