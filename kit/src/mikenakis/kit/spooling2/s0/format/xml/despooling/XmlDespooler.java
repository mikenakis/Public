package mikenakis.kit.spooling2.s0.format.xml.despooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.io.stream.text.TextStreamReader;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.spooling2.s0.format.xml.XmlSpoolingHelper;
import mikenakis.kit.xml.XmlReaderFactory;

import java.util.Map;

/**
 * XML Root {@link EntryDespoolStream}.
 *
 * @author michael.gr
 */
public final class XmlDespooler
{
	public static <T> T despool( XmlReaderFactory xmlReaderFactory, String rootElementName, TextStreamReader textStreamReader, //
		Function1<T,EntryDespoolStream> entryDespooler )
	{
		return Kit.tryGetWithResources( xmlReaderFactory.newXmlReaderOnTextStreamReader( textStreamReader ), xmlReader -> //
		{
			assert xmlReader.isStartDocument();
			xmlReader.skip();
			assert xmlReader.isStartElement();
			assert xmlReader.getLocalName().equals( rootElementName );
			Map<String,String> attributes = xmlReader.getAttributes();
			T result;
			if( attributes.size() == 1 && attributes.containsKey( XmlSpoolingHelper.ValueAttribute ) )
			{
				xmlReader.skip();
				assert xmlReader.isEndElement();
				xmlReader.skip();
				result = XmlGroupMemberEntryDespoolStream.despool( xmlReader, XmlSpoolingHelper.ValueAttribute, attributes, entryDespooler );
			}
			else
			{
				result = XmlGroupMemberEntryDespoolStream.despool( xmlReader, rootElementName, attributes, entryDespooler );
			}
			assert xmlReader.isEndDocument();
			return result;
		} );
	}
}
