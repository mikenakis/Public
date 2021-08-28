package mikenakis.kit.spooling2.s0.format.xml.enspooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.GroupEnspoolStream;
import mikenakis.kit.xml.XmlWriter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XML {@link GroupEnspoolStream}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
final class XmlGroupEnspoolStream implements GroupEnspoolStream.Defaults, Closeable.Defaults
{
	static void enspool( XmlWriter xmlWriter, String xmlElementName, Procedure1<GroupEnspoolStream> groupEnspooler )
	{
		Kit.tryWithResources( new XmlGroupEnspoolStream( xmlWriter, xmlElementName ), groupEnspooler );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final XmlWriter xmlWriter;
	private final String elementName;
	private final Map<String,String> attributes = new LinkedHashMap<>();
	private boolean hasNested;

	private XmlGroupEnspoolStream( XmlWriter xmlWriter, String elementName )
	{
		this.xmlWriter = xmlWriter;
		this.elementName = elementName;
	}

	@Override public void enspoolMember( String xmlElementName, Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		assert isAliveAssertion();
		XmlGroupMemberEntryEnspoolStream memberEnspoolStream = new XmlGroupMemberEntryEnspoolStream( xmlWriter, xmlElementName, elementName, attributes, hasNested );
		entryEnspooler.invoke( memberEnspoolStream );
		hasNested |= memberEnspoolStream.hasNested();
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		if( hasNested )
			xmlWriter.writeEndElement();
		else
		{
			xmlWriter.writeEmptyElement( elementName );
			writeAttributes();
		}
		lifeGuard.close();
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
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
		stringBuilder.append( elementName );
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
