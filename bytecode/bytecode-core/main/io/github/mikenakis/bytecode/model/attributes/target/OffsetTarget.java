package io.github.mikenakis.bytecode.model.attributes.target;

import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class OffsetTarget extends Target
{
	public static OffsetTarget read( BufferReader bufferReader, int targetTag )
	{
		int offset = bufferReader.readUnsignedShort();
		//TODO use the offset to lookup the instruction and realize the instruction, not the offset.
		return new OffsetTarget( targetTag, offset );
	}

	public final int offset;

	private OffsetTarget( int tag, int offset )
	{
		super( tag );
		assert tag == tag_InstanceOfOffset || tag == tag_NewExpressionOffset || tag == tag_NewMethodOffset ||
			tag == tag_IdentifierMethodOffset;
		this.offset = offset;
	}

	@Deprecated @Override public OffsetTarget asOffsetTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "offset = " + offset; }

	@Override public void intern( Interner interner )
	{
		/* nothing to do */
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( offset );
	}
}
