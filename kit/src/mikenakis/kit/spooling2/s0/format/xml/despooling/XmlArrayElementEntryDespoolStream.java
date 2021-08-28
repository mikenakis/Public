package mikenakis.kit.spooling2.s0.format.xml.despooling;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.s0.despooling.ArrayDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.GroupDespoolStream;
import mikenakis.kit.spooling2.s0.format.xml.XmlSpoolingHelper;
import mikenakis.kit.spooling2.codec.Codec;
import mikenakis.kit.xml.XmlReader;

import java.util.Map;

class XmlArrayElementEntryDespoolStream implements EntryDespoolStream.Defaults
{
	public static void despool( XmlReader xmlReader, String arrayElementName, Procedure1<EntryDespoolStream> entryDespooler )
	{
		EntryDespoolStream entryDespoolStream = new XmlArrayElementEntryDespoolStream( xmlReader, arrayElementName );
		while( !xmlReader.isEndElement() )
			entryDespooler.invoke( entryDespoolStream );
	}

	private final XmlReader xmlReader;
	private final String xmlElementName;
	private boolean isNested;

	private XmlArrayElementEntryDespoolStream( XmlReader xmlReader, String xmlElementName )
	{
		this.xmlReader = xmlReader;
		this.xmlElementName = xmlElementName;
	}

	@Override public <T> T despoolValue( Codec<T> codec, T defaultValue )
	{
		assert !isNested;
		assert xmlReader.isStartElement();
		assert xmlReader.getLocalName().equals( xmlElementName );
		Map<String,String> attributes = xmlReader.getAttributes();
		assert attributes.size() == 1;
		String attributeValue = attributes.get( XmlSpoolingHelper.ValueAttribute );
		T result = codec.instanceFromString( attributeValue );
		xmlReader.skip();
		assert xmlReader.isEndElement();
		xmlReader.skip();
		return result;
	}

	@Override public <T> T despoolGroup( Function1<T,GroupDespoolStream> groupDespooler )
	{
		assert !isNested;
		assert xmlReader.isStartElement();
		assert xmlReader.getLocalName().equals( xmlElementName );
		Map<String,String> attributes = xmlReader.getAttributes();
		xmlReader.skip();
		isNested = true;
		T result = XmlGroupDespoolStream.despool( xmlReader, xmlElementName, attributes, groupDespooler );
		isNested = false;
		return result;
	}

	@Override public <T> T despoolArray( String arrayElementName, Function1<T,ArrayDespoolStream> arrayDespooler )
	{
		assert !isNested;
		assert xmlReader.isStartElement();
		assert xmlReader.getLocalName().equals( xmlElementName );
		assert xmlReader.getAttributes().isEmpty();
		xmlReader.skip();
		isNested = true;
		T result = XmlArrayDespoolStream.despoolArray( xmlReader, xmlElementName, arrayElementName, arrayDespooler );
		isNested = false;
		return result;
	}

	@Override public final String toString()
	{
		var stringBuilder = new StringBuilder();
		stringBuilder.append( xmlElementName );
		return stringBuilder.toString();
	}
}
