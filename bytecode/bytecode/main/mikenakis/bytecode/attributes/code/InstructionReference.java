package mikenakis.bytecode.attributes.code;

import mikenakis.bytecode.ByteCodeHelpers;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;
import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.Optional;

/**
 * Instruction reference.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class InstructionReference extends Printable
{
	public static final Instruction END_INSTRUCTION = Instructions.newNop();
	public static final String END_LABEL = "@end";

	protected Optional<Instruction> targetInstruction;

	protected InstructionReference( Optional<Instruction> targetInstruction )
	{
		this.targetInstruction = targetInstruction;
	}

	@Override public final boolean equals( Object other )
	{
		if( other instanceof InstructionReference )
			return equalsInstructionReference( (InstructionReference)other );
		assert false;
		return false;
	}

	public final boolean equalsInstructionReference( InstructionReference other )
	{
		return targetInstruction == other.targetInstruction;
	}

	@Override public final int hashCode()
	{
		assert false; //should never be called.
		return 0;
	}

	@Override public final void toStringBuilder( StringBuilder builder )
	{
		if( targetInstruction.isEmpty() )
		{
			builder.append( "unknown" );
		}
		else if( targetInstruction.get() == END_INSTRUCTION )
		{
			builder.append( END_LABEL );
		}
		else
		{
			String pcId = ByteCodeHelpers.getPcId( targetInstruction.get().getPc() );
			builder.append( pcId );
		}
	}

	public abstract void write( boolean wide, BufferWriter bufferWriter );

	protected final void realizeTargetInstruction( CodeAttribute codeAttribute, int pc, Collection<Runnable> fixUps )
	{
		if( tryRealizeTargetInstruction( codeAttribute, pc ) )
			return;
		Kit.collection.add( fixUps, () -> realizeTargetInstruction( codeAttribute, pc ) );
	}

	protected final boolean tryRealizeTargetInstruction( CodeAttribute codeAttribute, int pc )
	{
		int instructionIndex = codeAttribute.getInstructionIndex( pc );
		if( instructionIndex < 0 )
			return false;
		targetInstruction = Optional.of( codeAttribute.instructions.get( instructionIndex ) );
		return true;
	}

	protected final void realizeTargetInstruction( CodeAttribute codeAttribute, int pc )
	{
		boolean ok = tryRealizeTargetInstruction( codeAttribute, pc );
		assert ok;
	}

	public final Instruction getTargetInstruction()
	{
		return targetInstruction.orElseThrow();
	}
}
