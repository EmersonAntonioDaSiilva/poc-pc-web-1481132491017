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

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class Conversation {
	 private final Logger systemLog = LoggerFactory.getLogger(Conversation.class);
	 
	 
	private ConversationService service;

	private String workspaceId;
	private String username;
	private String password;
	
	private static final String EMPTY = "";

	public Conversation() {
		service = new ConversationService(ConversationService.VERSION_DATE_2016_09_20);
		service.setApiKey(EMPTY);
	}

	public MessageResponse createHelloMessage(String name, String conversation_id, String system) {
		service.setUsernameAndPassword(username, password);
		return formatTxtWatson(name, conversation_id, system);
	}

	private MessageResponse formatTxtWatson(String texto, String context, String system) {
		MessageRequest request  = new MessageRequest.Builder().inputText(texto).alternateIntents(true).context(convertJsonfromMap(context, system)).build();
		MessageResponse response = service.message(workspaceId, request).execute();

		BasicConfigurator.configure();
		systemLog.info("Interlocutor: {}", response.getInputText());
		systemLog.info("Watson: {}", response.getText().get(0));
		systemLog.info("conversation_id: {}", response.getContext().get("conversation_id"));
		systemLog.info("system: {}", response.getContext().get("system"));
		systemLog.info("================================================================================");
		BasicConfigurator.resetConfiguration();
		
		return response;
	}

	private Map<String, Object> convertJsonfromMap(String conversation_id, String system) {
		Map<String, Object> context = null;
		try {
			if (conversation_id != null) {
				context = new HashMap<>();
				context.put("conversation_id", conversation_id);

				JsonParser parser = new JsonParser();
				Object obj = parser.parse(system);

				JsonObject jsonObject = (JsonObject) obj;
				
				context.put("system", jsonObject);
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
