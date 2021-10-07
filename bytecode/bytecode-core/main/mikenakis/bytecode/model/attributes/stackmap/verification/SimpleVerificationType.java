package mikenakis.bytecode.model.attributes.stackmap.verification;

import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;

/**
 * 'Simple' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SimpleVerificationType extends VerificationType
{
	public SimpleVerificationType( int tag )
	{
		super( tag );
		assert tag == tag_Top ||tag == tag_Integer || tag == tag_Float || tag == tag_Double || tag == tag_Long || tag == tag_Null || tag == tag_UninitializedThis;
	}

	@Deprecated @Override public SimpleVerificationType asSimpleVerificationType() { return this; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap )
	{
		bufferWriter.writeUnsignedByte( tag );
	}
}
