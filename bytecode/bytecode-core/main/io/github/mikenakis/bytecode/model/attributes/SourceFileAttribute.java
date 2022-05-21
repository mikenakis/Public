package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "Source File" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author michael.gr
 */
public final class SourceFileAttribute extends KnownAttribute
{
	public static SourceFileAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		Mutf8ValueConstant valueConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		return of( valueConstant );
	}

	public static SourceFileAttribute of( String sourceFile )
	{
		Mutf8ValueConstant valueConstant = Mutf8ValueConstant.of( sourceFile );
		return of( valueConstant );
	}

	public static SourceFileAttribute of( Mutf8ValueConstant sourceFileConstant )
	{
		return new SourceFileAttribute( sourceFileConstant );
	}

	private final Mutf8ValueConstant valueConstant;

	private SourceFileAttribute( Mutf8ValueConstant valueConstant )
	{
		super( tag_SourceFile );
		this.valueConstant = valueConstant;
	}

	public String value() { return valueConstant.stringValue(); }
	@Deprecated @Override public SourceFileAttribute asSourceFileAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
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
