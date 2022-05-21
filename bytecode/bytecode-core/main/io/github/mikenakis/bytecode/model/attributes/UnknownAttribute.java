package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.Buffer;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an unknown {@link Attribute} of a java class file.
 *
 * @author michael.gr
 */
public final class UnknownAttribute extends Attribute
{
	public static UnknownAttribute of( Mutf8ValueConstant name, Buffer buffer )
	{
		return new UnknownAttribute( name, buffer );
	}

	private final Buffer buffer;

	private UnknownAttribute( Mutf8ValueConstant mutf8name, Buffer buffer )
	{
		super( mutf8name );
		this.buffer = buffer;
	}

	public Buffer buffer()
	{
		return buffer;
	}

	@Deprecated @Override public boolean isKnown() { return false; }
	@Deprecated @Override public UnknownAttribute asUnknownAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return buffer.length() + " bytes"; }

	@Override public void intern( Interner interner )
	{
		// nothing to do
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeBuffer( buffer );
	}
}
