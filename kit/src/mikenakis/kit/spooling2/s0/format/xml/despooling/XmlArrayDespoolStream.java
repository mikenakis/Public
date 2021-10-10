package mikenakis.kit.spooling2.s0.format.xml.despooling;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.spooling2.s0.despooling.ArrayDespoolStream;
import mikenakis.kit.spooling2.s0.despooling.EntryDespoolStream;
import mikenakis.kit.xml.XmlReader;

/**
 * XML {@link ArrayDespoolStream}.
 *
 * @author michael.gr
 */
final class XmlArrayDespoolStream implements ArrayDespoolStream, Closeable.Defaults
{
	public static <T> T despoolArray( XmlReader xmlReader, String xmlElementName, String arrayElementName, Function1<T,ArrayDespoolStream> arrayDespooler )
	{
		XmlArrayDespoolStream despoolStream = new XmlArrayDespoolStream( xmlReader, xmlElementName, arrayElementName );
		T result = arrayDespooler.invoke( despoolStream );
		despoolStream.close();
		return result;
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final XmlReader xmlReader;
	private final String xmlElementName;
	private boolean isNested;
	private final String arrayElementName;

	private XmlArrayDespoolStream( XmlReader xmlReader, String xmlElementName, String arrayElementName )
	{
		this.xmlReader = xmlReader;
		this.xmlElementName = xmlElementName;
		this.arrayElementName = arrayElementName;
	}

	@Override public void despoolAllElements( Procedure1<EntryDespoolStream> entryDespooler )
	{
		assert isAliveAssertion();
		assert !isNested;
		isNested = true;
		XmlArrayElementEntryDespoolStream.despool( xmlReader, arrayElementName, entryDespooler );
		isNested = false;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert !isNested;
		assert xmlReader.isEndElement();
		assert xmlReader.getLocalName().equals( xmlElementName );
		xmlReader.skip();
		assert xmlReader.isEndElement() || xmlReader.isEndDocument() || xmlReader.isStartElement();
		lifeGuard.close();
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( lifeGuard.toString() );
		stringBuilder.append( ' ' );
		stringBuilder.append( xmlElementName );
		return stringBuilder.toString();
	}
}
