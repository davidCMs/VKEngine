package org.davidCMs.vkengine.vk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class VkEApplicationInfoTest {

	@Test
	void setApplicationVersionCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setApplicationVersion(new VkEVersion(0,0,0,0));
		});
	}

	@Test
	void setApplicationNameCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setApplicationName("t");
		});
	}

	@Test
	void setEngineVersionCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setEngineVersion(new VkEVersion(0,0,0,0));
		});
	}

	@Test
	void setEngineNameCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setEngineName("t");
		});
	}

	@Test
	void getApplicationVersionCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, info::getApplicationVersion);
	}

	@Test
	void getApplicationNameCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, info::getApplicationName);
	}

	@Test
	void getEngineVersionCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, info::getEngineVersion);
	}

	@Test
	void getInfoCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, info::getInfo);
	}

	@Test
	void getEngineNameCloseCheck() {
		VkEApplicationInfo info = new VkEApplicationInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, info::getEngineName);
	}

	@Test
	void testApplicationVersionGetSet() {
		VkEApplicationInfo info = new VkEApplicationInfo();

		VkEVersion version = new VkEVersion(0, 0, 0, 0);

		info.setApplicationVersion(version);

		assertEquals(version, info.getApplicationVersion());
	}

	@Test
	void testApplicationNameGetSet() {
		VkEApplicationInfo info = new VkEApplicationInfo();

		String name = "Test";

		info.setApplicationName(name);

		assertEquals(name, info.getApplicationName());
	}

	@Test
	void testEngineVersionGetSet() {
		VkEApplicationInfo info = new VkEApplicationInfo();

		VkEVersion version = new VkEVersion(0, 0, 0, 0);

		info.setEngineVersion(version);

		assertEquals(version, info.getEngineVersion());
	}

	@Test
	void testEngineNameGetSet() {
		VkEApplicationInfo info = new VkEApplicationInfo();

		String name = "Test";

		info.setEngineName(name);

		assertEquals(name, info.getEngineName());
	}

	@BeforeEach
	void setUp() {
		Configuration.DEBUG.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);
	}
}