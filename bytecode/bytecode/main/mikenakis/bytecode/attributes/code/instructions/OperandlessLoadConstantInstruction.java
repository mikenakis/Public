package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Collection;
import java.util.Optional;

public final class OperandlessLoadConstantInstruction extends LoadConstantInstruction
{
	public static final class Model extends LoadConstantInstruction.Model
	{
		public Model( int opCode )
		{
			super( opCode );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new OperandlessLoadConstantInstruction( this, pc );
		}

		@Override public LoadConstantInstruction newInstruction( Constant constant )
		{
			return new OperandlessLoadConstantInstruction( this, 0 );
		}
	}

	public final Model model;

	private OperandlessLoadConstantInstruction( Model model, int pc )
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
		bufferWriter.writeUnsignedByte( model.opCode );
	}

	@Override public Optional<OperandlessLoadConstantInstruction> tryAsOperandlessLoadConstantInstruction()
	{
		return Optional.of( this );
	}
}
