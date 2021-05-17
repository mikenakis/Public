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
 * Represents the "InnerClasses" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LocalVariableTypeTableAttribute extends Attribute
{
	public static final String NAME = "LocalVariableTypeTable";

	public static Optional<LocalVariableTypeTableAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asLocalVariableTypeTableAttribute() );
	}

	public final ArrayList<LocalVariableType> entries = new ArrayList<>();

	public LocalVariableTypeTableAttribute( Runnable observer )
	{
		super( observer, NAME );
	}

	public LocalVariableTypeTableAttribute( Runnable observer, CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		super( observer, NAME );
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		entries.clear();
		entries.ensureCapacity( count );
		for( int i = 0; i < count; i++ )
		{
			LocalVariableType entry = new LocalVariableType( codeAttribute, bufferReader );
			entries.add( entry );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( LocalVariableType entry : entries )
			entry.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( entries.size() );
		for( LocalVariableType entry : entries )
			entry.write( constantPool, bufferWriter );
	}

	@Override public Optional<LocalVariableTypeTableAttribute> tryAsLocalVariableTypeTableAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( entries.size() ).append( " entries" );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		for( LocalVariableType entry : entries )
			entry.collectTargets( targetInstructionConsumer );
	}
}
