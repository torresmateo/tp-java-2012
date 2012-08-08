package email;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class MonitorEMail {
	
	private Properties props;
	private Transport transport;
	private Session session;
	private MimeMessage message;
	
	// Constructor
	public MonitorEMail() {
		// Setear las configuraciones
		props = new Properties();
		
		// Nombre del host de correo
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
	
		// TLS si est� disponible
		props.setProperty("mail.smtp.starttls.enable", "true");
	
		// Puerto de gmail para envio de correos
		props.setProperty("mail.smtp.port","587");
	
		// Nombre del usuario
		props.setProperty("mail.smtp.user", "tcpipservicesmonitor@gmail.com");
	
		// Si requiere o no usuario y password para conectarse.
		props.setProperty("mail.smtp.auth", "true");
		
		session = Session.getDefaultInstance(props);
		
		//Preparar el mensaje
		message = new MimeMessage(session);
		try{
		// Quien envia el correo
		message.setFrom(new InternetAddress("tcpipservicesmonitor@gmail.com"));
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// Setear Asunto
	void setSubject(String subject){
		try{
			message.setSubject(subject);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// Setear el cuerpo del mail
	void setBody(String body){
		try{
			message.setText(body);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// Enviar el eMail
	void sendEMail(){
		try{
			transport = session.getTransport("smtp");
			transport.connect("tcpipservicesmonitor@gmail.com","JavaUca2012");
			transport.sendMessage(message,message.getAllRecipients());
			transport.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// A�adir destinatarios
	void addRecipient(String recipient){
		try{
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// Descartar todos los destinatarios
	void removeRecipients(){
		try{
			message.setRecipients(Message.RecipientType.TO, "");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// Imprimir destinatarios
	void printRecipients(){
		try{
			Address[] direcciones = message.getAllRecipients();
			for (int i = 0; i < direcciones.length; i++) {
				System.out.println(direcciones[i]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

