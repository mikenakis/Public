package mikenakis.bytecode.reading;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.kit.Kit;
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
			int tagNumber = bufferReader.readUnsignedByte();
			Constant.Tag tag = Constant.Tag.fromNumber( tagNumber );
			Buffer buffer = readConstantBuffer( tag, bufferReader );
			Entry entry = new Entry( tag, buffer );
			entries.add( entry );
			if( tag == Constant.Tag.Long || tag == Constant.Tag.Double )
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
		final Constant.Tag tag;
		Buffer buffer;
		Constant constant;
		boolean used;

		Entry( Constant.Tag tag, Buffer buffer )
		{
			this.tag = tag;
			this.buffer = buffer;
		}

		Entry( Constant constant )
		{
			tag = constant.tag;
			this.constant = constant;
		}

		Constant getConstant( ConstantPool constantPool )
		{
			if( constant != null )
				return constant;
			BufferReader bufferReader = BufferReader.of( buffer );
			constant = ByteCodeReader.readConstant( tag, constantPool, bufferReader );
			assert bufferReader.isAtEnd();
			return constant;
		}

		@Override public String toString()
		{
			return buffer.length() + " bytes";
		}
	}

	private static Buffer readConstantBuffer( Constant.Tag tag, BufferReader bufferReader )
	{
		return switch( tag )
			{
				case Class, MethodType, String -> bufferReader.readBuffer( 2 );
				case MethodHandle -> bufferReader.readBuffer( 3 );
				case FieldReference, NameAndDescriptor, Integer, Float, InvokeDynamic, //
					MethodReference, InterfaceMethodReference -> bufferReader.readBuffer( 4 );
				case Double, Long -> bufferReader.readBuffer( 8 );
				case Mutf8 -> readMutf8ConstantBuffer( bufferReader );
				default -> throw new AssertionError();
			};
	}

	private static Buffer readMutf8ConstantBuffer( BufferReader bufferReader )
	{
		int length = bufferReader.readUnsignedShort();
		return bufferReader.readBuffer( length );
	}
}
