package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * 'Same Locals, 1 Stack Item' {@link StackMapFrame}.
 * <p>
 * the frame has exactly the same locals as the previous stack map frame and the number of stack items is 1.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SameLocals1StackItemStackMapFrame extends StackMapFrame
{
	public static SameLocals1StackItemStackMapFrame of( Instruction targetInstruction, VerificationType stackVerificationType )
	{
		return new SameLocals1StackItemStackMapFrame( targetInstruction, stackVerificationType );
	}

	public static final int EXTENDED_FRAME_TYPE = 247;
	public static final String typeName = "SameLocals1StackItemFrame";
	public static final String extendedTypeName = "SameLocals1StackItemExtendedFrame";

	public final VerificationType stackVerificationType;

	private SameLocals1StackItemStackMapFrame( Instruction targetInstruction, VerificationType stackVerificationType )
	{
		super( typeName, targetInstruction );
		this.stackVerificationType = stackVerificationType;
	}

	@Deprecated @Override public Optional<SameLocals1StackItemStackMapFrame> tryAsSameLocals1StackItemStackMapFrame() { return Optional.of( this ); }
	@Override public String getName( Optional<StackMapFrame> previousFrame )
	{
		return typeName;
//		int offsetDelta = getOffsetDelta( previousFrame );
//		if( offsetDelta <= 127 )
//			return typeName;
//		return extendedTypeName;
	}
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "stackVerificationType = " + stackVerificationType; }
}
