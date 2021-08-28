package mikenakis.kit.spooling2.s0.format.xml.enspooling;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.spooling2.s0.enspooling.ArrayEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.GroupEnspoolStream;
import mikenakis.kit.spooling2.s0.format.xml.XmlSpoolingHelper;
import mikenakis.kit.spooling2.codec.Codec;
import mikenakis.kit.xml.XmlWriter;

import java.util.Objects;

class XmlArrayElementEntryEnspoolStream implements EntryEnspoolStream.Defaults
{
	private final XmlWriter xmlWriter;
	private final String xmlElementName;
	private final String arrayElementName;
	private boolean isNested;
	private boolean hasNested;

	XmlArrayElementEntryEnspoolStream( XmlWriter xmlWriter, String xmlElementName, String arrayElementName )
	{
		this.xmlWriter = xmlWriter;
		this.xmlElementName = xmlElementName;
		this.arrayElementName = arrayElementName;
	}

	boolean hasNested()
	{
		return hasNested;
	}

	@Override public <T> void enspoolValue( Codec<T> codec, T value, T defaultValue )
	{
		assert !isNested;
		if( !hasNested )
		{
			xmlWriter.writeStartElement( xmlElementName );
			hasNested = true;
		}
		if( Objects.equals( value, defaultValue ) )
			return;
		String attributeValue = codec.stringFromInstance( value );
		xmlWriter.writeEmptyElement( arrayElementName );
		xmlWriter.writeAttribute( XmlSpoolingHelper.ValueAttribute, attributeValue );
	}

	@Override public void enspoolGroup( Procedure1<GroupEnspoolStream> groupEnspooler )
	{
		openNestedScope();
		XmlGroupEnspoolStream.enspool( xmlWriter, arrayElementName, groupEnspooler );
		closeNestedScope();
	}

	@Override public void enspoolArray( String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler )
	{
		openNestedScope();
		XmlArrayEnspoolStream.enspoolArray( xmlWriter, this.arrayElementName, arrayElementName, arrayEnspooler );
		closeNestedScope();
	}

	private void openNestedScope()
	{
		assert !isNested;
		if( !hasNested )
		{
			xmlWriter.writeStartElement( xmlElementName );
			hasNested = true;
		}
		isNested = true;
	}

	private void closeNestedScope()
	{
		assert isNested;
		isNested = false;
	}
}
