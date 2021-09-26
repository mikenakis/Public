package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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
	public static final int tag_AnnotationDefault = 0;
	public static final int tag_BootstrapMethods = 1;
	public static final int tag_Code = 2;
	public static final int tag_ConstantValue = 3;
	public static final int tag_Deprecated = 4;
	public static final int tag_EnclosingMethod = 5;
	public static final int tag_Exceptions = 6;
	public static final int tag_InnerClasses = 7;
	public static final int tag_LineNumberTable = 8;
	public static final int tag_LocalVariableTable = 9;
	public static final int tag_LocalVariableTypeTable = 10;
	public static final int tag_MethodParameters = 11;
	public static final int tag_NestHost = 12;
	public static final int tag_NestMembers = 13;
	public static final int tag_RuntimeInvisibleAnnotations = 14;
	public static final int tag_RuntimeInvisibleParameterAnnotations = 15;
	public static final int tag_RuntimeInvisibleTypeAnnotations = 16;
	public static final int tag_RuntimeVisibleAnnotations = 17;
	public static final int tag_RuntimeVisibleParameterAnnotations = 18;
	public static final int tag_RuntimeVisibleTypeAnnotations = 19;
	public static final int tag_Signature = 20;
	public static final int tag_SourceFile = 21;
	public static final int tag_StackMapTable = 22;
	public static final int tag_Synthetic = 23;

	private static final List<Map.Entry<Integer,Mutf8Constant>> tagEntries = Stream.of(
		Map.entry( tag_AnnotationDefault, "AnnotationDefault" ),
		Map.entry( tag_BootstrapMethods, "BootstrapMethods" ),
		Map.entry( tag_Code, "Code" ),
		Map.entry( tag_ConstantValue, "ConstantValue" ),
		Map.entry( tag_Deprecated, "Deprecated" ),
		Map.entry( tag_EnclosingMethod, "EnclosingMethod" ),
		Map.entry( tag_Exceptions, "Exceptions" ),
		Map.entry( tag_InnerClasses, "InnerClasses" ),
		Map.entry( tag_LineNumberTable, "LineNumberTable" ),
		Map.entry( tag_LocalVariableTable, "LocalVariableTable" ),
		Map.entry( tag_LocalVariableTypeTable, "LocalVariableTypeTable" ),
		Map.entry( tag_MethodParameters, "MethodParameters" ),
		Map.entry( tag_NestHost, "NestHost" ),
		Map.entry( tag_NestMembers, "NestMembers" ),
		Map.entry( tag_RuntimeInvisibleAnnotations, "RuntimeInvisibleAnnotations" ),
		Map.entry( tag_RuntimeInvisibleParameterAnnotations, "RuntimeInvisibleParameterAnnotations" ),
		Map.entry( tag_RuntimeInvisibleTypeAnnotations, "RuntimeInvisibleTypeAnnotations" ),
		Map.entry( tag_RuntimeVisibleAnnotations, "RuntimeVisibleAnnotations" ),
		Map.entry( tag_RuntimeVisibleParameterAnnotations, "RuntimeVisibleParameterAnnotations" ),
		Map.entry( tag_RuntimeVisibleTypeAnnotations, "RuntimeVisibleTypeAnnotations" ),
		Map.entry( tag_Signature, "Signature" ),
		Map.entry( tag_SourceFile, "SourceFile" ),
		Map.entry( tag_StackMapTable, "StackMapTable" ),
		Map.entry( tag_Synthetic, "Synthetic" )
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

	@ExcludeFromJacocoGeneratedReport public AnnotationDefaultAttribute                    /**/ asAnnotationDefaultAttribute                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public BootstrapMethodsAttribute                     /**/ asBootstrapMethodsAttribute                     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public CodeAttribute                                 /**/ asCodeAttribute                                 /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ConstantValueAttribute                        /**/ asConstantValueAttribute                        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public DeprecatedAttribute                           /**/ asDeprecatedAttribute                           /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public EnclosingMethodAttribute                      /**/ asEnclosingMethodAttribute                      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ExceptionsAttribute                           /**/ asExceptionsAttribute                           /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InnerClassesAttribute                         /**/ asInnerClassesAttribute                         /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NestHostAttribute                             /**/ asNestHostAttribute                             /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NestMembersAttribute                          /**/ asNestMembersAttribute                          /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LineNumberTableAttribute                      /**/ asLineNumberTableAttribute                      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableTableAttribute                   /**/ asLocalVariableTableAttribute                   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableTypeTableAttribute               /**/ asLocalVariableTypeTableAttribute               /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodParametersAttribute                     /**/ asMethodParametersAttribute                     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public AnnotationsAttribute                          /**/ asAnnotationsAttribute                          /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleAnnotationsAttribute            /**/ asRuntimeVisibleAnnotationsAttribute            /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleAnnotationsAttribute          /**/ asRuntimeInvisibleAnnotationsAttribute          /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ParameterAnnotationsAttribute                 /**/ asParameterAnnotationsAttribute                 /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleParameterAnnotationsAttribute /**/ asRuntimeInvisibleParameterAnnotationsAttribute /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleParameterAnnotationsAttribute   /**/ asRuntimeVisibleParameterAnnotationsAttribute   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public TypeAnnotationsAttribute                      /**/ asTypeAnnotationsAttribute                      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleTypeAnnotationsAttribute      /**/ asRuntimeInvisibleTypeAnnotationsAttribute      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleTypeAnnotationsAttribute        /**/ asRuntimeVisibleTypeAnnotationsAttribute        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SignatureAttribute                            /**/ asSignatureAttribute                            /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SourceFileAttribute                           /**/ asSourceFileAttribute                           /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public StackMapTableAttribute                        /**/ asStackMapTableAttribute                        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SyntheticAttribute                            /**/ asSyntheticAttribute                            /**/() { return Kit.fail(); }
}
