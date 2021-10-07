package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public final class EmptyTarget extends Target // "empty_target" in jvms-4.7.20.1
{
	public static EmptyTarget of( int tag )
	{
		return new EmptyTarget( tag );
	}

	private EmptyTarget( int tag )
	{
		super( tag );
		assert tag == tag_FieldType || tag == tag_ReturnType || tag == tag_ReceiverType;
	}

	@Deprecated @Override public EmptyTarget asEmptyTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "(empty)"; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		// nothing to do
	}
}
