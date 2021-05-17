package mikenakis.bytecode.attributes.code;

import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Absolute instruction reference.  (From the beginning of the code block.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AbsoluteInstructionReference extends InstructionReference
{
	private final CodeAttribute codeAttribute;

	public AbsoluteInstructionReference( CodeAttribute codeAttribute, Instruction targetInstruction )
	{
		super( Optional.of( targetInstruction ) );
		this.codeAttribute = codeAttribute;
	}

	public AbsoluteInstructionReference( CodeAttribute codeAttribute, boolean wide, BufferReader bufferReader )
	{
		super( Optional.empty() );
		this.codeAttribute = codeAttribute;
		int pc = bufferReader.readUnsignedShortOrInt( wide );
		realizeTargetInstruction( codeAttribute, pc );
	}

	@Override public void write( boolean wide, BufferWriter bufferWriter )
	{
		assert targetInstruction.isPresent();
		int pc = targetInstruction.get() == END_INSTRUCTION ? codeAttribute.getEndPc() : targetInstruction.get().getPc();
		bufferWriter.writeUnsignedShortOrInt( wide, pc );
	}

	public int getPc()
	{
		assert targetInstruction.isPresent();
		if( targetInstruction.get() == END_INSTRUCTION )
			return codeAttribute.getEndPc();
		return targetInstruction.get().getPc();
	}
}
