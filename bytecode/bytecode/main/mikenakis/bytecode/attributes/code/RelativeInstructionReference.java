package mikenakis.bytecode.attributes.code;

import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Helpers;

import java.util.Collection;
import java.util.Optional;

/**
 * Relative instruction reference.  (From a given 'source' instruction.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RelativeInstructionReference extends InstructionReference
{
	private final Instruction sourceInstruction;

	public RelativeInstructionReference( Instruction sourceInstruction )
	{
		super( Optional.empty() );
		assert sourceInstruction != null;
		this.sourceInstruction = sourceInstruction;
	}

	public RelativeInstructionReference( Instruction sourceInstruction, Instruction targetInstruction )
	{
		super( Optional.of( targetInstruction ) );
		assert sourceInstruction != null;
		this.sourceInstruction = sourceInstruction;
	}

	public RelativeInstructionReference( CodeAttribute codeAttribute, Instruction sourceInstruction, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
	{
		super( Optional.empty() );
		assert sourceInstruction.getPc() != -1;
		this.sourceInstruction = sourceInstruction;
		int offset = bufferReader.readSignedShortOrInt( wide );
		realizeTargetInstruction( codeAttribute, sourceInstruction.getPc() + offset, fixUps );
	}

	@Override public void write( boolean wide, BufferWriter bufferWriter )
	{
		int offset = getOffset();
		bufferWriter.writeSignedShortOrInt( wide, offset );
	}

	public int getLength( boolean wide )
	{
		return wide ? 4 : 2;
	}

	public int getOffset()
	{
		assert targetInstruction.isPresent();
		if( !targetInstruction.get().isPcEstablished() )
			return 0;
		if( !sourceInstruction.isPcEstablished() )
			return 0;
		return targetInstruction.get().getPc() - sourceInstruction.getPc();
	}

	public boolean isShort()
	{
		int offset = getOffset();
		return Helpers.isSignedShort( offset );
	}
}
