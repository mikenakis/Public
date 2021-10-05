package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LookupSwitchInstruction extends Instruction
{
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
}
