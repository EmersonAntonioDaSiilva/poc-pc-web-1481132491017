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

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class Conversation {

	private ConversationService service;
	private MessageRequest newMessage;

	private String workspaceId;
	private String username;
	private String password;

	public Conversation() {
		service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);

	}

	public MessageResponse createHelloMessage(String name, String context) {
		service.setUsernameAndPassword(username, password);

		return formatTxtWatson(name, context);
	}

	private MessageResponse formatTxtWatson(String texto, String context) {
		newMessage = new MessageRequest.Builder().inputText(texto).context(convertJsonfromMap(context)).build();
		MessageResponse response = service.message(workspaceId, newMessage).execute();

		System.out.println(response);

		return response;
	}

	private Map<String, Object> convertJsonfromMap(String strContext) {
		Map<String, Object> context = null;
		try {
			if (strContext != null) {
				context = new HashMap<>();
				context.put("conversation_id", strContext);
			}

		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return context;
	}

	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
