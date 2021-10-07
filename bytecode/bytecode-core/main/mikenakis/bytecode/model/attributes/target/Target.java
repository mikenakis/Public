package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.exceptions.InvalidTargetTagException;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public abstract class Target // "target_info" in jvms-4.7.20.1
{
	public static final int tag_ClassTypeParameter       /**/ = 0X00; // TypeParameterTarget       // type parameter declaration of generic class or interface
	public static final int tag_MethodTypeParameter      /**/ = 0X01; // TypeParameterTarget       // type parameter declaration of generic method or constructor
	public static final int tag_Supertype                /**/ = 0X10; // SupertypeTarget           // type in extends or implements clause of class declaration (including the direct superclass or direct superinterface of an anonymous class declaration), or in extends clause of interface declaration
	public static final int tag_ClassTypeBound           /**/ = 0X11; // TypeParameterBoundTarget  // type in bound of type parameter declaration of generic class or interface
	public static final int tag_MethodTypeBound          /**/ = 0X12; // TypeParameterBoundTarget  // type in bound of type parameter declaration of generic method or constructor
	public static final int tag_FieldType                /**/ = 0X13; // EmptyTarget               // type in field declaration
	public static final int tag_ReturnType               /**/ = 0X14; // EmptyTarget               // return type of method, or type of newly constructed object
	public static final int tag_ReceiverType             /**/ = 0X15; // EmptyTarget               // receiver type of method or constructor
	public static final int tag_FormalParameter          /**/ = 0X16; // FormalParameterTarget     // type in formal parameter declaration of method, constructor, or lambda expression
	public static final int tag_Throws                   /**/ = 0X17; // ThrowsTarget              // type in throws clause of method or constructor
	public static final int tag_LocalVariable            /**/ = 0X40; // LocalvarTarget            // type in local variable declaration
	public static final int tag_ResourceLocalVariable    /**/ = 0X41; // LocalvarTarget            // type in resource variable declaration
	public static final int tag_Catch                    /**/ = 0X42; // CatchTarget               // type in exception parameter declaration
	public static final int tag_InstanceOfOffset         /**/ = 0X43; // OffsetTarget              // type in instanceof expression
	public static final int tag_NewExpressionOffset      /**/ = 0X44; // OffsetTarget              // type in new expression
	public static final int tag_NewMethodOffset          /**/ = 0X45; // OffsetTarget              // type in method reference expression using ::new
	public static final int tag_IdentifierMethodOffset   /**/ = 0X46; // OffsetTarget              // type in method reference expression using ::Identifier
	public static final int tag_CastArgument             /**/ = 0X47; // TypeArgumentTarget        // type in cast expression
	public static final int tag_ConstructorArgument      /**/ = 0X48; // TypeArgumentTarget        // type argument for generic constructor in new expression or explicit constructor invocation statement
	public static final int tag_MethodArgument           /**/ = 0X49; // TypeArgumentTarget        // type argument for generic method in method invocation expression
	public static final int tag_NewMethodArgument        /**/ = 0X4A; // TypeArgumentTarget        // type argument for generic constructor in method reference expression using ::new
	public static final int tag_IdentifierMethodArgument /**/ = 0X4B; // TypeArgumentTarget        // type argument for generic method in method reference expression using ::Identifier

	public static String tagName( int tag )
	{
		return switch( tag )
			{
				case tag_ClassTypeParameter        /**/ -> "ClassTypeParameter";
				case tag_MethodTypeParameter       /**/ -> "MethodTypeParameter";
				case tag_Supertype                 /**/ -> "Supertype";
				case tag_ClassTypeBound            /**/ -> "ClassTypeBound";
				case tag_MethodTypeBound           /**/ -> "MethodTypeBound";
				case tag_FieldType                 /**/ -> "FieldType";
				case tag_ReturnType                /**/ -> "ReturnType";
				case tag_ReceiverType              /**/ -> "ReceiverType";
				case tag_FormalParameter           /**/ -> "FormalParameter";
				case tag_Throws                    /**/ -> "Throws";
				case tag_LocalVariable             /**/ -> "LocalVariable";
				case tag_ResourceLocalVariable     /**/ -> "ResourceLocalVariable";
				case tag_Catch                     /**/ -> "Catch";
				case tag_InstanceOfOffset          /**/ -> "InstanceOfOffset";
				case tag_NewExpressionOffset       /**/ -> "NewExpressionOffset";
				case tag_NewMethodOffset           /**/ -> "NewMethodOffset";
				case tag_IdentifierMethodOffset    /**/ -> "IdentifierMethodOffset";
				case tag_CastArgument              /**/ -> "CastArgument";
				case tag_ConstructorArgument       /**/ -> "ConstructorArgument";
				case tag_MethodArgument            /**/ -> "MethodArgument";
				case tag_NewMethodArgument         /**/ -> "NewMethodArgument";
				case tag_IdentifierMethodArgument  /**/ -> "IdentifierMethodArgument";
				default -> throw new InvalidTargetTagException( tag );
			};
	}

	@ExcludeFromJacocoGeneratedReport public CatchTarget              /**/ asCatchTarget()              /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public EmptyTarget              /**/ asEmptyTarget()              /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FormalParameterTarget    /**/ asFormalParameterTarget()    /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableTarget      /**/ asLocalVariableTarget()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public OffsetTarget             /**/ asOffsetTarget()             /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SupertypeTarget          /**/ asSupertypeTarget()          /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ThrowsTarget             /**/ asThrowsTarget()             /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public TypeArgumentTarget       /**/ asTypeArgumentTarget()       /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public TypeParameterBoundTarget /**/ asTypeParameterBoundTarget() /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public TypeParameterTarget      /**/ asTypeParameterTarget()      /**/ { return Kit.fail(); }

	public final int tag;

	protected Target( int tag )
	{
		this.tag = tag;
	}

	public abstract void intern( Interner interner );
	public abstract void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap );
}
