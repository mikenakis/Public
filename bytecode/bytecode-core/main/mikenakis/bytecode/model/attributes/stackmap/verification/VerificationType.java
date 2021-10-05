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
	public static final int tag_Top               /**/ = 0;
	public static final int tag_Integer           /**/ = 1;
	public static final int tag_Float             /**/ = 2;
	public static final int tag_Double            /**/ = 3;
	public static final int tag_Long              /**/ = 4;
	public static final int tag_Null              /**/ = 5;
	public static final int tag_UninitializedThis /**/ = 6;
	public static final int tag_Object            /**/ = 7;
	public static final int tag_Uninitialized     /**/ = 8;

	public static String tagName( int verificationTypeTag )
	{
		return switch( verificationTypeTag )
		{
			case tag_Top               /**/ -> "Top";
			case tag_Integer           /**/ -> "Integer";
			case tag_Float             /**/ -> "Float";
			case tag_Double            /**/ -> "Double";
			case tag_Long              /**/ -> "Long";
			case tag_Null              /**/ -> "Null";
			case tag_UninitializedThis /**/ -> "UninitializedThis";
			case tag_Object            /**/ -> "Object";
			case tag_Uninitialized     /**/ -> "Uninitialized";
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
	@ExcludeFromJacocoGeneratedReport @Override @OverridingMethodsMustInvokeSuper public String toString() { return "tag = " + tagName( tag ); }
}
