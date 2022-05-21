package io.github.mikenakis.bytecode.model.constants;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.model.ByteCodeHelpers;
import io.github.mikenakis.bytecode.model.descriptors.FieldReference;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.java_type_model.FieldDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;

/**
 * Represents the JVMS::CONSTANT_Fieldref_info structure.
 *
 * @author michael.gr
 */
public final class FieldReferenceConstant extends ReferenceConstant
{
	public static FieldReferenceConstant read( BufferReader bufferReader, ReadingConstantPool constantPool, int constantTag )
	{
		assert constantTag == tag_FieldReference;
		FieldReferenceConstant fieldReferenceConstant = new FieldReferenceConstant();
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> fieldReferenceConstant.setDeclaringTypeConstant( c.asClassConstant() ) );
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> fieldReferenceConstant.setNameAndDescriptorConstant( c.asNameAndDescriptorConstant() ) );
		return fieldReferenceConstant;
	}

	public static FieldReferenceConstant of( FieldReference fieldReference )
	{
		FieldReferenceConstant fieldReferenceConstant = new FieldReferenceConstant();
		fieldReferenceConstant.setDeclaringTypeConstant( ClassConstant.of( fieldReference.declaringTypeDescriptor ) );
		fieldReferenceConstant.setNameAndDescriptorConstant( NameAndDescriptorConstant.of( fieldReference.fieldPrototype ) );
		return fieldReferenceConstant;
	}

	private FieldReferenceConstant()
	{
		super( tag_FieldReference );
	}

	// String Customer.name;
	//  (1)     (2)     (3)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: typeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public String fieldName() { return getNameAndDescriptorConstant().getNameConstant().stringValue(); }
	public FieldDescriptor fieldDescriptor() { return FieldDescriptor.of( ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( getNameAndDescriptorConstant().getDescriptorConstant() ) ); }
	public TypeDescriptor declaringTypeDescriptor() { return getDeclaringTypeConstant().typeDescriptor(); }
	@Deprecated @Override public FieldReferenceConstant asFieldReferenceConstant() { return this; }
}
