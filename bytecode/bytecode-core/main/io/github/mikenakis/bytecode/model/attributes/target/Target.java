package io.github.mikenakis.bytecode.model.attributes.target;

import io.github.mikenakis.bytecode.exceptions.InvalidTargetTagException;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

public abstract class Target // "target_info" in jvms-4.7.20.1
{
	public static final int tag_ClassTypeParameter       /**/ = 0x00; // TypeParameterTarget       // type parameter declaration of generic class or interface
	public static final int tag_MethodTypeParameter      /**/ = 0x01; // TypeParameterTarget       // type parameter declaration of generic method or constructor
	public static final int tag_Supertype                /**/ = 0x10; // SupertypeTarget           // type in extends or implements clause of class declaration (including the direct superclass or direct superinterface of an anonymous class declaration), or in extends clause of interface declaration
	public static final int tag_ClassTypeBound           /**/ = 0x11; // TypeParameterBoundTarget  // type in bound of type parameter declaration of generic class or interface
	public static final int tag_MethodTypeBound          /**/ = 0x12; // TypeParameterBoundTarget  // type in bound of type parameter declaration of generic method or constructor
	public static final int tag_FieldType                /**/ = 0x13; // EmptyTarget               // type in field declaration
	public static final int tag_ReturnType               /**/ = 0x14; // EmptyTarget               // return type of method, or type of newly constructed object
	public static final int tag_ReceiverType             /**/ = 0x15; // EmptyTarget               // receiver type of method or constructor
	public static final int tag_FormalParameter          /**/ = 0x16; // FormalParameterTarget     // type in formal parameter declaration of method, constructor, or lambda expression
	public static final int tag_Throws                   /**/ = 0x17; // ThrowsTarget              // type in throws clause of method or constructor
	public static final int tag_LocalVariable            /**/ = 0x40; // LocalvarTarget            // type in local variable declaration
	public static final int tag_ResourceLocalVariable    /**/ = 0x41; // LocalvarTarget            // type in resource variable declaration
	public static final int tag_Catch                    /**/ = 0x42; // CatchTarget               // type in exception parameter declaration
	public static final int tag_InstanceOfOffset         /**/ = 0x43; // OffsetTarget              // type in instanceof expression
	public static final int tag_NewExpressionOffset      /**/ = 0x44; // OffsetTarget              // type in new expression
	public static final int tag_NewMethodOffset          /**/ = 0x45; // OffsetTarget              // type in method reference expression using ::new
	public static final int tag_IdentifierMethodOffset   /**/ = 0x46; // OffsetTarget              // type in method reference expression using ::Identifier
	public static final int tag_CastArgument             /**/ = 0x47; // TypeArgumentTarget        // type in cast expression
	public static final int tag_ConstructorArgument      /**/ = 0x48; // TypeArgumentTarget        // type argument for generic constructor in new expression or explicit constructor invocation statement
	public static final int tag_MethodArgument           /**/ = 0x49; // TypeArgumentTarget        // type argument for generic method in method invocation expression
	public static final int tag_NewMethodArgument        /**/ = 0x4A; // TypeArgumentTarget        // type argument for generic constructor in method reference expression using ::new
	public static final int tag_IdentifierMethodArgument /**/ = 0x4B; // TypeArgumentTarget        // type argument for generic method in method reference expression using ::Identifier

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

	@ExcludeFromJacocoGeneratedReport public CatchTarget              /**/ asCatchTarget()              /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public EmptyTarget              /**/ asEmptyTarget()              /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public FormalParameterTarget    /**/ asFormalParameterTarget()    /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableTarget      /**/ asLocalVariableTarget()      /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public OffsetTarget             /**/ asOffsetTarget()             /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public SupertypeTarget          /**/ asSupertypeTarget()          /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public ThrowsTarget             /**/ asThrowsTarget()             /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public TypeArgumentTarget       /**/ asTypeArgumentTarget()       /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public TypeParameterBoundTarget /**/ asTypeParameterBoundTarget() /**/ { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public TypeParameterTarget      /**/ asTypeParameterTarget()      /**/ { throw new AssertionError(); }

	public final int tag;

	protected Target( int tag )
	{
		this.tag = tag;
	}

	public abstract void intern( Interner interner );
	public abstract void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap );
}
