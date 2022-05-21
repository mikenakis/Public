package io.github.mikenakis.bytecode.model.attributes.code.instructions;

import io.github.mikenakis.bytecode.model.attributes.code.Instruction;

public final class LookupSwitchEntry
{
	public static LookupSwitchEntry of( int value )
	{
		return new LookupSwitchEntry( value );
	}

	public final int value;
	private Instruction targetInstruction; //null means that it has not been set yet.

	private LookupSwitchEntry( int value )
	{
		this.value = value;
	}

	public Instruction getTargetInstruction()
	{
		assert targetInstruction != null;
		return targetInstruction;
	}

	public void setTargetInstruction( Instruction targetInstruction )
	{
		assert this.targetInstruction == null;
		assert targetInstruction != null;
		this.targetInstruction = targetInstruction;
	}

	@Override public String toString() { return String.valueOf( value ); }
}
