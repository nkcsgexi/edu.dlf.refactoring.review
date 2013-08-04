package edu.dlf.refactoring.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class XArrayList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 4763945155939258143L;

	public XArrayList() {
		super();
	}

	public XArrayList(Collection<? extends T> list) {
		super();
		this.addAll(list);
	}

	public XArrayList(T[] elements) {
		super();
		this.addAll(Arrays.asList(elements));
	}

	public XArrayList<T> where(Predicate<T> predicate) throws Exception {
		XArrayList<T> list = new XArrayList<T>();
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

	public <S> XArrayList<S> select(Function<T, S> mapper) throws Exception {
		List<S> tempList = new ArrayList<S>();
		for (T t : this) {
			tempList.add(mapper.apply(t));
		}
		return new XArrayList<S>(tempList);
	}
	
	
	public <S> XArrayList<S> selectMany(Function<T, Collection<S>> func)
	{
		XArrayList<S> list = new XArrayList<S>();
		for(T t : this)
		{
			list.addAll(func.apply(t));
		}
		return list;
	}

	public <S> XArrayList<S> convert(Function<T, S> convertor)
			throws Exception {
		List<S> tempList = new ArrayList<S>();
		for (T t : this) {
			tempList.add(convertor.apply(t));
		}
		return new XArrayList<S>(tempList);
	}

	public XArrayList<T> orderBy(Comparator<T> comparator) {
		ArrayList<T> tempList = new ArrayList<T>();
		tempList.addAll(this);
		Collections.sort(tempList, comparator);
		return new XArrayList<T>(tempList);
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
	
	public <S> XArrayList<S> cast(Class S) throws Exception
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

	public String toString() {
		return toString("\r\n");
	}
}