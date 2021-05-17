package mikenakis.bytecode.annotationvalues;

import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.ConstantKind;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.DoubleConstant;
import mikenakis.bytecode.constants.FloatConstant;
import mikenakis.bytecode.constants.IntegerConstant;
import mikenakis.bytecode.constants.LongConstant;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.constants.ValueConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * Represents a constant {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstAnnotationValue extends AnnotationValue
{
	//@formatter:off
	public static final Kind BYTE_KIND       /**/ = new Kind( 'B', "Byte"      /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.BYTE_KIND     , constantPool, bufferReader ); } };
	public static final Kind CHARACTER_KIND  /**/ = new Kind( 'C', "Character" /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.CHARACTER_KIND, constantPool, bufferReader ); } };
	public static final Kind DOUBLE_KIND     /**/ = new Kind( 'D', "Double"    /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.DOUBLE_KIND   , constantPool, bufferReader ); } };
	public static final Kind FLOAT_KIND      /**/ = new Kind( 'F', "Float"     /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.FLOAT_KIND    , constantPool, bufferReader ); } };
	public static final Kind INT_KIND        /**/ = new Kind( 'I', "Integer"   /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.INT_KIND      , constantPool, bufferReader ); } };
	public static final Kind LONG_KIND       /**/ = new Kind( 'J', "Long"      /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.LONG_KIND     , constantPool, bufferReader ); } };
	public static final Kind SHORT_KIND      /**/ = new Kind( 'S', "Short"     /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.SHORT_KIND    , constantPool, bufferReader ); } };
	public static final Kind BOOLEAN_KIND    /**/ = new Kind( 'Z', "Boolean"   /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.BOOLEAN_KIND  , constantPool, bufferReader ); } };
	public static final Kind STRING_KIND     /**/ = new Kind( 's', "String"    /**/ ) { @Override public AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader ) { return new ConstAnnotationValue /**/ ( observer, ConstAnnotationValue.STRING_KIND   , constantPool, bufferReader ); } };
	//@formatter:on

	public final ValueConstant<?> valueConstant;

	public ConstAnnotationValue( Runnable observer, Kind kind, ValueConstant<?> valueConstant )
	{
		super( observer, kind );
		assert valueConstant.kind == getValueConstantKind( kind.tag );
		this.valueConstant = valueConstant;
	}

	public ConstAnnotationValue( Runnable observer, Kind kind, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( observer, kind );
		valueConstant = constantPool.readIndexAndGetConstant( bufferReader ).asValueConstant();
		assert valueConstant.kind == getValueConstantKind( kind.tag );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		valueConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		super.write( constantPool, bufferWriter );
		valueConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public Optional<ConstAnnotationValue> tryAsConstAnnotationValue()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( kind.name );
		builder.append( " value = " );
		valueConstant.toStringBuilder( builder );
	}

	private static ConstantKind getValueConstantKind( int tag )
	{
		switch( tag )
		{
			case 'B': //byte
			case 'C': //char
			case 'I': //int
			case 'S': //short
			case 'Z': //boolean
				return IntegerConstant.KIND;
			case 'J': //long
				return LongConstant.KIND;
			case 'D': //double
				return DoubleConstant.KIND;
			case 'F': //float
				return FloatConstant.KIND;
			case 's': //string
				return Utf8Constant.KIND;
			default:
				throw new AssertionError();
		}
	}
}
