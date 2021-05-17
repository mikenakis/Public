package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Collection;
import java.util.Optional;

public final class OperandlessInstruction extends Instruction
{
	public static final class Model extends InstructionModel
	{
		public Model( int opCode )
		{
			super( opCode );
		}

		public OperandlessInstruction newInstruction()
		{
			return new OperandlessInstruction( this );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new OperandlessInstruction( this, pc );
		}
	}

	public final Model model;

	private OperandlessInstruction( Model model )
	{
		super( Optional.empty() );
		this.model = model;
	}

	private OperandlessInstruction( Model model, int pc )
	{
		super( Optional.of( pc ) );
		this.model = model;
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
		int opCode = model.opCode;
		bufferWriter.writeUnsignedByte( opCode );
	}

	@Override public Optional<OperandlessInstruction> tryAsOperandlessInstruction()
	{
		return Optional.of( this );
	}
}
