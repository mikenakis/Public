package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
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
	public static AttributeSet read( AttributeReader attributeReader )
	{
		int count = attributeReader.readUnsignedShort();
		Map<Mutf8ValueConstant,Attribute> attributesFromNames = new LinkedHashMap<>( count );
		for( int i = 0; i < count; i++ )
		{
			Mutf8ValueConstant nameConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
			int attributeLength = attributeReader.readInt();
			int endPosition = attributeReader.bufferReader.getPosition() + attributeLength;
			Attribute attribute = Attribute.read( attributeReader, nameConstant, attributeLength );
			assert attributeReader.bufferReader.getPosition() == endPosition;
			Kit.map.add( attributesFromNames, nameConstant, attribute );
		}
		return new AttributeSet( attributesFromNames );
	}

	public static AttributeSet of()
	{
		return new AttributeSet( new LinkedHashMap<>() );
	}

	private final Map<Mutf8ValueConstant,Attribute> attributesByName;
	private final Map<Integer,KnownAttribute> knownAttributesByTag;

	private AttributeSet( Map<Mutf8ValueConstant,Attribute> attributesByName )
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

	public void intern( Interner interner )
	{
		for( Attribute attribute : allAttributes() )
		{
			attribute.mutf8Name.intern( interner );
			attribute.intern( interner );
		}
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( size() );
		for( Attribute attribute : allAttributes() )
		{
			Mutf8ValueConstant nameConstant = attribute.mutf8Name;
			constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( nameConstant ) );
			int position = constantWriter.bufferWriter.getPosition();
			constantWriter.writeInt( 0 );
			attribute.write( constantWriter );
			int length = constantWriter.bufferWriter.getPosition() - position - 4;
			constantWriter.bufferWriter.writeInt( position, length );
		}
	}
}
