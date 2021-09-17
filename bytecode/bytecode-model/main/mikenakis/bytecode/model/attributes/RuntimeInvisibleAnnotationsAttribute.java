package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeInvisibleAnnotations" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuntimeInvisibleAnnotationsAttribute extends AnnotationsAttribute
{
	public static RuntimeInvisibleAnnotationsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static RuntimeInvisibleAnnotationsAttribute of( List<ByteCodeAnnotation> annotations )
	{
		return new RuntimeInvisibleAnnotationsAttribute( annotations );
	}

	public static final String name = "RuntimeInvisibleAnnotations";
	public static final Kind kind = new Kind( name );

	private RuntimeInvisibleAnnotationsAttribute( List<ByteCodeAnnotation> annotations )
	{
		super( kind, annotations );
	}

	@Deprecated @Override public RuntimeInvisibleAnnotationsAttribute asRuntimeInvisibleAnnotationsAttribute()
	{
		return this;
	}
}
