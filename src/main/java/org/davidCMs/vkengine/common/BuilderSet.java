package org.davidCMs.vkengine.common;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Wrapper class for {@link Set} that allows it to be used in builder patterns
 *
 * @param <Builder> the builder type that the {@link BuilderSet#ret()} will return
 * @param <Element> the Element type that will be stored in the set
 *
 * @see BuilderList
 * */
public class BuilderSet<Builder, Element> implements Iterable<Element> {

    /** The Underlying set that holds the elements */
    private final Set<Element> set;
    /** The reference to the builder owning this set */
    private final Builder builder;

    /** Constructor that only takes a builder and constructs a new {@link HashSet} for the underlying set
     *
     * @param builder the owning builder of this set */
    public BuilderSet(Builder builder) {
        this.builder = builder;
        set = new HashSet<>();
    }

    /** Same as {@link BuilderSet#BuilderSet(Builder)} but with an additional argument to specify the initial capacity of the set
     *
     * @param builder the owning builder of this set
     * @param initialCapacity the initial capacity of the created {@link HashSet} */
    public BuilderSet(Builder builder, int initialCapacity) {
        this.builder = builder;
        set = new HashSet<>(initialCapacity);
    }

    /** Constructor that instead of making a new set takes on as an argument
     *
     * @param builder the owning builder of this set
     * @param set the set that will be used as the underlying set
     *
     * @implNote This is not like {@link HashSet#HashSet(Collection)} it will not clone the set passed in! it will wrap it */
    public BuilderSet(Builder builder, Set<Element> set) {
        this.builder = builder;
        this.set = set;
    }

    public BuilderSet<Builder, Element> add(Element element) {
        set.add(element);
        return this;
    }

    @SafeVarargs
    public final BuilderSet<Builder, Element> add(Element... elements) {
        for (Element element : elements)
            set.add(element);
        return this;
    }

    public BuilderSet<Builder, Element> add(@NotNull Collection<? extends Element> c) {
        set.addAll(c);
        return this;
    }

    public BuilderSet<Builder, Element> remove(Element o) {
        set.remove(o);
        return this;
    }

    @SafeVarargs
    public final BuilderSet<Builder, Element> remove(Element... elements) {
        for (Element element : elements)
            set.remove(element);
        return this;
    }

    public BuilderSet<Builder, Element> remove(Collection<? extends Element> c) {
        set.removeAll(c);
        return this;
    }

    public BuilderSet<Builder, Element> removeIf(@NotNull Predicate<? super Element> filter) {
        set.removeIf(filter);
        return this;
    }

    public boolean contains(Element o) {
        return set.contains(o);
    }

    public boolean containsAll(@NotNull Collection<Element> c) {
        return set.containsAll(c);
    }

    public Stream<Element> parallelStream() {
        return set.parallelStream();
    }

    public Stream<Element> stream() {
        return set.stream();
    }

    public BuilderSet<Builder, Element> clear() {
        set.clear();
        return this;
    }

    public Object[] toArray() {
        return set.toArray();
    }

    public Element[] toArray(@NotNull IntFunction<Element[]> generator) {
        return set.toArray(generator);
    }

    public Element[] toArray(Element[] a) {
        return set.toArray(a);
    }

    public int size() {
        return set.size();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public BuilderSet<Builder, Element> retainAll(@NotNull Collection<Element> c) {
        set.retainAll(c);
        return this;
    }

    public Set<Element> getSet() {
        return set;
    }

    /** @return the builder this set is a part of */
    public Builder ret() {
        return builder;
    }

    @NotNull
    @Override
    public Iterator<Element> iterator() {
        return set.iterator();
    }

    /** Creates a new {@link BuilderSet} with a copy of the set in this one and with a new owning builder passed via params
     *
     * @param builder the builder that will own the copied {@link BuilderSet}
     * @return a copy of this set with a new owning builder */
    public BuilderSet<Builder, Element> copy(Builder builder) {
        return new BuilderSet<>(builder, new HashSet<>(set));
    }

    @Override
    public String toString() {
        return set.toString();
    }
}
