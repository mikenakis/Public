package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import mikenakis.bytecode.reading.ReadingConstantPool;

/**
 * Represents the JVMS::CONSTANT_Methodref_info and JVMS::CONSTANT_InterfaceMethodref_info structures.
 *
 * @author michael.gr
 */
public final class MethodReferenceConstant extends ReferenceConstant
{
	public static MethodReferenceConstant read( BufferReader bufferReader, ReadingConstantPool constantPool, int tag )
	{
		MethodReferenceConstant interfaceMethodReferenceConstant = of( tag );
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> interfaceMethodReferenceConstant.setDeclaringTypeConstant( c.asClassConstant() ) );
		constantPool.setConstant( bufferReader.readUnsignedShort(), c -> interfaceMethodReferenceConstant.setNameAndDescriptorConstant( c.asNameAndDescriptorConstant() ) );
		return interfaceMethodReferenceConstant;
	}

	public static MethodReferenceConstant of( MethodReference methodReference )
	{
		ClassConstant declaringTypeConstant = ClassConstant.of( methodReference.declaringTypeDescriptor );
		NameAndDescriptorConstant nameAndDescriptorConstant = NameAndDescriptorConstant.of( methodReference.methodPrototype );
		MethodReferenceConstant methodReferenceConstant = of( methodReference.kind, declaringTypeConstant, nameAndDescriptorConstant );
		return methodReferenceConstant;
	}

	public static MethodReferenceConstant of( MethodReferenceKind kind, ClassConstant declaringTypeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		MethodReferenceConstant methodReferenceConstant = of( kind );
		methodReferenceConstant.setDeclaringTypeConstant( declaringTypeConstant );
		methodReferenceConstant.setNameAndDescriptorConstant( nameAndDescriptorConstant );
		return methodReferenceConstant;
	}

	public static MethodReferenceConstant of( MethodReferenceKind kind )
	{
		return new MethodReferenceConstant( tagFromMethodReferenceKind( kind ) );
	}

	public static MethodReferenceConstant of( int tag )
	{
		return new MethodReferenceConstant( tag );
	}

	private MethodReferenceConstant( int tag )
	{
		super( tag );
		assert tag == tag_PlainMethodReference || tag == tag_InterfaceMethodReference;
	}

	private static int tagFromMethodReferenceKind( MethodReferenceKind kind )
	{
		return switch( kind )
		{
			case Plain -> tag_PlainMethodReference;
			case Interface -> tag_InterfaceMethodReference;
		};
	}

	private static MethodReferenceKind methodReferenceKindFromTag( int tag )
	{
		return switch( tag )
		{
			case tag_PlainMethodReference -> MethodReferenceKind.Plain;
			case tag_InterfaceMethodReference -> MethodReferenceKind.Interface;
			default -> throw new AssertionError( tag );
		};
	}

	// String Customer.name( arguments );
	//  (1)     (2)     (3)     (1)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: declaringTypeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public MethodReference methodReference()
	{
		return MethodReference.of( methodReferenceKind(), getDeclaringTypeConstant().typeDescriptor(), //
			ByteCodeHelpers.methodPrototypeFromNameAndDescriptorConstant( getNameAndDescriptorConstant() ) );
	}

	public MethodReferenceKind methodReferenceKind()
	{
		return methodReferenceKindFromTag( tag );
	}

	@Deprecated @Override public MethodReferenceConstant asMethodReferenceConstant() { return this; }
}
