package mikenakis.bytecode.reading;

import mikenakis.bytecode.exceptions.InvalidConstantTagException;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;

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
final class ConstantPool
{
	private final List<Entry> entries;

	ConstantPool( BufferReader bufferReader )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		entries = new ArrayList<>( count );
		entries.add( null ); // first entry is empty. (Ancient legacy bollocks.)
		for( int index = 1; index < count; index++ )
		{
			int constantTag = bufferReader.readUnsignedByte();
			Buffer buffer = readConstantBuffer( constantTag, bufferReader );
			Entry entry = new Entry( constantTag, buffer );
			entries.add( entry );
			if( constantTag == Constant.tagLong || constantTag == Constant.tagDouble )
			{
				entries.add( null );
				index++; //8-byte constants occupy two entries. (Ancient legacy bollocks.)
			}
		}
		assert size() == count;
	}

	Constant readIndexAndGetConstant( BufferReader bufferReader )
	{
		int constantIndex = bufferReader.readUnsignedShort();
		return getConstant( constantIndex );
	}

	Optional<Constant> tryReadIndexAndGetConstant( BufferReader bufferReader )
	{
		int constantIndex = bufferReader.readUnsignedShort();
		if( constantIndex == 0 )
			return Optional.empty();
		return Optional.of( getConstant( constantIndex ) );
	}

	Constant getConstant( int constantIndex )
	{
		Entry entry = entries.get( constantIndex );
		entry.used = true;
		return entry.getConstant( this );
	}

	Collection<Constant> getExtraConstants()
	{
		Collection<Entry> extraEntries = new ArrayList<>();
		for( Entry entry : entries )
		{
			if( entry == null )
				continue;
			if( entry.used )
				continue;
			extraEntries.add( entry );
		}
		Collection<Constant> extraConstants = new ArrayList<>();
		for( Entry entry : extraEntries )
		{
			Constant constant = entry.getConstant( this );
			extraConstants.add( constant );
		}
		return extraConstants;
	}

	private int tryGetIndex( Constant constant )
	{
		if( constant == null )
			return 0;
		for( int i = 0; i < entries.size(); i++ )
		{
			Entry constantEntry = entries.get( i );
			if( constantEntry == null )
				continue;
			Constant existingConstant = constantEntry.getConstant( this );
			if( existingConstant.equals( constant ) )
				return i;
		}
		return -1;
	}

	public int getIndex( Constant constant )
	{
		int index = tryGetIndex( constant );
		assert index != -1;
		return index;
	}

	public int size()
	{
		return entries.size();
	}

	private final Function1<Optional<Constant>,Entry> converterAndFilterer = ( Entry constantEntry ) -> //
	{
		if( constantEntry == null )
			return Optional.empty();
		Constant constant = constantEntry.getConstant( this );
		return Optional.of( constant );
	};

	public Iterable<Constant> constants()
	{
		return Kit.iterable.convertedAndFiltered( entries, converterAndFilterer );
	}

	@Override public String toString()
	{
		return size() + " entries";
	}

	private static final class Entry
	{
		final int constantTag;
		Buffer buffer;
		Constant constant;
		boolean used;

		Entry( int constantTag, Buffer buffer )
		{
			this.constantTag = constantTag;
			this.buffer = buffer;
		}

		Entry( Constant constant )
		{
			constantTag = constant.tag;
			this.constant = constant;
		}

		Constant getConstant( ConstantPool constantPool )
		{
			if( constant != null )
				return constant;
			BufferReader bufferReader = BufferReader.of( buffer );
			constant = ByteCodeReader.readConstant( constantTag, constantPool, bufferReader );
			assert bufferReader.isAtEnd();
			return constant;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return buffer.length() + " bytes";
		}
	}

	private static Buffer readConstantBuffer( int constantTag, BufferReader bufferReader )
	{
		return switch( constantTag )
			{
				case Constant.tagClass, Constant.tagMethodType, Constant.tagString -> bufferReader.readBuffer( 2 );
				case Constant.tagMethodHandle -> bufferReader.readBuffer( 3 );
				case Constant.tagFieldReference, Constant.tagNameAndDescriptor, Constant.tagInteger, Constant.tagFloat, Constant.tagInvokeDynamic, //
					Constant.tagMethodReference, Constant.tagInterfaceMethodReference -> bufferReader.readBuffer( 4 );
				case Constant.tagDouble, Constant.tagLong -> bufferReader.readBuffer( 8 );
				case Constant.tagMutf8 -> readMutf8ConstantBuffer( bufferReader );
				default -> throw new InvalidConstantTagException( constantTag );
			};
	}

	private static Buffer readMutf8ConstantBuffer( BufferReader bufferReader )
	{
		int length = bufferReader.readUnsignedShort();
		return bufferReader.readBuffer( length );
	}
}
