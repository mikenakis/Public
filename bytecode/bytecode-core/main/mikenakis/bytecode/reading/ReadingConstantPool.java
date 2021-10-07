package mikenakis.bytecode.reading;

import mikenakis.bytecode.exceptions.InvalidConstantTagException;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.value.DoubleValueConstant;
import mikenakis.bytecode.model.constants.value.FloatValueConstant;
import mikenakis.bytecode.model.constants.value.IntegerValueConstant;
import mikenakis.bytecode.model.constants.value.LongValueConstant;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.model.constants.value.StringValueConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Represents the constant pool of a java class file.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 */
public final class ReadingConstantPool
{
	public static ReadingConstantPool read( BufferReader bufferReader )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<Constant> constants = new ArrayList<>( count );
		constants.add( null ); // first entry is empty. (Ancient legacy bollocks.)
		boolean[] used = new boolean[count];
		ReadingConstantPool readingConstantPool = new ReadingConstantPool( constants, used );
		for( int index = 1; index < count; index++ )
		{
			int constantTag = bufferReader.readUnsignedByte();
			Constant constant = switch( constantTag )
				{
					case Constant.tag_Class -> ClassConstant.read( bufferReader, readingConstantPool, constantTag );
					case Constant.tag_String -> StringValueConstant.read( bufferReader, readingConstantPool, constantTag );
					case Constant.tag_MethodType -> MethodTypeConstant.read( bufferReader, readingConstantPool, constantTag );
					case Constant.tag_FieldReference -> FieldReferenceConstant.read( bufferReader, readingConstantPool, constantTag );
					case Constant.tag_InterfaceMethodReference, Constant.tag_PlainMethodReference -> MethodReferenceConstant.read( bufferReader, readingConstantPool, constantTag );
					case Constant.tag_InvokeDynamic -> InvokeDynamicConstant.read( bufferReader, readingConstantPool, constantTag );
					case Constant.tag_Double -> DoubleValueConstant.read( bufferReader, constantTag );
					case Constant.tag_Float -> FloatValueConstant.read( bufferReader, constantTag );
					case Constant.tag_Integer -> IntegerValueConstant.read( bufferReader, constantTag );
					case Constant.tag_Long -> LongValueConstant.read( bufferReader, constantTag );
					case Constant.tag_Mutf8 -> Mutf8ValueConstant.read( bufferReader, constantTag );
					case Constant.tag_NameAndDescriptor -> NameAndDescriptorConstant.read( bufferReader, readingConstantPool, constantTag );
					case Constant.tag_MethodHandle -> MethodHandleConstant.read( bufferReader, readingConstantPool, constantTag );
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
		readingConstantPool.runConstantFixUps();
		return readingConstantPool;
	}

	private final List<Constant> constants;
	private final boolean[] used;
	private final Collection<Procedure0> constantFixUps = new ArrayList<>();
	private final Collection<Procedure1<BootstrapMethodsAttribute>> bootstrapFixUps = new ArrayList<>();

	private ReadingConstantPool( List<Constant> constants, boolean[] used )
	{
		this.constants = constants;
		this.used = used;
	}

	public void setConstant( int constantIndex, Procedure1<Constant> setter )
	{
		if( constantIndex < constants.size() )
		{
			Constant constant = getConstant( constantIndex );
			setter.invoke( constant );
		}
		else
			constantFixUps.add( () -> setConstant( constantIndex, setter ) );
	}

	public Constant getConstant( int constantIndex )
	{
		assert constantIndex != 0;
		used[constantIndex] = true;
		return constants.get( constantIndex );
	}

	public void setBootstrap( int bootstrapIndex, Procedure1<BootstrapMethod> setter )
	{
		bootstrapFixUps.add( bootstrapMethodsAttribute -> setBootstrap( bootstrapMethodsAttribute, bootstrapIndex, setter ) );
	}

	private static void setBootstrap( BootstrapMethodsAttribute bootstrapMethodsAttribute, int bootstrapIndex, Procedure1<BootstrapMethod> setter )
	{
		BootstrapMethod bootstrapMethod = bootstrapMethodsAttribute.getBootstrapMethodByIndex( bootstrapIndex );
		setter.invoke( bootstrapMethod );
	}

	public void runBootstrapFixUps( BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		for( var bootstrapFixUp : bootstrapFixUps )
			bootstrapFixUp.invoke( bootstrapMethodsAttribute );
		bootstrapFixUps.clear();
	}

	public Optional<Constant> tryGetConstant( int constantIndex )
	{
		if( constantIndex == 0 )
			return Optional.empty();
		return Optional.of( getConstant( constantIndex ) );
	}

	public Collection<ClassConstant> getExtraClassReferences()
	{
		Collection<ClassConstant> extraClassReferences = new ArrayList<>();
		for( int i = 0;  i < constants.size();  i++ )
		{
			Constant constant = constants.get( i );
			if( constant == null )
				continue;
			if( used[i] )
				continue;
			assert constant.tag == Constant.tag_Class;
			extraClassReferences.add( constant.asClassConstant() );
		}
		return extraClassReferences;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return constants.size() + " constants";
	}

	private void runConstantFixUps()
	{
		for( Procedure0 constantFixUp : constantFixUps )
			constantFixUp.invoke();
		constantFixUps.clear();
	}
}
