package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.Kit;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Target // "target_info" in jvms-4.7.20.1
{
	@SuppressWarnings( "FieldNamingConvention" )
	public enum Type
	{
		TypeParameterDeclarationOfGenericClassOrInterface                                        /**/( 0X00 ), // TypeParameterTarget       // type parameter declaration of generic class or interface
		TypeParameterDeclarationOfGenericMethodOrConstructor                                     /**/( 0X01 ), // TypeParameterTarget       // type parameter declaration of generic method or constructor
		TypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration /**/( 0X10 ), // SupertypeTarget           // type in extends or implements clause of class declaration (including the direct superclass or direct superinterface of an anonymous class declaration), or in extends clause of interface declaration
		TypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface                           /**/( 0X11 ), // TypeParameterBoundTarget  // type in bound of type parameter declaration of generic class or interface
		TypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor                        /**/( 0X12 ), // TypeParameterBoundTarget  // type in bound of type parameter declaration of generic method or constructor
		TypeInFieldDeclaration                                                                   /**/( 0X13 ), // EmptyTarget               // type in field declaration
		ReturnTypeOfMethodOrTypeOfNewlyConstructedObject                                         /**/( 0X14 ), // EmptyTarget               // return type of method, or type of newly constructed object
		ReceiverTypeOfMethodOrConstructor                                                        /**/( 0X15 ), // EmptyTarget               // receiver type of method or constructor
		TypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression                    /**/( 0X16 ), // FormalParameterTarget     // type in formal parameter declaration of method, constructor, or lambda expression
		TypeInThrowsClauseOfMethodOrConstructor                                                  /**/( 0X17 ), // ThrowsTarget              // type in throws clause of method or constructor
		TypeInLocalVariableDeclaration                                                           /**/( 0X40 ), // LocalvarTarget            // type in local variable declaration
		TypeInResourceVariableDeclaration                                                        /**/( 0X41 ), // LocalvarTarget            // type in resource variable declaration
		TypeInExceptionParameterDeclaration                                                      /**/( 0X42 ), // CatchTarget               // type in exception parameter declaration
		TypeInInstanceofExpression                                                               /**/( 0X43 ), // OffsetTarget              // type in instanceof expression
		TypeInNewExpression                                                                      /**/( 0X44 ), // OffsetTarget              // type in new expression
		TypeInMethodReferenceExpressionUsingNew                                                  /**/( 0X45 ), // OffsetTarget              // type in method reference expression using ::new
		TypeInMethodReferenceExpressionUsingIdentifier                                           /**/( 0X46 ), // OffsetTarget              // type in method reference expression using ::Identifier
		TypeInCastExpression                                                                     /**/( 0X47 ), // TypeArgumentTarget        // type in cast expression
		TypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement /**/( 0X48 ), // TypeArgumentTarget        // type argument for generic constructor in new expression or explicit constructor invocation statement
		TypeArgumentForGenericMethodInMethodInvocationExpression                                 /**/( 0X49 ), // TypeArgumentTarget        // type argument for generic method in method invocation expression
		TypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew                     /**/( 0X4A ), // TypeArgumentTarget        // type argument for generic constructor in method reference expression using ::new
		TypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier                   /**/( 0X4B ); // TypeArgumentTarget        // type argument for generic method in method reference expression using ::Identifier

		private static final Map<Integer,Type> map = Kit.iterable.valuesToMap( List.of( values() ), v -> v.tag );

		public final int tag;

		Type( int tag )
		{
			this.tag = tag;
		}

		public static Type fromTag( int value )
		{
			return Kit.map.get( map, value );
		}
	}






















































	//@formatter:off
	public Optional<CatchTarget>              tryAsCatchTarget()              { return Optional.empty(); }
	public Optional<EmptyTarget>              tryAsEmptyTarget()              { return Optional.empty(); }
	public Optional<FormalParameterTarget>    tryAsFormalParameterTarget()    { return Optional.empty(); }
	public Optional<LocalVariableTarget>      tryAsLocalVariableTarget()      { return Optional.empty(); }
	public Optional<OffsetTarget>             tryAsOffsetTarget()             { return Optional.empty(); }
	public Optional<SupertypeTarget>          tryAsSupertypeTarget()          { return Optional.empty(); }
	public Optional<ThrowsTarget>             tryAsThrowsTarget()             { return Optional.empty(); }
	public Optional<TypeArgumentTarget>       tryAsTypeArgumentTarget()       { return Optional.empty(); }
	public Optional<TypeParameterBoundTarget> tryAsTypeParameterBoundTarget() { return Optional.empty(); }
	public Optional<TypeParameterTarget>      tryAsTypeParameterTarget()      { return Optional.empty(); }

	public final CatchTarget              asCatchTarget()              { return tryAsCatchTarget()              .orElseThrow(); }
	public final EmptyTarget              asEmptyTarget()              { return tryAsEmptyTarget()              .orElseThrow(); }
	public final FormalParameterTarget    asFormalParameterTarget()    { return tryAsFormalParameterTarget()    .orElseThrow(); }
	public final LocalVariableTarget      asLocalVariableTarget()      { return tryAsLocalVariableTarget()      .orElseThrow(); }
	public final OffsetTarget             asOffsetTarget()             { return tryAsOffsetTarget()             .orElseThrow(); }
	public final SupertypeTarget          asSupertypeTarget()          { return tryAsSupertypeTarget()          .orElseThrow(); }
	public final ThrowsTarget             asThrowsTarget()             { return tryAsThrowsTarget()             .orElseThrow(); }
	public final TypeArgumentTarget       asTypeArgumentTarget()       { return tryAsTypeArgumentTarget()       .orElseThrow(); }
	public final TypeParameterBoundTarget asTypeParameterBoundTarget() { return tryAsTypeParameterBoundTarget() .orElseThrow(); }
	public final TypeParameterTarget      asTypeParameterTarget()      { return tryAsTypeParameterTarget()      .orElseThrow(); }

	public final boolean isCatchTarget()              { return tryAsCatchTarget()              .isPresent(); }
	public final boolean isEmptyTarget()              { return tryAsEmptyTarget()              .isPresent(); }
	public final boolean isFormalParameterTarget()    { return tryAsFormalParameterTarget()    .isPresent(); }
	public final boolean isLocalVariableTarget()      { return tryAsLocalVariableTarget()      .isPresent(); }
	public final boolean isOffsetTarget()             { return tryAsOffsetTarget()             .isPresent(); }
	public final boolean isSupertypeTarget()          { return tryAsSupertypeTarget()          .isPresent(); }
	public final boolean isThrowsTarget()             { return tryAsThrowsTarget()             .isPresent(); }
	public final boolean isTypeArgumentTarget()       { return tryAsTypeArgumentTarget()       .isPresent(); }
	public final boolean isTypeParameterBoundTarget() { return tryAsTypeParameterBoundTarget() .isPresent(); }
	public final boolean isTypeParameterTarget()      { return tryAsTypeParameterTarget()      .isPresent(); }
	//@formatter:on

	public final Type type;

	protected Target( Type type )
	{
		this.type = type;
	}
}
