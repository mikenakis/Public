package mikenakis.bytecode.model.descriptors;

import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.TypeDescriptor;

//TODO: replace DirectMethodHandleDesc with this class.
public final class MethodHandleDescriptor
{
	public static MethodHandleDescriptor of( MethodDescriptor methodDescriptor, TypeDescriptor ownerTypeDescriptor )
	{
		return new MethodHandleDescriptor( methodDescriptor, ownerTypeDescriptor );
	}

	public final MethodDescriptor methodDescriptor;
	public final TypeDescriptor ownerTypeDescriptor;

	private MethodHandleDescriptor( MethodDescriptor methodDescriptor, TypeDescriptor ownerTypeDescriptor )
	{
		this.methodDescriptor = methodDescriptor;
		this.ownerTypeDescriptor = ownerTypeDescriptor;
	}
}
