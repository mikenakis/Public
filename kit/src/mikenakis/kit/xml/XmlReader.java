package mikenakis.kit.xml;

import mikenakis.kit.lifetime.Closeable;

import java.util.Map;

/**
 * An XML Reader that actually makes sense.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface XmlReader extends Closeable
{
	boolean isStartElement();

	boolean isStartDocument();

	boolean isEndElement();

	boolean isEndDocument();

	String getLocalName();

	boolean hasNext();

	void skip();

	Map<String,String> getAttributes();

	boolean hasAttributes();

	String tryGetAttributeValue( String attributeName );

	void appendToStringBuilder( StringBuilder stringBuilder );

	interface Defaults extends XmlReader, Closeable.Defaults
	{
	}
}
