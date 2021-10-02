package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public class LoadConstantInstruction extends Instruction
{
	public static LoadConstantInstruction of( boolean value )
	{
		return of( IntegerConstant.of( value ? 1 : 0 ) );
	}

	public static LoadConstantInstruction of( int value )
	{
		return of( IntegerConstant.of( value ) );
	}

	public static LoadConstantInstruction of( float value )
	{
		return of( FloatConstant.of( value ) );
	}

	public static LoadConstantInstruction of( long value )
	{
		return of( LongConstant.of( value ) );
	}

	public static LoadConstantInstruction of( double value )
	{
		return of( DoubleConstant.of( value ) );
	}

	public static LoadConstantInstruction of( String value )
	{
		return of( StringConstant.of( value ) );
	}

	public static LoadConstantInstruction of( TypeDescriptor value )
	{
		return of( ClassConstant.of( value ) );
	}

	public static LoadConstantInstruction of( Constant constant )
	{
		return new LoadConstantInstruction( constant );
	}

	public static final int opCode = OpCode.LDC;

	public final Constant constant;

	private LoadConstantInstruction( Constant constant )
	{
		super( groupTag_LoadConstant );
		assert isValidAssertion( constant );
		this.constant = constant;
	}

	private static boolean isValidAssertion( Constant constant )
	{
		switch( constant.tag )
		{
			case Constant.tag_Integer, Constant.tag_Float, Constant.tag_Long, Constant.tag_Double, Constant.tag_String, Constant.tag_Class:
				return true;
			default:
				assert false : constant;
				return false;
		}
	}

	@Deprecated @Override public LoadConstantInstruction asLoadConstantInstruction() { return this; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "LoadConstant";
	}
}
