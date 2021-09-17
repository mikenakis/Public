package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "Deprecated" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class DeprecatedAttribute extends Attribute
{
	public static DeprecatedAttribute of()
	{
		return new DeprecatedAttribute();
	}

	public static final String name = "Deprecated";
	public static final Kind kind = new Kind( name );

	private DeprecatedAttribute()
	{
		super( kind );
	}

	@Deprecated @Override public DeprecatedAttribute asDeprecatedAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return kind.name;
	}
}
