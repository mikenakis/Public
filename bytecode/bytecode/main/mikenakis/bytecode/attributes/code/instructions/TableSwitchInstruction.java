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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class TableSwitchInstruction extends Instruction
{
	public static final class Model extends InstructionModel
	{
		public Model()
		{
			super( OpCode.TABLESWITCH );
		}

		public TableSwitchInstruction newInstruction()
		{
			return new TableSwitchInstruction( this );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new TableSwitchInstruction( this, codeAttribute, pc, bufferReader, fixUps );
		}
	}

	public final Model model;
	public final RelativeInstructionReference defaultInstructionReference;
	public int lowValue = 0;
	public final List<RelativeInstructionReference> instructionReferences;

	private TableSwitchInstruction( Model model )
	{
		super( Optional.empty() );
		this.model = model;
		defaultInstructionReference = new RelativeInstructionReference( this );
		instructionReferences = new ArrayList<>();
	}

	private TableSwitchInstruction( Model model, CodeAttribute codeAttribute, int pc, BufferReader bufferReader, Collection<Runnable> fixUps )
	{
		super( Optional.of( pc ) );
		this.model = model;
		int padding = ByteCodeHelpers.getPadding( bufferReader.getPosition() );
		bufferReader.skip( padding );
		defaultInstructionReference = new RelativeInstructionReference( codeAttribute, this, true, bufferReader, fixUps );
		lowValue = bufferReader.readInt();
		int highValue = bufferReader.readInt();
		int entryCount = highValue - lowValue + 1;
		instructionReferences = new ArrayList<>( entryCount );
		for( int index = 0; index < entryCount; index++ )
		{
			RelativeInstructionReference codeOffset = new RelativeInstructionReference( codeAttribute, this, true, bufferReader, fixUps );
			instructionReferences.add( codeOffset );
		}
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( model.opCode );
		bufferWriter.skip( ByteCodeHelpers.getPadding( bufferWriter.getPosition() ) );
		defaultInstructionReference.write( true, bufferWriter );
		bufferWriter.writeInt( lowValue );
		bufferWriter.writeInt( lowValue + instructionReferences.size() - 1 );
		for( RelativeInstructionReference instructionReference : instructionReferences )
			instructionReference.write( true, bufferWriter );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( defaultInstructionReference.getTargetInstruction() );
		for( RelativeInstructionReference instructionReference : instructionReferences )
			targetInstructionConsumer.accept( instructionReference.getTargetInstruction() );
	}

	@Override public Optional<TableSwitchInstruction> tryAsTableSwitchInstruction()
	{
		return Optional.of( this );
	}
}
