package dev.davidCMs.vkengine.common;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
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

    /** Flag that if list makes the list throw an {@link ObjectFrozenException} if any mutating methods are called effectively making the list immutable */
    private boolean frozen = false;

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

    /** Constructor that instead of making a new list takes one as an argument
     *
     * @param builder the owning builder of this list
     * @param list the list that will be used as the underlying list
     *
     * @implNote This is not like {@link ArrayList#ArrayList(Collection)} it will not clone the list passed in! it will wrap it */
    public BuilderList(Builder builder, List<Element> list) {
        this.builder = builder;
        this.list = list;
    }

    /** Constructor that instead of making a new list takes one as an argument, also allows for setting the {@link BuilderList#frozen} flag
     *
     * @param builder the owning builder of this list
     * @param list the list that will be used as the underlying list
     * @param frozen list the frozen flag allowing making a list frozen from the start
     *
     * @implNote This is not like {@link ArrayList#ArrayList(Collection)} it will not clone the list passed in! it will wrap it */
    public BuilderList(Builder builder, List<Element> list, boolean frozen) {
        this.builder = builder;
        this.list = list;
        this.frozen = frozen;
    }

    public BuilderList<Builder, Element> add(Element element) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        list.add(element);
        return this;
    }

    @SafeVarargs
    public final BuilderList<Builder, Element> add(Element... elements) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        for (Element element : elements)
            list.add(element);
        return this;
    }

    public BuilderList<Builder, Element> add(@NotNull Collection<? extends Element> c) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        list.addAll(c);
        return this;
    }

    public Element get(int index) {
        return list.get(index);
    }

    public Element getLast() {
        return list.getLast();
    }

    public Element getFirst() {
        return list.getFirst();
    }

    public BuilderList<Builder, Element> remove(Element o) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        list.remove(o);
        return this;
    }

    @SafeVarargs
    public final BuilderList<Builder, Element> remove(Element... elements) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        for (Element element : elements)
            list.remove(element);
        return this;
    }

    public BuilderList<Builder, Element> remove(Collection<? extends Element> c) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        list.removeAll(c);
        return this;
    }

    public BuilderList<Builder, Element> removeIf(@NotNull Predicate<? super Element> filter) {
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
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
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
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
        if (frozen) throw new ObjectFrozenException("Cannot mutate object as it is frozen");
        list.retainAll(c);
        return this;
    }

    /** @return the underlying list */
    public List<Element> getList() {
        return frozen ? Collections.unmodifiableList(list) : list;
    }

    /** @return the builder this list is a part of */
    public Builder ret() {
        return builder;
    }

    @NotNull
    @Override
    public Iterator<Element> iterator() {
        if (!frozen) return list.iterator();

        return new Iterator<Element>() {
            private final Iterator<Element> i = list.iterator();

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
     * Freezes the list making it immutable throwing an {@link ObjectFrozenException} if any mutating methods are called.
     * */
    public BuilderList<Builder, Element> freeze() {
        frozen = true;
        return this;
    }

    public boolean isFrozen() {
        return frozen;
    }

    /** Creates a new {@link BuilderList} with a copy of the list in this one and with a new owning builder passed via params
     *
     * @param builder the builder that will own the copied {@link BuilderList}
     * @return a copy of this list with a new owning builder */
    public <NewBuilder> BuilderList<NewBuilder, Element> copy(NewBuilder builder) {
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
