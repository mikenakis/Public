package mikenakis.bytecode;

import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.ObservableMap;
import mikenakis.bytecode.kit.Printable;
import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A collection of {@link Attribute}s.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Attributes extends Printable implements Iterable<Attribute>
{
	public interface Factory
	{
		Attribute newAttribute( String attributeName, BufferReader bufferReader );
	}

	private final Map<String,AttributeBuffer> attributeBuffersFromNames;
	private final Runnable observer;

	public Attributes( Runnable observer )
	{
		this.observer = observer;
		attributeBuffersFromNames = newMap( 0 );
	}

	public Attributes( Runnable observer, ConstantPool constantPool, Factory attributeFactory, BufferReader bufferReader )
	{
		this.observer = observer;
		int count = bufferReader.readUnsignedShort();
		attributeBuffersFromNames = newMap( count );
		for( int i = 0; i < count; i++ )
		{
			Utf8Constant nameConstant = constantPool.readIndexAndGetConstant( bufferReader ).asUtf8Constant();
			String attributeName = nameConstant.getStringValue();
			AttributeBuffer attributeBuffer = new AttributeBuffer( attributeName, attributeFactory, bufferReader );
			Kit.map.add( attributeBuffersFromNames, attributeName, attributeBuffer );
		}
	}

	public void intern( ConstantPool constantPool )
	{
		for( AttributeBuffer attributeBuffer : attributeBuffersFromNames.values() )
			attributeBuffer.intern( constantPool );
	}

	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( attributeBuffersFromNames.size() );
		for( AttributeBuffer attributeBuffer : attributeBuffersFromNames.values() )
			attributeBuffer.write( constantPool, bufferWriter );
	}

	public Collection<String> getAttributeNames()
	{
		return attributeBuffersFromNames.keySet();
	}

	public Optional<Attribute> tryGetAttributeByName( String attributeName )
	{
		return Optional.ofNullable( Kit.map.tryGet( attributeBuffersFromNames, attributeName ) ).map( b -> b.getAttribute() );
	}

	public void addAttribute( Attribute attribute )
	{
		AttributeBuffer attributeBuffer = new AttributeBuffer( attribute );
		Kit.map.add( attributeBuffersFromNames, attribute.name, attributeBuffer );
	}

	public void addOrReplaceAttribute( Attribute attribute )
	{
		AttributeBuffer attributeBuffer = new AttributeBuffer( attribute );
		Kit.map.addOrReplace( attributeBuffersFromNames, attribute.name, attributeBuffer );
	}

	public void replaceAttribute( Attribute attribute )
	{
		AttributeBuffer attributeBuffer = new AttributeBuffer( attribute );
		Kit.map.replace( attributeBuffersFromNames, attribute.name, attributeBuffer );
	}

	public void removeAttribute( Attribute attribute )
	{
		Kit.map.remove( attributeBuffersFromNames, attribute.name );
	}

	public void removeAttributeByNameIfPresent( String attributeName )
	{
		Kit.map.removeIfPresent( attributeBuffersFromNames, attributeName );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( size() ).append( " entries" );
	}

	public int size()
	{
		return attributeBuffersFromNames.size();
	}

	@Override public Iterator<Attribute> iterator()
	{
		return attributeBuffersFromNames.values().stream().map( attributeBuffer -> attributeBuffer.getAttribute() ).iterator();
	}

	/**
	 * Contains an attribute, either packed in a buffer, or expanded.
	 *
	 * @author Michael Belivanakis (michael.gr)
	 */
	static class AttributeBuffer
	{
		private final String attributeName;
		private final Utf8Constant nameConstant;
		private final Optional<Factory> attributeFactory;
		private Optional<Buffer> attributeBuffer;
		private Optional<Attribute> attribute;

		AttributeBuffer( String attributeName, Factory attributeFactory, BufferReader bufferReader )
		{
			this.attributeName = attributeName;
			nameConstant = new Utf8Constant( attributeName );
			this.attributeFactory = Optional.of( attributeFactory );
			int attributeLength = bufferReader.readInt();
			attributeBuffer = Optional.of( bufferReader.readBuffer( attributeLength ) );
			attribute = Optional.empty();
		}

		AttributeBuffer( Attribute attribute )
		{
			attributeName = attribute.name;
			nameConstant = new Utf8Constant( attribute.name );
			attributeFactory = Optional.empty();
			attributeBuffer = Optional.empty();
			this.attribute = Optional.of( attribute );
		}

		Attribute getAttribute()
		{
			expand();
			return attribute.orElseThrow();
		}

		void expand()
		{
			if( attribute.isEmpty() )
			{
				assert attributeFactory.isPresent();
				assert attributeBuffer.isPresent();
				BufferReader bufferReader = new BufferReader( attributeBuffer.get() );
				Attribute newAttribute = attributeFactory.get().newAttribute( attributeName, bufferReader );
				assert bufferReader.isAtEnd();
				attribute = Optional.of( newAttribute );
				attributeBuffer = Optional.empty();
			}
		}

		void intern( ConstantPool constantPool )
		{
			nameConstant.intern( constantPool );
			attribute.ifPresent( value -> value.intern( constantPool ) );
		}

		void write( ConstantPool constantPool, BufferWriter bufferWriter )
		{
			nameConstant.writeIndex( constantPool, bufferWriter );
			if( attribute.isPresent() )
			{
				BufferWriter attributeBufferWriter = new BufferWriter();
				attribute.get().write( constantPool, attributeBufferWriter );
				attributeBuffer = Optional.of( attributeBufferWriter.toBuffer() );
			}
			assert attributeBuffer.isPresent();
			bufferWriter.writeInt( attributeBuffer.get().getLength() );
			bufferWriter.writeBuffer( attributeBuffer.get() );
			if( attribute.isPresent() )
				attributeBuffer = Optional.empty();
		}
	}

	private Map<String,AttributeBuffer> newMap( int capacity )
	{
		Map<String,AttributeBuffer> map = new LinkedHashMap<>( capacity );
		return new ObservableMap<>( map, this::markAsDirty );
	}

	private void markAsDirty()
	{
		observer.run();
	}
}
