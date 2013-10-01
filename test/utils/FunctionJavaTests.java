package utils;

import junit.framework.TestSuite;

import org.junit.Test;

import fj.F;
import fj.F2;
import fj.data.Array;
import static fj.data.Array.array;
import fj.data.List;
import fj.data.List.Buffer;



public class FunctionJavaTests extends TestSuite{
	
	@Test
	public void listTest()
	{
		Buffer<Integer> buffer = new Buffer<Integer>();
		buffer = buffer.snoc(1);
		buffer.snoc(2);
		buffer.snoc(3);
		buffer.snoc(4);
		List<Integer> list = buffer.toList();
		list.snoc(2);
 	}
	
	
	@Test
	public void arrayTest()
	{
		Array<Integer> a = array(1, 2, 3, 2);
		Array<Integer> b = array(3, 4, 5, 6);
		List<Integer>c = a.zipWith(b, new F2<Integer, Integer, Integer>(){
			@Override
			public Integer f(Integer arg0, Integer arg1) {
				return arg0 + arg1;
			}}
		).toList();
		
		c = c.breakk(new F<Integer, Boolean>(){
			@Override
			public Boolean f(Integer i) {
				return i > 1;
			}})._2();
		
		for( Integer i : c)
		{
			System.out.println(i);
		}
	}
	
	@Test
	public void listSpanTest()
	{
		Buffer<Integer> buffer = new Buffer<Integer>();
		buffer = buffer.snoc(1);
		buffer.snoc(2);
		buffer.snoc(3);
		buffer.snoc(4);
		List<Integer> list = buffer.toList();
		list = List.nil();
		list.span(new F<Integer, Boolean>(){
			@Override
			public Boolean f(Integer arg0) {
				return true;
			}});
	}

}
