package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.MethodHandleConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;
import mikenakis.kit.Kit;

import java.util.ArrayList;

/**
 * An entry of the {@link BootstrapMethodsAttribute}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class BootstrapMethod extends Printable
{
	public final MethodHandleConstant methodHandleConstant;
	public final ArrayList<Constant> argumentConstants;

	public BootstrapMethod( MethodHandleConstant methodHandleConstant )
	{
		this.methodHandleConstant = methodHandleConstant;
		argumentConstants = new ArrayList<>();
	}

	public BootstrapMethod( ConstantPool constantPool, BufferReader bufferReader )
	{
		methodHandleConstant = constantPool.readIndexAndGetConstant( bufferReader ).asMethodHandleConstant();
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		argumentConstants = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			Constant argumentConstant = constantPool.readIndexAndGetConstant( bufferReader );
			argumentConstants.add( argumentConstant );
		}
	}

	public void intern( ConstantPool constantPool )
	{
		methodHandleConstant.intern( constantPool );
		for( Constant argumentConstant : argumentConstants )
			argumentConstant.intern( constantPool );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		methodHandleConstant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( argumentConstants.size() );
		for( Constant argumentConstant : argumentConstants )
			argumentConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "methodHandle = " );
		methodHandleConstant.toStringBuilder( builder );
		if( !argumentConstants.isEmpty() )
		{
			builder.append( ", arguments = " );
			boolean first = true;
			for( Constant argumentConstant : argumentConstants )
			{
				first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
				argumentConstant.toStringBuilder( builder );
			}
		}
	}
}
