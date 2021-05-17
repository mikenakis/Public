package mikenakis.bytecode.attributes;

import mikenakis.bytecode.attributes.code.AbsoluteInstructionReference;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

/**
 * Represents an entry of the {@link LineNumberTableAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LineNumber extends Printable
{
	public final AbsoluteInstructionReference startPc;
	public final int lineNumber;

	public LineNumber( AbsoluteInstructionReference startPc, int lineNumber )
	{
		this.startPc = startPc;
		this.lineNumber = lineNumber;
	}

	public LineNumber( CodeAttribute code, BufferReader bufferReader )
	{
		startPc = new AbsoluteInstructionReference( code, false, bufferReader );
		lineNumber = bufferReader.readUnsignedShort();
	}

	public void write( BufferWriter bufferWriter )
	{
		startPc.write( false, bufferWriter );
		bufferWriter.writeUnsignedShort( lineNumber );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "lineNumber = " ).append( lineNumber );
		builder.append( ", startPc = " ).append( startPc.getPc() );
	}
}
