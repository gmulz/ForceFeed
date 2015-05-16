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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.ServerRequest;
import models.Paragraph;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Application extends Controller {
	private static HashSet<String> doneGifs = new HashSet<String>();
	public static Result index(){
		return ok("pants");
	}
    public static Result process() {
        ArrayList<Paragraph> paragraphList = new ArrayList<Paragraph>();
    	ServerRequest r = Json.fromJson(request().body().asJson(),ServerRequest.class);
    	
    	String url = r.getUrl();
    	int templateNumber = r.getTNum();

    	WSRequestHolder holder = WS.url("http://thehoneybee.us/comedy/extract_url.php")
    								.setQueryParameter("ENTER_URL",url);
    	Promise<JsonNode> jsonPromise = holder.get().map(
		    new Function<WSResponse, JsonNode>() {
		        public JsonNode apply(WSResponse response) {
		            JsonNode json = response.asJson();
		            return json;
		        }
		    }
		);


    	JsonNode response = jsonPromise.get(5000);
    	Iterator<JsonNode> arrs = response.elements();

    	while(arrs.hasNext()){
    		Paragraph nextPara = parseGarysBullshit(arrs.next());
    		paragraphList.add(nextPara);
    	}
		//AlchemyResponse alresp = Json.fromJson(jsonPromise.get(),AlchemyResponse.class);
		//Paragraph[] segments = alresp.getParagraphs();

    	
    	ObjectNode ret = null;

		switch(templateNumber){
			case 1:
				ret = processTemplateOne(paragraphList);
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			default:

				break;
		}
		response().setHeader("Access-Control-Allow-Origin","*");
		response().setHeader("Access-Control-Allow-Methods","POST,GET,OPTIONS");
		response().setHeader("Access-Control-Allow-Headers","X-PINGOTHER");
		response().setHeader("Access-Control-Max-Age","1728000");
		


		return ok(ret);


        
    }


    private static ObjectNode processTemplateOne(ArrayList<Paragraph> paragraphs){
    	Random random = new Random();
    	ArrayList<ForceFedParagraph> newParagraphs = new ArrayList<ForceFedParagraph>();
    	for(Paragraph paragraph : paragraphs){
    		ForceFedParagraph f = new ForceFedParagraph();
    		f.summary = paragraph.getParagraph().split("\\. ")[0];

    		for(int i=0;i<5;i++){
    			String[] keywords = paragraph.getKeywords();
    			String keyword = keywords[random.nextInt(keywords.length)];
    			String imgurl = getGiphyGif(keyword);
    			if(imgurl.equals("")){
    				try{
    					imgurl = getGooglePicture(keyword);
    				}
    				catch(IOException e){
    					System.out.println("derp");
    				}
    			}
    			f.gifs.add(imgurl);
    		}
    		newParagraphs.add(f);
    	}
    	ObjectNode result = Json.newObject();
    	result.put("template",1);
    	ArrayNode contentArray = result.putArray("content");
    	for(ForceFedParagraph ffp : newParagraphs){
    		ObjectNode contentObj = contentArray.addObject();
    		contentObj.put("type","text");
    		contentObj.put("content",ffp.summary);
    		for(String image : ffp.gifs){
    			ObjectNode newImageObj = contentArray.addObject();
    			newImageObj.put("type","image");
    			newImageObj.put("content",image);
    		}
    	}
    	return result;


    }

    private static class ForceFedParagraph{
    	String summary;
    	ArrayList<String> gifs;

    	ForceFedParagraph(){
    		summary = "";
    		gifs = new ArrayList<String>();
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
		Random random = new Random();
		String url ="http://api.giphy.com/v1/gifs/search?q=";
		String[] query = qry.split(" ");
		for(String s:query){
			url = url.concat(s+"+");
		}
		url = url.concat("&api_key=dc6zaTOxFJmzC");
		WSRequestHolder holder = WS.url(url);
    	Promise<JsonNode> jsonPromise = holder.get().map(
		    new Function<WSResponse, JsonNode>() {
		        public JsonNode apply(WSResponse response) {
		            JsonNode json = response.asJson();
		            return json;
		        }
		    }
		);
		JsonNode gifJson = jsonPromise.get(5000);
		Iterator<JsonNode> dataIterator = gifJson.get("data").elements();
		int i = 0;
		int rando = random.nextInt(25);
		String ret = "";
		while(dataIterator.hasNext()){
			JsonNode gifObj = dataIterator.next();
			if(rando==i || !dataIterator.hasNext()){
				ret = gifObj.get("embed_url").asText();
			}
		}
		System.out.println(ret);
		return ret;
	}


	private static Paragraph parseGarysBullshit(JsonNode array){
		Paragraph ret = new Paragraph();
		int i = 0;
		Iterator<JsonNode> data = array.elements();
		while(data.hasNext()){
			if(i==0){
				ret.setParagraph(data.next().asText());
				i++;
			}
			else{
				ret.setKeywords(data.next().asText().split(", "));
			}
		}
		return ret;
	}
}
