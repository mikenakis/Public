package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "Exceptions" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionsAttribute extends KnownAttribute
{
	public static ExceptionsAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<ClassConstant> exceptionClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant constant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asClassConstant();
			exceptionClassConstants.add( constant );
		}
		return of( exceptionClassConstants );
	}

	public static ExceptionsAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static ExceptionsAttribute of( List<ClassConstant> exceptionClassConstants )
	{
		return new ExceptionsAttribute( exceptionClassConstants );
	}

	private final List<ClassConstant> exceptionClassConstants;

	private ExceptionsAttribute( List<ClassConstant> exceptionClassConstants )
	{
		super( tag_Exceptions );
		this.exceptionClassConstants = exceptionClassConstants;
	}

	public List<TerminalTypeDescriptor> exceptions() { return exceptionClassConstants.stream().map( c -> c.terminalTypeDescriptor() ).toList(); }
	@Deprecated @Override public ExceptionsAttribute asExceptionsAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return exceptionClassConstants.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( ClassConstant exceptionClassConstant : exceptionClassConstants )
			exceptionClassConstant.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( exceptionClassConstants.size() );
		for( ClassConstant exceptionClassConstant : exceptionClassConstants )
			bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( exceptionClassConstant ) );
	}
}
