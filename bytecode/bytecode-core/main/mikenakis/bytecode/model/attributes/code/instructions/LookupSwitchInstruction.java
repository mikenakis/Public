package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

public final class LookupSwitchInstruction extends Instruction
{
	public static LookupSwitchInstruction read( CodeAttributeReader codeAttributeReader, boolean wide, int opCode )
	{
		assert !wide;
		assert opCode == OpCode.LOOKUPSWITCH;
		codeAttributeReader.skipToAlign();
		int defaultInstructionOffset = codeAttributeReader.readInt();
		int count = codeAttributeReader.readInt();
		assert count > 0;
		LookupSwitchInstruction instruction = of( count );
		codeAttributeReader.setRelativeTargetInstruction( instruction, defaultInstructionOffset, instruction::setDefaultInstruction );
		for( int index = 0; index < count; index++ )
		{
			int value = codeAttributeReader.readInt();
			int entryInstructionOffset = codeAttributeReader.readInt();
			LookupSwitchEntry lookupSwitchEntry = LookupSwitchEntry.of( value );
			codeAttributeReader.setRelativeTargetInstruction( instruction, entryInstructionOffset, lookupSwitchEntry::setTargetInstruction );
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