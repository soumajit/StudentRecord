package cloud.assignment.server;



import cloud.assignment.client.GreetingService;
import cloud.assignment.shared.FieldVerifier;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;



/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		input = escapeHtml(input);
		String[] msg = input.split(",");
		if(msg[0].equals("register")){
			try{
				String name = msg[1];
				String roll = msg[2];
				String dob = msg[3];
				if (!FieldVerifier.isValidName(dob)) {
					// If the input is not valid, throw an IllegalArgumentException back to
					// the client.
					throw new IllegalArgumentException(
							"Date format is wrong");
				}
				DatastoreService dataStoreObj = DatastoreServiceFactory.getDatastoreService(); 
                Transaction tnxn = dataStoreObj.beginTransaction();
                Entity studentEntity = new Entity("Student",name);
                studentEntity.setProperty("Name", name);
                studentEntity.setProperty("Roll", roll);
                studentEntity.setProperty("DOB", dob);
                dataStoreObj.put(studentEntity);
                tnxn.commit();
                return "Name: " + name + " !<br><br>Roll number: " + roll
                                + "<br><br>DOB:<br>" + dob + "<br><br>Data saved Succesfully !<br>";
			}catch(Exception e){
				return "Error, pushing data !";
			}
		}else if(msg[0].equals("search")){
			try{
				String name = msg[1];
				DatastoreService dataStoreObj = DatastoreServiceFactory.getDatastoreService(); 
                Transaction tnxn = dataStoreObj.beginTransaction();
                Key rollKey = KeyFactory.createKey("Student", name);
                Entity studentEntity = dataStoreObj.get(rollKey);
                String roll = studentEntity.getProperty("Roll").toString();
                String dob = studentEntity.getProperty("DOB").toString();
                tnxn.commit();
                return "Name: " + name + " !<br><br>Roll Number: " + roll
                                + "<br><br>DOB:<br>" + dob + "<br><br>Succesfully Retireved Data !<br>";
			}catch(Exception e){
				return "No results found !";
			}
		}else {
			return "OOps! Something seems to be wrong.";
			
		}
		

		/*DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


		Entity student = new Entity("student", name);

		student.setProperty("name", name);
		student.setProperty("roll", roll);
		//Date hireDate = new Date();
		student.setProperty("dob", dob);
		datastore.put(student);*/
		
		//String serverInfo = getServletContext().getServerInfo();
		//String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		
		//userAgent = escapeHtml(userAgent);
		
	
		
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
