package poc.pc.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
		
		
		JsonObject JsonObjectDadosExtras = new JsonObject();
		JsonObjectDadosExtras.addProperty("nome", "empty");
		JsonObjectDadosExtras.addProperty("cpf", "empty");
		JsonObjectDadosExtras.addProperty("carteira", "empty");
		jsonObjectSystem.add("dadosExtras", JsonObjectDadosExtras);

		JsonObject JsonObjectNodeOutput = (JsonObject) jsonObjectSystem.get("_node_output_map");

		JsonElement jsonStart = JsonObjectNodeOutput.get("start");
		JsonElement jsonDocumento = JsonObjectNodeOutput.get("Documento");
		JsonElement jsonConfirmarDados = JsonObjectNodeOutput.get("Confirmar_dados");
		
		
		
		if(jsonStart != null && jsonDocumento == null){
			JsonObject jsonObjectDadosExtras = (JsonObject) jsonObjectSystem.get("dadosExtras");
			
			if(jsonObjectDadosExtras != null){
				String cpf = jsonObjectDadosExtras.get("cpf").toString();
				String carteira = jsonObjectDadosExtras.get("carteira").toString();
				
				if((cpf == null && cpf.isEmpty()) || 
				   (carteira == null && carteira.isEmpty())){
					response.getText().remove(0);
					response.getText().add("OK, poderia me dizer o código da sua carteira, ou o número do CPF do titular?");
					response.getContext().put("system", "{dialog_stack=[{dialog_node=Documento}], dialog_turn_counter=2.0, dialog_request_counter=2.0, _node_output_map={start=[0.0], Documento=[0.0]}}");
					
					return response;
				}
			}
		}
		
		if(jsonStart == null && jsonDocumento == null){
			response.getText().remove(0);
			response.getText().add("OK, Eu entendi, mas poderia me dizer o código da sua carteira, ou o número do CPF do titular?");
			response.getContext().put("system", "{dialog_stack=[{dialog_node=Documento}], dialog_turn_counter=2.0, dialog_request_counter=2.0, _node_output_map={start=[0.0], Documento=[0.0]}}");
 			
			return response;
		}

//		if(jsonStart != null && jsonDocumento != null && jsonConfirmarDados != null){
//			response.getText().remove(0);
//			response.getText().add("Confirmando o seus dados: o seu nome é José da Silva, o seu telefone é 011 3605-1423.");
//			response.getContext().put("system", "{dialog_stack=[{dialog_node=Documento}], dialog_turn_counter=2.0, dialog_request_counter=2.0, _node_output_map={start=[0.0], Documento=[0.0]}}");
//			
//			return response;
//		}
		
		
		
		
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
		retorno.append("\"}");

		return retorno.toString();
	}

}
