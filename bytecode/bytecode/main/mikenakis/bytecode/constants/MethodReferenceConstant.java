package mikenakis.bytecode.constants;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.BufferReader;

/**
 * Base class for representing the JVMS::CONSTANT_Methodref_info and JVMS::CONSTANT_InterfaceMethodref_info structures.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class MethodReferenceConstant extends ReferenceConstant
{
	abstract static class Kind extends ReferenceConstant.Kind
	{
		Kind( int tag, String name )
		{
			super( tag, name );
			assert tag == PlainMethodReferenceConstant.TAG || tag == InterfaceMethodReferenceConstant.TAG;
		}
	}

	protected MethodReferenceConstant( Kind kind, ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( kind, typeConstant, nameAndTypeConstant );
	}

	protected MethodReferenceConstant( Kind kind, ConstantPool constantPool, BufferReader bufferReader )
	{
		super( constantPool, kind, bufferReader );
	}

	@Deprecated @Override public MethodReferenceConstant asMethodReferenceConstant()
	{
		return this;
	}
}
