package io.github.mikenakis.bytecode.writing;

import io.github.mikenakis.bytecode.kit.Helpers;
import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.kit.Kit;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Special {@link InstructionWriter} used for building the {@link WritingLocationMap}.
 *   - The getOffset() method always returns Short.MAX_VALUE + 1 to fool branch instructions into thinking that their target instruction is far,
 *     thus causing them to emit their longest form.
 *   - All write...() methods are fake, they discard the bytes being written.
 */
public class FakeInstructionWriter implements InstructionWriter
{
	private final WritingConstantPool constantPool;
	private final WritingLocationMap writingLocationMap;
	public int location;
	public final Collection<Instruction> sourceInstructions = new LinkedHashSet<>();

	public FakeInstructionWriter( WritingConstantPool constantPool, WritingLocationMap writingLocationMap )
	{
		this.constantPool = constantPool;
		this.writingLocationMap = writingLocationMap;
	}

	@Override public void writeUnsignedByte( int value ) { location += 1; }
	@Override public void writeSignedByte( int value ) { location += 1; }
	@Override public void writeUnsignedShort( int value ) { location += 2; }
	@Override public void writeSignedShort( int value ) { location += 2; }
	@Override public void writeInt( int value ) { location += 4; }
	@Override public void skipToAlign() { location += Helpers.padding( location ); }

	@Override public int getIndex( Constant constant )
	{
		return constantPool.getConstantIndex( constant );
	}

	@Override public int getOffset( Instruction sourceInstruction, Instruction targetInstruction )
	{
		// Add the source instruction to the set of instructions.
		// add-or-replace is necessary because some instructions (e.g. LOOKUPSWITCH) will invoke this method many times.
		Kit.collection.addOrReplace( sourceInstructions, sourceInstruction );

		// If the target is already known, compute the offset and return it.
		if( writingLocationMap.contains( targetInstruction ) )
			return writingLocationMap.getOffset( sourceInstruction, targetInstruction );

		// Assume that the target will be so far away that a long index will be needed.
		return Short.MAX_VALUE + 1;
	}
}
