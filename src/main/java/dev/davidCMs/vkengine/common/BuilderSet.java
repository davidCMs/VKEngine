package dev.davidCMs.vkengine.common;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
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

    /** Flag that if set makes the set throw an {@link ObjectFrozenException} if any mutating methods are called effectively making the set immutable */
    private boolean frozen = false;

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

    /** Constructor that instead of making a new set takes one as an argument
     *
     * @param builder the owning builder of this set
     * @param set the set that will be used as the underlying set
     *
     * @implNote This is not like {@link HashSet#HashSet(Collection)} it will not clone the set passed in! it will wrap it */
    public BuilderSet(Builder builder, Set<Element> set) {
        this.builder = builder;
        this.set = set;
    }

    /** Constructor that instead of making a new set takes one as an argument, also allows for setting the {@link BuilderSet#frozen} flag
     *
     * @param builder the owning builder of this set
     * @param set the set that will be used as the underlying set
     * @param frozen sets the frozen flag allowing making a set frozen from the start
     *
     * @implNote This is not like {@link HashSet#HashSet(Collection)} it will not clone the set passed in! it will wrap it */
    public BuilderSet(Builder builder, Set<Element> set, boolean frozen) {
        this.builder = builder;
        this.set = set;
        this.frozen = frozen;
    }

    public BuilderSet<Builder, Element> add(Element element) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        set.add(element);
        return this;
    }

    @SafeVarargs
    public final BuilderSet<Builder, Element> add(Element... elements) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        for (Element element : elements)
            set.add(element);
        return this;
    }

    public BuilderSet<Builder, Element> add(@NotNull Collection<? extends Element> c) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        set.addAll(c);
        return this;
    }

    public BuilderSet<Builder, Element> remove(Element o) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        set.remove(o);
        return this;
    }

    @SafeVarargs
    public final BuilderSet<Builder, Element> remove(Element... elements) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        for (Element element : elements)
            set.remove(element);
        return this;
    }

    public BuilderSet<Builder, Element> remove(Collection<? extends Element> c) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        set.removeAll(c);
        return this;
    }

    public BuilderSet<Builder, Element> removeIf(@NotNull Predicate<? super Element> filter) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
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
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
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
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        set.retainAll(c);
        return this;
    }

    /** @return the underlying set */
    public Set<Element> getSet() {
        return frozen ? Collections.unmodifiableSet(set) : set;
    }

    /** @return the builder this set is a part of */
    public Builder ret() {
        return builder;
    }

    @NotNull
    @Override
    public Iterator<Element> iterator() {
        if (!frozen) return set.iterator();

        return new Iterator<Element>() {
            private final Iterator<Element> i = set.iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public Element next() {
                return i.next();
            }

            @Override
            public void remove() {
                throw new ObjectFrozenException("Cannot mutate object as it is frozen");
            }

            @Override
            public void forEachRemaining(Consumer<? super Element> action) {
                i.forEachRemaining(action);
            }
        };
    }

    /**
     * Freezes the set making it immutable throwing an {@link ObjectFrozenException} if any mutating methods are called.
     * */
    public BuilderSet<Builder, Element> freeze() {
        frozen = true;
        return this;
    }

    public boolean isFrozen() {
        return frozen;
    }

    /** Creates a new {@link BuilderSet} with a copy of the set in this one and with a new owning builder passed via params
     *
     * @param builder the builder that will own the copied {@link BuilderSet}
     * @return a copy of this set with a new owning builder */
    public <NewBuilder> BuilderSet<NewBuilder, Element> copy(NewBuilder builder) {
        return new BuilderSet<>(builder, new HashSet<>(set));
    }

    /** Creates a new immutable set with the same contents as this {@link BuilderSet}
     * @return a new immutable set with the same contents as this {@link BuilderSet}  */
    public Set<Element> copyAsImmutableSet() {
        return Set.copyOf(set);
    }

    @Override
    public String toString() {
        return set.toString();
    }

}
