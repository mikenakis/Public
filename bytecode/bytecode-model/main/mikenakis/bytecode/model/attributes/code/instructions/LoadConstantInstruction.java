package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.StringConstant;

//TODO: merge all the load-constant instructions into one pseudo-instruction! Determine which one to use depending on the type and value of the constant.
public abstract class LoadConstantInstruction extends Instruction
{
	public static LoadConstantInstruction of( boolean value )
	{
		return of( value? 1 : 0 );
	}

	public static LoadConstantInstruction of( int value )
	{
		switch( value )
		{
			case -1: return OperandlessLoadConstantInstruction.of( OpCode.ICONST_M1 );
			case 0: return OperandlessLoadConstantInstruction.of( OpCode.ICONST_0 );
			case 1: return OperandlessLoadConstantInstruction.of( OpCode.ICONST_1 );
			case 2: return OperandlessLoadConstantInstruction.of( OpCode.ICONST_2 );
			case 3: return OperandlessLoadConstantInstruction.of( OpCode.ICONST_3 );
			case 4: return OperandlessLoadConstantInstruction.of( OpCode.ICONST_4 );
			case 5: return OperandlessLoadConstantInstruction.of( OpCode.ICONST_5 );
			default: break;
		}
		if( Helpers.isSignedByte( value ) )
			return ImmediateLoadConstantInstruction.of( OpCode.BIPUSH, value );
		if( Helpers.isSignedShort( value ) )
			return ImmediateLoadConstantInstruction.of( OpCode.SIPUSH, value );
		return IndirectLoadConstantInstruction.of( OpCode.LDC, IntegerConstant.of( value ) );
	}

	public static LoadConstantInstruction of( long value )
	{
		if( value == 0L )
			return OperandlessLoadConstantInstruction.of( OpCode.LCONST_0 );
		else if( value == 1L )
			return OperandlessLoadConstantInstruction.of( OpCode.LCONST_1 );
		return IndirectLoadConstantInstruction.of( OpCode.LDC2_W, LongConstant.of( value ) );
	}

	public static LoadConstantInstruction of( float value )
	{
		if( value == 0.0f )
			return OperandlessLoadConstantInstruction.of( OpCode.FCONST_0 );
		else if( value == 1.0f )
			return OperandlessLoadConstantInstruction.of( OpCode.FCONST_1 );
		else if( value == 2.0f )
			return OperandlessLoadConstantInstruction.of( OpCode.FCONST_2 );
		return IndirectLoadConstantInstruction.of( OpCode.LDC, FloatConstant.of( value ) );
	}

	public static LoadConstantInstruction of( double value )
	{
		if( value == 0.0 )
			return OperandlessLoadConstantInstruction.of( OpCode.DCONST_0 );
		else if( value == 1.0 )
			return OperandlessLoadConstantInstruction.of( OpCode.DCONST_1 );
		return IndirectLoadConstantInstruction.of( OpCode.LDC2_W, DoubleConstant.of( value ) );
	}

	public static LoadConstantInstruction of( String value )
	{
		return IndirectLoadConstantInstruction.of( OpCode.LDC2_W, StringConstant.of( value ) );
	}

	protected LoadConstantInstruction( Group group )
	{
		super( group );
	}
}
