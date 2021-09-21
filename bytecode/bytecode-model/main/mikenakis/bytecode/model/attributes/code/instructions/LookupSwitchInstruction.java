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

	private Optional<Instruction> defaultInstruction;
	public final List<LookupSwitchEntry> entries;

	private LookupSwitchInstruction( List<LookupSwitchEntry> entries )
	{
		super( Group.LookupSwitch );
		this.entries = entries;
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

	@Deprecated @Override public LookupSwitchInstruction asLookupSwitchInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( OpCode.LOOKUPSWITCH );
	}
}
