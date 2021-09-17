package mikenakis.bytecode.writing;

import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;

/**
 * Special {@link InstructionWriter} used for building the {@link LocationMap}.
 *   - The getOffset() method always returns Short.MAX_VALUE + 1 to fool branch instructions into thinking that their target instruction is far,
 *     thus causing them to emit their longest form.
 *   - All write...() methods are fake, they discard the bytes being written.
 */
class InterimInstructionWriter implements InstructionWriter
{
	private final ConstantPool constantPool;
	private int location;

	InterimInstructionWriter( ConstantPool constantPool )
	{
		this.constantPool = constantPool;
	}

	@Override public void writeUnsignedByte( int value ) { location += 1; }
	@Override public void writeSignedByte( int value ) { location += 1; }
	@Override public void writeUnsignedShort( int value ) { location += 2; }
	@Override public void writeSignedShort( int value ) { location += 2; }
	@Override public void writeInt( int value ) { location += 4; }
	@Override public void skipToAlign() { location += Helpers.padding( location ); }

	@Override public int getIndex( Constant constant )
	{
		return constantPool.getIndex( constant );
	}

	@Override public int getOffset( Instruction sourceInstruction, Instruction targetInstruction )
	{
		return Short.MAX_VALUE + 1;
	}

	int getLocation()
	{
		return location;
	}
}
