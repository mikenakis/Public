package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TableSwitchInstruction extends Instruction
{
	public static TableSwitchInstruction of( int entryCount, int lowValue )
	{
		return new TableSwitchInstruction( entryCount, lowValue );
	}

	private Optional<Instruction> defaultInstruction;
	public final int lowValue;
	private final List<Instruction> targetInstructions;

	private TableSwitchInstruction( int entryCount, int lowValue )
	{
		super( Group.TableSwitch );
		targetInstructions = new ArrayList<>( entryCount );
		this.lowValue = lowValue;
		for( int i = 0; i < entryCount; i++ )
			targetInstructions.add( null );
	}

	public Instruction getDefaultInstruction()
	{
		assert defaultInstruction.isPresent();
		return defaultInstruction.get();
	}

	public void setDefaultInstruction( Instruction defaultInstruction )
	{
		this.defaultInstruction = Optional.of( defaultInstruction );
	}

	public int getTargetInstructionCount()
	{
		return targetInstructions.size();
	}

	public Instruction getTargetInstruction( int index )
	{
		Instruction targetInstruction = targetInstructions.get( index );
		assert targetInstruction != null;
		return targetInstruction;
	}

	public List<Instruction> targetInstructions()
	{
		return targetInstructions;
	}

	@Override public int getOpCode()
	{
		return OpCode.TABLESWITCH;
	}

	@Deprecated @Override public TableSwitchInstruction asTableSwitchInstruction()
	{
		return this;
	}
}
