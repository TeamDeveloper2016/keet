package mx.org.kaana.libs.echarts.beans;

import java.io.Serializable;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/09/2019
 *@time 11:15:22 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class TextStyle implements Serializable {

	private static final long serialVersionUID=-6049969544415495534L;
	
	private String color;
	private String fontStyle;
	private String fontWeight;
	private String fontFamily;
	private Integer fontSize;

	public TextStyle() {
		this(Axis.COLOR_BLACK);
	}

	public TextStyle(Integer fontSize) {
		this(Axis.COLOR_BLACK, "normal", "normal", "Roboto, sans-serif", fontSize);
	}
	
	public TextStyle(String color) {
		this(color, "normal", "normal", "Roboto, sans-serif", 14);
	}

	public TextStyle(String color, Integer fontSize) {
		this(color, "normal", "normal", "Roboto, sans-serif", fontSize);
	}
	
	public TextStyle(String color, String fontStyle, String fontWeight, String fontFamily, Integer fontSize) {
		this.color=color;
		this.fontStyle=fontStyle;
		this.fontWeight=fontWeight;
		this.fontFamily=fontFamily;
		this.fontSize=fontSize;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color=color;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize=fontSize;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle=fontStyle;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight=fontWeight;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily=fontFamily;
	}

	@Override
	public String toString() {
		return "TextStyle{"+"color="+color+", fontStyle="+fontStyle+", fontWeight="+fontWeight+", fontFamily="+fontFamily+", fontSize="+fontSize+'}';
	}
	
}
