package org.zkoss.training;

public class InvalidVM {

	private String hello = "";

	//TODO not a valid command - just a todo
	public void hello(){
		hello = "hello world";
	}

	public String getHello() {
		return hello;
	}
	public void setHello(String hello) {
		this.hello = hello;
	}
}