package utils;

import junit.framework.TestSuite;

import org.junit.Test;

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
		
		for(int i : buffer)
		{
			System.out.println(i);
		}
		
		for(int i : list)
		{
			System.out.println(i);
		}
		
		System.out.println(buffer.toList().splitAt(1)._1().length());
		System.out.println(buffer.toList().splitAt(1)._2().length());
 	}
	
	
	@Test
	public void arrayTest()
	{
		
	}

}
