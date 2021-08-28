package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Collection;
import java.util.Optional;

public final class ConstantReferencingInstruction extends Instruction
{
	public static final class Model extends InstructionModel
	{
		public Model( int opCode )
		{
			super( opCode );
		}

		public ConstantReferencingInstruction newInstruction( Constant constant )
		{
			return new ConstantReferencingInstruction( this, constant );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new ConstantReferencingInstruction( this, codeAttribute, pc, bufferReader );
		}
	}

	public final Model model;
	public final Constant constant;

	private ConstantReferencingInstruction( Model model, Constant constant )
	{
		super( Optional.empty() );
		this.model = model;
		this.constant = constant;
	}

	private ConstantReferencingInstruction( Model model, CodeAttribute codeAttribute, int pc, BufferReader bufferReader )
	{
		super( Optional.of( pc ) );
		this.model = model;
		constant = codeAttribute.constantPool.readIndexAndGetConstant( bufferReader );
	}

	@Override public Optional<ConstantReferencingInstruction> tryAsConstantReferencingInstruction()
	{
		return Optional.of( this );
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		constant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( model.opCode );
		constant.writeIndex( constantPool, bufferWriter );
	}
}
