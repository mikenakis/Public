package mikenakis.bytecode.model.attributes.code;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Absolute instruction reference.  (From the beginning of the code block.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
// TODO: get rid of!
public final class AbsoluteInstructionReference
{
	public static AbsoluteInstructionReference of( Optional<Instruction> targetInstruction )
	{
		return new AbsoluteInstructionReference( targetInstruction );
	}

	private final Optional<Instruction> targetInstruction; //Optional.empty() if pointing at the end of the last instruction.

	private AbsoluteInstructionReference( Optional<Instruction> targetInstruction )
	{
		this.targetInstruction = targetInstruction;
	}

//	public void write( boolean wide, BufferWriter bufferWriter )
//	{
//		bufferWriter.writeUnsignedShortOrInt( wide, getPc() );
//	}

//	public int getPc()
//	{
//		return targetInstruction.map( Instruction::getPc ).orElseGet( codeAttribute::getEndPc );
//	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof AbsoluteInstructionReference )
			return equalsAbsoluteInstructionReference( (AbsoluteInstructionReference)other );
		assert false;
		return false;
	}

	public boolean equalsAbsoluteInstructionReference( AbsoluteInstructionReference other )
	{
		return targetInstruction == other.targetInstruction;
	}

	@Override public int hashCode()
	{
		assert false; //should never be called.
		return 0;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "-> " + targetInstruction;
	}

	public Optional<Instruction> targetInstruction()
	{
		return targetInstruction;
	}
}
