package org.davidCMs.vkengine.util;

import java.util.*;

public interface Copyable {

	Copyable copy();

	@SuppressWarnings("unchecked")
	static <C extends Copyable> Set<C> copySet(Set<C> originalSet) {
		if (originalSet == null)
			return null;

		Set<C> copiedSet = new HashSet<>(originalSet.size());
		for (C copyable : originalSet)
			copiedSet.add((C) copyable.copy());
		return copiedSet;
	}

	@SuppressWarnings("unchecked")
	static <C extends Copyable> List<C> copyList(List<C> originalList) {
		if (originalList == null)
			return null;

		List<C> copiedList = new ArrayList<>(originalList.size());
		for (int i = 0; i < originalList.size(); i++) {
			copiedList.add(i, (C) originalList.get(i).copy());
		}
		return copiedList;
	}

	@SuppressWarnings("unchecked")
	static <C extends Copyable> C safeCopy(C copyable) {
		return copyable != null ? (C) copyable.copy() : null;
	}
}
