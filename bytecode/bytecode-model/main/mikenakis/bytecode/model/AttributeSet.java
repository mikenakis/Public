package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.Mutf8Constant;
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

	public static AttributeSet of( Map<Mutf8Constant,Attribute> attributesFromNames )
	{
		return new AttributeSet( attributesFromNames );
	}

	private final Map<Mutf8Constant,Attribute> attributesFromNames;

	private AttributeSet( Map<Mutf8Constant,Attribute> attributesFromNames )
	{
		this.attributesFromNames = attributesFromNames;
	}

	public Optional<Attribute> tryGetAttributeByName( Mutf8Constant attributeName )
	{
		return Kit.map.getOptional( attributesFromNames, attributeName );
	}

	public void addAttribute( Attribute attribute )
	{
		Kit.map.add( attributesFromNames, attribute.kind.mutf8Name, attribute );
	}

	public void addOrReplaceAttribute( Attribute attribute )
	{
		Kit.map.addOrReplace( attributesFromNames, attribute.kind.mutf8Name, attribute );
	}

	public void replaceAttribute( Attribute attribute )
	{
		Kit.map.replace( attributesFromNames, attribute.kind.mutf8Name, attribute );
	}

	public void removeAttribute( Attribute attribute )
	{
		Kit.map.remove( attributesFromNames, attribute.kind.mutf8Name );
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
