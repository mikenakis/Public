package mikenakis.bytecode.attributes.code;

import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.kit.BufferReader;

import java.util.Collection;

/**
 * Instruction Model.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class InstructionModel
{
	public final int opCode;

	protected InstructionModel( int opCode )
	{
		this.opCode = opCode;
	}

	public abstract Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps );

	@Override public final String toString()
	{
		return getName();
	}

	public String getName()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
