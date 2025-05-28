package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;

public class VulkanMessageFactory implements MessageFactory {

	public static final VulkanMessageFactory INSTANCE = new VulkanMessageFactory();

	@Override
	public Message newMessage(Object o) {
		return new SimpleMessage("[VULKAN] " + o.toString());
	}

	@Override
	public Message newMessage(String s) {
		return new SimpleMessage("[VULKAN] " + s);
	}

	@Override
	public Message newMessage(String s, Object... objects) {
		return new ParameterizedMessage("[VULKAN] " + s, objects);
	}


}
