package org.davidCMs.vkengine.vk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.vulkan.VK14;

import java.util.Random;

class VkVersionTest {

	private Random random;

	private int variant;
	private int major;
	private int minor;
	private int patch;

	int correctIntVer;

	@BeforeEach
	void beforeEach() {
		random = new Random(System.currentTimeMillis());

		variant = random.nextInt(5);
		major = random.nextInt(10);
		minor = random.nextInt(10);
		patch = random.nextInt(10);

		correctIntVer = VK14.VK_MAKE_API_VERSION(
				variant, major, minor, patch
		);
	}

	@Test
	void testMakeVersion() {


		VkVersion vkVersion = new VkVersion(
				variant, major, minor, patch
		);

		Assertions.assertEquals(correctIntVer, vkVersion.makeVersion());
	}

	@Test
	void testVerIntConstructor() {
		VkVersion version = new VkVersion(correctIntVer);

		Assertions.assertEquals(variant, version.variant());
		Assertions.assertEquals(major, version.major());
		Assertions.assertEquals(minor, version.minor());
		Assertions.assertEquals(patch, version.patch());
	}

}