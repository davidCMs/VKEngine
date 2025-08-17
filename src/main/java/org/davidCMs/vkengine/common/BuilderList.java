package org.davidCMs.vkengine.common;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Wrapper class for {@link List} that allows it to be used in builder patterns
 *
 * @param <Builder> the builder type that the {@link BuilderList#ret()} will return
 * @param <Element> the Element type that will be stored in the list
 *
 * @see BuilderSet
 * */
public class BuilderList<Builder, Element> implements Iterable<Element> {

    /** The Underlying list that holds the elements */
    private final List<Element> list;
    /** The reference to the builder owning this list */
    private final Builder builder;

    /** Constructor that only takes a builder and constructs a new {@link ArrayList} for the underlying list
     *
     * @param builder the owning builder of this list */
    public BuilderList(Builder builder) {
        this.builder = builder;
        list = new ArrayList<>();
    }

    /** Same as {@link BuilderList#BuilderList(Builder)} but with an additional argument to specify the initial capacity of the list
     *
     * @param builder the owning builder of this list
     * @param initialCapacity the initial capacity of the created {@link ArrayList} */
    public BuilderList(Builder builder, int initialCapacity) {
        this.builder = builder;
        list = new ArrayList<>(initialCapacity);
    }

    /** Constructor that instead of making a new list takes on as an argument
     *
     * @param builder the owning builder of this list
     * @param list the list that will be used as the underlying list
     *
     * @implNote This is not like {@link ArrayList#ArrayList()} it will not clone the list passed in! it will wrap it */
    public BuilderList(Builder builder, List<Element> list) {
        this.builder = builder;
        this.list = list;
    }

    public BuilderList<Builder, Element> add(Element element) {
        list.add(element);
        return this;
    }

    @SafeVarargs
    public final BuilderList<Builder, Element> add(Element... elements) {
        for (Element element : elements)
            list.add(element);
        return this;
    }

    public BuilderList<Builder, Element> add(@NotNull Collection<? extends Element> c) {
        list.addAll(c);
        return this;
    }

    public BuilderList<Builder, Element> remove(Element o) {
        list.remove(o);
        return this;
    }

    @SafeVarargs
    public final BuilderList<Builder, Element> remove(Element... elements) {
        for (Element element : elements)
            list.remove(element);
        return this;
    }

    public BuilderList<Builder, Element> remove(Collection<? extends Element> c) {
        list.removeAll(c);
        return this;
    }

    public BuilderList<Builder, Element> removeIf(@NotNull Predicate<? super Element> filter) {
        list.removeIf(filter);
        return this;
    }

    public boolean contains(Element o) {
        return list.contains(o);
    }

    public boolean containsAll(@NotNull Collection<Element> c) {
        return list.containsAll(c);
    }

    public Stream<Element> parallelStream() {
        return list.parallelStream();
    }

    public Stream<Element> stream() {
        return list.stream();
    }

    public BuilderList<Builder, Element> clear() {
        list.clear();
        return this;
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public Element[] toArray(@NotNull IntFunction<Element[]> generator) {
        return list.toArray(generator);
    }

    public Element[] toArray(Element[] a) {
        return list.toArray(a);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public BuilderList<Builder, Element> retainAll(@NotNull Collection<Element> c) {
        list.retainAll(c);
        return this;
    }

    public List<Element> getList() {
        return list;
    }

    /** @return the builder this list is a part of */
    public Builder ret() {
        return builder;
    }

    @NotNull
    @Override
    public Iterator<Element> iterator() {
        return list.iterator();
    }

    /** Creates a new {@link BuilderList} with a copy of the list in this one and with a new owning builder passed via params
     *
     * @param builder the builder that will own the copied {@link BuilderList}
     * @return a copy of this list with a new owning builder */
    public BuilderList<Builder, Element> copy(Builder builder) {
        return new BuilderList<>(builder, new ArrayList<>(list));
    }

    /** Creates a new immutable list with the same contents as this {@link BuilderList}
     * @return a new immutable list with the same contents as this {@link BuilderList}  */
    public List<Element> copyAsImmutableList() {
        return List.copyOf(list);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
