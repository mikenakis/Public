package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeField;
import io.github.mikenakis.bytecode.model.constants.ValueConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "ConstantValue" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeField}
 *
 * @author michael.gr
 */
public final class ConstantValueAttribute extends KnownAttribute
{
	public static ConstantValueAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		ValueConstant valueConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asValueConstant();
		return of( valueConstant );
	}

	public static ConstantValueAttribute of( ValueConstant valueConstant )
	{
		return new ConstantValueAttribute( valueConstant );
	}

	public final ValueConstant valueConstant;

	private ConstantValueAttribute( ValueConstant valueConstant )
	{
		super( tag_ConstantValue );
		this.valueConstant = valueConstant;
	}

	@Deprecated @Override public ConstantValueAttribute asConstantValueAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "value = " + valueConstant.toString(); }

	@Override public void intern( Interner interner )
	{
		valueConstant.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( valueConstant ) );
	}
}
