package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "Source File" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SourceFileAttribute extends KnownAttribute
{
	public static SourceFileAttribute of( String sourceFile )
	{
		Mutf8Constant valueConstant = Mutf8Constant.of( sourceFile );
		return of( valueConstant );
	}

	public static SourceFileAttribute of( Mutf8Constant sourceFileConstant )
	{
		return new SourceFileAttribute( sourceFileConstant );
	}

	public final Mutf8Constant valueConstant;

	private SourceFileAttribute( Mutf8Constant valueConstant )
	{
		super( tag_SourceFile );
		this.valueConstant = valueConstant;
	}

	public String value() { return valueConstant.stringValue(); }

	@Deprecated @Override public SourceFileAttribute asSourceFileAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "value = " + valueConstant.toString();
	}
}
