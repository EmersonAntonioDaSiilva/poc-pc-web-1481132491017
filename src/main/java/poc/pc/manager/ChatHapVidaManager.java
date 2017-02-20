package poc.pc.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;


@Path("/hapvida")
public class ChatHapVidaManager {

	@Inject
	private Conversation conversation;

	private static String workspaceId = "d6fe397a-343b-47b5-a132-a1def577b235";
	private static String username = "fb3ea18a-08b4-48ee-ac83-630fe19a68ef";
	private static String password = "6qOUoIn3UXnD";


	@GET
	@Path("/json/{dialog}/{conversation_id}/{system}")
	@Produces("application/json")
	public String getDialog(@PathParam("dialog") String dialog, @PathParam("conversation_id") String conversation_id, @PathParam("system") String system) {
		conversation.setWorkspaceId(workspaceId);
		conversation.setUsername(username);
		conversation.setPassword(password);

		if ("000".equals(conversation_id)) {
			conversation_id = null;
			system = null;
		}

		MessageResponse createHelloMessage = validarRetorno(conversation.createHelloMessage(dialog, conversation_id, system));
		
		
		
		return formJson(createHelloMessage);
	}

	private MessageResponse validarRetorno(MessageResponse response) {
		JsonParser parser = new JsonParser();

		Object objSystem = parser.parse(response.getContext().get("system").toString());
		JsonObject jsonObjectSystem = (JsonObject) objSystem;
		JsonObject JsonObjectNodeOutput = (JsonObject) jsonObjectSystem.get("_node_output_map");

		JsonArray JsonArrayDialog_stack = (JsonArray) jsonObjectSystem.get("dialog_stack");
		JsonObject JsonObjectDialog = (JsonObject) JsonArrayDialog_stack.get(0);
		
		
		JsonElement jsonStart = JsonObjectNodeOutput.get("start");
		JsonElement jsonDocumento = JsonObjectNodeOutput.get("Documento");
		
		if(jsonStart != null && jsonDocumento == null && !JsonObjectDialog.get("dialog_node").getAsString().equals("start")){
			response.getText().remove(0);
			response.getText().add("OK, Eu entendi, mas poderia me dizer o código da sua carteira, ou o número do CPF do titular?");
			response.getContext().put("system", "{dialog_stack=[{dialog_node=Documento}], dialog_turn_counter=2.0, dialog_request_counter=2.0, _node_output_map={start=[0.0], Documento=[0.0]}}");
 			
			return response;
		}

		return response;
	}

	private String formJson(MessageResponse response) {
		StringBuffer retorno = new StringBuffer();
		retorno.append("{\"");
		retorno.append("result\":\"" + response.getText().get(0));
		retorno.append("\",");
		retorno.append("\"conversation_id\":\"" + response.getContext().get("conversation_id"));		
		retorno.append("\",");
		retorno.append("\"system\":\"" + response.getContext().get("system"));
		retorno.append("\",");
		retorno.append("\"audio\":\"" + response.getOutput().get("nodes_visited").toString().toLowerCase());
		retorno.append("\",");
		retorno.append("\"action\":\"" + getAction(response));		
		retorno.append("\"}");

		return retorno.toString();
	}

	private String getAction(MessageResponse response) {
		List<String> tagsFinais = new ArrayList<>();
		tagsFinais.add("Agenda_Doutor");
		tagsFinais.add("Agenda_Doutora");
		tagsFinais.add("Fim_Agendamento");
		
		String returno = "continuar";
		if(tagsFinais.contains(response.getOutput().get("nodes_visited").toString())){
			returno = "finalizar";
		}
		
		return returno;
	}

}
