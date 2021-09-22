package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Annotation;
import mikenakis.bytecode.model.Attribute;

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

	public static RuntimeVisibleAnnotationsAttribute of( List<Annotation> annotations )
	{
		return new RuntimeVisibleAnnotationsAttribute( annotations );
	}

	private RuntimeVisibleAnnotationsAttribute( List<Annotation> annotations )
	{
		super( tagRuntimeVisibleAnnotations, annotations );
	}

	@Deprecated @Override public RuntimeVisibleAnnotationsAttribute asRuntimeVisibleAnnotationsAttribute()
	{
		return this;
	}
}
