package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the "InnerClasses" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InnerClassesAttribute extends Attribute
{
	public static InnerClassesAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static InnerClassesAttribute of( List<InnerClass> innerClasses )
	{
		return new InnerClassesAttribute( innerClasses );
	}

	public static final String name = "InnerClasses";
	public static final Kind kind = new Kind( name );

	private final List<InnerClass> innerClasses;

	private InnerClassesAttribute( List<InnerClass> innerClasses )
	{
		super( kind );
		this.innerClasses = innerClasses;
	}

	public List<InnerClass> innerClasses()
	{
		return Collections.unmodifiableList( innerClasses );
	}

	@Deprecated @Override public InnerClassesAttribute asInnerClassesAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return innerClasses.size() + " entries";
	}
}
