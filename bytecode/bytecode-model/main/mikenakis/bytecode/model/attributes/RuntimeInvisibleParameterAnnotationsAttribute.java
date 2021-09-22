package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeInvisibleParameterAnnotations" {@link Attribute} of a java class file.
 * <p>
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeInvisibleParameterAnnotationsAttribute extends ParameterAnnotationsAttribute
{
	public static RuntimeInvisibleParameterAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeInvisibleParameterAnnotationsAttribute of( List<ParameterAnnotationSet> entries )
	{
		return new RuntimeInvisibleParameterAnnotationsAttribute( entries );
	}

	private RuntimeInvisibleParameterAnnotationsAttribute( List<ParameterAnnotationSet> entries )
	{
		super( tagRuntimeInvisibleParameterAnnotations, entries );
	}

	@Deprecated @Override public RuntimeInvisibleParameterAnnotationsAttribute asRuntimeInvisibleParameterAnnotationsAttribute()
	{
		return this;
	}
}
