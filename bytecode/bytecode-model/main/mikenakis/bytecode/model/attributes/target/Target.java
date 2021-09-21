package mikenakis.bytecode.model.attributes.target;

import mikenakis.kit.Kit;

import java.util.List;
import java.util.Map;

public abstract class Target // "target_info" in jvms-4.7.20.1
{
	public interface Switcher<T>
	{
		T caseCatchTarget();
		T caseEmptyTarget();
		T caseFormalParameterTarget();
		T caseLocalVariableTarget();
		T caseOffsetTarget();
		T caseSupertypeTarget();
		T caseThrowsTarget();
		T caseTypeArgumentTarget();
		T caseTypeParameterBoundTarget();
		T caseTypeParameterTarget();
	}

	public static <T> T doSwitch( Type type, Switcher<T> switcher )
	{
		return switch( type )
			{
				case TypeParameterDeclarationOfGenericClassOrInterface, //
					TypeParameterDeclarationOfGenericMethodOrConstructor -> //
					switcher.caseTypeParameterTarget();
				case TypeInExtendsOrImplementsClauseOfClassDeclarationOrInExtendsClauseOfInterfaceDeclaration -> //
					switcher.caseSupertypeTarget();
				case TypeInBoundOfTypeParameterDeclarationOfGenericClassOrInterface, //
					TypeInBoundOfTypeParameterDeclarationOfGenericMethodOrConstructor -> //
					switcher.caseTypeParameterBoundTarget();
				case TypeInFieldDeclaration, //
					ReturnTypeOfMethodOrTypeOfNewlyConstructedObject, //
					ReceiverTypeOfMethodOrConstructor -> //
					switcher.caseEmptyTarget();
				case TypeInFormalParameterDeclarationOfMethodConstructorOrLambdaExpression -> //
					switcher.caseFormalParameterTarget();
				case TypeInThrowsClauseOfMethodOrConstructor -> //
					switcher.caseThrowsTarget();
				case TypeInLocalVariableDeclaration, //
					TypeInResourceVariableDeclaration -> //
					switcher.caseLocalVariableTarget();
				case TypeInExceptionParameterDeclaration -> //
					switcher.caseCatchTarget();
				case TypeInInstanceofExpression, TypeInNewExpression, TypeInMethodReferenceExpressionUsingNew, TypeInMethodReferenceExpressionUsingIdentifier -> //
					switcher.caseOffsetTarget();
				case TypeInCastExpression, //
					TypeArgumentForGenericConstructorInNewExpressionOrExplicitConstructorInvocationStatement, //
					TypeArgumentForGenericMethodInMethodInvocationExpression, //
					TypeArgumentForGenericConstructorInMethodReferenceExpressionUsingNew, //
					TypeArgumentForGenericMethodInMethodReferenceExpressionUsingIdentifier -> //
					switcher.caseTypeArgumentTarget();
			};
	}

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

		private static final List<Type> values = List.of( values() );
		private static final Map<Integer,Type> map = Kit.iterable.valuesToMap( values, v -> v.number );

		public final int number;

		Type( int number )
		{
			this.number = number;
		}

		public static Type fromNumber( int value )
		{
			return Kit.map.get( map, value );
		}
	}

	public interface Visitor<T>
	{
		T visit( CatchTarget              /**/ catchTarget );
		T visit( EmptyTarget              /**/ emptyTarget );
		T visit( FormalParameterTarget    /**/ formalParameterTarget );
		T visit( LocalVariableTarget      /**/ localVariableTarget );
		T visit( OffsetTarget             /**/ offsetTarget );
		T visit( SupertypeTarget          /**/ supertypeTarget );
		T visit( ThrowsTarget             /**/ throwsTarget );
		T visit( TypeArgumentTarget       /**/ typeArgumentTarget );
		T visit( TypeParameterBoundTarget /**/ typeParameterBoundTarget );
		T visit( TypeParameterTarget      /**/ typeParameterTarget );
	}

	public <T> T visit( Visitor<T> visitor )
	{
		return doSwitch( type, new Switcher<>()
		{
			@Override public T caseCatchTarget()              /**/ { return visitor.visit( asCatchTarget() ); }
			@Override public T caseEmptyTarget()              /**/ { return visitor.visit( asEmptyTarget() ); }
			@Override public T caseFormalParameterTarget()    /**/ { return visitor.visit( asFormalParameterTarget() ); }
			@Override public T caseLocalVariableTarget()      /**/ { return visitor.visit( asLocalVariableTarget() ); }
			@Override public T caseOffsetTarget()             /**/ { return visitor.visit( asOffsetTarget() ); }
			@Override public T caseSupertypeTarget()          /**/ { return visitor.visit( asSupertypeTarget() ); }
			@Override public T caseThrowsTarget()             /**/ { return visitor.visit( asThrowsTarget() ); }
			@Override public T caseTypeArgumentTarget()       /**/ { return visitor.visit( asTypeArgumentTarget() ); }
			@Override public T caseTypeParameterBoundTarget() /**/ { return visitor.visit( asTypeParameterBoundTarget() ); }
			@Override public T caseTypeParameterTarget()      /**/ { return visitor.visit( asTypeParameterTarget() ); }
		} );
	}

	public CatchTarget              /**/ asCatchTarget()              /**/ { return Kit.fail(); }
	public EmptyTarget              /**/ asEmptyTarget()              /**/ { return Kit.fail(); }
	public FormalParameterTarget    /**/ asFormalParameterTarget()    /**/ { return Kit.fail(); }
	public LocalVariableTarget      /**/ asLocalVariableTarget()      /**/ { return Kit.fail(); }
	public OffsetTarget             /**/ asOffsetTarget()             /**/ { return Kit.fail(); }
	public SupertypeTarget          /**/ asSupertypeTarget()          /**/ { return Kit.fail(); }
	public ThrowsTarget             /**/ asThrowsTarget()             /**/ { return Kit.fail(); }
	public TypeArgumentTarget       /**/ asTypeArgumentTarget()       /**/ { return Kit.fail(); }
	public TypeParameterBoundTarget /**/ asTypeParameterBoundTarget() /**/ { return Kit.fail(); }
	public TypeParameterTarget      /**/ asTypeParameterTarget()      /**/ { return Kit.fail(); }

	public final Type type;

	protected Target( Type type )
	{
		this.type = type;
	}
}
