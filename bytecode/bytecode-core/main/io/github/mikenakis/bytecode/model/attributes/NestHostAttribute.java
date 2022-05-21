package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.model.constants.ClassConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "NestHost" {@link Attribute} of a java class file.
 * <p>
 * See <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7.28">JVMS 4.7.28 "The NestHost Attribute"</a>
 * <p>
 * This attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * See <a href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7-320">JVMS Table 4.7-C, "Predefined class file attributes (by location)"</a>
 *
 * @author michael.gr
 */
public final class NestHostAttribute extends KnownAttribute
{
	public static NestHostAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		ClassConstant hostClassConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
		return of( hostClassConstant );
	}

	public static NestHostAttribute of( ClassConstant classConstant )
	{
		return new NestHostAttribute( classConstant );
	}

	private final ClassConstant hostClassConstant;

	private NestHostAttribute( ClassConstant hostClassConstant )
	{
		super( tag_NestHost );
		this.hostClassConstant = hostClassConstant;
	}

	public TerminalTypeDescriptor hostClass() { return hostClassConstant.terminalTypeDescriptor(); }
	@Deprecated @Override public NestHostAttribute asNestHostAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return " -> " + hostClassConstant; }

	@Override public void intern( Interner interner )
	{
		hostClassConstant.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( hostClassConstant ) );
	}
}
