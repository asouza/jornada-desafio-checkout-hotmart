package com.deveficiente.desafiocheckouthotmart.clientesremotos.provedor1email;

public class Provider1EmailRequest {
	private String subject;
	private String from;
	private String to;
	private String body;

	public Provider1EmailRequest(String subject, String from, String to,
			String body) {
		super();
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.body = body;
	}

	@Override
	public String toString() {
		return "Provider1EmailRequest [subject=" + subject + ", from=" + from
				+ ", to=" + to + ", body=" + body + "]";
	}

	public String getSubject() {
		return subject;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getBody() {
		return body;
	}
	
	

}
