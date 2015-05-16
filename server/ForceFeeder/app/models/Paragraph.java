package models;

import play.db.ebean.Model;


public class Paragraph extends Model{
	private String paragraph;
	private String[] keywords;

	public String getParagraph(){return paragraph;}
	public void setParagraph(String u){this.paragraph = u;}

	public String[] getKeywords(){return keywords;}
	public void setKeywords(String[] k){this.keywords = k;}
}