package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

public final class TableSwitchInstruction extends Instruction
{
	public static TableSwitchInstruction read( CodeAttributeReader codeAttributeReader, boolean wide )
	{
		assert !wide;
		codeAttributeReader.skipToAlign();
		int defaultInstructionOffset = codeAttributeReader.readInt();
		int lowValue = codeAttributeReader.readInt();
		int highValue = codeAttributeReader.readInt();
		int entryCount = highValue - lowValue + 1;
		TableSwitchInstruction tableSwitchInstruction = of( entryCount, lowValue );
		codeAttributeReader.setRelativeTargetInstruction( tableSwitchInstruction, defaultInstructionOffset, tableSwitchInstruction::setDefaultInstruction );
		for( int index = 0; index < entryCount; index++ )
		{
			int targetInstructionOffset = codeAttributeReader.readInt();
			codeAttributeReader.setRelativeTargetInstruction( tableSwitchInstruction, targetInstructionOffset, //
				targetInstruction -> tableSwitchInstruction.targetInstructions.add( targetInstruction ) );
		}
		return tableSwitchInstruction;
	}

	public static TableSwitchInstruction of( int entryCount, int lowValue )
	{
		return new TableSwitchInstruction( entryCount, lowValue );
	}

	private Instruction defaultInstruction; // null means it has not yet been initialized.
	public final int lowValue;
	public final List<Instruction> targetInstructions;

	private TableSwitchInstruction( int entryCount, int lowValue )
	{
		super( groupTag_TableSwitch );
		targetInstructions = new ArrayList<>( entryCount );
		this.lowValue = lowValue;
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

	@Deprecated @Override public TableSwitchInstruction asTableSwitchInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return OpCode.getOpCodeName( OpCode.TABLESWITCH ); }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		instructionWriter.writeUnsignedByte( OpCode.TABLESWITCH );
		instructionWriter.skipToAlign();
		int defaultInstructionOffset = instructionWriter.getOffset( this, getDefaultInstruction() );
		instructionWriter.writeInt( defaultInstructionOffset );
		instructionWriter.writeInt( lowValue );
		instructionWriter.writeInt( lowValue + targetInstructions.size() - 1 );
		for( Instruction targetInstruction : targetInstructions )
		{
			int targetInstructionOffset = instructionWriter.getOffset( this, targetInstruction );
			instructionWriter.writeInt( targetInstructionOffset );
		}
	}
}
