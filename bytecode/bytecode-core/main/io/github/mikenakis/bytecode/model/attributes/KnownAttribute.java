package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a known {@link Attribute}.
 * <p>
 * @author michael.gr
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

	private static final List<Map.Entry<Integer,Mutf8ValueConstant>> tagEntries = Stream.of(
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
	).map( e -> Map.entry( e.getKey(), Mutf8ValueConstant.of( e.getValue() ) ) ).toList();

	private static final Map<Integer,Mutf8ValueConstant> namesByTag = tagEntries.stream() //
		.collect( Collectors.toMap( e -> e.getKey(), e -> e.getValue() ) );
	private static final Map<Mutf8ValueConstant,Integer> tagsByName = tagEntries.stream() //
		.collect( Collectors.toMap( e -> e.getValue(), e -> e.getKey() ) );

	public static Mutf8ValueConstant nameFromTag( int knownAttributeTag )
	{
		return Kit.map.get( namesByTag, knownAttributeTag );
	}

	public static Optional<Integer> tagFromName( Mutf8ValueConstant attributeName )
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

	@ExcludeFromJacocoGeneratedReport public AnnotationDefaultAttribute                    /**/ asAnnotationDefaultAttribute                    /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public BootstrapMethodsAttribute                     /**/ asBootstrapMethodsAttribute                     /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public CodeAttribute                                 /**/ asCodeAttribute                                 /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public ConstantValueAttribute                        /**/ asConstantValueAttribute                        /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public DeprecatedAttribute                           /**/ asDeprecatedAttribute                           /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public EnclosingMethodAttribute                      /**/ asEnclosingMethodAttribute                      /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public ExceptionsAttribute                           /**/ asExceptionsAttribute                           /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public InnerClassesAttribute                         /**/ asInnerClassesAttribute                         /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public NestHostAttribute                             /**/ asNestHostAttribute                             /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public NestMembersAttribute                          /**/ asNestMembersAttribute                          /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public LineNumberTableAttribute                      /**/ asLineNumberTableAttribute                      /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableTableAttribute                   /**/ asLocalVariableTableAttribute                   /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableTypeTableAttribute               /**/ asLocalVariableTypeTableAttribute               /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public MethodParametersAttribute                     /**/ asMethodParametersAttribute                     /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public AnnotationsAttribute                          /**/ asAnnotationsAttribute                          /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleAnnotationsAttribute            /**/ asRuntimeVisibleAnnotationsAttribute            /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleAnnotationsAttribute          /**/ asRuntimeInvisibleAnnotationsAttribute          /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public ParameterAnnotationsAttribute                 /**/ asParameterAnnotationsAttribute                 /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleParameterAnnotationsAttribute /**/ asRuntimeInvisibleParameterAnnotationsAttribute /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleParameterAnnotationsAttribute   /**/ asRuntimeVisibleParameterAnnotationsAttribute   /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public TypeAnnotationsAttribute                      /**/ asTypeAnnotationsAttribute                      /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleTypeAnnotationsAttribute      /**/ asRuntimeInvisibleTypeAnnotationsAttribute      /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleTypeAnnotationsAttribute        /**/ asRuntimeVisibleTypeAnnotationsAttribute        /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public SignatureAttribute                            /**/ asSignatureAttribute                            /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public SourceFileAttribute                           /**/ asSourceFileAttribute                           /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public StackMapTableAttribute                        /**/ asStackMapTableAttribute                        /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public SyntheticAttribute                            /**/ asSyntheticAttribute                            /**/() { throw new AssertionError(); }
}
