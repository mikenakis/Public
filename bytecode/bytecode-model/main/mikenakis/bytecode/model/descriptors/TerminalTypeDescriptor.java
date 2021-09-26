package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;

import java.lang.constant.ClassDesc;

public class TerminalTypeDescriptor extends TypeDescriptor
{
	public static TerminalTypeDescriptor of( Class<?> javaClass )
	{
		assert !javaClass.isArray();
		return new TerminalTypeDescriptor( javaClass.describeConstable().orElseThrow() );
	}

	public static TerminalTypeDescriptor of( String typeName )
	{
		String descriptorString = "L" + typeName.replace('.', '/') + ";";
		ClassDesc classDesc = ClassDesc.ofDescriptor( descriptorString );
		return new TerminalTypeDescriptor( classDesc );
	}

	public static TerminalTypeDescriptor of( ClassConstant classConstant )
	{
		return new TerminalTypeDescriptor( classConstant.classDesc() );
	}

	private final ClassDesc classDesc;

	private TerminalTypeDescriptor( ClassDesc classDesc )
	{
		this.classDesc = classDesc;
	}

	public ClassConstant classConstant()
	{
		ClassConstant classConstant = new ClassConstant();
		classConstant.setNameConstant( Mutf8Constant.of( classDesc.descriptorString() ) );
		return classConstant;
	}

	@Override public String name()
	{
		return ByteCodeHelpers.typeNameFromClassDesc( classDesc );
	}

	public boolean equalsClassDesc( ClassDesc classDesc )
	{
		return this.classDesc.equals( classDesc );
	}
}
