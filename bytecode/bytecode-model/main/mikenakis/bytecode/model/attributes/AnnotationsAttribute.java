package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.List;

/**
 * Common base class for the "RuntimeVisibleAnnotations" or "RuntimeInvisibleAnnotations" {@link Attribute}s of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class AnnotationsAttribute extends Attribute
{
	private final List<Annotation> annotations;

	AnnotationsAttribute( Kind kind, List<Annotation> annotations )
	{
		super( kind );
		this.annotations = annotations;
	}

	@Deprecated @Override public AnnotationsAttribute asAnnotationsAttribute()
	{
		return this;
	}

	public Collection<Annotation> annotations()
	{
		return annotations;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return annotations.size() + " entries";
	}
}
