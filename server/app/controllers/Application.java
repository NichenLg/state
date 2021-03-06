package controllers;

import play.*;
import play.mvc.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.*;

import org.apache.commons.io.IOUtils;

/**
 * 应用程序的关于全局的一些接口或处理
 * @author nichen
 * 
 */
@With(Secure.class)
public class Application extends Controller {
	
	/**
	 * 返回主页
	 */
	public static void index() {
		renderTemplate("/public/index.html");
	}
}