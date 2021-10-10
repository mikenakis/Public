package mikenakis.kit.xml;

import mikenakis.kit.io.stream.text.TextStreamReader;

/**
 * {@link XmlReader} factory.
 *
 * @author michael.gr
 */
public interface XmlReaderFactory
{
	XmlReader newXmlReaderOnTextStreamReader( TextStreamReader textStreamReader );

	interface Defaults extends XmlReaderFactory
	{
	}
}
