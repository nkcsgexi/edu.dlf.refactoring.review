package edu.dlf.refactoring.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class XList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 4763945155939258143L;

	public XList() {
		super();
	}
	
	public static <S> XList<S> CreateList()
	{
		return new XList<S>();
	}
	
	public XList(Collection<? extends T> list) {
		super();
		this.addAll(list);
	}

	public XList(T[] elements) {
		super();
		this.addAll(Arrays.asList(elements));
	}

	public XList<T> where(Predicate<T> predicate) throws Exception {
		XList<T> list = new XList<T>();
		for(T t : this)
		{
			if(predicate.apply(t))
			{
				list.add(t);
			}
		}
		return list;
	}

	public boolean exist(Predicate<T> predicate) throws Exception {
		return where(predicate).size() > 0;
	}

	public boolean all(final Predicate<T> predicate) throws Exception {
		return where(predicate).size() == size();
	}
	

	public void operateOnElement(Function<T, Void> op) throws Exception {
		for(T t : this)
		{
			op.apply(t);
		}	
	}

	public <S> XList<S> select(Function<T, S> mapper) throws Exception {
		List<S> tempList = new ArrayList<S>();
		for (T t : this) {
			tempList.add(mapper.apply(t));
		}
		return new XList<S>(tempList);
	}
	
	
	public <S> XList<S> selectMany(Function<T, Collection<S>> func)
	{
		XList<S> list = new XList<S>();
		for(T t : this)
		{
			list.addAll(func.apply(t));
		}
		return list;
	}
	

	public <S> XList<S> convert(Function<T, S> convertor)
			throws Exception {
		List<S> tempList = new ArrayList<S>();
		for (T t : this) {
			tempList.add(convertor.apply(t));
		}
		return new XList<S>(tempList);
	}

	public XList<T> orderBy(Comparator<T> comparator) {
		ArrayList<T> tempList = new ArrayList<T>();
		tempList.addAll(this);
		Collections.sort(tempList, comparator);
		return new XList<T>(tempList);
	}

	public T max(Comparator<T> comparator) {
		return Collections.max(this, comparator);
	}

	public T min(Comparator<T> comparator) {
		return Collections.min(this, comparator);
	}

	public T first(Predicate<T> predicate) throws Exception {
		List<T> tempList = this.where(predicate);
		if (tempList.size() > 0) {
			return tempList.get(0);
		}
		return null;
	}

	public T last(Predicate<T> predicate) throws Exception {
		List<T> tempList = this.where(predicate);
		if (tempList.size() > 0) {
			return tempList.get(tempList.size() - 1);
		}
		return null;
	}

	public boolean empty() {
		return this.size() == 0;
	}

	public boolean any() {
		return this.size() > 0;
	}
	
	public boolean any(Predicate<T> p)
	{
		for(T t : this)
		{
			if(p.apply(t))
			{
				return true;
			}
		}
		return false;
	}
	
	
	public <S> XList<S> cast(Class S) throws Exception
	{
		return this.select(new Function<T, S>(){
			@Override
			public S apply(T t) {
				return (S)t;
			}});
	}

	public int count(Predicate<T> pre) throws Exception {
		int count = 0;
		for (T t : this) {
			if (pre.apply(t)) {
				count++;
			}
		}
		return count;
	}

	public Double sum(Function<T, Double> convertor) throws Exception {
		Double all = 0.0;
		for (T t : this) {
			all += convertor.apply(t);
		}
		return all;
	}

	public String toString(String delimit) {
		StringBuilder sb = new StringBuilder();
		for(T t : this) {
			sb.append(t.toString());
			sb.append(delimit);
		}
		return sb.toString();
	}
	
	public XList<T> except(XList<T> list, IEqualityComparer<T> compare)
	{
		
		XList<T> results = CreateList();
		for(T t : this)
		{
			boolean isCommon = false;
			
			for (T another : list) {
				if(compare.AreEqual(t, another))
				{
					isCommon = true;
					break;
				}
			}
			if(!isCommon)
			{
				results.add(t);
			}
		}
		return results;
	}
	
	
	public XList<T> intersect(XList<T> list, IEqualityComparer<T> compare)
	{
		XList<T> result = new XList<T>();
		for(T t : this)
		{
			boolean isCommon = false;
			for(T another : list)
			{
				if(compare.AreEqual(t, another))
				{
					isCommon = true;
				}
			}
			if(isCommon)
			{
				result.add(t);
			}
				
		}
		return result;
	}
	
	@Override
	public XList<T> subList(int fromInclusive, int toExclusive)
	{
		XList<T> result = new XList<T>();
		for(int i = fromInclusive; i < toExclusive; i ++)
		{
			result.add(this.get(i));
		}
		return result;
	
	}
	
	
	public XList<T> cloneList()
	{
		return subList(0, this.size());
	}
	
	
	public T aggregate(IAggregator<T> operator)
	{
		 Iterator<T> it = this.iterator();
		 T current = null;
		 if(it.hasNext())
			 current = it.next();
		 while(it.hasNext())
		 {
			 current = operator.aggregate(current, it.next());
		 }
		 return current;
	}
	
	public interface IAggregator<S>
	{
		S aggregate(S s1, S s2);
	}
	
	
	public String toString() {
		return toString("\r\n");
	}
}