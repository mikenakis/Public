package mikenakis.bytecode.model.attributes.target;

import java.util.Optional;

public abstract class Target // "target_info" in jvms-4.7.20.1
{
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
}
