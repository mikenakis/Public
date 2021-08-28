package mikenakis.kit.spooling2.s0.format.xml.despooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.spooling2.s0.despooling.ArrayDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.GroupDespoolStream;
import mikenakis.kit.spooling2.codec.Codec;
import mikenakis.kit.xml.XmlReader;

import java.util.Map;
import java.util.Optional;

class XmlGroupMemberEntryDespoolStream implements EntryDespoolStream.Defaults
{
	static <T> T despool( XmlReader xmlReader, String xmlElementName, Map<String,String> attributes, Function1<T,EntryDespoolStream> entryDespooler )
	{
		EntryDespoolStream entryDespoolStream = new XmlGroupMemberEntryDespoolStream( xmlReader, xmlElementName, attributes );
		T result = entryDespooler.invoke( entryDespoolStream );
		return result;
	}

	private final XmlReader xmlReader;
	private final String xmlElementName;
	private final Map<String,String> attributes;
	private boolean isNested;

	private XmlGroupMemberEntryDespoolStream( XmlReader xmlReader, String xmlElementName, Map<String,String> attributes )
	{
		this.xmlReader = xmlReader;
		this.xmlElementName = xmlElementName;
		this.attributes = attributes;
	}

	@Override public <T> T despoolValue( Codec<T> codec, T defaultValue )
	{
		assert !isNested;
		//return attributes.tryGet( xmlElementName ).map( s -> valueType.instanceFromString( s ) ).or( () -> defaultValue ); //TODO: when reading, use tryRemove instead of tryGet
		Optional<String> stringValue = Kit.map.getOptional( attributes, xmlElementName ); //TODO: when reading, use tryRemove instead of tryGet
		if( stringValue.isEmpty() )
			return defaultValue;
		return codec.instanceFromString( stringValue.get() );
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
