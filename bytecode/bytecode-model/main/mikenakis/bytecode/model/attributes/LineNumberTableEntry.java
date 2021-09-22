package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an entry of the {@link LineNumberTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LineNumberTableEntry
{
	public static LineNumberTableEntry of( Instruction instruction, int lineNumber )
	{
		return new LineNumberTableEntry( instruction, lineNumber );
	}

	private final Instruction instruction;
	private final int lineNumber;

	private LineNumberTableEntry( Instruction instruction, int lineNumber )
	{
		this.instruction = instruction;
		this.lineNumber = lineNumber;
	}

	public Instruction instruction() { return instruction; }
	public int lineNumber() { return lineNumber; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "lineNumber = " + lineNumber;
	}
}
