package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class OffsetTarget extends Target
{
	public static OffsetTarget read( BufferReader bufferReader, int targetTag )
	{
		int offset = bufferReader.readUnsignedShort();
		return new OffsetTarget( targetTag, offset );
	}

	public final int offset; //TODO what kind of offset is this?

	private OffsetTarget( int tag, int offset )
	{
		super( tag );
		assert tag == tag_InstanceOfOffset || tag == tag_NewExpressionOffset || tag == tag_NewMethodOffset ||
			tag == tag_IdentifierMethodOffset;
		this.offset = offset;
	}

	@Deprecated @Override public OffsetTarget asOffsetTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "offset = " + offset; }

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( offset );
	}
}
