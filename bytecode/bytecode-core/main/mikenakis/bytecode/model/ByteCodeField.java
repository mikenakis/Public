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
	public static ByteCodeField of( FlagSet<Modifier> modifiers, FieldPrototype fieldPrototype )
	{
		String descriptorString = ByteCodeHelpers.descriptorStringFromTypeDescriptor( fieldPrototype.descriptor.typeDescriptor );
		return of( modifiers, Mutf8Constant.of( fieldPrototype.fieldName ), Mutf8Constant.of( descriptorString ), AttributeSet.of() );
	}

	public static ByteCodeField of( FlagSet<Modifier> modifiers, Mutf8Constant fieldNameConstant, Mutf8Constant fieldDescriptorStringConstant, AttributeSet attributeSet )
	{
		return new ByteCodeField( modifiers, fieldNameConstant, fieldDescriptorStringConstant, attributeSet );
	}

	public enum Modifier
	{
		Public, Private, Protected, Static, Final, Volatile, Transient, Synthetic, Enum
	}

	public static final FlagEnum<Modifier> modifierEnum = FlagEnum.of( Modifier.class, //
		Map.entry( Modifier.Public    /**/, 0x0001 ),   // ACC_PUBLIC    -- Declared public; may be accessed from outside its package.
		Map.entry( Modifier.Private   /**/, 0x0002 ),   // ACC_PRIVATE   -- Declared private; usable only within the defining class.
		Map.entry( Modifier.Protected /**/, 0x0004 ),   // ACC_PROTECTED -- Declared protected; may be accessed within subclasses.
		Map.entry( Modifier.Static    /**/, 0x0008 ),   // ACC_STATIC    -- Declared static.
		Map.entry( Modifier.Final     /**/, 0x0010 ),   // ACC_FINAL     -- Declared final; never directly assigned to after object construction (JLS §17.5).
		Map.entry( Modifier.Volatile  /**/, 0x0040 ),   // ACC_VOLATILE  -- Declared volatile; cannot be cached.
		Map.entry( Modifier.Transient /**/, 0x0080 ),   // ACC_TRANSIENT -- Declared transient; not written or read by a persistent object manager.
		Map.entry( Modifier.Synthetic /**/, 0x1000 ),   // ACC_SYNTHETIC -- Declared synthetic; not present in the source code.
		Map.entry( Modifier.Enum      /**/, 0x4000 ) ); // ACC_ENUM      -- Declared as an element of an enum.

	public final FlagSet<Modifier> modifiers;
	public final Mutf8Constant fieldDescriptorStringConstant;

	private ByteCodeField( FlagSet<Modifier> modifiers, Mutf8Constant fieldNameConstant, Mutf8Constant fieldDescriptorStringConstant, AttributeSet attributeSet )
	{
		super( fieldNameConstant, attributeSet );
		this.modifiers = modifiers;
		this.fieldDescriptorStringConstant = fieldDescriptorStringConstant;
	}

	public FieldDescriptor descriptor() { return FieldDescriptor.of( ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( fieldDescriptorStringConstant ) ); }
	public FieldPrototype prototype() { return FieldPrototype.of( name(), descriptor() ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "accessFlags = " + modifiers + ", name = " + memberNameConstant + ", descriptor = " + fieldDescriptorStringConstant;
	}
}
