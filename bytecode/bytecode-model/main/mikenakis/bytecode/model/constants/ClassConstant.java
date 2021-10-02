package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.descriptors.ArrayTypeDescriptor;
import mikenakis.bytecode.model.descriptors.TerminalTypeDescriptor;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Class_info structure. See JVMS §4.4.1.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassConstant extends Constant
{
	public static ClassConstant of( TypeDescriptor typeDescriptor )
	{
		if( typeDescriptor.isArray() )
			return ofInternalNameOrDescriptorString( typeDescriptor.descriptorString() );
		if( typeDescriptor.isPrimitive() )
			return ofInternalNameOrDescriptorString( typeDescriptor.descriptorString() );
		assert typeDescriptor.isTerminal();
		return ofInternalNameOrDescriptorString( typeDescriptor.asTerminalTypeDescriptor().internalName() );
	}

	public static ClassConstant ofTypeName( String typeName )
	{
		String internalName = ByteCodeHelpers.internalFromBinary( typeName );
		return ofInternalNameOrDescriptorString( internalName );
	}

	public static ClassConstant ofInternalNameOrDescriptorString( String internalName )
	{
		ClassConstant classConstant = new ClassConstant();
		classConstant.setInternalNameOrDescriptorStringConstant( Mutf8Constant.of( internalName ) );
		return classConstant;
	}

	// According to §4.4.1 this is supposed to be a "binary class or interface name encoded in internal form."
	// PEARL: the term "binary name" here is a red herring, because binary names and internal names are different things.
	//        in class java.lang.constants.ConstantUtils there exist methods binaryToInternal() and internalToBinary().
	private Mutf8Constant internalNameOrDescriptorStringConstant;

	public ClassConstant()
	{
		super( tag_Class );
	}

	public Mutf8Constant getInternalNameOrDescriptorStringConstant()
	{
		assert internalNameOrDescriptorStringConstant != null;
		return internalNameOrDescriptorStringConstant;
	}

	public void setInternalNameOrDescriptorStringConstant( Mutf8Constant internalNameOrDescriptorStringConstant )
	{
		assert this.internalNameOrDescriptorStringConstant == null;
		assert internalNameOrDescriptorStringConstant != null;
		assert isValidInternalNameOrDescriptorString( internalNameOrDescriptorStringConstant.stringValue() );
		this.internalNameOrDescriptorStringConstant = internalNameOrDescriptorStringConstant;
	}

	// PEARL: the vast majority of ClassConstant instances will contain strings of the form `java/lang/String`, but you cannot rely on this to always be the
	//        case: every once in a while you will find a string of the form `Ljava/lang/String;`.
	//        There is a cryptic sentence further down in §4.4.1 which kind of points to this, by saying: "Because arrays are objects, the opcodes anewarray and
	//        multianewarray - but not the opcode new - can reference array "classes" via CONSTANT_Class_info structures in the constant_pool table. For such
	//        array classes, the name of the class is the descriptor of the array type (§4.3.2)." However, even if we concede any meaningfulness to this
	//        sentence, it still does not cover all cases. An array-descriptor-style class constant can be encountered in other contexts, for example an
	//        invokevirtual instruction has a method reference constant which has a class constant that specifies the declaring type, which is going to be an
	//        array type if `array.clone()` is being invoked.
	private static boolean isValidInternalNameOrDescriptorString( String name )
	{
		if( name.charAt( 0 ) == '[' )
			return ByteCodeHelpers.isValidDescriptorString( name );
		return ByteCodeHelpers.isValidInternalName( name );
	}

	public String descriptorString()
	{
		return ByteCodeHelpers.descriptorStringFromInternalName( internalNameOrDescriptorStringConstant.stringValue() );
	}

	public TerminalTypeDescriptor terminalTypeDescriptor()
	{
		String internalName = internalNameOrDescriptorStringConstant.stringValue();
		return TerminalTypeDescriptor.ofInternalName( internalName );
	}

	public TypeDescriptor typeDescriptor()
	{
		String internalNameOrDescriptorString = internalNameOrDescriptorStringConstant.stringValue();
		if( internalNameOrDescriptorString.charAt( 0 ) == '[' )
			return ArrayTypeDescriptor.ofDescriptorString( internalNameOrDescriptorString );
		return TerminalTypeDescriptor.ofInternalName( internalNameOrDescriptorString );
	}

	public ArrayTypeDescriptor arrayTypeDescriptor()
	{
		String descriptorString = internalNameOrDescriptorStringConstant.stringValue();
		assert descriptorString.charAt( 0 ) == '[';
		return ArrayTypeDescriptor.ofDescriptorString( descriptorString );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return internalNameOrDescriptorStringConstant == null? "(uninitialized)" : internalNameOrDescriptorStringConstant.stringValue();
	}

	@Deprecated @Override public ClassConstant asClassConstant() { return this; }

	@Override public boolean equals( Object other )
	{
		if( other instanceof ClassConstant otherClassConstant )
			return equalsClassConstant( otherClassConstant );
		return false;
	}

	public boolean equalsClassConstant( ClassConstant other )
	{
		return internalNameOrDescriptorStringConstant.equalsMutf8Constant( other.internalNameOrDescriptorStringConstant );
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, internalNameOrDescriptorStringConstant );
	}
}
