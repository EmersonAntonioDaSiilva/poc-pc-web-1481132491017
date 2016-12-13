/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package poc.pc.manager;

import java.util.Map;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class Conversation {

	private ConversationService service;
	private MessageRequest newMessage;

	private String workspaceId = "acfc6b01-ee0a-4614-ad94-858a33807290";

	public Conversation() {
		service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
		service.setUsernameAndPassword("377ab19c-3ab6-4f2a-af10-2e461e77d7c2", "XKjXU2vqxM0d");

	}

	public MessageResponse createHelloMessage(String name, Map<String, Object> context) {
		return formatTxtWatson(name, context);
	}

	private MessageResponse formatTxtWatson(String texto, Map<String, Object> context) {
		newMessage = new MessageRequest.Builder().inputText(texto).context(context).build();
		MessageResponse response = service.message(workspaceId, newMessage).execute();

		System.out.println(response);

		return response;
	}
}
