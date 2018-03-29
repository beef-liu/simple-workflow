package simpleworkflow.engine.junittest;

import org.junit.Test;

import java.lang.reflect.Method;

public class Test1 {
	
	@Test
	public void test1() {
		try {
			Method method = Test1.class.getMethod("test1");
			
			Class<?> cls = method.getDeclaringClass();
			
			System.out.println("cls:" + cls.getName());
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

}
