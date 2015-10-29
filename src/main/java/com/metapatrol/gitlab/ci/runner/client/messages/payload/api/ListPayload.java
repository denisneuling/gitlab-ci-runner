package com.metapatrol.gitlab.ci.runner.client.messages.payload.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by ska on 03.01.15.
 */
public class ListPayload<T extends Payload> extends Payload implements List<T> {

   List<T> elements = new LinkedList<T>();


    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return elements.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        return elements.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return elements.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return elements.addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        return elements.addAll(i, collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return elements.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return elements.retainAll(collection);
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public T get(int i) {
        return elements.get(i);
    }

    @Override
    public T set(int i, T t) {
        return elements.set(i, t);
    }

    @Override
    public void add(int i, T t) {
        elements.add(i, t);
    }

    @Override
    public T remove(int i) {
        return elements.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return elements.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return elements.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return elements.listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i1) {
        return elements.subList(i, i1);
    }

    @Override
    public String toString() {
        return "ListPayload{" +
                "elements=" + elements +
                '}';
    }
}
