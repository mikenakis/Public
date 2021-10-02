package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.descriptors.MethodDescriptor;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;

/**
 * Represents the JVMS::CONSTANT_Methodref_info and JVMS::CONSTANT_InterfaceMethodref_info structures.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodReferenceConstant extends ReferenceConstant
{
	public static MethodReferenceConstant plainOf( ClassConstant declaringTypeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		return of( tag_PlainMethodReference, declaringTypeConstant, nameAndDescriptorConstant );
	}

	public static MethodReferenceConstant interfaceOf( ClassConstant declaringTypeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		return of( tag_InterfaceMethodReference, declaringTypeConstant, nameAndDescriptorConstant );
	}

	public static MethodReferenceConstant of( int tag, ClassConstant declaringTypeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		MethodReferenceConstant plainMethodReferenceConstant = of( tag );
		plainMethodReferenceConstant.setDeclaringTypeConstant( declaringTypeConstant );
		plainMethodReferenceConstant.setNameAndDescriptorConstant( nameAndDescriptorConstant );
		return plainMethodReferenceConstant;
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

	// String Customer.name( arguments );
	//  (1)     (2)     (3)     (1)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: declaringTypeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public MethodReference methodReference()
	{
		return MethodReference.of( methodReferenceKind(), //
			getDeclaringTypeConstant().typeDescriptor(), //
			MethodPrototype.of( getNameAndDescriptorConstant().getNameConstant().stringValue(), //
				MethodDescriptor.ofDescriptorString( getNameAndDescriptorConstant().getDescriptorConstant().stringValue() ) ) );
	}

	private MethodReference.Kind methodReferenceKind()
	{
		return switch( tag )
			{
				case tag_PlainMethodReference -> MethodReference.Kind.Plain;
				case tag_InterfaceMethodReference -> MethodReference.Kind.Interface;
				default -> throw new AssertionError( tag );
			};
	}

	@Deprecated @Override public MethodReferenceConstant asMethodReferenceConstant() { return this; }
}
