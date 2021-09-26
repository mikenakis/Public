package mikenakis.bytecode.reading;

import mikenakis.bytecode.exceptions.InvalidConstantTagException;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the constant pool of a java class file.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 */
final class ConstantPoolReader
{
	static ConstantPool read( BufferReader bufferReader )
	{
		ConstantPoolReader constantPoolReader = new ConstantPoolReader( bufferReader );
		return new ConstantPool( constantPoolReader.constants, constantPoolReader.used, constantPoolReader.bootstrapFixUps );
	}

	private final BufferReader bufferReader;
	private final List<Constant> constants;
	private final boolean[] used;
	private final Collection<Procedure0> constantFixUps = new ArrayList<>();
	private final Collection<Procedure1<BootstrapMethodsAttribute>> bootstrapFixUps = new ArrayList<>();

	private ConstantPoolReader( BufferReader bufferReader )
	{
		this.bufferReader = bufferReader;
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		constants = new ArrayList<>( count );
		constants.add( null ); // first entry is empty. (Ancient legacy bollocks.)
		used = new boolean[count];
		for( int index = 1; index < count; index++ )
		{
			int constantTag = bufferReader.readUnsignedByte();
			Constant constant = switch( constantTag )
				{
					case Constant.tag_Class -> readClassConstant();
					case Constant.tag_String -> readStringConstant();
					case Constant.tag_MethodType -> readMethodTypeConstant();
					case Constant.tag_FieldReference -> readFieldReferenceConstant();
					case Constant.tag_InterfaceMethodReference -> readInterfaceMethodReferenceConstant();
					case Constant.tag_MethodReference -> readPlainMethodReferenceConstant();
					case Constant.tag_InvokeDynamic -> readInvokeDynamicConstant();
					case Constant.tag_Double -> readDoubleConstant();
					case Constant.tag_Float -> readFloatConstant();
					case Constant.tag_Integer -> readIntegerConstant();
					case Constant.tag_Long -> readLongConstant();
					case Constant.tag_Mutf8 -> readMutf8Constant();
					case Constant.tag_NameAndDescriptor -> readNameAndDescriptorConstant();
					case Constant.tag_MethodHandle -> readMethodHandleConstant();
					default -> throw new InvalidConstantTagException( constantTag );
				};
			constants.add( constant );
			if( constantTag == Constant.tag_Long || constantTag == Constant.tag_Double )
			{
				constants.add( null ); //8-byte constants occupy two entries. (Ancient legacy bollocks.)
				index++;
			}
		}
		assert constants.size() == count;
		for( Procedure0 constantFixUp : constantFixUps )
			constantFixUp.invoke();
		constantFixUps.clear();
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return constants.size() + " constants";
	}

	private MethodHandleConstant readMethodHandleConstant()
	{
		int referenceKindNumber = bufferReader.readUnsignedByte();
		MethodHandleConstant.ReferenceKind referenceKind = MethodHandleConstant.ReferenceKind.tryFromNumber( referenceKindNumber ).orElseThrow();
		MethodHandleConstant methodHandleConstant = new MethodHandleConstant( referenceKind );
		readIndexAndSetConstant( c -> methodHandleConstant.setReferenceConstant( c.asReferenceConstant() ) );
		return methodHandleConstant;
	}

	private NameAndDescriptorConstant readNameAndDescriptorConstant()
	{
		NameAndDescriptorConstant nameAndDescriptorConstant = new NameAndDescriptorConstant();
		readIndexAndSetConstant( c -> nameAndDescriptorConstant.setNameConstant( c.asMutf8Constant() ) );
		readIndexAndSetConstant( c -> nameAndDescriptorConstant.setDescriptorConstant( c.asMutf8Constant() ) );
		return nameAndDescriptorConstant;
	}

	private Mutf8Constant readMutf8Constant()
	{
		int length = bufferReader.readUnsignedShort();
		Buffer buffer = bufferReader.readBuffer( length );
		return Mutf8Constant.of( buffer );
	}

	private LongConstant readLongConstant()
	{
		long value = bufferReader.readLong();
		return LongConstant.of( value );
	}

	private IntegerConstant readIntegerConstant()
	{
		int value = bufferReader.readInt();
		return IntegerConstant.of( value );
	}

	private FloatConstant readFloatConstant()
	{
		float value = bufferReader.readFloat();
		return FloatConstant.of( value );
	}

	private DoubleConstant readDoubleConstant()
	{
		double value = bufferReader.readDouble();
		return DoubleConstant.of( value );
	}

	private InvokeDynamicConstant readInvokeDynamicConstant()
	{
		InvokeDynamicConstant invokeDynamicConstant = new InvokeDynamicConstant();
		readIndexAndSetBootstrap( b -> invokeDynamicConstant.setBootstrapMethod( b ) );
		readIndexAndSetConstant( c -> invokeDynamicConstant.setNameAndDescriptorConstant( c.asNameAndDescriptorConstant() ) );
		return invokeDynamicConstant;
	}

	private PlainMethodReferenceConstant readPlainMethodReferenceConstant()
	{
		PlainMethodReferenceConstant plainMethodReferenceConstant = new PlainMethodReferenceConstant();
		readIndexAndSetConstant( c -> plainMethodReferenceConstant.setDeclaringTypeConstant( c.asClassConstant() ) );
		readIndexAndSetConstant( c -> plainMethodReferenceConstant.setNameAndDescriptorConstant( c.asNameAndDescriptorConstant() ) );
		return plainMethodReferenceConstant;
	}

	private InterfaceMethodReferenceConstant readInterfaceMethodReferenceConstant()
	{
		InterfaceMethodReferenceConstant interfaceMethodReferenceConstant = new InterfaceMethodReferenceConstant();
		readIndexAndSetConstant( c -> interfaceMethodReferenceConstant.setDeclaringTypeConstant( c.asClassConstant() ) );
		readIndexAndSetConstant( c -> interfaceMethodReferenceConstant.setNameAndDescriptorConstant( c.asNameAndDescriptorConstant() ) );
		return interfaceMethodReferenceConstant;
	}

	private FieldReferenceConstant readFieldReferenceConstant()
	{
		FieldReferenceConstant fieldReferenceConstant = new FieldReferenceConstant();
		readIndexAndSetConstant( c -> fieldReferenceConstant.setDeclaringTypeConstant( c.asClassConstant() ) );
		readIndexAndSetConstant( c -> fieldReferenceConstant.setNameAndDescriptorConstant( c.asNameAndDescriptorConstant() ) );
		return fieldReferenceConstant;
	}

	private MethodTypeConstant readMethodTypeConstant()
	{
		MethodTypeConstant methodTypeConstant = new MethodTypeConstant();
		readIndexAndSetConstant( c -> methodTypeConstant.setDescriptorConstant( c.asMutf8Constant() ) );
		return methodTypeConstant;
	}

	private StringConstant readStringConstant()
	{
		StringConstant stringConstant = new StringConstant();
		readIndexAndSetConstant( c -> stringConstant.setValueConstant( c.asMutf8Constant() ) );
		return stringConstant;
	}

	private ClassConstant readClassConstant()
	{
		ClassConstant classConstant = new ClassConstant();
		readIndexAndSetConstant( c -> classConstant.setNameConstant( c.asMutf8Constant() ) );
		return classConstant;
	}

	private void readIndexAndSetConstant( Procedure1<Constant> setter )
	{
		int constantIndex = bufferReader.readUnsignedShort();
		if( constantIndex < constants.size() )
			setConstant( constantIndex, setter );
		else
			constantFixUps.add( () -> setConstant( constantIndex, setter ) );
	}

	private void setConstant( int constantIndex, Procedure1<Constant> setter )
	{
		Constant constant = getConstant( constantIndex );
		setter.invoke( constant );
	}

	private Constant getConstant( int constantIndex )
	{
		used[constantIndex] = true;
		return constants.get( constantIndex );
	}

	private void readIndexAndSetBootstrap( Procedure1<BootstrapMethod> setter )
	{
		int bootstrapIndex = bufferReader.readUnsignedShort();
		bootstrapFixUps.add( bootstrapMethodsAttribute -> setBootstrap( bootstrapMethodsAttribute, bootstrapIndex, setter ) );
	}

	private static void setBootstrap( BootstrapMethodsAttribute bootstrapMethodsAttribute, int bootstrapIndex, Procedure1<BootstrapMethod> setter )
	{
		BootstrapMethod bootstrapMethod = bootstrapMethodsAttribute.getBootstrapMethodByIndex( bootstrapIndex );
		setter.invoke( bootstrapMethod );
	}
}
