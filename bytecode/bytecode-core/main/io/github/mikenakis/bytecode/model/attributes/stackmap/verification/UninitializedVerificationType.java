package io.github.mikenakis.bytecode.model.attributes.stackmap.verification;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;

/**
 * 'Uninitialized' {@link VerificationType}.
 *
 * @author michael.gr
 */
public final class UninitializedVerificationType extends VerificationType
{
	public static UninitializedVerificationType read( BufferReader bufferReader, ReadingLocationMap locationMap )
	{
		Instruction instruction = locationMap.getInstruction( bufferReader.readUnsignedShort() ).orElseThrow();
		return of( instruction );
	}

	public static UninitializedVerificationType of( Instruction instruction )
	{
		return new UninitializedVerificationType( instruction );
	}

	public final Instruction instruction;

	private UninitializedVerificationType( Instruction instruction )
	{
		super( tag_Uninitialized );
		this.instruction = instruction;
	}

	@Deprecated @Override public UninitializedVerificationType asUninitializedVerificationType() { return this; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap )
	{
		bufferWriter.writeUnsignedByte( tag );
		int targetLocation = locationMap.getLocation( instruction );
		bufferWriter.writeUnsignedShort( targetLocation );
	}
}
