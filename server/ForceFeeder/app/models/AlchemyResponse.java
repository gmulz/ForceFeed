package models;

import play.db.ebean.Model;

public class AlchemyResponse extends Model{
	private Paragraph[] paras;

	public Paragraph[] getParagraphs(){return paras;}
	public void setUrl(Paragraph[] u){this.paras = u;}

}