package mikenakis.kit.spooling2.s0.format.xml.enspooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.s0.enspooling.ArrayEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.GroupEnspoolStream;
import mikenakis.kit.spooling2.codec.Codec;
import mikenakis.kit.xml.XmlWriter;

import java.util.Map;
import java.util.Objects;

class XmlGroupMemberEntryEnspoolStream implements EntryEnspoolStream.Defaults
{
	private final XmlWriter xmlWriter;
	private final String xmlElementName;
	private final String memberName;
	private final Map<String,String> attributes;
	private boolean isNested;
	private boolean hasNested;

	XmlGroupMemberEntryEnspoolStream( XmlWriter xmlWriter, String xmlElementName, String memberName, Map<String,String> attributes, boolean hasNested )
	{
		this.xmlWriter = xmlWriter;
		this.xmlElementName = xmlElementName;
		this.memberName = memberName;
		this.attributes = attributes;
		this.hasNested = hasNested;
	}

	boolean hasNested()
	{
		return hasNested;
	}

	@Override public <T> void enspoolValue( Codec<T> codec, T value, T defaultValue )
	{
		assert !isNested;
		assert !hasNested;
		if( Objects.equals( value, defaultValue ) )
			return;
		String attributeValue = codec.stringFromInstance( value );
		Kit.map.add( attributes, xmlElementName, attributeValue );
	}

	@Override public void enspoolGroup( Procedure1<GroupEnspoolStream> groupEnspooler )
	{
		openNestedScope();
		XmlGroupEnspoolStream.enspool( xmlWriter, xmlElementName, groupEnspooler );
		closeNestedScope();
	}

	@Override public void enspoolArray( String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler )
	{
		openNestedScope();
		XmlArrayEnspoolStream.enspoolArray( xmlWriter, xmlElementName, arrayElementName, arrayEnspooler );
		closeNestedScope();
	}

	private void openNestedScope()
	{
		assert !isNested;
		if( !hasNested )
		{
			xmlWriter.writeStartElement( memberName );
			writeAttributes();
			hasNested = true;
		}
		isNested = true;
	}

	private void closeNestedScope()
	{
		assert isNested;
		isNested = false;
	}

	private void writeAttributes()
	{
		for( Map.Entry<String,String> attribute : attributes.entrySet() )
			xmlWriter.writeAttribute( attribute.getKey(), attribute.getValue() );
		attributes.clear();
	}
}
