package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an entry of the {@link LineNumberTableAttribute}.
 *
 * @author michael.gr
 */
public final class LineNumberTableEntry
{
	public static LineNumberTableEntry read( BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		Instruction startInstruction = locationMap.getInstruction( bufferReader.readUnsignedShort() ).orElseThrow();
		int lineNumber = bufferReader.readUnsignedShort();
		return of( startInstruction, lineNumber );
	}

	public static LineNumberTableEntry of( Instruction instruction, int lineNumber )
	{
		return new LineNumberTableEntry( instruction, lineNumber );
	}

	public final Instruction instruction;
	public final int lineNumber;

	private LineNumberTableEntry( Instruction instruction, int lineNumber )
	{
		this.instruction = instruction;
		this.lineNumber = lineNumber;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "lineNumber = " + lineNumber; }

	public void write( BufferWriter bufferWriter, WritingLocationMap locationMap )
	{
		bufferWriter.writeUnsignedShort( locationMap.getLocation( instruction ) );
		bufferWriter.writeUnsignedShort( lineNumber );
	}
}
