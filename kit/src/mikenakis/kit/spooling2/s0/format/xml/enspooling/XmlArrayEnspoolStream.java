package mikenakis.kit.spooling2.s0.format.xml.enspooling;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.spooling2.s0.enspooling.ArrayEnspoolStream;
import mikenakis.kit.spooling2.s0.enspooling.EntryEnspoolStream;
import mikenakis.kit.xml.XmlWriter;

/**
 * XML {@link ArrayEnspoolStream}.
 *
 * @author michael.gr
 */
final class XmlArrayEnspoolStream implements ArrayEnspoolStream.Defaults, Closeable.Defaults
{
	public static void enspoolArray( XmlWriter xmlWriter, String xmlElementName, String arrayElementName, Procedure1<ArrayEnspoolStream> arrayEnspooler )
	{
		Kit.tryWithResources( new XmlArrayEnspoolStream( xmlWriter, xmlElementName, arrayElementName ), arrayEnspooler );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final XmlWriter xmlWriter;
	private final String xmlElementName;
	private boolean isNested;
	private final XmlArrayElementEntryEnspoolStream xmlArrayElementEnspooler;

	private XmlArrayEnspoolStream( XmlWriter xmlWriter, String xmlElementName, String arrayElementName )
	{
		this.xmlWriter = xmlWriter;
		this.xmlElementName = xmlElementName;
		xmlArrayElementEnspooler = new XmlArrayElementEntryEnspoolStream( xmlWriter, xmlElementName, arrayElementName );
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert !isNested;
		if( xmlArrayElementEnspooler.hasNested() )
			xmlWriter.writeEndElement();
		else
			xmlWriter.writeEmptyElement( xmlElementName );
		lifeGuard.close();
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void enspoolElement( Procedure1<EntryEnspoolStream> entryEnspooler )
	{
		assert isAliveAssertion();
		assert !isNested;
		isNested = true;
		entryEnspooler.invoke( xmlArrayElementEnspooler );
		isNested = false;
	}

	@Override public String toString()
	{
		var stringBuilder = new StringBuilder();
		stringBuilder.append( lifeGuard.toString() );
		stringBuilder.append( ' ' );
		stringBuilder.append( xmlElementName );
		return stringBuilder.toString();
	}
}
