package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.TypeAnnotation;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collections;
import java.util.List;

/**
 * Common base class for the "RuntimeVisibleTypeAnnotations" or "RuntimeInvisibleTypeAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType} {@link ByteCodeMethod} {@link ByteCodeField} {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class TypeAnnotationsAttribute extends Attribute
{
	private final List<TypeAnnotation> typeAnnotations;

	TypeAnnotationsAttribute( Kind kind, List<TypeAnnotation> typeAnnotations )
	{
		super( kind );
		this.typeAnnotations = typeAnnotations;
	}

	public List<TypeAnnotation> typeAnnotations()
	{
		return Collections.unmodifiableList( typeAnnotations );
	}

	@Deprecated @Override public TypeAnnotationsAttribute asTypeAnnotationsAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return kind.name + ' ' + typeAnnotations.size() + " entries";
	}
}
