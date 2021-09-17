package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeInvisibleParameterAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
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

	public static final String name = "RuntimeInvisibleParameterAnnotations";
	public static final Kind kind = new Kind( name );

	private RuntimeInvisibleParameterAnnotationsAttribute( List<ParameterAnnotationSet> entries )
	{
		super( kind, entries );
	}

	@Deprecated @Override public RuntimeInvisibleParameterAnnotationsAttribute asRuntimeInvisibleParameterAnnotationsAttribute()
	{
		return this;
	}
}
