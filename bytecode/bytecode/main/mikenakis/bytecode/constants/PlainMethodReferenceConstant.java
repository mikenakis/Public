package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;

/**
 * Represents the JVMS::CONSTANT_Methodref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class PlainMethodReferenceConstant extends MethodReferenceConstant
{
	public static final int TAG = 10; // JVMS::CONSTANT_MethodRef_info

	static class Kind extends MethodReferenceConstant.Kind
	{
		Kind( int tag, String name )
		{
			super( tag, name );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new PlainMethodReferenceConstant( constantPool, bufferReader );
		}
	}

	public static final Kind KIND = new Kind( TAG, "MethodReference" );

	public PlainMethodReferenceConstant( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( KIND, typeConstant, nameAndTypeConstant );
	}

	private PlainMethodReferenceConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND, constantPool, bufferReader );
	}

	@Deprecated @Override public PlainMethodReferenceConstant asPlainMethodReferenceConstant()
	{
		return this;
	}
}
