package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModels;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.constants.DoubleConstant;
import mikenakis.bytecode.constants.LongConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Helpers;

import java.util.Collection;
import java.util.Optional;

public final class IndirectLoadConstantInstruction extends LoadConstantInstruction
{
	public static Model getModelFromConstant( Constant constant )
	{
		if( constant.kind == LongConstant.KIND || constant.kind == DoubleConstant.KIND )
			return InstructionModels.LDC2_W_MODEL;
		return InstructionModels.LDC_MODEL;
	}

	public static final class Model extends LoadConstantInstruction.Model
	{
		public Model( int opCode )
		{
			super( opCode );
		}

		@Override public LoadConstantInstruction newInstruction( Constant constant )
		{
			assert this == InstructionModels.LDC_MODEL || this == InstructionModels.LDC2_W_MODEL;
			return new IndirectLoadConstantInstruction( this, constant );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new IndirectLoadConstantInstruction( this, codeAttribute, pc, bufferReader, fixUps );
		}
	}

	public final Model model;
	public final Constant constant;

	private IndirectLoadConstantInstruction( Model model, Constant constant )
	{
		super( Optional.empty() );
		this.model = model;
		this.constant = constant;
	}

	private IndirectLoadConstantInstruction( Model model, CodeAttribute codeAttribute, int pc, BufferReader bufferReader, Collection<Runnable> fixUps )
	{
		super( Optional.of( pc ) );
		this.model = model;
		switch( model.opCode )
		{
			case OpCode.LDC:
			{
				int constantIndexValue = bufferReader.readUnsignedByte();
				constant = codeAttribute.method.declaringType.constantPool.getConstant( constantIndexValue );
				break;
			}
			case OpCode.LDC_W:
			case OpCode.LDC2_W:
			{
				int constantIndexValue = bufferReader.readUnsignedShort();
				constant = codeAttribute.method.declaringType.constantPool.getConstant( constantIndexValue );
				break;
			}
			default:
				throw new AssertionError();
		}
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
		int constantIndex = constantPool.getIndex( constant );
		if( model.opCode == OpCode.LDC2_W )
		{
			bufferWriter.writeUnsignedByte( OpCode.LDC2_W );
			bufferWriter.writeUnsignedShort( constantIndex );
		}
		else
		{
			if( Helpers.isUnsignedByte( constantIndex ) )
			{
				bufferWriter.writeUnsignedByte( OpCode.LDC );
				bufferWriter.writeUnsignedByte( constantIndex );
			}
			else
			{
				bufferWriter.writeUnsignedByte( OpCode.LDC_W );
				bufferWriter.writeUnsignedShort( constantIndex );
			}
		}
	}

	@Override public Optional<IndirectLoadConstantInstruction> tryAsIndirectLoadConstantInstruction()
	{
		return Optional.of( this );
	}
}
