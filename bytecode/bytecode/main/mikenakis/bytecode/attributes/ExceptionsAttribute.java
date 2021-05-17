package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.ByteCodeMethod;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

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
public final class ExceptionsAttribute extends Attribute
{
	public static final String NAME = "Exceptions";

	public final List<ClassConstant> exceptionClassConstants;

	public ExceptionsAttribute( Runnable observer )
	{
		super( observer, NAME );
		exceptionClassConstants = new ArrayList<>();
	}

	public ExceptionsAttribute( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, NAME );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		exceptionClassConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			ClassConstant constant = constantPool.readIndexAndGetConstant( bufferReader ).asClassConstant();
			exceptionClassConstants.add( constant );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( ClassConstant exceptionClassConstant : exceptionClassConstants )
			exceptionClassConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( exceptionClassConstants.size() );
		for( ClassConstant exceptionClassConstant : exceptionClassConstants )
			exceptionClassConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public Optional<ExceptionsAttribute> tryAsExceptionsAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( exceptionClassConstants.size() ).append( " entries" );
	}
}
