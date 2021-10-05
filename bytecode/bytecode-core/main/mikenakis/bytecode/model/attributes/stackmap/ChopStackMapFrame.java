package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * 'Chop' {@link StackMapFrame}.
 * <p>
 * the operand stack is empty and the current locals are the same as the locals in the previous frame, except that the k last locals are absent.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ChopStackMapFrame extends StackMapFrame
{
	public static ChopStackMapFrame of( Instruction targetInstruction, int count )
	{
		return new ChopStackMapFrame( targetInstruction, count );
	}

	public static final String typeName = "ChopFrame";

	private final int count;

	private ChopStackMapFrame( Instruction targetInstruction, int count )
	{
		super( tag_Chop, targetInstruction );
		assert count >= 0 && count <= 3;
		this.count = count;
	}

	public int count()
	{
		return count;
	}

	@Override public String getName( Optional<StackMapFrame> previousFrame ) { return typeName; }
	@Deprecated @Override public ChopStackMapFrame asChopStackMapFrame() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "count = " + count; }
}
