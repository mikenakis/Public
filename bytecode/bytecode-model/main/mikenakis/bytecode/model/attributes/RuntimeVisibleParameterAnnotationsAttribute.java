package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeVisibleParameterAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeVisibleParameterAnnotationsAttribute extends ParameterAnnotationsAttribute
{
	public static RuntimeVisibleParameterAnnotationsAttribute of()
	{
		return new RuntimeVisibleParameterAnnotationsAttribute( new ArrayList<>() );
	}

	public static RuntimeVisibleParameterAnnotationsAttribute of( List<ParameterAnnotationSet> entries )
	{
		return new RuntimeVisibleParameterAnnotationsAttribute( entries );
	}

	public static final String name = "RuntimeVisibleParameterAnnotations";
	public static final Kind kind = new Kind( name );

	private RuntimeVisibleParameterAnnotationsAttribute( List<ParameterAnnotationSet> entries )
	{
		super( kind, entries );
	}

	@Deprecated @Override public RuntimeVisibleParameterAnnotationsAttribute asRuntimeVisibleParameterAnnotationsAttribute()
	{
		return this;
	}
}
