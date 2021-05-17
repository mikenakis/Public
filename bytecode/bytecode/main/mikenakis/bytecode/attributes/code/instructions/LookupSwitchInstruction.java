package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.ByteCodeHelpers;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.RelativeInstructionReference;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class LookupSwitchInstruction extends Instruction
{
	public static final class Model extends InstructionModel
	{
		public Model()
		{
			super( OpCode.LOOKUPSWITCH );
		}

		public LookupSwitchInstruction newInstruction()
		{
			return new LookupSwitchInstruction( this );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new LookupSwitchInstruction( this, codeAttribute, pc, bufferReader, fixUps );
		}
	}

	public final class Entry extends Printable
	{
		public final int value;
		public final RelativeInstructionReference instructionReference;

		Entry( CodeAttribute codeAttribute, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			value = bufferReader.readInt();
			instructionReference = new RelativeInstructionReference( codeAttribute, LookupSwitchInstruction.this, true, bufferReader, fixUps );
		}

		@SuppressWarnings( "EmptyMethod" ) void intern()
		{
			/* nothing to do */
		}

		void write( BufferWriter bufferWriter )
		{
			bufferWriter.writeInt( value );
			instructionReference.write( true, bufferWriter );
		}

		@Override public void toStringBuilder( StringBuilder builder )
		{
			builder.append( value ).append( ":" );
			instructionReference.toStringBuilder( builder );
		}
	}

	public final Model model;
	public final RelativeInstructionReference defaultInstructionReference;
	public final List<Entry> entries;

	private LookupSwitchInstruction( Model model )
	{
		super( Optional.empty() );
		this.model = model;
		defaultInstructionReference = new RelativeInstructionReference( this );
		entries = new ArrayList<>();
	}

	private LookupSwitchInstruction( Model model, CodeAttribute codeAttribute, int pc, BufferReader bufferReader, Collection<Runnable> fixUps )
	{
		super( Optional.of( pc ) );
		this.model = model;
		bufferReader.skip( ByteCodeHelpers.getPadding( bufferReader.getPosition() ) );
		defaultInstructionReference = new RelativeInstructionReference( codeAttribute, this, true, bufferReader, fixUps );
		int count = bufferReader.readInt();
		assert count > 0;
		entries = new ArrayList<>( count );
		for( int index = 0; index < count; index++ )
		{
			Entry entry = new Entry( codeAttribute, bufferReader, fixUps );
			entries.add( entry );
		}
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( Entry entry : entries )
			entry.intern();
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( model.opCode );
		int padding = ByteCodeHelpers.getPadding( bufferWriter.getPosition() );
		bufferWriter.skip( padding );
		defaultInstructionReference.write( true, bufferWriter );
		bufferWriter.writeInt( entries.size() );
		for( Entry entry : entries )
			entry.write( bufferWriter );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( defaultInstructionReference.getTargetInstruction() );
		for( Entry entry : entries )
			targetInstructionConsumer.accept( entry.instructionReference.getTargetInstruction() );
	}

	@Override public Optional<LookupSwitchInstruction> tryAsLookupSwitchInstruction()
	{
		return Optional.of( this );
	}
}
