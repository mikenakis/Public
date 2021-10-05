package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.kit.Helpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.model.constants.value.DoubleValueConstant;
import mikenakis.bytecode.model.constants.value.FloatValueConstant;
import mikenakis.bytecode.model.constants.value.IntegerValueConstant;
import mikenakis.bytecode.model.constants.value.LongValueConstant;
import mikenakis.bytecode.model.constants.value.StringValueConstant;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.InstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public class LoadConstantInstruction extends Instruction
{

	public static LoadConstantInstruction read( CodeAttributeReader codeAttributeReader, boolean wide, int opCode )
	{
		assert !wide;
		Constant constant = switch( opCode )
			{
				case OpCode.ICONST_M1 -> IntegerValueConstant.of( -1 );
				case OpCode.ICONST_0 -> IntegerValueConstant.of( 0 );
				case OpCode.ICONST_1 -> IntegerValueConstant.of( 1 );
				case OpCode.ICONST_2 -> IntegerValueConstant.of( 2 );
				case OpCode.ICONST_3 -> IntegerValueConstant.of( 3 );
				case OpCode.ICONST_4 -> IntegerValueConstant.of( 4 );
				case OpCode.ICONST_5 -> IntegerValueConstant.of( 5 );
				case OpCode.FCONST_0 -> FloatValueConstant.of( 0.0f );
				case OpCode.FCONST_1 -> FloatValueConstant.of( 1.0f );
				case OpCode.FCONST_2 -> FloatValueConstant.of( 2.0f );
				case OpCode.LCONST_0 -> LongValueConstant.of( 0L );
				case OpCode.LCONST_1 -> LongValueConstant.of( 1L );
				case OpCode.DCONST_0 -> DoubleValueConstant.of( 0.0 );
				case OpCode.DCONST_1 -> DoubleValueConstant.of( 1.0 );
				case OpCode.BIPUSH -> IntegerValueConstant.of( codeAttributeReader.readUnsignedByte() );
				case OpCode.SIPUSH -> IntegerValueConstant.of( codeAttributeReader.readUnsignedShort() );
				case OpCode.LDC -> readLdcConstant( codeAttributeReader );
				case OpCode.LDC_W -> readLdcWConstant( codeAttributeReader );
				case OpCode.LDC2_W -> readLdc2WConstant( codeAttributeReader );
				default -> throw new AssertionError( opCode );
			};
		return of( constant );
	}

	private static Constant readLdcConstant( CodeAttributeReader codeAttributeReader )
	{
		int constantIndexValue = codeAttributeReader.readUnsignedByte();
		Constant c = codeAttributeReader.getConstant( constantIndexValue );
		assert c.tag == Constant.tag_Integer || c.tag == Constant.tag_Float || c.tag == Constant.tag_String || c.tag == Constant.tag_Class;
		return c;
	}

	private static Constant readLdcWConstant( CodeAttributeReader codeAttributeReader )
	{
		int constantIndexValue = codeAttributeReader.readUnsignedShort();
		Constant c = codeAttributeReader.getConstant( constantIndexValue );
		assert c.tag == Constant.tag_Integer || c.tag == Constant.tag_Float || c.tag == Constant.tag_String || c.tag == Constant.tag_Class;
		return c;
	}

	private static Constant readLdc2WConstant( CodeAttributeReader codeAttributeReader )
	{
		int constantIndexValue = codeAttributeReader.readUnsignedShort();
		Constant c = codeAttributeReader.getConstant( constantIndexValue );
		assert c.tag == Constant.tag_Long || c.tag == Constant.tag_Double;
		return c;
	}

	public static LoadConstantInstruction of( boolean value )
	{
		return of( IntegerValueConstant.of( value ? 1 : 0 ) );
	}

	public static LoadConstantInstruction of( int value )
	{
		return of( IntegerValueConstant.of( value ) );
	}

	public static LoadConstantInstruction of( float value )
	{
		return of( FloatValueConstant.of( value ) );
	}

	public static LoadConstantInstruction of( long value )
	{
		return of( LongValueConstant.of( value ) );
	}

	public static LoadConstantInstruction of( double value )
	{
		return of( DoubleValueConstant.of( value ) );
	}

	public static LoadConstantInstruction of( String value )
	{
		return of( StringValueConstant.of( value ) );
	}

	public static LoadConstantInstruction of( TypeDescriptor value )
	{
		return of( ClassConstant.of( value ) );
	}

	private static LoadConstantInstruction of( Constant constant )
	{
		return new LoadConstantInstruction( constant );
	}

	public static final int opCode = OpCode.LDC;

	private final Constant constant;

	private LoadConstantInstruction( Constant constant )
	{
		super( groupTag_LoadConstant );
		assert isValidAssertion( constant );
		this.constant = constant;
	}

	public boolean isValue() { return !isType(); }
	public boolean isType() { return  constant.tag == Constant.tag_Class; }
	public ValueConstant getValue() { return constant.asValueConstant(); }
	public TypeDescriptor getTypeDescriptor() { return constant.asClassConstant().typeDescriptor(); }
	@Deprecated @Override public LoadConstantInstruction asLoadConstantInstruction() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "LoadConstant"; }

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

	@Override public void intern( Interner interner )
	{
		switch( constant.tag )
		{
			case Constant.tag_Integer:
			{
				IntegerValueConstant integerConstant = constant.asIntegerValueConstant();
				switch( integerConstant.value )
				{
					case -1:
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return;
					default:
						if( Helpers.isSignedByte( integerConstant.value ) )
							return;
						else if( Helpers.isSignedShort( integerConstant.value ) )
							return;
						break;
				}
				break;
			}
			case Constant.tag_Float:
			{
				FloatValueConstant floatConstant = constant.asFloatValueConstant();
				if( floatConstant.value == 0.0f )
					return;
				else if( floatConstant.value == 1.0f )
					return;
				else if( floatConstant.value == 2.0f )
					return;
				break;
			}
			case Constant.tag_Long:
			{
				LongValueConstant longConstant = constant.asLongValueConstant();
				if( longConstant.value == 0L )
					return;
				else if( longConstant.value == 1L )
					return;
				break;
			}
			case Constant.tag_Double:
			{
				DoubleValueConstant doubleConstant = constant.asDoubleValueConstant();
				if( doubleConstant.value == 0.0 )
					return;
				else if( doubleConstant.value == 1.0 )
					return;
				break;
			}
			case Constant.tag_String:
			case Constant.tag_Class:
				break;
			default:
				assert false;
				break;
		}

		constant.intern( interner );
	}

	@Override public void write( InstructionWriter instructionWriter )
	{
		switch( constant.tag )
		{
			case Constant.tag_Integer:
			{
				IntegerValueConstant integerConstant = constant.asIntegerValueConstant();
				switch( integerConstant.value )
				{
					case -1:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_M1 );
						return;
					case 0:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_0 );
						return;
					case 1:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_1 );
						return;
					case 2:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_2 );
						return;
					case 3:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_3 );
						return;
					case 4:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_4 );
						return;
					case 5:
						instructionWriter.writeUnsignedByte( OpCode.ICONST_5 );
						return;
					default:
						if( Helpers.isSignedByte( integerConstant.value ) )
						{
							instructionWriter.writeUnsignedByte( OpCode.BIPUSH );
							instructionWriter.writeUnsignedByte( integerConstant.value );
							return;
						}
						else if( Helpers.isSignedShort( integerConstant.value ) )
						{
							instructionWriter.writeUnsignedByte( OpCode.SIPUSH );
							instructionWriter.writeUnsignedShort( integerConstant.value );
							return;
						}
						break;
				}
				break;
			}
			case Constant.tag_Float:
			{
				FloatValueConstant floatConstant = constant.asFloatValueConstant();
				if( floatConstant.value == 0.0f )
				{
					instructionWriter.writeUnsignedByte( OpCode.FCONST_0 );
					return;
				}
				else if( floatConstant.value == 1.0f )
				{
					instructionWriter.writeUnsignedByte( OpCode.FCONST_1 );
					return;
				}
				else if( floatConstant.value == 2.0f )
				{
					instructionWriter.writeUnsignedByte( OpCode.FCONST_2 );
					return;
				}
				break;
			}
			case Constant.tag_Long:
			{
				LongValueConstant longConstant = constant.asLongValueConstant();
				if( longConstant.value == 0L )
				{
					instructionWriter.writeUnsignedByte( OpCode.LCONST_0 );
					return;
				}
				else if( longConstant.value == 1L )
				{
					instructionWriter.writeUnsignedByte( OpCode.LCONST_1 );
					return;
				}
				break;
			}
			case Constant.tag_Double:
			{
				DoubleValueConstant doubleConstant = constant.asDoubleValueConstant();
				if( doubleConstant.value == 0.0 )
				{
					instructionWriter.writeUnsignedByte( OpCode.DCONST_0 );
					return;
				}
				else if( doubleConstant.value == 1.0 )
				{
					instructionWriter.writeUnsignedByte( OpCode.DCONST_1 );
					return;
				}
				break;
			}
			case Constant.tag_String:
			case Constant.tag_Class:
				break;
			default:
				assert false;
				break;
		}

		int constantIndex = instructionWriter.getIndex( constant );
		if( constant.tag == Constant.tag_Long || constant.tag == Constant.tag_Double )
		{
			instructionWriter.writeUnsignedByte( OpCode.LDC2_W );
			instructionWriter.writeUnsignedShort( constantIndex );
		}
		else
		{
			if( Helpers.isUnsignedByte( constantIndex ) )
			{
				instructionWriter.writeUnsignedByte( OpCode.LDC );
				instructionWriter.writeUnsignedByte( constantIndex );
			}
			else
			{
				instructionWriter.writeUnsignedByte( OpCode.LDC_W );
				instructionWriter.writeUnsignedShort( constantIndex );
			}
		}
	}
}
