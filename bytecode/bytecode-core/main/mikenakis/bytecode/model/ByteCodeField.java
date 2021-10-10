package mikenakis.bytecode.model;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.java_type_model.FieldDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.collections.FlagEnum;
import mikenakis.kit.collections.FlagSet;

import java.util.Map;
import java.util.Optional;

/**
 * Represents a field.
 *
 * @author michael.gr
 */
public final class ByteCodeField extends ByteCodeMember
{
	public static ByteCodeField read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		FlagSet<Modifier> fieldModifiers = ByteCodeField.modifierEnum.fromBits( bufferReader.readUnsignedShort() );
		Mutf8ValueConstant nameConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		Mutf8ValueConstant descriptorConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		AttributeSet attributes = AttributeSet.read( bufferReader, constantPool, Optional.empty() );
		return of( fieldModifiers, nameConstant, descriptorConstant, attributes );
	}

	public static ByteCodeField of( FlagSet<Modifier> modifiers, FieldPrototype fieldPrototype )
	{
		String descriptorString = ByteCodeHelpers.descriptorStringFromTypeDescriptor( fieldPrototype.descriptor.typeDescriptor );
		return of( modifiers, Mutf8ValueConstant.of( fieldPrototype.fieldName ), Mutf8ValueConstant.of( descriptorString ), AttributeSet.of() );
	}

	public static ByteCodeField of( FlagSet<Modifier> modifiers, Mutf8ValueConstant fieldNameConstant, Mutf8ValueConstant fieldDescriptorStringConstant, AttributeSet attributeSet )
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
	private final Mutf8ValueConstant fieldNameConstant;
	private final Mutf8ValueConstant fieldDescriptorStringConstant;
	public final AttributeSet attributeSet;

	private ByteCodeField( FlagSet<Modifier> modifiers, Mutf8ValueConstant fieldNameConstant, Mutf8ValueConstant fieldDescriptorStringConstant, AttributeSet attributeSet )
	{
		this.modifiers = modifiers;
		this.fieldNameConstant = fieldNameConstant;
		this.fieldDescriptorStringConstant = fieldDescriptorStringConstant;
		this.attributeSet = attributeSet;
	}

	@Override public String name() { return fieldNameConstant.stringValue(); }
	public FieldDescriptor descriptor() { return FieldDescriptor.of( ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( fieldDescriptorStringConstant ) ); }
	public FieldPrototype prototype() { return FieldPrototype.of( name(), descriptor() ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "accessFlags = " + modifiers + ", name = " + fieldNameConstant + ", descriptor = " + fieldDescriptorStringConstant; }

	public void intern( Interner interner )
	{
		fieldNameConstant.intern( interner );
		attributeSet.intern( interner );
		fieldDescriptorStringConstant.intern( interner );
	}

	public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( modifiers.getBits() );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( fieldNameConstant ) );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( fieldDescriptorStringConstant ) );
		attributeSet.write( bufferWriter, constantPool,  locationMap );
	}
}
