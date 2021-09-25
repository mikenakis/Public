package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.exceptions.InvalidVerificationTypeTagException;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Base class for verification types.  See JVMS §4.10.1.2. "Verification Type System"
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class VerificationType
{
	public static final int tagTop               /**/ = 0;
	public static final int tagInteger           /**/ = 1;
	public static final int tagFloat             /**/ = 2;
	public static final int tagDouble            /**/ = 3;
	public static final int tagLong              /**/ = 4;
	public static final int tagNull              /**/ = 5;
	public static final int tagUninitializedThis /**/ = 6;
	public static final int tagObject            /**/ = 7;
	public static final int tagUninitialized     /**/ = 8;

	public static String tagName( int verificationTypeTag )
	{
		return switch( verificationTypeTag )
		{
			case tagTop               /**/ -> "Top";
			case tagInteger           /**/ -> "Integer";
			case tagFloat             /**/ -> "Float";
			case tagDouble            /**/ -> "Double";
			case tagLong              /**/ -> "Long";
			case tagNull              /**/ -> "Null";
			case tagUninitializedThis /**/ -> "UninitializedThis";
			case tagObject            /**/ -> "Object";
			case tagUninitialized     /**/ -> "Uninitialized";
			default -> throw new InvalidVerificationTypeTagException( verificationTypeTag );
		};
	}

	public final int tag;

	protected VerificationType( int tag )
	{
		this.tag = tag;
	}

	@ExcludeFromJacocoGeneratedReport public ObjectVerificationType asObjectVerificationType() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SimpleVerificationType asSimpleVerificationType() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public UninitializedVerificationType asUninitializedVerificationType() { return Kit.fail(); }

	@ExcludeFromJacocoGeneratedReport @Override @OverridingMethodsMustInvokeSuper public String toString()
	{
		return "tag = " + tagName( tag );
	}
}
