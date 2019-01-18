package org.zkoss.training;

public class HelloVM {

	private String hello = "";

	//TODO, 3, write a hello method
	public void hello(){
		hello = "hello world";
	}
	//a comment
	// TODO, 1-3, hello getter
	public String getHello() {
		return hello;
	}
	//setter
	public void setHello(String hello) {
		this.hello = hello;
	}

	//not todo marker
	public void todo(){

	}
}