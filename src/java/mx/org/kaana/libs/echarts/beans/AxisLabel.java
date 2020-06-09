package mx.org.kaana.libs.echarts.beans;

import java.io.Serializable;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/09/2019
 *@time 11:14:06 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class AxisLabel implements Serializable {

	private static final long serialVersionUID=-3694091918311484602L;

	private Boolean inside; 
	private TextStyle textStyle;
	private String formatter;
	private String color;
	private Integer fontSize;
	private String fontWeight;
	private String fontFamily;
	private Integer margin;

	public AxisLabel() {
		this(Boolean.FALSE, new TextStyle());
	}

	public AxisLabel(Boolean inside, TextStyle textStyle) {
		this(inside, textStyle, null);
	}
	
	public AxisLabel(Boolean inside, TextStyle textStyle, String formatter) {
		this(inside, textStyle, formatter, 30);
	}

	public AxisLabel(TextStyle textStyle) {
		this(Boolean.FALSE, textStyle, null, 30);
	}

	public AxisLabel(Boolean inside, TextStyle textStyle, String formatter, Integer margin) {
		this(inside, textStyle, formatter, "#0000FF", 18, "normal", "Roboto, sans-serif", margin);
	}

	public AxisLabel(Boolean inside, TextStyle textStyle, String formatter, String color, Integer fontSize, String fontWeight, String fontFamily, Integer margin) {
		this.inside=inside;
		this.textStyle=textStyle;
		this.formatter=formatter;
		this.color=color;
		this.fontSize=fontSize;
		this.fontWeight=fontWeight;
		this.fontFamily=fontFamily;
		this.margin=margin;
	}
	
	public Boolean getInside() {
		return inside;
	}

	public void setInside(Boolean inside) {
		this.inside=inside;
	}

	public TextStyle getTextStyle() {
		return textStyle;
	}

	public void setTextStyle(TextStyle textStyle) {
		this.textStyle=textStyle;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter=formatter;
	}

	public void setMargin(Integer margin) {
		this.margin=margin;
	}

	public Integer getMargin() {
		return margin;
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
		return "AxisLabel{"+"inside="+inside+", textStyle="+textStyle+", formatter="+formatter+", color="+color+", fontSize="+fontSize+", fontWeight="+fontWeight+", fontFamily="+fontFamily+", margin="+margin+'}';
	}
	
}
