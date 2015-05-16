package controllers;

import play.*;
import play.mvc.*;

import views.html.*;


import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;

import java.io.IOException;
import java.util.Random;
import java.util.LinkedList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.Request;

public class Application extends Controller {

	public static Result index(){
		return ok("pants");
	}
    public static Result process() {
        
    	Request r = Json.fromJson(request().body().asJson(),Request.class);
    	
    	String url = r.getUrl();
    	int templateNumber = r.getTNum();

    	WSRequestHolder holder = WS.url("http://thehoneybee.us/comedy/alchemyapi.php")
    								.setQueryParameter("url",url);
    	Promise<JsonNode> jsonPromise = WS.url(url).get().map(
		    new Function<WSResponse, JsonNode>() {
		        public JsonNode apply(WSResponse response) {
		            JsonNode json = response.asJson();
		            return json;
		        }
		    }
		);


		AlchemyResponse alresp = Json.fromJson(jsonPromise.get(),AlchemyResponse.class);
		Paragraph[] segments = alresp.getParagraphs();

		switch(templateNumber){
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				
				break;
		}




        
    }

    private static String getGooglePicture(String qry) throws IOException{
    	Random random = new Random();
		String url ="https://google.com/search?tbm=isch&q=";
		String[] query = qry.split(" ");
		for(String s:query){
			url = url.concat(s+"+");
		}
		Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://google.com").ignoreHttpErrors(true).timeout(0).get();
		//System.out.println(doc);
		
		Elements divs = doc.getElementsByClass("rg_di");
		Element imgdiv = divs.first();
		int i =1;
		for(;i<divs.size() && imgdiv.classNames().size()!=1;i++){
			imgdiv = doc.getElementsByClass("rg_di").get(i);
		}
		//System.out.println("Google getting choice: "+(divs.size()-i));
		int rando = random.nextInt(10)+i;
		//System.out.println(rando);
		for(int j=i;j<divs.size();j++){
			imgdiv = divs.get(j);
			if(j==rando)break;
		}
		String almost = imgdiv.getElementsByTag("a").first().attr("href");
		
		int idx1=almost.indexOf("=")+1;
		int idx2=almost.indexOf("&");
		String source = almost.substring(idx1,idx2);
		source = source.replaceAll("%253F", "?");
		source = source.replaceAll("%253D","=");
		source = source.replaceAll("%2526","&");
		source = source.replace("%20", " ");
		source = source.replace("%252520", "%20");
		source = source.replace("%2525C3","");
		source = source.replace("%2525A7","");
		return source;
		
	}

	private static String getGiphyGif(String qry){

	}

}
