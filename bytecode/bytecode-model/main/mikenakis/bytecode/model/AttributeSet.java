package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A collection of {@link Attribute}s.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AttributeSet implements Iterable<Attribute>
{
	public static AttributeSet of()
	{
		return of( new LinkedHashMap<>() );
	}

	public static AttributeSet of( Map<Utf8Constant,Attribute> attributesFromNames )
	{
		return new AttributeSet( attributesFromNames );
	}

	private final Map<Utf8Constant,Attribute> attributesFromNames;

	private AttributeSet( Map<Utf8Constant,Attribute> attributesFromNames )
	{
		this.attributesFromNames = attributesFromNames;
	}

	public Optional<Attribute> tryGetAttributeByName( Utf8Constant attributeName )
	{
		return Kit.map.getOptional( attributesFromNames, attributeName );
	}

	public void addAttribute( Attribute attribute )
	{
		Kit.map.add( attributesFromNames, attribute.kind.utf8Name, attribute );
	}

	public void addOrReplaceAttribute( Attribute attribute )
	{
		Kit.map.addOrReplace( attributesFromNames, attribute.kind.utf8Name, attribute );
	}

	public void replaceAttribute( Attribute attribute )
	{
		Kit.map.replace( attributesFromNames, attribute.kind.utf8Name, attribute );
	}

	public void removeAttribute( Attribute attribute )
	{
		Kit.map.remove( attributesFromNames, attribute.kind.utf8Name );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " entries";
	}

	public int size()
	{
		return attributesFromNames.size();
	}

	@Override public Iterator<Attribute> iterator()
	{
		return attributesFromNames.values().stream().iterator();
	}
}
