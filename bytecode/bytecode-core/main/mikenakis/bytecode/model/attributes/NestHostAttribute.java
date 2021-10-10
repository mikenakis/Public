package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "NestHost" {@link Attribute} of a java class file.
 * <p>
 * See JVMS 4.7.28 "The NestHost Attribute", https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7.28
 * <p>
 * This attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * See JVMS Table 4.7-C, "Predefined class file attributes (by location)" https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7-320
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
