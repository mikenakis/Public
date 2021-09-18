package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeVisibleAnnotations" {@link Attribute} of a java class file.
 * <p>
 * See JVMS 4.7.17 "The RuntimeInvisibleAnnotations attribute", https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.17
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
