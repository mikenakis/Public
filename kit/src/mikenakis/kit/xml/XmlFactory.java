package mikenakis.kit.xml;

/**
 * XML Factory.
 *
 * @author michael.gr
 */
public interface XmlFactory
{
	XmlReaderFactory getXmlReaderFactory();

	XmlWriterFactory getXmlWriterFactory();
}
