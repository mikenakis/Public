package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Collection;
import java.util.Optional;

public final class MultiANewArrayInstruction extends Instruction
{
	public static final class Model extends InstructionModel
	{
		public Model()
		{
			super( OpCode.MULTIANEWARRAY );
		}

		public MultiANewArrayInstruction newInstruction( Constant constant, int dimensionCount )
		{
			return new MultiANewArrayInstruction( this, constant, dimensionCount );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new MultiANewArrayInstruction( this, codeAttribute, pc, bufferReader );
		}
	}

	public final Model model;
	public final Constant constant;
	public final int dimensionCount;

	private MultiANewArrayInstruction( Model model, Constant constant, int dimensionCount )
	{
		super( Optional.empty() );
		this.model = model;
		this.constant = constant;
		this.dimensionCount = dimensionCount;
	}

	private MultiANewArrayInstruction( Model model, CodeAttribute codeAttribute, int pc, BufferReader bufferReader )
	{
		super( Optional.of( pc ) );
		this.model = model;
		constant = codeAttribute.method.declaringType.constantPool.readIndexAndGetConstant( bufferReader );
		dimensionCount = bufferReader.readUnsignedByte();
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
		bufferWriter.writeUnsignedByte( dimensionCount );
	}

	@Override public Optional<MultiANewArrayInstruction> tryAsMultiANewArrayInstruction()
	{
		return Optional.of( this );
	}
}
