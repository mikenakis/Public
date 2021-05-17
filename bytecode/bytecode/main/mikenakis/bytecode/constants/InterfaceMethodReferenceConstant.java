package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;

/**
 * Represents the JVMS::CONSTANT_InterfaceMethodref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InterfaceMethodReferenceConstant extends MethodReferenceConstant
{
	public static final int TAG = 11; // JVMS::CONSTANT_InterfaceMethodRef_info

	static class Kind extends MethodReferenceConstant.Kind
	{
		Kind( int tag, String name )
		{
			super( tag, name );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new InterfaceMethodReferenceConstant( constantPool, bufferReader );
		}
	}

	public static final Kind KIND = new Kind( TAG, "InterfaceMethodReference" );

	public InterfaceMethodReferenceConstant( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( KIND, typeConstant, nameAndTypeConstant );
	}

	private InterfaceMethodReferenceConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND, constantPool, bufferReader );
	}

	@Deprecated @Override public InterfaceMethodReferenceConstant asInterfaceMethodReferenceConstant()
	{
		return this;
	}
}
