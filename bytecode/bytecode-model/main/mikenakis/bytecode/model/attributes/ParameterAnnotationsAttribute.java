package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collections;
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
public abstract class ParameterAnnotationsAttribute extends Attribute
{
	private final List<ParameterAnnotationSet> parameterAnnotationSets;

	ParameterAnnotationsAttribute( Kind kind, List<ParameterAnnotationSet> parameterAnnotationSets )
	{
		super( kind );
		this.parameterAnnotationSets = parameterAnnotationSets;
	}

	public final List<ParameterAnnotationSet> parameterAnnotationSets()
	{
		return Collections.unmodifiableList( parameterAnnotationSets );
	}

	@Deprecated @Override public ParameterAnnotationsAttribute asParameterAnnotationsAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return kind.name + " " + parameterAnnotationSets.size() + " entries";
	}
}
