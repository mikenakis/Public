package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.Utf8Constant;
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
public final class SourceFileAttribute extends Attribute
{
	public static SourceFileAttribute of( String sourceFile )
	{
		Utf8Constant valueConstant = Utf8Constant.of( sourceFile );
		return of( valueConstant );
	}

	public static SourceFileAttribute of( Utf8Constant sourceFileConstant )
	{
		return new SourceFileAttribute( sourceFileConstant );
	}

	public static final String name = "SourceFile";
	public static final Kind kind = new Kind( name );

	private final Utf8Constant valueConstant;

	private SourceFileAttribute( Utf8Constant valueConstant )
	{
		super( kind );
		this.valueConstant = valueConstant;
	}

	public Utf8Constant valueConstant() { return valueConstant; }

	@Deprecated @Override public SourceFileAttribute asSourceFileAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "value = " + valueConstant.toString();
	}
}
