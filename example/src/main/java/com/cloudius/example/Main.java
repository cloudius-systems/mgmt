package com.cloudius.example;

public class Main implements Runnable {

  private boolean run = true;

  public static void main(String [] args) {
    new Main().run();
  }

  public void run(){
    int i =0;
    while(run){
	try{
	  Thread.sleep(1000);
	  System.out.println(i++);
	} catch (Exception e) {
	  System.err.println(e);
	}
    }
  }

  public void stop(){
    run = false;
  }

}
