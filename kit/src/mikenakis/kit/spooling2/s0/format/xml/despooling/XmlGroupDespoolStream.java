package mikenakis.kit.spooling2.s0.format.xml.despooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.GroupDespoolStream;
import mikenakis.kit.xml.XmlReader;

import java.util.Map;

/**
 * XML {@link GroupDespoolStream}.
 *
 * @author michael.gr
 */
final class XmlGroupDespoolStream implements GroupDespoolStream.Defaults, Closeable.Defaults
{
	public static <T> T despool( XmlReader xmlReader, String xmlElementName, Map<String,String> attributes, Function1<T,GroupDespoolStream> groupDespooler )
	{
		return Kit.tryGetWithResources( new XmlGroupDespoolStream( xmlReader, xmlElementName, attributes ), groupDespooler );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final XmlReader xmlReader;
	private final String xmlElementName;
	private final Map<String,String> attributes;
	private boolean isNested;

	private XmlGroupDespoolStream( XmlReader xmlReader, String xmlElementName, Map<String,String> attributes )
	{
		this.xmlReader = xmlReader;
		this.xmlElementName = xmlElementName;
		this.attributes = attributes;
	}

	@Override public void close()
	{
		assert lifeGuard.isAliveAssertion();
		assert !isNested;
		assert xmlReader.isEndElement();
		assert xmlReader.getLocalName().equals( xmlElementName );
		xmlReader.skip();
		assert xmlReader.isEndElement() || xmlReader.isEndDocument() || xmlReader.isStartElement();
		lifeGuard.close();
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public <T> T despoolMember( String xmlElementName, Function1<T,EntryDespoolStream> entryDespooler )
	{
		assert lifeGuard.isAliveAssertion();
		assert !isNested;
		isNested = true;
		T result = XmlGroupMemberEntryDespoolStream.despool( xmlReader, xmlElementName, attributes, entryDespooler );
		isNested = false;
		return result;
	}

	@Override public String toString()
	{
		var stringBuilder = new StringBuilder();
		stringBuilder.append( lifeGuard.toString() );
		stringBuilder.append( ' ' );
		stringBuilder.append( xmlElementName );
		return stringBuilder.toString();
	}
}
