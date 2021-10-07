package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( offset );
	}
}
