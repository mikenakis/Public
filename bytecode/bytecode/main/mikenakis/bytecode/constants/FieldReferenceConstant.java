package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;

/**
 * Represents the JVMS::CONSTANT_Fieldref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FieldReferenceConstant extends ReferenceConstant
{
	public static final int TAG = 9; // JVMS::CONSTANT_Fieldref_info

	static class Kind extends ReferenceConstant.Kind
	{
		Kind( int tag, String name )
		{
			super( tag, name );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new FieldReferenceConstant( constantPool, bufferReader );
		}
	}

	public static final Kind KIND = new Kind( TAG, "FieldReference" );

	public FieldReferenceConstant( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( KIND, typeConstant, nameAndTypeConstant );
	}

	private FieldReferenceConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( constantPool, KIND, bufferReader );
	}

	@Deprecated @Override public FieldReferenceConstant asFieldReferenceConstant()
	{
		return this;
	}
}
