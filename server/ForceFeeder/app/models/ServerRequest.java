package models;

import play.db.ebean.Model;


public class ServerRequest extends Model{
	private String url;
	private int tNum;

	public String getUrl(){return url;}
	public void setUrl(String u){this.url = u;}

	public int getTNum(){return tNum;}
	public void setTNum(int num){tNum = num;}
}