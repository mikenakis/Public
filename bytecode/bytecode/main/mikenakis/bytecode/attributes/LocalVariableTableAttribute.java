package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents the "LocalVariableTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTableAttribute extends Attribute
{
	public static final String NAME = "LocalVariableTable";

	public static Optional<LocalVariableTableAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asLocalVariableTableAttribute() );
	}

	public final ArrayList<LocalVariable> localVariables;

	public LocalVariableTableAttribute( Runnable observer )
	{
		super( observer, NAME );
		localVariables = new ArrayList<>();
	}

	public LocalVariableTableAttribute( Runnable observer, CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		super( observer, NAME );
		int count = bufferReader.readUnsignedShort();
		localVariables = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			LocalVariable localVariable = new LocalVariable( codeAttribute, bufferReader );
			localVariables.add( localVariable );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( LocalVariable localVariable : localVariables )
			localVariable.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( localVariables.size() );
		for( LocalVariable localVariable : localVariables )
			localVariable.write( constantPool, bufferWriter );
	}

	@Override public Optional<LocalVariableTableAttribute> tryAsLocalVariableTableAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( localVariables.size() ).append( " entries" );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		for( LocalVariable localVariable : localVariables )
			localVariable.collectTargets( targetInstructionConsumer );
	}
}
