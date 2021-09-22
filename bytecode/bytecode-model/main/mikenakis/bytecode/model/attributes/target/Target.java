package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.exceptions.InvalidTargetTagException;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public abstract class Target // "target_info" in jvms-4.7.20.1
{
	public static final int tagTypeParameterDeclarationOfGenericClassOrInterface                                        /**/ = 0X00; // TypeParameterTarget       // type parameter declaration of generic class or interface
	public static final int tagTypeParameterDeclarationOfGenericMethodOrConstructor                                     /**/ = 0X01; // TypeParameterTarget       // type parameter declaration of generic method or constructor
	public static final int tagTypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration /**/ = 0X10; // SupertypeTarget           // type in extends or implements clause of class declaration (including the direct superclass or direct superinterface of an anonymous class declaration), or in extends clause of interface declaration
	public static final int tagTypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface                           /**/ = 0X11; // TypeParameterBoundTarget  // type in bound of type parameter declaration of generic class or interface
	public static final int tagTypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor                        /**/ = 0X12; // TypeParameterBoundTarget  // type in bound of type parameter declaration of generic method or constructor
	public static final int tagTypeInFieldDeclaration                                                                   /**/ = 0X13; // EmptyTarget               // type in field declaration
	public static final int tagReturnTypeOfMethodOrTypeOfNewlyConstructedObject                                         /**/ = 0X14; // EmptyTarget               // return type of method, or type of newly constructed object
	public static final int tagReceiverTypeOfMethodOrConstructor                                                        /**/ = 0X15; // EmptyTarget               // receiver type of method or constructor
	public static final int tagTypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression                    /**/ = 0X16; // FormalParameterTarget     // type in formal parameter declaration of method, constructor, or lambda expression
	public static final int tagTypeInThrowsClauseOfMethodOrConstructor                                                  /**/ = 0X17; // ThrowsTarget              // type in throws clause of method or constructor
	public static final int tagTypeInLocalVariableDeclaration                                                           /**/ = 0X40; // LocalvarTarget            // type in local variable declaration
	public static final int tagTypeInResourceVariableDeclaration                                                        /**/ = 0X41; // LocalvarTarget            // type in resource variable declaration
	public static final int tagTypeInExceptionParameterDeclaration                                                      /**/ = 0X42; // CatchTarget               // type in exception parameter declaration
	public static final int tagTypeInInstanceofExpression                                                               /**/ = 0X43; // OffsetTarget              // type in instanceof expression
	public static final int tagTypeInNewExpression                                                                      /**/ = 0X44; // OffsetTarget              // type in new expression
	public static final int tagTypeInMethodReferenceExpressionUsingNew                                                  /**/ = 0X45; // OffsetTarget              // type in method reference expression using ::new
	public static final int tagTypeInMethodReferenceExpressionUsingIdentifier                                           /**/ = 0X46; // OffsetTarget              // type in method reference expression using ::Identifier
	public static final int tagTypeInCastExpression                                                                     /**/ = 0X47; // TypeArgumentTarget        // type in cast expression
	public static final int tagTypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement /**/ = 0X48; // TypeArgumentTarget        // type argument for generic constructor in new expression or explicit constructor invocation statement
	public static final int tagTypeArgumentForGenericMethodInMethodInvocationExpression                                 /**/ = 0X49; // TypeArgumentTarget        // type argument for generic method in method invocation expression
	public static final int tagTypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew                     /**/ = 0X4A; // TypeArgumentTarget        // type argument for generic constructor in method reference expression using ::new
	public static final int tagTypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier                   /**/ = 0X4B; // TypeArgumentTarget        // type argument for generic method in method reference expression using ::Identifier

	public static String tagName( int tag )
	{
		return switch( tag )
			{
				case tagTypeParameterDeclarationOfGenericClassOrInterface                                        /**/ -> "TypeParameterDeclarationOfGenericClassOrInterface";
				case tagTypeParameterDeclarationOfGenericMethodOrConstructor                                     /**/ -> "TypeParameterDeclarationOfGenericMethodOrConstructor";
				case tagTypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration /**/ -> "TypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration";
				case tagTypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface                           /**/ -> "TypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface";
				case tagTypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor                        /**/ -> "TypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor";
				case tagTypeInFieldDeclaration                                                                   /**/ -> "TypeInFieldDeclaration";
				case tagReturnTypeOfMethodOrTypeOfNewlyConstructedObject                                         /**/ -> "ReturnTypeOfMethodOrTypeOfNewlyConstructedObject";
				case tagReceiverTypeOfMethodOrConstructor                                                        /**/ -> "ReceiverTypeOfMethodOrConstructor";
				case tagTypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression                    /**/ -> "TypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression";
				case tagTypeInThrowsClauseOfMethodOrConstructor                                                  /**/ -> "TypeInThrowsClauseOfMethodOrConstructor";
				case tagTypeInLocalVariableDeclaration                                                           /**/ -> "TypeInLocalVariableDeclaration";
				case tagTypeInResourceVariableDeclaration                                                        /**/ -> "TypeInResourceVariableDeclaration";
				case tagTypeInExceptionParameterDeclaration                                                      /**/ -> "TypeInExceptionParameterDeclaration";
				case tagTypeInInstanceofExpression                                                               /**/ -> "TypeInInstanceofExpression";
				case tagTypeInNewExpression                                                                      /**/ -> "TypeInNewExpression";
				case tagTypeInMethodReferenceExpressionUsingNew                                                  /**/ -> "TypeInMethodReferenceExpressionUsingNew";
				case tagTypeInMethodReferenceExpressionUsingIdentifier                                           /**/ -> "TypeInMethodReferenceExpressionUsingIdentifier";
				case tagTypeInCastExpression                                                                     /**/ -> "TypeInCastExpression";
				case tagTypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement /**/ -> "TypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement";
				case tagTypeArgumentForGenericMethodInMethodInvocationExpression                                 /**/ -> "TypeArgumentForGenericMethodInMethodInvocationExpression";
				case tagTypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew                     /**/ -> "TypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew";
				case tagTypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier                   /**/ -> "TypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier";
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
}
