package mikenakis.bytecode.model.attributes.stackmap.verification;

import java.util.Optional;

/**
 * 'Simple' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SimpleVerificationType extends VerificationType
{
	public static final int topTag               = 0;
	public static final int integerTag           = 1;
	public static final int floatTag             = 2;
	public static final int doubleTag            = 3;
	public static final int longTag              = 4;
	public static final int nullTag              = 5;
	public static final int uninitializedThisTag = 6;

	public static final String topTagName               = "Top";
	public static final String integerTagName           = "Integer";
	public static final String floatTagName             = "Float";
	public static final String doubleTagName            = "Double";
	public static final String longTagName              = "Long";
	public static final String nullTagName              = "Null";
	public static final String uninitializedThisTagName = "UninitializedThis";

	public SimpleVerificationType( int tag )
	{
		super( tag );
		assert tag >= 0 && tag <= 6;
	}

	@Deprecated @Override public Optional<SimpleVerificationType> tryAsSimpleVerificationType() { return Optional.of( this ); }
}
