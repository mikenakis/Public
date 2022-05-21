package io.github.mikenakis.bytecode.model.attributes.stackmap.verification;

import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferWriter;

/**
 * 'Simple' {@link VerificationType}.
 *
 * @author michael.gr
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
