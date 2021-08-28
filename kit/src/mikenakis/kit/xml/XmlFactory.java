package mikenakis.kit.xml;

/**
 * XML Factory.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface XmlFactory
{
	XmlReaderFactory getXmlReaderFactory();

	XmlWriterFactory getXmlWriterFactory();
}
