package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.java_type_model.FieldDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.kit.collections.FlagSet;

import java.util.Map;

/**
 * Represents a field.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeField extends ByteCodeMember
{
	public static ByteCodeField of( FlagSet<Modifier> modifiers, String name, FieldDescriptor fieldDescriptor )
	{
		String descriptorString = ByteCodeHelpers.descriptorStringFromTypeDescriptor( fieldDescriptor.typeDescriptor );
		return of( modifiers, Mutf8Constant.of( name ), Mutf8Constant.of( descriptorString ), AttributeSet.of() );
	}

	public static ByteCodeField of( FlagSet<Modifier> modifiers, Mutf8Constant nameConstant, Mutf8Constant descriptorConstant, AttributeSet attributeSet )
	{
		return new ByteCodeField( modifiers, nameConstant, descriptorConstant, attributeSet );
	}

	public enum Modifier
	{
		Public, Private, Protected, Static, Final, Volatile, Transient, Synthetic, Enum
	}

	public static final FlagEnum<Modifier> modifierEnum = FlagEnum.of( Modifier.class, //
		Map.entry( Modifier.Public    /**/, 0x0001 ),   // ACC_PUBLIC    = 0x0001;
		Map.entry( Modifier.Private   /**/, 0x0002 ),   // ACC_PRIVATE   = 0x0002;
		Map.entry( Modifier.Protected /**/, 0x0004 ),   // ACC_PROTECTED = 0x0004;
		Map.entry( Modifier.Static    /**/, 0x0008 ),   // ACC_STATIC    = 0x0008;
		Map.entry( Modifier.Final     /**/, 0x0010 ),   // ACC_FINAL     = 0x0010;
		Map.entry( Modifier.Volatile  /**/, 0x0040 ),   // ACC_VOLATILE  = 0x0040;
		Map.entry( Modifier.Transient /**/, 0x0080 ),   // ACC_TRANSIENT = 0x0080;
		Map.entry( Modifier.Synthetic /**/, 0x1000 ),   // ACC_SYNTHETIC = 0x1000;
		Map.entry( Modifier.Enum      /**/, 0x4000 ) ); // ACC_ENUM      = 0x4000;

	public final FlagSet<Modifier> modifiers;
	public final Mutf8Constant descriptorConstant;

	private ByteCodeField( FlagSet<Modifier> modifiers, Mutf8Constant nameConstant, Mutf8Constant descriptorConstant, AttributeSet attributeSet )
	{
		super( nameConstant, attributeSet );
		this.modifiers = modifiers;
		this.descriptorConstant = descriptorConstant;
	}

	public FieldDescriptor descriptor() {
		return FieldDescriptor.of( ByteCodeHelpers.typeDescriptorFromDescriptorString( descriptorConstant.stringValue() ) ); }
	public FieldPrototype prototype() { return FieldPrototype.of( name(), descriptor() ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "accessFlags = " + modifiers + ", name = " + nameConstant + ", descriptor = " + descriptorConstant;
	}
}
