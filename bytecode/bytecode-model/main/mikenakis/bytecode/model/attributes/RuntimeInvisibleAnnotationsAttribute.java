package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "RuntimeInvisibleAnnotations" {@link Attribute} of a java class file.
 * <p>
 * See JVMS 4.7.16 "The RuntimeVisibleAnnotations attribute", https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16
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
