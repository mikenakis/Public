package mikenakis.kit.spooling2.s0.format.xml.enspooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.io.stream.text.TextStreamWriter;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.spooling2.s0.enspooling.ArrayEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.GroupEnspoolStream;
import mikenakis.kit.spooling2.s0.format.xml.XmlSpoolingHelper;
import mikenakis.kit.spooling2.codec.Codec;
import mikenakis.kit.xml.XmlWriter;
import mikenakis.kit.xml.XmlWriterFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * XML Root {@link EntryEnspoolStream}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class XmlEntryEnspoolStream implements EntryEnspoolStream.Defaults, Closeable.Defaults
{
	public static void enspool( XmlWriterFactory xmlWriterFactory, String rootElementName, TextStreamWriter textStreamWriter, //
		Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		Kit.tryWithResources( xmlWriterFactory.newXmlWriterOnTextStreamWriter( textStreamWriter ), xmlWriter -> //
			Kit.tryWithResources( new XmlEntryEnspoolStream( xmlWriter, rootElementName ), entryEnspooler ) );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final XmlWriter xmlWriter;
	private final String rootElementName;
	private final Map<String,String> attributes = new LinkedHashMap<>();
	private boolean hasNested;
	private boolean nonEmpty;
	private boolean isNested;

	private XmlEntryEnspoolStream( XmlWriter xmlWriter, String rootElementName )
	{
		this.xmlWriter = xmlWriter;
		this.rootElementName = rootElementName;
		xmlWriter.writeStartDocument(); // Writes "<?xml version="1.0" encoding="utf-8"?>"
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		if( !hasNested )
		{
			if( nonEmpty )
			{
				xmlWriter.writeEmptyElement( rootElementName );
				writeAttributes();
			}
		}
		xmlWriter.writeEndDocument(); // "Closes any start tags and writes corresponding end tags."
		lifeGuard.close();
	}

	@Override public <T> void enspoolValue( Codec<T> codec, T value, T defaultValue )
	{
		assert !isNested;
		assert !hasNested;
		nonEmpty = true;
		if( Objects.equals( value, defaultValue ) )
			return;
		String attributeValue = codec.stringFromInstance( value );
		Kit.map.add( attributes, XmlSpoolingHelper.ValueAttribute, attributeValue );
	}

	@Override public void enspoolGroup( Procedure1<GroupEnspoolStream> groupEnspooler )
	{
		assert !isNested;
		isNested = true;
		XmlGroupEnspoolStream.enspool( xmlWriter, rootElementName, groupEnspooler );
		isNested = false;
		hasNested = true;
	}

	@Override public void enspoolArray( String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler )
	{
		assert !isNested;
		isNested = true;
		XmlArrayEnspoolStream.enspoolArray( xmlWriter, rootElementName, arrayElementName, arrayEnspooler );
		isNested = false;
		hasNested = true;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( value );
		return true;
	}

	private void writeAttributes()
	{
		for( Map.Entry<String,String> attribute : attributes.entrySet() )
			xmlWriter.writeAttribute( attribute.getKey(), attribute.getValue() );
		attributes.clear();
	}

	@Override public String toString()
	{
		var stringBuilder = new StringBuilder();
		stringBuilder.append( lifeGuard.toString() );
		stringBuilder.append( ' ' );
		stringBuilder.append( rootElementName );
		if( !attributes.isEmpty() )
		{
			stringBuilder.append( " attributes: " );
			boolean first = true;
			for( Map.Entry<String,String> attribute : attributes.entrySet() )
			{
				if( first )
					first = false;
				else
					stringBuilder.append( ", " );
				String key = attribute.getKey();
				String value = attribute.getValue();
				stringBuilder.append( key ).append( "=" ).append( value );
			}
		}
		return stringBuilder.toString();
	}
}
