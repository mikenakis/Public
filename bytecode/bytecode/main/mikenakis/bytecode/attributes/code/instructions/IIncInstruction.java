package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Helpers;

import java.util.Collection;
import java.util.Optional;

public final class IIncInstruction extends Instruction
{
	public static final class Model extends InstructionModel
	{
		public Model()
		{
			super( OpCode.IINC );
		}

		public IIncInstruction newInstruction( int localVariableIndex, int delta )
		{
			return new IIncInstruction( this, localVariableIndex, delta );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			return new IIncInstruction( this, pc, wide, bufferReader );
		}
	}

	public final Model model;
	private boolean wide;
	public final int localVariableIndex;
	public final int delta;

	private IIncInstruction( Model model, int localVariableIndex, int delta )
	{
		super( Optional.empty() );
		this.model = model;
		wide = false;
		this.localVariableIndex = localVariableIndex;
		this.delta = delta;
	}

	private IIncInstruction( Model model, int pc, boolean wide, BufferReader bufferReader )
	{
		super( Optional.of( pc ) );
		this.model = model;
		this.wide = wide;
		localVariableIndex = bufferReader.readUnsignedByteOrShort( wide );
		delta = bufferReader.readSignedByteOrShort( wide );
	}

	public boolean isWide()
	{
		return wide;
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		wide = !(Helpers.isUnsignedByte( localVariableIndex ) && Helpers.isSignedByte( delta ));
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		if( wide )
			bufferWriter.writeUnsignedByte( OpCode.WIDE );
		bufferWriter.writeUnsignedByte( model.opCode );
		bufferWriter.writeUnsignedByteOrShort( wide, localVariableIndex );
		bufferWriter.writeSignedByteOrShort( wide, delta );
	}

	@Override public Optional<IIncInstruction> tryAsIIncInstruction()
	{
		return Optional.of( this );
	}
}
