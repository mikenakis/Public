package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.TypeAnnotation;
import mikenakis.bytecode.model.constants.Mutf8Constant;
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
public abstract class TypeAnnotationsAttribute extends KnownAttribute
{
	public final List<TypeAnnotation> typeAnnotations;

	TypeAnnotationsAttribute( int tag, List<TypeAnnotation> typeAnnotations )
	{
		super( tag );
		this.typeAnnotations = typeAnnotations;
	}

	@Deprecated @Override public TypeAnnotationsAttribute asTypeAnnotationsAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return mutf8Name.stringValue() + ' ' + typeAnnotations.size() + " entries";
	}
}
