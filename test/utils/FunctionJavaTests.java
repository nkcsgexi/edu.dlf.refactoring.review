package utils;

import static fj.data.Array.array;
import junit.framework.TestSuite;

import org.junit.Test;

import fj.Effect;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.Array;
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
	
	
	@Test
	public void listBindTest()
	{
		Buffer<Integer> buffer = new Buffer<Integer>();
		buffer = buffer.snoc(1);
		buffer.snoc(2);
		buffer.snoc(3);
		buffer.snoc(4);
		List<Integer> list = buffer.toList();
		List<P2<Integer, Integer>> l2 = list.bind(list, new F2<Integer, Integer, 
			P2<Integer, Integer>>() {
			@Override
			public P2<Integer, Integer> f(Integer arg0, Integer arg1) {
				return List.single(arg0).zip(List.single(arg1)).head();
			}
		});
		
		l2.nub(Equal.equal(new F<P2<Integer, Integer>, F<P2<Integer, Integer>,Boolean>>() {
			@Override
			public F<P2<Integer, Integer>, Boolean> f(final P2<Integer, Integer> p1) {
				return new F<P2<Integer,Integer>, Boolean>() {
					@Override
					public Boolean f(P2<Integer, Integer> p2) {
						return p1._1() == p2._1() || p1._2() == p2._2() ;
					}
				};
			}
		})).foreach(new Effect<P2<Integer, Integer>>() {
			@Override
			public void e(P2<Integer, Integer> arg0) {
				System.out.println(arg0._1()+ " " + arg0._2());
				
			}
		});;
	}

}
