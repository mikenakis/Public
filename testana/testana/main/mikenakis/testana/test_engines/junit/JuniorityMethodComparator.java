package mikenakis.testana.test_engines.junit;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

class JuniorityMethodComparator implements Comparator<Method>
{
	private final Map<Class<?>,Integer> juniorityFromJavaClassMap = new HashMap<>();

	JuniorityMethodComparator()
	{
	}

	@Override public int compare( Method method1, Method method2 )
	{
		int seniority1 = getMethodJuniority( method1 );
		int seniority2 = getMethodJuniority( method2 );
		return Integer.compare( seniority1, seniority2 );
	}

	private int getMethodJuniority( Method method )
	{
		Class<?> declaringClass = method.getDeclaringClass();
		return juniorityFromJavaClassMap.computeIfAbsent( declaringClass, JuniorityMethodComparator::calculateClassJuniority );
	}

	private static int calculateClassJuniority( Class<?> javaClass )
	{
		int juniority = 0;
		while( javaClass != Object.class )
		{
			juniority++;
			javaClass = javaClass.getSuperclass();
		}
		return juniority;
	}
}
