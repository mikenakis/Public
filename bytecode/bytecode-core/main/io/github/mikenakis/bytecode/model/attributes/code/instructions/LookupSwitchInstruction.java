package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.InstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.Helpers;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.OpCode;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

public final class LookupSwitchInstruction extends Instruction
{
	public static LookupSwitchInstruction read( BufferReader bufferReader, ReadingLocationMap locationMap, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.LOOKUPSWITCH;
		bufferReader.skip( Helpers.padding( bufferReader.getPosition() ) );
		int defaultInstructionOffset = bufferReader.readInt();
		int count = bufferReader.readInt();
		//assert count > 0; it can actually be zero, if a 'typeSwitch' (and possibly other types of switch) only has a default statement!
		LookupSwitchInstruction instruction = of( count );
		locationMap.setRelativeTargetInstruction( instruction, defaultInstructionOffset, instruction::setDefaultInstruction );
		for( int index = 0; index < count; index++ )
		{
			int value = bufferReader.readInt();
			int entryInstructionOffset = bufferReader.readInt();
			LookupSwitchEntry lookupSwitchEntry = LookupSwitchEntry.of( value );
			locationMap.setRelativeTargetInstruction( instruction, entryInstructionOffset, lookupSwitchEntry::setTargetInstruction );
			instruction.entries.add( lookupSwitchEntry );
		}
		return instruction;
	}

	public static LookupSwitchInstruction of()
	{
		return of( 0 );
	}

	public static LookupSwitchInstruction of( int count )
	{
		return new LookupSwitchInstruction( new ArrayList<>( count ) );
	}

	private Instruction defaultInstruction; // null means that it has not been set yet.
	public final List<LookupSwitchEntry> entries;

	private LookupSwitchInstruction( List<LookupSwitchEntry> entries )
	{
		super( groupTag_LookupSwitch );
		this.entries = entries;
	}

	public Instruction getDefaultInstruction()
	{
		assert defaultInstruction != null;
		return defaultInstruction;
	}

	public void setDefaultInstruction( Instruction defaultInstruction )
	{
		assert this.defaultInstruction == null;
		assert defaultInstruction != null;
		this.defaultInstruction = defaultInstruction;
	}

	@Deprecated @Override public LookupSwitchInstruction asLookupSwitchInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.LOOKUPSWITCH ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		int defaultInstructionOffset = instructionWriter.getOffset( this, getDefaultInstruction() );
		instructionWriter.writeUnsignedByte( OpCode.LOOKUPSWITCH );
		instructionWriter.skipToAlign();
		instructionWriter.writeInt( defaultInstructionOffset );
		instructionWriter.writeInt( entries.size() );
		for( LookupSwitchEntry lookupSwitchEntry : entries )
		{
			instructionWriter.writeInt( lookupSwitchEntry.value );
			int entryInstructionOffset = instructionWriter.getOffset( this, lookupSwitchEntry.getTargetInstruction() );
			instructionWriter.writeInt( entryInstructionOffset );
		}
	}
}
