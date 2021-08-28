package mikenakis.kit.xml;

import mikenakis.kit.lifetime.Closeable;

/**
 * An XML Writer that actually makes sense.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface XmlWriter extends Closeable
{
	void writeStartDocument();

	void writeEndDocument();

	void writeStartElement( String localName );

	void writeEndElement();

	void writeEmptyElement( String localName );

	void writeAttribute( String localName, String value );

	void appendToStringBuilder( StringBuilder stringBuilder );

	interface Defaults extends XmlWriter, Closeable.Defaults
	{
	}
}
