package poc.pc.manager;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * A simple REST service which is able to say hello to someone using Conversation Please take a look at the web.xml
 * where JAX-RS is enabled And notice the @PathParam which expects the URL to contain /json/David or /xml/Mary
 *
 * @author bsutter@redhat.com
 */

@Path("/")
public class ChatManager {

	@Inject
	private Conversation conversation;

	@GET
	@Path("/json/{dialog}")
	@Produces("application/json")
	public String getDialog(@PathParam("dialog") String dialog) {
		return "{\"result\":\"" + conversation.createHelloMessage(dialog) + "\"}";
	}

}
