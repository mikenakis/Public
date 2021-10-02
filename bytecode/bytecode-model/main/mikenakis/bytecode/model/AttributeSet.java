package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A collection of {@link Attribute}s.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AttributeSet
{
	public static AttributeSet of()
	{
		return of( new LinkedHashMap<>() );
	}

	public static AttributeSet of( Map<Mutf8Constant,Attribute> attributesFromNames )
	{
		return new AttributeSet( attributesFromNames );
	}

	private final Map<Mutf8Constant,Attribute> attributesByName;
	private final Map<Integer,KnownAttribute> knownAttributesByTag;

	private AttributeSet( Map<Mutf8Constant,Attribute> attributesByName )
	{
		this.attributesByName = attributesByName;
		knownAttributesByTag = attributesByName.values().stream() //
			.filter( a -> a.isKnown() ) //
			.map( a -> a.asKnownAttribute() )
			.collect( Collectors.toMap( a -> a.tag, a -> a ) );
	}

	public Optional<KnownAttribute> tryGetKnownAttributeByTag( int knownAttributeTag )
	{
		return Kit.map.getOptional( knownAttributesByTag, knownAttributeTag );
	}

	public <T extends Attribute> T addAttribute( T attribute )
	{
		Kit.map.add( attributesByName, attribute.mutf8Name, attribute );
		if( attribute.isKnown() )
		{
			KnownAttribute knownAttribute = attribute.asKnownAttribute();
			Kit.map.add( knownAttributesByTag, knownAttribute.tag, knownAttribute );
		}
		return attribute;
	}

	public void addOrReplaceAttribute( Attribute attribute )
	{
		Kit.map.addOrReplace( attributesByName, attribute.mutf8Name, attribute );
		if( attribute.isKnown() )
		{
			KnownAttribute knownAttribute = attribute.asKnownAttribute();
			Kit.map.addOrReplace( knownAttributesByTag, knownAttribute.tag, knownAttribute );
		}
	}

	public void replaceAttribute( Attribute attribute )
	{
		Kit.map.replace( attributesByName, attribute.mutf8Name, attribute );
		if( attribute.isKnown() )
		{
			KnownAttribute knownAttribute = attribute.asKnownAttribute();
			Kit.map.replace( knownAttributesByTag, knownAttribute.tag, knownAttribute );
		}
	}

	public void removeAttribute( Attribute attribute )
	{
		Kit.map.remove( attributesByName, attribute.mutf8Name );
		if( attribute.isKnown() )
		{
			KnownAttribute knownAttribute = attribute.asKnownAttribute();
			Kit.map.remove( knownAttributesByTag, knownAttribute.tag, knownAttribute );
		}
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " entries";
	}

	public int size()
	{
		return attributesByName.size();
	}

	public Collection<Attribute> allAttributes()
	{
		return attributesByName.values();
	}

	public Collection<KnownAttribute> knownAttributes()
	{
		return knownAttributesByTag.values();
	}
}
