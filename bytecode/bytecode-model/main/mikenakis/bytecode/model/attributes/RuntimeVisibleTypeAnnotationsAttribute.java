package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.TypeAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeVisibleTypeAnnotations" {@link Attribute} of a java class file.
 * <p>
 * See https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.20
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeVisibleTypeAnnotationsAttribute extends TypeAnnotationsAttribute
{
	public static RuntimeVisibleTypeAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeVisibleTypeAnnotationsAttribute of( List<TypeAnnotation> entries )
	{
		return new RuntimeVisibleTypeAnnotationsAttribute( entries );
	}

	private RuntimeVisibleTypeAnnotationsAttribute( List<TypeAnnotation> entries )
	{
		super( tag_RuntimeVisibleTypeAnnotations, entries );
	}

	@Deprecated @Override public RuntimeVisibleTypeAnnotationsAttribute asRuntimeVisibleTypeAnnotationsAttribute()
	{
		return this;
	}
}
