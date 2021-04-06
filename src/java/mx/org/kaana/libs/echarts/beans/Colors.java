package mx.org.kaana.libs.echarts.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 3/10/2019
 *@time 16:18:22 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class Colors implements Serializable {

	public static final String[] SERIES_COLORS= {"#008000", "#ffff00", "#00bcd4", "#e91e63", "#ffb300", "#607d8b", "#ff00ff", "#d2691e", "#f4A460", "#800000", "#778899", "#696969", "#8B4513", "#9ACD32", "#808000", "#3CB371", "#ADFF2F", "#7FFF00", "#6A5ACD", "#9932CC", "#458ADE", "#9370DB", "#FF00FF", "#BDB76B", "#FF7F50", "#FF4500", "#FF8C00", "#DC143C", "#FF69B4", "#2F4F4F", "#F4A460", "#191970", "#556B2F", "#ADFF2F", "#EE82EE", "#BDB76B", "#DC143C"};
	public static final String COLOR_BLACK = "#000000";
	public static final String COLOR_WHITE = "#FFFFFF";
	public static final String COLOR_BLUE  = "#0000FF";
	public static final String COLOR_RED   = "#FF0000";
	public static final String COLOR_GREEN = "#248823";
	public static final String COLOR_MARRON= "#800000";
	private static final long serialVersionUID=4966824514724029183L;
	private static final int TOP_LIST_COLORS= 25;
	
	private static List<String> colors;
	
	static {
		colors= new ArrayList<>();
	}
	
	private static String lookForNewColor() {
		String regresar= SERIES_COLORS[new Random().nextInt(SERIES_COLORS.length)];
		if(colors.indexOf(regresar)>= 0)
			regresar= lookForNewColor();
		return regresar;
	}
	
	public static String toColor(int topColors) {
		String color= SERIES_COLORS[new Random().nextInt(SERIES_COLORS.length)];
		if(topColors>= TOP_LIST_COLORS)
			topColors= TOP_LIST_COLORS- 2;
    if(colors.size()>= topColors) 
			colors.remove(0);
  	if(colors.indexOf(color)>= 0)
			color= lookForNewColor();
		colors.add(color);
		return color;
	}
	
	public static String toColor() {
		return toColor(TOP_LIST_COLORS);
	}
	
  public static String color(int index) {
    if(index>= 0 && index< SERIES_COLORS.length)
      return SERIES_COLORS[index];
    else
      return toColor();
  }
  
}
