package mikenakis.kit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to exclude certain methods from coverage report.
 *
 * The "Intellij IDEA" coverage runner does not support this annotation,
 * but IntellijIdea also supports the "Jacoco" coverage runner,
 * and this runner does support this annotation.
 *
 * The cumbersome name of this annotation is due to the fact that Jacoco excludes
 * methods from coverage if they have an annotation that contains the word "generated" in its name,
 * but we do not want to mark methods as "generated", because they are not.  So, the name
 * "ExcludeFromJacocoGeneratedReport" was chosen so as to trigger exclusion without suggesting
 * that the method is generated.
 *
 * Original idea from here: https://stackoverflow.com/a/66918619/773113
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ExcludeFromJacocoGeneratedReport
{
}
