package mmall;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * 这是BigDecimal测试类
 * @author Administrator
 *
 */

public class BigDecimalTest {
	
	@Test
	public void test1(){
		System.out.println(0.05+0.01);
		System.out.println(1.0-0.32);
		System.out.println(1.245*100);
		System.out.println(12.3/10);	
	}
	
	@Test
	public void test2(){
		BigDecimal b1 = new BigDecimal(0.05);
		BigDecimal b2 = new BigDecimal(0.01);
		System.out.println(b1.add(b2));
	}
	
	@Test
	public void test3(){
		BigDecimal b1 = new BigDecimal("0.05");
		BigDecimal b2 = new BigDecimal("0.01");
		System.out.println(b1.add(b2));
	}
	
}
