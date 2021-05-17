package mikenakis.bytecode;

import mikenakis.bytecode.attributes.AnnotationDefaultAttribute;
import mikenakis.bytecode.attributes.AnnotationsAttribute;
import mikenakis.bytecode.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.ConstantValueAttribute;
import mikenakis.bytecode.attributes.DeprecatedAttribute;
import mikenakis.bytecode.attributes.EnclosingMethodAttribute;
import mikenakis.bytecode.attributes.ExceptionsAttribute;
import mikenakis.bytecode.attributes.InnerClassesAttribute;
import mikenakis.bytecode.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.attributes.MethodParametersAttribute;
import mikenakis.bytecode.attributes.ParameterAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleParameterAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeInvisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleParameterAnnotationsAttribute;
import mikenakis.bytecode.attributes.RuntimeVisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.SignatureAttribute;
import mikenakis.bytecode.attributes.SourceFileAttribute;
import mikenakis.bytecode.attributes.StackMapTableAttribute;
import mikenakis.bytecode.attributes.SyntheticAttribute;
import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.attributes.UnknownAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents an "Attribute" in a java class file.
 *
 * Predefined attribute names:
 * (+ = must be recognized and correctly read)
 * <pre>
 * + ConstantValue
 * + Code
 * + StackMapTable
 * + Exceptions
 * + InnerClasses
 * + EnclosingMethod
 * + Synthetic
 * + Signature
 *   SourceFile
 *   SourceDebugExtension
 *   LineNumberTable
 *   LocalVariableTable
 *   LocalVariableTypeTable
 *   Deprecated
 * + RuntimeVisibleAnnotations
 * + RuntimeInvisibleAnnotations
 * + RuntimeVisibleParameterAnnotations
 * + RuntimeInvisibleParameterAnnotations
 * + AnnotationDefault
 * + BootstrapMethods
 * </pre>
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Attribute extends Printable
{
	protected static Optional<Attribute> tryFrom( Attributes attributes, String name )
	{
		return attributes.tryGetAttributeByName( name );
	}

	public final String name;
	private final Runnable observer;

	protected Attribute( Runnable observer, String name )
	{
		this.name = name;
		this.observer = observer;
	}

	public abstract void intern( ConstantPool constantPool );

	public abstract void write( ConstantPool constantPool, BufferWriter bufferWriter );

	//@formatter:off
	public Optional<AnnotationDefaultAttribute>                    tryAsAnnotationDefaultAttribute                   () { return Optional.empty(); }
	public Optional<BootstrapMethodsAttribute>                     tryAsBootstrapMethodsAttribute                    () { return Optional.empty(); }
	public Optional<CodeAttribute>                                 tryAsCodeAttribute                                () { return Optional.empty(); }
	public Optional<ConstantValueAttribute>                        tryAsConstantValueAttribute                       () { return Optional.empty(); }
	public Optional<DeprecatedAttribute>                           tryAsDeprecatedAttribute                          () { return Optional.empty(); }
	public Optional<EnclosingMethodAttribute>                      tryAsEnclosingMethodAttribute                     () { return Optional.empty(); }
	public Optional<ExceptionsAttribute>                           tryAsExceptionsAttribute                          () { return Optional.empty(); }
	public Optional<InnerClassesAttribute>                         tryAsInnerClassesAttribute                        () { return Optional.empty(); }
	public Optional<LineNumberTableAttribute>                      tryAsLineNumberTableAttribute                     () { return Optional.empty(); }
	public Optional<LocalVariableTableAttribute>                   tryAsLocalVariableTableAttribute                  () { return Optional.empty(); }
	public Optional<LocalVariableTypeTableAttribute>               tryAsLocalVariableTypeTableAttribute              () { return Optional.empty(); }
	public Optional<MethodParametersAttribute>                     tryAsMethodParametersAttribute                    () { return Optional.empty(); }
	public Optional<AnnotationsAttribute>                          tryAsAnnotationsAttribute                         () { return Optional.empty(); }
	public Optional<RuntimeVisibleAnnotationsAttribute>            tryAsRuntimeVisibleAnnotationsAttribute           () { return Optional.empty(); }
	public Optional<RuntimeInvisibleAnnotationsAttribute>          tryAsRuntimeInvisibleAnnotationsAttribute         () { return Optional.empty(); }
	public Optional<ParameterAnnotationsAttribute>                 tryAsParameterAnnotationsAttribute                () { return Optional.empty(); }
	public Optional<RuntimeInvisibleParameterAnnotationsAttribute> tryAsRuntimeInvisibleParameterAnnotationsAttribute() { return Optional.empty(); }
	public Optional<RuntimeVisibleParameterAnnotationsAttribute>   tryAsRuntimeVisibleParameterAnnotationsAttribute  () { return Optional.empty(); }
	public Optional<TypeAnnotationsAttribute>                      tryAsTypeAnnotationsAttribute                     () { return Optional.empty(); }
	public Optional<RuntimeVisibleTypeAnnotationsAttribute>        tryAsRuntimeVisibleTypeAnnotationsAttribute       () { return Optional.empty(); }
	public Optional<RuntimeInvisibleTypeAnnotationsAttribute>      tryAsRuntimeInvisibleTypeAnnotationsAttribute     () { return Optional.empty(); }
	public Optional<SignatureAttribute>                            tryAsSignatureAttribute                           () { return Optional.empty(); }
	public Optional<SourceFileAttribute>                           tryAsSourceFileAttribute                          () { return Optional.empty(); }
	public Optional<StackMapTableAttribute>                        tryAsStackMapTableAttribute                       () { return Optional.empty(); }
	public Optional<SyntheticAttribute>                            tryAsSyntheticAttribute                           () { return Optional.empty(); }
	public Optional<UnknownAttribute>                              tryAsUnknownAttribute                             () { return Optional.empty(); }
	//@formatter:on

	//@formatter:off
	public final AnnotationDefaultAttribute                    asAnnotationDefaultAttribute                   () { return tryAsAnnotationDefaultAttribute                   ().orElseThrow();  }
	public final BootstrapMethodsAttribute                     asBootstrapMethodsAttribute                    () { return tryAsBootstrapMethodsAttribute                    ().orElseThrow();  }
	public final CodeAttribute                                 asCodeAttribute                                () { return tryAsCodeAttribute                                ().orElseThrow();  }
	public final ConstantValueAttribute                        asConstantValueAttribute                       () { return tryAsConstantValueAttribute                       ().orElseThrow();  }
	public final DeprecatedAttribute                           asDeprecatedAttribute                          () { return tryAsDeprecatedAttribute                          ().orElseThrow();  }
	public final EnclosingMethodAttribute                      asEnclosingMethodAttribute                     () { return tryAsEnclosingMethodAttribute                     ().orElseThrow();  }
	public final ExceptionsAttribute                           asExceptionsAttribute                          () { return tryAsExceptionsAttribute                          ().orElseThrow();  }
	public final InnerClassesAttribute                         asInnerClassesAttribute                        () { return tryAsInnerClassesAttribute                        ().orElseThrow();  }
	public final LineNumberTableAttribute                      asLineNumberTableAttribute                     () { return tryAsLineNumberTableAttribute                     ().orElseThrow();  }
	public final LocalVariableTableAttribute                   asLocalVariableTableAttribute                  () { return tryAsLocalVariableTableAttribute                  ().orElseThrow();  }
	public final LocalVariableTypeTableAttribute               asLocalVariableTypeTableAttribute              () { return tryAsLocalVariableTypeTableAttribute              ().orElseThrow();  }
	public final MethodParametersAttribute                     asMethodParametersAttribute                    () { return tryAsMethodParametersAttribute                    ().orElseThrow();  }
	public final AnnotationsAttribute                          asAnnotationsAttribute                         () { return tryAsAnnotationsAttribute                         ().orElseThrow();  }
	public final RuntimeVisibleAnnotationsAttribute            asRuntimeVisibleAnnotationsAttribute           () { return tryAsRuntimeVisibleAnnotationsAttribute           ().orElseThrow();  }
	public final RuntimeInvisibleAnnotationsAttribute          asRuntimeInvisibleAnnotationsAttribute         () { return tryAsRuntimeInvisibleAnnotationsAttribute         ().orElseThrow();  }
	public final ParameterAnnotationsAttribute                 asParameterAnnotationsAttribute                () { return tryAsParameterAnnotationsAttribute                ().orElseThrow();  }
	public final RuntimeInvisibleParameterAnnotationsAttribute asRuntimeInvisibleParameterAnnotationsAttribute() { return tryAsRuntimeInvisibleParameterAnnotationsAttribute().orElseThrow();  }
	public final RuntimeVisibleParameterAnnotationsAttribute   asRuntimeVisibleParameterAnnotationsAttribute  () { return tryAsRuntimeVisibleParameterAnnotationsAttribute  ().orElseThrow();  }
	public final TypeAnnotationsAttribute                      asTypeAnnotationsAttribute                     () { return tryAsTypeAnnotationsAttribute                     ().orElseThrow();  }
	public final RuntimeInvisibleTypeAnnotationsAttribute      asRuntimeInvisibleTypeAnnotationsAttribute     () { return tryAsRuntimeInvisibleTypeAnnotationsAttribute     ().orElseThrow();  }
	public final RuntimeVisibleTypeAnnotationsAttribute        asRuntimeVisibleTypeAnnotationsAttribute       () { return tryAsRuntimeVisibleTypeAnnotationsAttribute       ().orElseThrow();  }
	public final SignatureAttribute                            asSignatureAttribute                           () { return tryAsSignatureAttribute                           ().orElseThrow();  }
	public final SourceFileAttribute                           asSourceFileAttribute                          () { return tryAsSourceFileAttribute                          ().orElseThrow();  }
	public final StackMapTableAttribute                        asStackMapTableAttribute                       () { return tryAsStackMapTableAttribute                       ().orElseThrow();  }
	public final SyntheticAttribute                            asSyntheticAttribute                           () { return tryAsSyntheticAttribute                           ().orElseThrow();  }
	public final UnknownAttribute                              asUnknownAttribute                             () { return tryAsUnknownAttribute                             ().orElseThrow();  }
	//@formatter:on

	//@formatter:off
	public final boolean isAnnotationDefaultAttribute                   () { return tryAsAnnotationDefaultAttribute                   ().isPresent(); }
	public final boolean isBootstrapMethodsAttribute                    () { return tryAsBootstrapMethodsAttribute                    ().isPresent(); }
	public final boolean isCodeAttribute                                () { return tryAsCodeAttribute                                ().isPresent(); }
	public final boolean isConstantValueAttribute                       () { return tryAsConstantValueAttribute                       ().isPresent(); }
	public final boolean isDeprecatedAttribute                          () { return tryAsDeprecatedAttribute                          ().isPresent(); }
	public final boolean isEnclosingMethodAttribute                     () { return tryAsEnclosingMethodAttribute                     ().isPresent(); }
	public final boolean isExceptionsAttribute                          () { return tryAsExceptionsAttribute                          ().isPresent(); }
	public final boolean isInnerClassesAttribute                        () { return tryAsInnerClassesAttribute                        ().isPresent(); }
	public final boolean isLineNumberTableAttribute                     () { return tryAsLineNumberTableAttribute                     ().isPresent(); }
	public final boolean isLocalVariableTableAttribute                  () { return tryAsLocalVariableTableAttribute                  ().isPresent(); }
	public final boolean isLocalVariableTypeTableAttribute              () { return tryAsLocalVariableTypeTableAttribute              ().isPresent(); }
	public final boolean isMethodParametersAttribute                    () { return tryAsMethodParametersAttribute                    ().isPresent(); }
	public final boolean isAnnotationsAttribute                         () { return tryAsAnnotationsAttribute                         ().isPresent(); }
	public final boolean isRuntimeVisibleAnnotationsAttribute           () { return tryAsRuntimeVisibleAnnotationsAttribute           ().isPresent(); }
	public final boolean isRuntimeInvisibleAnnotationsAttribute         () { return tryAsRuntimeInvisibleAnnotationsAttribute         ().isPresent(); }
	public final boolean isParameterAnnotationsAttribute                () { return tryAsParameterAnnotationsAttribute                ().isPresent(); }
	public final boolean isRuntimeInvisibleParameterAnnotationsAttribute() { return tryAsRuntimeInvisibleParameterAnnotationsAttribute().isPresent(); }
	public final boolean isRuntimeVisibleParameterAnnotationsAttribute  () { return tryAsRuntimeVisibleParameterAnnotationsAttribute  ().isPresent(); }
	public final boolean isTypeAnnotationsAttribute                     () { return tryAsTypeAnnotationsAttribute                     ().isPresent(); }
	public final boolean isRuntimeInvisibleTypeAnnotationsAttribute     () { return tryAsRuntimeInvisibleTypeAnnotationsAttribute     ().isPresent(); }
	public final boolean isRuntimeVisibleTypeAnnotationsAttribute       () { return tryAsRuntimeVisibleTypeAnnotationsAttribute       ().isPresent(); }
	public final boolean isSignatureAttribute                           () { return tryAsSignatureAttribute                           ().isPresent(); }
	public final boolean isSourceFileAttribute                          () { return tryAsSourceFileAttribute                          ().isPresent(); }
	public final boolean isStackMapTableAttribute                       () { return tryAsStackMapTableAttribute                       ().isPresent(); }
	public final boolean isSyntheticAttribute                           () { return tryAsSyntheticAttribute                           ().isPresent(); }
	public final boolean isUnknownAttribute                             () { return tryAsUnknownAttribute                             ().isPresent(); }
	//@formatter:on

	public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		/* by default, nothing to do */
	}

	public void markAsDirty()
	{
		observer.run();
	}
}
