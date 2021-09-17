package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * 'Same' {@link StackMapFrame}.
 * <p>
 * the frame has exactly the same locals as the previous stack map frame and the number of stack items is zero.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SameStackMapFrame extends StackMapFrame
{
	public static SameStackMapFrame of( Instruction targetInstruction )
	{
		return new SameStackMapFrame( targetInstruction );
	}

	public static final int EXTENDED_FRAME_TYPE = 251;
	public static final String typeName = "SameFrame";
	public static final String extendedTypeName = "SameExtendedFrame";

	private SameStackMapFrame( Instruction targetInstruction )
	{
		super( typeName, targetInstruction );
	}

	@Override public String getName( Optional<StackMapFrame> previousFrame )
	{
		return typeName;
//		int offsetDelta = getOffsetDelta( previousFrame );
//		if( offsetDelta >= 0 && offsetDelta <= 63 )
//			return typeName;
//		return extendedTypeName;
	}

	@Deprecated @Override public Optional<SameStackMapFrame> tryAsSameStackMapFrame() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "targetInstruction = " + getTargetInstruction();
	}
}
