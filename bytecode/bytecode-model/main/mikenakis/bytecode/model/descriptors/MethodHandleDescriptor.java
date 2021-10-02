package mikenakis.bytecode.model.descriptors;

public class MethodHandleDescriptor
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
