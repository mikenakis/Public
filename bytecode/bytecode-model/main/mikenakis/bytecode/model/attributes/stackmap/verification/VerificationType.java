package mikenakis.bytecode.model.attributes.stackmap.verification;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;

/**
 * Verification Type.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class VerificationType
{
	public static String getTagNameFromTag( int tag )
	{
		return switch( tag )
			{
				case SimpleVerificationType.topTag -> SimpleVerificationType.topTagName;
				case SimpleVerificationType.integerTag -> SimpleVerificationType.integerTagName;
				case SimpleVerificationType.floatTag -> SimpleVerificationType.floatTagName;
				case SimpleVerificationType.doubleTag -> SimpleVerificationType.doubleTagName;
				case SimpleVerificationType.longTag -> SimpleVerificationType.longTagName;
				case SimpleVerificationType.nullTag -> SimpleVerificationType.nullTagName;
				case SimpleVerificationType.uninitializedThisTag -> SimpleVerificationType.uninitializedThisTagName;
				case ObjectVerificationType.tag -> ObjectVerificationType.tagName;
				case UninitializedVerificationType.tag -> UninitializedVerificationType.tagName;
				default -> throw new AssertionError( tag );
			};
	}

	public final int tag;

	protected VerificationType( int tag )
	{
		this.tag = tag;
	}

	//@formatter:off
	public Optional<ObjectVerificationType>        tryAsObjectVerificationType()        { return Optional.empty(); }
	public Optional<SimpleVerificationType>        tryAsSimpleVerificationType()        { return Optional.empty(); }
	public Optional<UninitializedVerificationType> tryAsUninitializedVerificationType() { return Optional.empty(); }

	public final ObjectVerificationType        asObjectVerificationType()        { return tryAsObjectVerificationType().orElseThrow(); }
	public final SimpleVerificationType        asSimpleVerificationType()        { return tryAsSimpleVerificationType().orElseThrow(); }
	public final UninitializedVerificationType asUninitializedVerificationType() { return tryAsUninitializedVerificationType().orElseThrow(); }

	public final boolean isObjectVerificationType()        { return tryAsObjectVerificationType().isPresent(); }
	public final boolean isSimpleVerificationType()        { return tryAsSimpleVerificationType().isPresent(); }
	public final boolean isUninitializedVerificationType() { return tryAsUninitializedVerificationType().isPresent(); }
	//@formatter:on

	@Override @OverridingMethodsMustInvokeSuper public String toString()
	{
		return "tag = " + getTagNameFromTag( tag );
	}
}
