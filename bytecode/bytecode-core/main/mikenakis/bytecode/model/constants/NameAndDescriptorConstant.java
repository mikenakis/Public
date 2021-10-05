package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.reading.ConstantReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_NameAndType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NameAndDescriptorConstant extends Constant
{
	public static NameAndDescriptorConstant read( ConstantReader constantReader, int constantTag )
	{
		assert constantTag == tag_NameAndDescriptor;
		NameAndDescriptorConstant nameAndDescriptorConstant = new NameAndDescriptorConstant();
		constantReader.readIndexAndSetConstant( c -> nameAndDescriptorConstant.setNameConstant( c.asMutf8ValueConstant() ) );
		constantReader.readIndexAndSetConstant( c -> nameAndDescriptorConstant.setDescriptorConstant( c.asMutf8ValueConstant() ) );
		return nameAndDescriptorConstant;
	}

	public static NameAndDescriptorConstant of( FieldPrototype fieldPrototype )
	{
		return of( Mutf8ValueConstant.of( fieldPrototype.fieldName ), //
			Mutf8ValueConstant.of( ByteCodeHelpers.descriptorStringFromTypeDescriptor( fieldPrototype.descriptor.typeDescriptor ) ) );
	}

	public static NameAndDescriptorConstant of( MethodPrototype methodPrototype )
	{
		return of( Mutf8ValueConstant.of( methodPrototype.methodName ), //
			Mutf8ValueConstant.of( ByteCodeHelpers.descriptorStringFromMethodDescriptor( methodPrototype.descriptor ) ) );
	}

	public static NameAndDescriptorConstant of( Mutf8ValueConstant nameConstant, Mutf8ValueConstant descriptorConstant ) //TODO: remove
	{
		NameAndDescriptorConstant nameAndDescriptorConstant = new NameAndDescriptorConstant();
		nameAndDescriptorConstant.setNameConstant( nameConstant );
		nameAndDescriptorConstant.setDescriptorConstant( descriptorConstant );
		return nameAndDescriptorConstant;
	}

	private Mutf8ValueConstant nameConstant;
	private Mutf8ValueConstant descriptorConstant;

	public NameAndDescriptorConstant()
	{
		super( tag_NameAndDescriptor );
	}

	public Mutf8ValueConstant getNameConstant()
	{
		assert nameConstant != null;
		return nameConstant;
	}

	public void setNameConstant( Mutf8ValueConstant nameConstant )
	{
		assert this.nameConstant == null;
		assert nameConstant != null;
		this.nameConstant = nameConstant;
	}

	public Mutf8ValueConstant getDescriptorConstant()
	{
		assert descriptorConstant != null;
		return descriptorConstant;
	}

	public void setDescriptorConstant( Mutf8ValueConstant descriptorConstant )
	{
		assert this.descriptorConstant == null;
		assert descriptorConstant != null;
		this.descriptorConstant = descriptorConstant;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "name = " + nameConstant + ", descriptor = " + descriptorConstant; }
	@Deprecated @Override public NameAndDescriptorConstant asNameAndDescriptorConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof NameAndDescriptorConstant kin && equals( kin ); }
	@Override public int hashCode() { return Objects.hash( tag, nameConstant, descriptorConstant ); }
	public boolean equals( NameAndDescriptorConstant other ) { return nameConstant.equals( other.nameConstant ) && descriptorConstant.equals( other.descriptorConstant ); }

	@Override public void intern( Interner interner )
	{
		interner.intern( this );
		getNameConstant().intern( interner );
		getDescriptorConstant().intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( getNameConstant() ) );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( getDescriptorConstant() ) );
	}
}
