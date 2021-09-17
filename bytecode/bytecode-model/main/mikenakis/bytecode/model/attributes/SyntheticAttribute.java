package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "Synthetic" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SyntheticAttribute extends Attribute
{
	public static SyntheticAttribute of()
	{
		return new SyntheticAttribute();
	}

	public static final String name = "Synthetic";
	public static final Kind kind = new Kind( name );

	private SyntheticAttribute()
	{
		super( kind );
	}

	@Deprecated @Override public SyntheticAttribute asSyntheticAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "synthetic";
	}
}
