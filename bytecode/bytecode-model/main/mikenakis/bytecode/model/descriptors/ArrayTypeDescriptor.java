package mikenakis.bytecode.model.descriptors;

public class ArrayTypeDescriptor extends TypeDescriptor
{
	public static ArrayTypeDescriptor ofArrayClass( Class<?> arrayClass )
	{
		assert arrayClass.isArray();
		return ofComponentClass( arrayClass.componentType() );
	}

	public static ArrayTypeDescriptor ofComponentClass( Class<?> componentClass )
	{
		TypeDescriptor componentTypeDescriptor = TypeDescriptor.of( componentClass );
		return new ArrayTypeDescriptor( componentTypeDescriptor );
	}

	public static ArrayTypeDescriptor of( TypeDescriptor componentTypeDescriptor )
	{
		return new ArrayTypeDescriptor( componentTypeDescriptor );
	}

	public final TypeDescriptor componentTypeDescriptor;

	private ArrayTypeDescriptor( TypeDescriptor componentTypeDescriptor )
	{
		this.componentTypeDescriptor = componentTypeDescriptor;
	}

	@Override public String name()
	{
		return componentTypeDescriptor.name() + "[]";
	}

	public boolean equalsTypeDesc( java.lang.invoke.TypeDescriptor javaTypeDescriptor )
	{
		assert false; //todo
		return false;
	}
}
