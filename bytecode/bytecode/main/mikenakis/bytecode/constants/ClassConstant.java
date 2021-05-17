package mikenakis.bytecode.constants;

import mikenakis.bytecode.ByteCodeHelpers;
import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantKind;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents the JVMS::CONSTANT_Class_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassConstant extends Constant
{
	public static final int TAG = 7; // JVMS::CONSTANT_Class_info
	public static final ConstantKind KIND = new ConstantKind( TAG, "Class" )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 2 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new ClassConstant( constantPool, bufferReader );
		}
	};

	public final Optional<Utf8Constant> nameConstant;

	public ClassConstant( String name )
	{
		super( KIND );
		nameConstant = Optional.of( new Utf8Constant( name ) );
	}

	private ClassConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND );
		int index = bufferReader.readUnsignedShort();
		nameConstant = index == 0 ? Optional.empty() : Optional.of( constantPool.getConstant( index ).asUtf8Constant() );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		nameConstant.orElseThrow().intern( constantPool );
		super.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		nameConstant.orElseThrow().writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( getClassName() );
	}

	@Deprecated @Override public ClassConstant asClassConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof ClassConstant )
		{
			ClassConstant otherClassConstant = (ClassConstant)other;
			return equalsClassConstant( otherClassConstant );
		}
		return false;
	}

	public boolean equalsClassConstant( ClassConstant other )
	{
		return nameConstant.equals( other.nameConstant );
	}

	@Override public int hashCode()
	{
		return Objects.hash( kind, nameConstant );
	}

	public String getClassName()
	{
		return ByteCodeHelpers.getJavaTypeNameFromJvmTypeName( nameConstant.orElseThrow().getStringValue() );
	}
}
