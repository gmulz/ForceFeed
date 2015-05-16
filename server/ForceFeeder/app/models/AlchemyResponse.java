package models;

import play.db.ebean.Model;

public class Request extends Model{
	private Paragraph[] paras;

	public String getParagraphs(){return paras;}
	public void setUrl(Paragraph[] u){this.paras = u;}

}