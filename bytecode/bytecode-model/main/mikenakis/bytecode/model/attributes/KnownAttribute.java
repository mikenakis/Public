package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.Kit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a known {@link Attribute}.
 * <p>
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class KnownAttribute extends Attribute
{
	public static final int tagAnnotationDefault                    = 0;
	public static final int tagBootstrapMethods                     = 1;
	public static final int tagCode                                 = 2;
	public static final int tagConstantValue                        = 3;
	public static final int tagDeprecated                           = 4;
	public static final int tagEnclosingMethod                      = 5;
	public static final int tagExceptions                           = 6;
	public static final int tagInnerClasses                         = 7;
	public static final int tagLineNumberTable                      = 8;
	public static final int tagLocalVariableTable                   = 9;
	public static final int tagLocalVariableTypeTable               = 10;
	public static final int tagMethodParameters                     = 11;
	public static final int tagNestHost                             = 12;
	public static final int tagNestMembers                          = 13;
	public static final int tagRuntimeInvisibleAnnotations          = 14;
	public static final int tagRuntimeInvisibleParameterAnnotations = 15;
	public static final int tagRuntimeInvisibleTypeAnnotations      = 16;
	public static final int tagRuntimeVisibleAnnotations            = 17;
	public static final int tagRuntimeVisibleParameterAnnotations   = 18;
	public static final int tagRuntimeVisibleTypeAnnotations        = 19;
	public static final int tagSignature                            = 20;
	public static final int tagSourceFile                           = 21;
	public static final int tagStackMapTable                        = 22;
	public static final int tagSynthetic                            = 23;

	private static final List<Map.Entry<Integer,Mutf8Constant>> tagEntries = Stream.of(
		Map.entry( tagAnnotationDefault                    , "AnnotationDefault" ),
		Map.entry( tagBootstrapMethods                     , "BootstrapMethods" ),
		Map.entry( tagCode                                 , "Code" ),
		Map.entry( tagConstantValue                        , "ConstantValue" ),
		Map.entry( tagDeprecated                           , "Deprecated" ),
		Map.entry( tagEnclosingMethod                      , "EnclosingMethod" ),
		Map.entry( tagExceptions                           , "Exceptions" ),
		Map.entry( tagInnerClasses                         , "InnerClasses" ),
		Map.entry( tagLineNumberTable                      , "LineNumberTable" ),
		Map.entry( tagLocalVariableTable                   , "LocalVariableTable" ),
		Map.entry( tagLocalVariableTypeTable               , "LocalVariableTypeTable" ),
		Map.entry( tagMethodParameters                     , "MethodParameters" ),
		Map.entry( tagNestHost                             , "NestHost" ),
		Map.entry( tagNestMembers                          , "NestMembers" ),
		Map.entry( tagRuntimeInvisibleAnnotations          , "RuntimeInvisibleAnnotations" ),
		Map.entry( tagRuntimeInvisibleParameterAnnotations , "RuntimeInvisibleParameterAnnotations" ),
		Map.entry( tagRuntimeInvisibleTypeAnnotations      , "RuntimeInvisibleTypeAnnotations" ),
		Map.entry( tagRuntimeVisibleAnnotations            , "RuntimeVisibleAnnotations" ),
		Map.entry( tagRuntimeVisibleParameterAnnotations   , "RuntimeVisibleParameterAnnotations" ),
		Map.entry( tagRuntimeVisibleTypeAnnotations        , "RuntimeVisibleTypeAnnotations" ),
		Map.entry( tagSignature                            , "Signature" ),
		Map.entry( tagSourceFile                           , "SourceFile" ),
		Map.entry( tagStackMapTable                        , "StackMapTable" ),
		Map.entry( tagSynthetic                            , "Synthetic" )
	).map( e -> Map.entry( e.getKey(), Mutf8Constant.of( e.getValue() ) ) ).toList();

	private static final Map<Integer,Mutf8Constant> namesByTag = tagEntries.stream() //
		.collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue() ) );
	private static final Map<Mutf8Constant,Integer> tagsByName = tagEntries.stream() //
		.collect( Collectors.toMap( e -> e.getValue(), e -> e.getKey() ) );

	public static Mutf8Constant nameFromTag( int knownAttributeTag )
	{
		return Kit.map.get( namesByTag, knownAttributeTag );
	}

	public static Optional<Integer> tagFromName( Mutf8Constant attributeName )
	{
		return Kit.map.getOptional( tagsByName, attributeName );
	}

	public final int tag;

	protected KnownAttribute( int tag )
	{
		super( nameFromTag( tag ) );
		this.tag = tag;
	}

	@Deprecated @Override public boolean isKnown() { return true; }
	@Deprecated @Override public KnownAttribute asKnownAttribute() { return this; }
}
