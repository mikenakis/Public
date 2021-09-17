package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.AbsoluteInstructionReference;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an entry of the {@link LineNumberTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LineNumber
{
	public static LineNumber of( AbsoluteInstructionReference instructionReference, int lineNumber )
	{
		return new LineNumber( instructionReference, lineNumber );
	}

	private final AbsoluteInstructionReference instructionReference;
	private final int lineNumber;

	private LineNumber( AbsoluteInstructionReference instructionReference, int lineNumber )
	{
		this.instructionReference = instructionReference;
		this.lineNumber = lineNumber;
	}

	public AbsoluteInstructionReference instructionReference() { return instructionReference; }
	public int lineNumber() { return lineNumber; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "lineNumber = " + lineNumber;
	}
}
