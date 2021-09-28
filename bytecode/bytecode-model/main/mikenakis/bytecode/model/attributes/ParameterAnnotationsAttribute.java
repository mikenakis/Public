package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;

/**
 * Common base class for the "RuntimeVisibleParameterAnnotations" and "RuntimeInvisibleParameterAnnotations" {@link Attribute}s of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ParameterAnnotationsAttribute extends KnownAttribute
{
	public final List<ParameterAnnotationSet> parameterAnnotationSets;

	ParameterAnnotationsAttribute( int tag, List<ParameterAnnotationSet> parameterAnnotationSets )
	{
		super( tag );
		this.parameterAnnotationSets = parameterAnnotationSets;
	}

	@Deprecated @Override public ParameterAnnotationsAttribute asParameterAnnotationsAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return mutf8Name.stringValue() + " " + parameterAnnotationSets.size() + " entries";
	}
}
