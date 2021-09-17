package mikenakis.kit.xml;

import mikenakis.kit.io.stream.text.TextStreamWriter;

/**
 * {@link XmlWriter} factory.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface XmlWriterFactory
{
	XmlWriter newXmlWriterOnTextStreamWriter( TextStreamWriter textStreamWriter );

	interface Defaults extends XmlWriterFactory
	{
	}
}