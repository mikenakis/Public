package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;

import java.util.Optional;

public final class LookupSwitchEntry
{
	public static LookupSwitchEntry of( int value )
	{
		return new LookupSwitchEntry( value );
	}

	private final int value;
	private Optional<Instruction> targetInstruction = Optional.empty(); //empty means that it has not been set yet.

	private LookupSwitchEntry( int value )
	{
		this.value = value;
	}

	public int value() { return value; }

	public Instruction getTargetInstruction()
	{
		assert targetInstruction.isPresent();
		return targetInstruction.get();
	}

	public void setTargetInstruction( Instruction targetInstruction )
	{
		this.targetInstruction = Optional.of( targetInstruction );
	}

	//	@SuppressWarnings( "EmptyMethod" ) void intern()
	//	{
	//		/* nothing to do */
	//	}

	//	public void write( BufferWriter bufferWriter )
	//	{
	//		bufferWriter.writeInt( value );
	//		instructionReference.write( true, bufferWriter );
	//	}

	@Override public String toString()
	{
		return String.valueOf( value );
	}
}
