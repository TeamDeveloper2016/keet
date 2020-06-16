package mx.org.kaana.libs.echarts.beans;

import java.io.Serializable;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/09/2019
 *@time 10:22:46 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class ToolTip implements Serializable {

	private static final long serialVersionUID=6209057606818339102L;

	private String trigger;
	private AxisPointer axisPointer;
	private String formatter;
	private TextStyle textStyle;

	public ToolTip() {
		this("axis", new AxisPointer(), "{a} <br/>{b}: {c} ({d}%)");
	}

	public ToolTip(String trigger, AxisPointer axisPointer, String formatter) {
		this.trigger=trigger;
		this.axisPointer=axisPointer;
		this.formatter=formatter;
		this.textStyle= new TextStyle(Axis.COLOR_BLACK, 14);
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger=trigger;
	}

	public AxisPointer getAxisPointer() {
		return axisPointer;
	}

	public void setAxisPointer(AxisPointer axisPointer) {
		this.axisPointer=axisPointer;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter=formatter;
	}

	public TextStyle getTextStyle() {
		return textStyle;
	}

	public void setTextStyle(TextStyle textStyle) {
		this.textStyle=textStyle;
	}

	@Override
	public String toString() {
		return "ToolTip{"+"trigger="+trigger+", axisPointer="+axisPointer+", formatter="+formatter+", textStyle="+textStyle+'}';
	}

}
