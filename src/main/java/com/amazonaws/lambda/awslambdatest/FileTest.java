package com.amazonaws.lambda.awslambdatest;

import java.io.File;

public class FileTest {

	public static void main(String[] args) {
		File file = new File("G:\\Docs\\MyText.txt");
	      System.out.println(file.exists());

	}

}
