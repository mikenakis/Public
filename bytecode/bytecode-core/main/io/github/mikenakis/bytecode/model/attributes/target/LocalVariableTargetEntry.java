package io.github.mikenakis.bytecode.model.attributes.target;

import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class LocalVariableTargetEntry
{
	public static LocalVariableTargetEntry read( BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		int startLocation = bufferReader.readUnsignedShort();
		int length = bufferReader.readUnsignedShort();
		int index = bufferReader.readUnsignedShort();
		Instruction startInstruction = locationMap.getInstruction( startLocation ).orElseThrow();
		Optional<Instruction> endInstruction = locationMap.getInstruction( startLocation + length );
		return new LocalVariableTargetEntry( startInstruction, endInstruction, index );
	}

	public final Instruction startInstruction;
	public final Optional<Instruction> endInstruction;
	public final int index;

	private LocalVariableTargetEntry( Instruction startInstruction, Optional<Instruction> endInstruction, int index )
	{
		this.startInstruction = startInstruction;
		this.endInstruction = endInstruction;
		this.index = index;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "startInstruction = " + startInstruction + ", endInstruction = " + endInstruction + ", index = " + index; }

	@SuppressWarnings( "MethodMayBeStatic" ) public void intern( Interner interner )
	{
		Kit.get( interner ); // nothing to do
	}

	public void write( BufferWriter bufferWriter, WritingLocationMap locationMap )
	{
		int startLocation = locationMap.getLocation( startInstruction );
		int endLocation = locationMap.getLocation( endInstruction );
		bufferWriter.writeUnsignedShort( startLocation );
		bufferWriter.writeUnsignedShort( endLocation - startLocation );
		bufferWriter.writeUnsignedShort( index );
	}
}
