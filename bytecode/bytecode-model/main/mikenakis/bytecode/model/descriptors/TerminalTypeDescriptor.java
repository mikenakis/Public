package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.constants.ClassConstant;

import java.lang.constant.ClassDesc;

public class TerminalTypeDescriptor
{
	public static TerminalTypeDescriptor of( String typeName )
	{
		String descriptorString = "L" + typeName.replace('.', '/') + ";";
		ClassDesc classDesc = ClassDesc.ofDescriptor( descriptorString );
		return of( classDesc );
	}

	public static TerminalTypeDescriptor of( ClassDesc classDesc )
	{
		return new TerminalTypeDescriptor( classDesc );
	}

	public static TerminalTypeDescriptor of( ClassConstant classConstant )
	{
		return of( classConstant.classDescriptor() );
	}

	public final ClassDesc classDesc;

	private TerminalTypeDescriptor( ClassDesc classDesc )
	{
		this.classDesc = classDesc;
	}

	public String name()
	{
		return ByteCodeHelpers.typeNameFromClassDesc( classDesc );
	}
}
