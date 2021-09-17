package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeVisibleAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeVisibleAnnotationsAttribute extends AnnotationsAttribute
{
	public static RuntimeVisibleAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeVisibleAnnotationsAttribute of( List<ByteCodeAnnotation> annotations )
	{
		return new RuntimeVisibleAnnotationsAttribute( annotations );
	}

	public static final String name = "RuntimeVisibleAnnotations";
	public static final Kind kind = new Kind( name );

	private RuntimeVisibleAnnotationsAttribute( List<ByteCodeAnnotation> annotations )
	{
		super( kind, annotations );
	}

	@Deprecated @Override public RuntimeVisibleAnnotationsAttribute asRuntimeVisibleAnnotationsAttribute()
	{
		return this;
	}
}
