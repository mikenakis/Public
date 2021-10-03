package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

public final class TableSwitchInstruction extends Instruction
{
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
}
