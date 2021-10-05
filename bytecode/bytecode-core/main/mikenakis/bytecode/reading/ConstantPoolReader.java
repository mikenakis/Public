package mikenakis.bytecode.reading;

import mikenakis.bytecode.exceptions.InvalidConstantTagException;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.value.DoubleValueConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.value.FloatValueConstant;
import mikenakis.bytecode.model.constants.value.IntegerValueConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.value.LongValueConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.value.StringValueConstant;
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
		ConstantReader constantReader = new ConstantReader()
		{
			@Override public int readInt() { return bufferReader.readInt(); }
			@Override public int readUnsignedByte() { return bufferReader.readUnsignedByte(); }
			@Override public int readUnsignedShort() { return bufferReader.readUnsignedShort(); }
			@Override public float readFloat() { return bufferReader.readFloat(); }
			@Override public long readLong() { return bufferReader.readLong(); }
			@Override public double readDouble() { return bufferReader.readDouble(); }
			@Override public Buffer readBuffer( int count ) { return bufferReader.readBuffer( count ); }
			@Override public void readIndexAndSetConstant( Procedure1<Constant> setter ) { ConstantPoolReader.this.readIndexAndSetConstant( setter ); }
			@Override public void readIndexAndSetBootstrap( Procedure1<BootstrapMethod> setter ) { ConstantPoolReader.this.readIndexAndSetBootstrap( setter ); }
		};
		for( int index = 1; index < count; index++ )
		{
			int constantTag = bufferReader.readUnsignedByte();
			Constant constant = switch( constantTag )
				{
					case Constant.tag_Class -> ClassConstant.read( constantReader, constantTag );
					case Constant.tag_String -> StringValueConstant.read( constantReader, constantTag );
					case Constant.tag_MethodType -> MethodTypeConstant.read( constantReader, constantTag );
					case Constant.tag_FieldReference -> FieldReferenceConstant.read( constantReader, constantTag );
					case Constant.tag_InterfaceMethodReference, Constant.tag_PlainMethodReference -> MethodReferenceConstant.read( constantReader, constantTag );
					case Constant.tag_InvokeDynamic -> InvokeDynamicConstant.read( constantReader, constantTag );
					case Constant.tag_Double -> DoubleValueConstant.read( constantReader, constantTag );
					case Constant.tag_Float -> FloatValueConstant.read( constantReader, constantTag );
					case Constant.tag_Integer -> IntegerValueConstant.read( constantReader, constantTag );
					case Constant.tag_Long -> LongValueConstant.read( constantReader, constantTag );
					case Constant.tag_Mutf8 -> Mutf8ValueConstant.read( constantReader, constantTag );
					case Constant.tag_NameAndDescriptor -> NameAndDescriptorConstant.read( constantReader, constantTag );
					case Constant.tag_MethodHandle -> MethodHandleConstant.read( constantReader, constantTag );
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
